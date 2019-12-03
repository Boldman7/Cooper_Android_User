package com.boldman.cooperuser.QuickBlox.MessageChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.core.app.NotificationManagerCompat;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;

import org.json.JSONObject;

/*
reply, mark as read

- background service
- app resume
- app stop
- overview resume
- overview stop
- messaging resume (other chat)
- messaging stop (all chat)
- other resume (overview exist or not)
- other stop (overview exist or not)
(out of overview, messaging)
push notification
 */

public class NotificationActionReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", -1);
        String dialogId = intent.getStringExtra("DIALOG_ID");
        String messageId = intent.getStringExtra("MESSAGE_ID");

        if (action.equals("REPLY_INTENT")) {
            // TODO: cancel has a problem
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);

            final QBChatDialog chat = QBMgr.getChatDialog(dialogId);
            String jsonData = chat.getCustomData() != null ? chat.getCustomData().getString("json_data") : "";
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                final long offerId = jsonObject.getJSONObject("offer").getLong("id");

                Intent newIntent = new Intent(context, MessagingActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                newIntent.putExtra("DialogId", chat.getDialogId());
                newIntent.putExtra("OfferId", offerId);

                MessagingActivity.finishActivity();
                context.startActivity(newIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals("MARK_AS_READ_INTENT")) {
            // TODO: cancel has a problem
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);

            StringifyArrayList<String> messagesIDs = new StringifyArrayList<String>();
            messagesIDs.add(messageId);
            QBRestChatService.markMessagesAsRead(dialogId, messagesIDs).performAsync(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    //MessagesOverviewFrag.updateFragment();
                }

                @Override
                public void onError(QBResponseException e) {
                }
            });
        }
    }
}
