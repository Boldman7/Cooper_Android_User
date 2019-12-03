package com.boldman.cooperuser.QuickBlox.MessageChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;

import java.util.ArrayList;
import java.util.Date;

public class MessagingActivity extends AppCompatActivity implements QBMgr.QBChatDialogMessageListenerEx {

    public static final int CODE_START = 3000;
    public static final int REQUEST_CODE = CODE_START + 10;
    public static final int RESULT_CODE_BACK = CODE_START + 20;
    public static final int RESULT_CODE_CHAT_UPDATED = CODE_START + 30;

    protected static MessagingActivity s_instance;

    protected boolean finishWhenBack = false;

    protected String dialogId;
    protected QBChatDialog chat;

    protected ListView listView;
    protected MessagingListAdapter listAdapter;

    int my_qb_id;
    int other_qb_id;

    public static void finishActivity() {
        MessagingActivity activity = s_instance;
        if (activity != null)
            activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Parse intent data
        Intent intent = getIntent();

        my_qb_id = intent.getIntExtra("OfferId", -1); // this can be made by me or another
        other_qb_id = intent.getIntExtra("OfferPartnerID", -1); // this can be me or another

        dialogId = intent.getStringExtra("DialogId"); // can be null
        if (dialogId != null) {
            chat = QBMgr.getChatDialog(dialogId);
            if (chat != null) {
                chat.initForChat(QBChatService.getInstance());
            }
        }

        // Set UI
        setContentView(R.layout.activity_message);

        findViewById(R.id.view_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MessagingActivity.this.onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.txt_partner_name)).setText("My partner name");

//        if (isThisMyActiveOffer) {
//            ((TextView) findViewById(R.id.txt_offer_title_and_location)).setText(
//                    getResources().getString(R.string.your_offer)
//                            + " - "
//                            + offer.getTitle());
//        } else {
//            ((TextView) findViewById(R.id.txt_offer_title_and_location)).setText(
//                    offer.getTitle()
//                            + "-"
//                            + offer.getLocation().getName());
//        }

        // Message list view
        listView = (ListView) findViewById(R.id.listview_messaging);

        // TODO : We cannot adjust footer view height
        if (listView.getFooterViewsCount() == 0) {
            View view = getLayoutInflater().inflate(R.layout.txt_view_5, null);
            listView.addFooterView(view, null, false);
        }

        this.listAdapter = new MessagingListAdapter(this);
        listView.setAdapter(listAdapter);
        QBMgr.addMsgToAdapter(dialogId, listAdapter);

        // Send message button
        findViewById(R.id.view_send_message).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.edt_txt_message);
                if (editText.getText().length() == 0)
                    return;
                finishWhenBack = true;

                final String message = editText.getText().toString();

                if (chat == null) {
                    QBMgr.createChatDialog(
                            my_qb_id,
                            other_qb_id,
                            new QBEntityCallback<QBChatDialog>() {

                                @Override
                                public void onSuccess(QBChatDialog result, Bundle params) {

                                    dialogId = result.getDialogId();
                                    chat = result;
                                    sendMsg(message, true);
                                }

                                @Override
                                public void onError(QBResponseException responseException) {

                                }
                            });
                } else {
                    sendMsg(message, false);
                }

                editText.setText("");
            }
        });

        // Set incoming message listener
        QBMgr.setExternalDlgMsgListenerForMessaging(this);

        // Receive existing message
        receiveMsg(0); // ignore stored message count

        s_instance = this;
    }

    @Override
    public void onDestroy() {
        s_instance = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        QBMgr.setExternalDlgMsgListenerForMessaging(null);

        if (finishWhenBack) {
            setResult(RESULT_CODE_CHAT_UPDATED);
            finish();
        } else {
            setResult(RESULT_CODE_BACK);
            super.onBackPressed();
        }
    }

    @Override
    public boolean processMessage(QBChatDialog dlg, String dialogId, QBChatMessage message, Integer senderId) {
        if (chat != null && chat.getDialogId().equals(dialogId)) {
            QBMgr.addMsgToAdapter(dialogId, listAdapter);

            StringifyArrayList<String> messagesIDs = new StringifyArrayList<String>();
            messagesIDs.add(message.getId());
            QBRestChatService.markMessagesAsRead(dialogId, messagesIDs).performAsync(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                }

                @Override
                public void onError(QBResponseException e) {
                }
            });

            return true;
        }
        return false;
    }

    @Override
    public boolean processError(QBChatDialog dlg, String dialogId, QBChatException exception, QBChatMessage message, Integer senderId) {
        if (chat != null && chat.getDialogId().equals(dialogId)) {
            return true;
        }
        return false;
    }

    protected void receiveMsg(final int curCount) {
        if (chat != null) {

            final int skipCount = 20;
            QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
            messageGetBuilder.sortDesc("date_sent");
            messageGetBuilder.setSkip(curCount);
            messageGetBuilder.setLimit(skipCount);

            QBMgr.receiveMsgs(dialogId
                    , messageGetBuilder
                    , new QBEntityCallback<ArrayList<QBChatMessage>>() {

                        @Override
                        public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                            if (qbChatMessages.isEmpty()) {
                                return;
                            }

                            QBMgr.addMsgToAdapter(dialogId, listAdapter);

                            if (qbChatMessages.size() == skipCount) {
                                receiveMsg(curCount + skipCount);
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
        }
    }

    protected void sendMsg(final String message, boolean setJSONData) {
        final QBChatMessage msg = new QBChatMessage();
        msg.setDialogId(chat.getDialogId());
        msg.setSenderId((int) my_qb_id);
        msg.setRecipientId((int) other_qb_id);
        msg.setBody(message);

        // TODO : always setting ?
//        if (setJSONData) {
//            String jsonData = "{" + offer.toString("offer")
//                    + "," + offerPartner.toString("user") + "}";
//            msg.setProperty("json_data", jsonData);
//        }
        msg.setProperty("save_to_history", "1");
        long time = System.currentTimeMillis() / 1000;
        msg.setProperty("date_sent", time + "");
        msg.setDateSent(time);

        chat.sendMessage(msg, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                System.out.println("*************sendMessage onSuccess");

                QBMgr.addMsgToCache(dialogId, msg);

                QBMgr.addMsgToAdapter(dialogId, listAdapter);
            }

            @Override
            public void onError(QBResponseException e) {
                System.out.println("*************sendMessage onError");
            }
        });
    }

    class MessagingListAdapter extends ArrayAdapter<QBChatMessage> {

        MessagingListAdapter(Context context) {
            super(context, R.layout.layout_my_msg_list_item);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int msgSize;
            QBChatMessage msg;
            QBChatMessage prevMsg = null;
            QBChatMessage nextMsg = null;
//
            msgSize = getCount();
            msg = getItem(position);
            if (position > 0)
                prevMsg = getItem(position - 1);
            if (position < msgSize - 1)
                nextMsg = getItem(position + 1);
//
            boolean isMe = msg.getSenderId().equals(my_qb_id);
            boolean showLargeSpaceDivider = position == 0 || (!prevMsg.getSenderId().equals(msg.getSenderId()));
            boolean showDateTimeBar = position == 0 || (!prevMsg.getSenderId().equals(msg.getSenderId()));
            int msgTimeType = 0; // 0:start, 1:middle, 2:end
            if (position == 0 || (!prevMsg.getSenderId().equals(msg.getSenderId()))) {
                msgTimeType = 0;
            } else if (position == msgSize - 1 || (!nextMsg.getSenderId().equals(msg.getSenderId()))) {
                msgTimeType = 2;
            } else {
                msgTimeType = 1;
            }

            View view;
//            if (convertView != null) {
//                View imgViewPartnerPhoto = convertView.findViewById(R.id.img_view_partner_photo);
//                if (isMe) {
//                    if (imgViewPartnerPhoto != null)
//                        convertView = null;
//                } else {
//                    if (imgViewPartnerPhoto == null)
//                        convertView = null;
//                }
//            }

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (isMe)
                view = inflater.inflate(R.layout.layout_my_msg_list_item, null);
            else
                view = inflater.inflate(R.layout.layout_partner_msg_list_item, null);

            View viewLargeSpace = view.findViewById(R.id.view_large_space);
            if (showLargeSpaceDivider)
                viewLargeSpace.setVisibility(View.VISIBLE);
            else
                viewLargeSpace.setVisibility(View.GONE);

            View dateTimeBar = view.findViewById(R.id.layout_date_time_bar);
            if (showDateTimeBar) {
                TextView txtDate = (TextView) view.findViewById(R.id.txt_date);
                txtDate.setText(DateUtil.toString_today(new Date(msg.getDateSent() * 1000)));
                TextView txtTime = (TextView) view.findViewById(R.id.txt_time);
                txtTime.setText(DateUtil.toString_h_mm_nbsp_la(new Date(msg.getDateSent() * 1000)));
                dateTimeBar.setVisibility(View.VISIBLE);
            } else {
                dateTimeBar.setVisibility(View.GONE);
            }

            TextView txtMsg = (TextView) view.findViewById(R.id.txt_msg);
            txtMsg.setText(msg.getBody());

            if (isMe) {
                if (msgTimeType == 0)
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gradient_green_my_msg_start));
                else if (msgTimeType == 1)
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gradient_green_my_msg_middle));
                else
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gradient_green_my_msg_end));
            } else {
                if (msgTimeType == 0)
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gray_partner_msg_start));
                else if (msgTimeType == 1)
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gray_partner_msg_middle));
                else
                    txtMsg.setBackground(getResources().getDrawable(R.drawable.shape_round_gray_partner_msg_end));
            }

            if (!isMe) {
//                AppCompatImageView_AlignTopHorzCenter_Oval1 myPartnerPhoto = (AppCompatImageView_AlignTopHorzCenter_Oval1) view.findViewById(R.id.img_view_partner_photo);
//                AvatarMgr.setImage(myPartnerPhoto, myPartner.getAvatar());

                Toast.makeText(MessagingActivity.this, "HERE!", Toast.LENGTH_SHORT).show();

            }

            return view;
        }
    }
}
