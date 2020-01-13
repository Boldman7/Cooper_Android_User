package com.boldman.cooperuser.QuickBlox.MessageChat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.boldman.cooperuser.EventBus.MessageEvent;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.facebook.share.Share;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBHttpConnectionConfig;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionListener;

import java.util.ArrayList;
import java.util.HashMap;

public class QBMgr {

    static final String APP_ID = "79428";
    static final String AUTH_KEY = "CDxJhjxqffqRCUK";
    static final String AUTH_SECRET = "h-eEj3bqWTLFrpK";
    static final String ACCOUNT_KEY = "ESxHkzRTxRxQw1WtUFNL";
    protected static final int TIMEOUT = 30000;
    private static final Object msgLocker = new Object();
    public static QBChatService chatService;
    protected static int myQbId;
    protected static QBUser user;
    protected static QBChatDialogMessageListener dlgMsgListener;
    protected static QBChatDialogMessageListenerEx externalDlgMsgListenerForMessaging;
    protected static QBChatDialogMessageListenerEx externalDlgMsgListenerForOverview;
    protected static QBChatDialogMessageListenerEx externalDlgMsgListenerForMainActivity;
    protected static HashMap<String, QBChatDialog> dlgIdDlgMap = new HashMap<>();
    protected static HashMap<String, ArrayList<QBChatMessage>> allChatMsgs = new HashMap<String, ArrayList<QBChatMessage>>();
    // TODO: heads up not working for xiaomi
    protected static int notificationId = 0;

    public static void initialize(Context context) {

        /******************************************************************************************/

        // call before init method for QBSettings
        //QBSettings.getInstance().setStoringMehanism(StoringMechanism.UNSECURED);

        QBSettings.getInstance().init(context, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        /******************************************************************************************/

        // TIMEOUT value in milliseconds.
        QBHttpConnectionConfig.setConnectTimeout(TIMEOUT);

        // TIMEOUT value in milliseconds.
        QBHttpConnectionConfig.setReadTimeout(TIMEOUT);

        /******************************************************************************************/

        QBChatService.setDebugEnabled(true); // enable chat logging

        // set reply TIMEOUT in milliseconds for connection's packet.
        // Can be used for events like login, join to dialog to increase waiting response time from server if network is slow.
        QBChatService.setDefaultPacketReplyTimeout(TIMEOUT);

        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();

        // Sets chat socket's read TIMEOUT in seconds
        chatServiceConfigurationBuilder.setSocketTimeout(TIMEOUT / 1000);

        // Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setKeepAlive(true);

        // Sets the TLS security mode used when making the connection. By default TLS is disabled.
        chatServiceConfigurationBuilder.setUseTls(true);
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);

        chatService = QBChatService.getInstance();
        chatService.setReconnectionAllowed(true);

        /******************************************************************************************/

        user = new QBUser();

        /******************************************************************************************/

        dlgMsgListener = new QBChatDialogMessageListener() {

            @Override
            public void processMessage(String dialogId
                    , QBChatMessage message
                    , Integer senderId) {

                System.out.println("********* QB dlgMsgListener processMessage");

                // Caculate unread message count
                int cntUnreadMsg = 0;

                if (GlobalConstants.isMsgActivity == false){

                    if (SharedHelper.getKey(context, "cntUnreadMsg") != null)
                        if (!SharedHelper.getKey(context, "cntUnreadMsg").equalsIgnoreCase(""))
                            cntUnreadMsg = Integer.valueOf(SharedHelper.getKey(context, "cntUnreadMsg"));

                    cntUnreadMsg ++;
                } else {
                    cntUnreadMsg = 0;
                }

                SharedHelper.putKey(context, "cntUnreadMsg", cntUnreadMsg + "");
                EventBus.getDefault().post(new MessageEvent.ReceivedMessage(cntUnreadMsg));

                QBChatDialog dlg = getDlgFromIncomingMsg(dialogId, message, senderId);

                addMsgToCache(dialogId, message);

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForMessaging;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processMessage(dlg, dialogId, message, senderId))
                        return;
                }

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForOverview;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processMessage(dlg, dialogId, message, senderId))
                        return;
                }

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForMainActivity;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processMessage(dlg, dialogId, message, senderId))
                        return;
                }
            }

            @Override
            public void processError(String dialogId
                    , QBChatException exception
                    , QBChatMessage message, Integer senderId) {

                System.out.println("********* QB dlgMsgListener processError");

                QBChatDialog dlg = getDlgFromIncomingMsg(dialogId, message, senderId);

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForMessaging;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processError(dlg, dialogId, exception, message, senderId))
                        return;
                }

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForOverview;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processError(dlg, dialogId, exception, message, senderId))
                        return;
                }

                {
                    QBChatDialogMessageListenerEx externalDlgMsgListener = externalDlgMsgListenerForMainActivity;
                    if (externalDlgMsgListener != null && externalDlgMsgListener.processError(dlg, dialogId, exception, message, senderId))
                        return;
                }
            }
        };
    }

    public static QBChatDialog getChatDialog(String dialogId) {
        return dlgIdDlgMap.get(dialogId);
    }

    public static void createNewUser(int id, String pwd) {

        user.setLogin("cooper_quickblox_username_" + id);
        user.setPassword(pwd);
    }

    public static void setUser(String qbUserName, int qbId, String pwd){

        myQbId = qbId;
        user.setId(qbId);
        user.setLogin(qbUserName);
        user.setPassword(pwd);
    }

    public static QBUser getUser(){

        return user;
    }

    public static void setExternalDlgMsgListenerForMessaging(QBChatDialogMessageListenerEx listener) {
        externalDlgMsgListenerForMessaging = listener;
    }

    public static void setExternalDlgMsgListenerForOverview(QBChatDialogMessageListenerEx listener) {
        externalDlgMsgListenerForOverview = listener;
    }

    public static void setExternalDlgMsgListenerForMainActivity(QBChatDialogMessageListenerEx listener) {
        externalDlgMsgListenerForMainActivity = listener;
    }

    public static void signUp(final QBEntityCallback<QBSession> callback) {

        QBUser newUser = new QBUser();

        QBAuth.createSession(newUser).performAsync( new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {

                System.out.println("********* QB createSession onSuccess");

                QBUsers.signUp(user).performAsync( new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser mUser, Bundle args) {

                        user.setId(mUser.getId());

                        System.out.println("********* QB signUp User onSuccess");

                        if (callback != null) {
                            callback.onSuccess(session, params);
                        }
                    }

                    @Override
                    public void onError(QBResponseException errors) {

                        System.out.println("********* QB signUp User onError");

                        if (callback != null) {
                            callback.onError(errors);
                        }
                    }
                });

            }

            @Override
            public void onError(QBResponseException errors) {

                System.out.println("********* QB createSession onError");

                if (callback != null) {
                    callback.onError(errors);
                }
            }
        });
    }


    public static void login(final QBEntityCallback<QBSession> callback) {

        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {

                System.out.println("********* QB createSession onSuccess");

                // success, login to chat
//                user.setId(session.getUserId()); // qbId

                chatService.login(user, new QBEntityCallback<QBSession>() {

                    @Override
                    public void onSuccess(QBSession session, Bundle params) {

                        System.out.println("********* QB login onSuccess");

                        chatService.getIncomingMessagesManager().addDialogMessageListener(dlgMsgListener);

                        if (callback != null) {
                            callback.onSuccess(session, params);
                        }
                    }

                    @Override
                    public void onError(QBResponseException errors) {

                        System.out.println("********* QB login onError");

                        if (callback != null) {
                            callback.onError(errors);
                        }
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

                System.out.println("********* QB createSession onError");

                if (callback != null) {
                    callback.onError(errors);
                }
            }
        });
    }

    public static boolean checkQBLoggedIn(){
        return chatService.isLoggedIn();
    }

    public static void logout(final QBEntityCallback<Void> callback) {

        if (!chatService.isLoggedIn()) {
            if (callback != null) {
                callback.onSuccess(null, null);
            }
            return;
        }

        chatService.logout(new QBEntityCallback<Void>() {

            @Override
            public void onSuccess(Void data, Bundle params) {

                System.out.println("********* QB logout onSuccess");

                // TODO
                chatService.getIncomingMessagesManager().removeDialogMessageListrener(dlgMsgListener);

                // success
                chatService.destroy();

                if (callback != null) {
                    callback.onSuccess(data, params);
                }
            }

            @Override
            public void onError(QBResponseException errors) {

                System.out.println("********* QB logout onError");

                if (callback != null) {
                    callback.onError(errors);
                }
            }
        });
    }

    public static void addConnectionListener(final ConnectionListener connectionListener) {
        chatService.addConnectionListener(connectionListener);
    }

    public static void createChatDialog(
            int creatorQbId
            , int partnerQbId
            , final QBEntityCallback<QBChatDialog> callback) {

        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        occupantIdsList.add(partnerQbId); // first partner
        occupantIdsList.add(creatorQbId); // second creator

        QBChatDialog dialog = DialogUtils.buildDialog("", QBDialogType.PRIVATE, occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {

            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {

                System.out.println("********* QB createChatDialog onSuccess");

                addDlg(result);

                if (callback != null) {
                    callback.onSuccess(result, params);
                }
            }

            @Override
            public void onError(QBResponseException responseException) {

                System.out.println("********* QB createChatDialog onError");

                if (callback != null) {
                    callback.onError(responseException);
                }
            }
        });
    }

    public static void getOtherOfferChatDialogs(final QBEntityCallback<ArrayList<QBChatDialog>> callback) {

        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
//        requestGetBuilder.setLimit(100);
//        requestGetBuilder.setSkip(5);
        requestGetBuilder.sortDesc("last_message_date_sent");
        requestGetBuilder.eq("data[partner_qb_id]", myQbId);
        getChatDialogs(requestGetBuilder, callback);
    }

    public static void getMyOfferChatDialogs(final QBEntityCallback<ArrayList<QBChatDialog>> callback, Integer myUserQbId) {

        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
//        requestGetBuilder.setLimit(100);
//        requestGetBuilder.setSkip(5);
        requestGetBuilder.sortDesc("last_message_date_sent");
        requestGetBuilder.eq("data[creator_qb_id]", myUserQbId);
        getChatDialogs(requestGetBuilder, callback);
    }

    public static void getChatDialogs(QBRequestGetBuilder requestGetBuilder
            , final QBEntityCallback<ArrayList<QBChatDialog>> callback) {

        QBRestChatService.getChatDialogs(QBDialogType.PRIVATE, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {

            @Override
            public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {

                System.out.println("********* QB getChatDialogs onSuccess");

                for (QBChatDialog dlg : result) {
                    addDlg(dlg);
                }

                //int totalEntries = params.getInt("total_entries");

                if (callback != null) {
                    callback.onSuccess(result, params);
                }
            }

            @Override
            public void onError(QBResponseException responseException) {

                System.out.println("********* QB getChatDialogs onError");

                if (callback != null) {
                    callback.onError(responseException);
                }
            }
        });
    }

    protected static QBChatDialog getDlgFromIncomingMsg(String dialogId, QBChatMessage message, Integer senderId) {

        QBChatDialog dlg;

        if (dlgIdDlgMap.containsKey(dialogId)) {

            dlg = dlgIdDlgMap.get(dialogId);
        } else {
            dlg = new QBChatDialog(dialogId);

            ArrayList<Integer> occupantIds = new ArrayList<>();
            occupantIds.add(myQbId); // first partner
            occupantIds.add(senderId); // second creator
            dlg.setOccupantsIds(occupantIds);

            // custom data(only json_data)
            QBDialogCustomData customData = new QBDialogCustomData("Custom");
            customData.putString("json_data", (String) message.getProperty("json_data"));
            dlg.setCustomData(customData);

            // init Dialog for chatting
            dlg.initForChat(dialogId, QBDialogType.PRIVATE, chatService);

            // add dlg to cache
            addDlg(dlg);
        }

        return dlg;
    }

    protected static void addDlg(QBChatDialog dlg) {
        dlgIdDlgMap.put(dlg.getDialogId(), dlg);
        //dlg.addMessageListener(dlgMsgListener);
    }

    public static synchronized ArrayList<QBChatMessage> getChatMsgs(String dialogId) {

        if (!allChatMsgs.containsKey(dialogId)) {
            allChatMsgs.put(dialogId, new ArrayList<QBChatMessage>());
        }

        return allChatMsgs.get(dialogId);
    }

    public static void addMsgToCache(String dialogId, QBChatMessage msg) {

        if (dialogId == null || dialogId.equals(""))
            return;

        synchronized (msgLocker) {

            ArrayList<QBChatMessage> msgs = getChatMsgs(dialogId);

            if (msgs.isEmpty()) {
                msgs.add(msg);
                return;
            }

            for (int i = msgs.size() - 1; i >= 0; i--) {
                QBChatMessage prevMsg = msgs.get(i);
                if (prevMsg.getId().equals(msg.getId())) {
                    msgs.remove(i);
                    break;
                }
            }

            for (int i = msgs.size() - 1; i >= 0; i--) {
                QBChatMessage prevMsg = msgs.get(i);
                if (msg.getDateSent() >= prevMsg.getDateSent()) {

                    if (i == msgs.size() - 1) {
                        msgs.add(msg);
                    } else {
                        msgs.add(i + 1, msg);
                    }

                    return;
                }
            }

            msgs.add(0, msg);
        }
    }

    public static void addMsgToAdapter(String dialogId, ArrayAdapter<QBChatMessage> adapter) {

        if (dialogId == null || dialogId.equals(""))
            return;

        synchronized (msgLocker) {

            ArrayList<QBChatMessage> msgs = getChatMsgs(dialogId);
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(msgs);
            adapter.notifyDataSetChanged();
        }
    }

    public static void receiveMsgs(final String dialogId
            , QBMessageGetBuilder messageGetBuilder
            , final QBEntityCallback<ArrayList<QBChatMessage>> callback) {

        if (dialogId == null || dialogId.equals(""))
            return;

        QBRestChatService.getDialogMessages(getChatDialog(dialogId), messageGetBuilder)
                .performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {

                    @Override
                    public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                        for (int i = qbChatMessages.size() - 1; i >= 0; i--) {
                            QBChatMessage msg = qbChatMessages.get(i);
                            addMsgToCache(dialogId, msg);
                        }

                        callback.onSuccess(qbChatMessages, bundle);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        callback.onError(e);
                    }
                });
    }

    public static synchronized void showNotification(Context context
            , QBChatDialog dlg
            , String dialogId
            , QBChatMessage message
            , Integer senderId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("DeedsChannel"
                    , "DeedsChannel",
                    NotificationManager.IMPORTANCE_HIGH);   // for heads-up notifications
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        /*
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,PendingIntent.FLAG_ONE_SHOT);
        * */

        Intent replyIntent = new Intent(context, NotificationActionReceiver.class);
        replyIntent.setAction("REPLY_INTENT");
        replyIntent.putExtra("NOTIFICATION_ID", notificationId);
        replyIntent.putExtra("DIALOG_ID", dialogId);
        replyIntent.putExtra("MESSAGE_ID", message.getId());
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent markAsReadIntent = new Intent(context, NotificationActionReceiver.class);
        markAsReadIntent.setAction("MARK_AS_READ_INTENT");
        markAsReadIntent.putExtra("NOTIFICATION_ID", notificationId);
        markAsReadIntent.putExtra("DIALOG_ID", dialogId);
        markAsReadIntent.putExtra("MESSAGE_ID", message.getId());
        PendingIntent markAsReadPendingIntent = PendingIntent.getBroadcast(context, 0, markAsReadIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(context, "DeedsChannel")
                //.setAutoCancel(true)
                //.setLargeIcon()
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                //.setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("Sender Name")
                .setContentText(message.getBody())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)   // for heads-up notifications
                .setContentIntent(replyPendingIntent)
                .setFullScreenIntent(replyPendingIntent, true) // for heads-up notifications
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.setVibrate(new long[0])
                .addAction(0, "Reply", replyPendingIntent)
                .addAction(0, "Mark As Read", markAsReadPendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);

        notificationId++;
    }

    public interface QBChatDialogMessageListenerEx {

        boolean processMessage(
                QBChatDialog dlg
                , String dialogId
                , QBChatMessage message
                , Integer senderId);

        boolean processError(
                QBChatDialog dlg
                , String dialogId
                , QBChatException exception
                , QBChatMessage message
                , Integer senderId);
    }
}
