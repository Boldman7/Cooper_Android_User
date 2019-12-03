package com.boldman.cooperuser.QuickBlox.VideoChat.services.fcm;

import android.util.Log;

import com.boldman.cooperuser.QuickBlox.MessageChat.QBMgr;
import com.boldman.cooperuser.QuickBlox.VideoChat.services.LoginService;
import com.boldman.cooperuser.QuickBlox.VideoChat.utils.SharedPrefsHelper;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;

import java.util.Map;


public class PushListenerService extends QBFcmPushListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
//        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = QBMgr.getUser();
            Log.d(TAG, "App has logged user" + qbUser.getId());
            LoginService.start(this, qbUser);
//        }
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);
    }
}