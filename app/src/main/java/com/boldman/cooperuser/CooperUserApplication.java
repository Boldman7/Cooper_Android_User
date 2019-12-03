package com.boldman.cooperuser;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.boldman.cooperuser.QuickBlox.MessageChat.QBMgr;
import com.boldman.cooperuser.QuickBlox.VideoChat.util.QBResRequestExecutor;
import com.boldman.cooperuser.Utils.DateUtil;
import com.quickblox.auth.session.QBSettings;

/*
*   CooperUserApplication
*   Created by HappyStar. 2019.05.09
* */

public class CooperUserApplication extends Application {

    private static CooperUserApplication mInstance;

    private QBResRequestExecutor qbResRequestExecutor;

    public static final String USER_DEFAULT_PASSWORD = "quickblox";

    private RequestQueue mRequestQueue;
    public static final String TAG = CooperUserApplication.class
            .getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        DateUtil.initialize(getApplicationContext());
        QBMgr.initialize(getApplicationContext());
    }

    public static synchronized CooperUserApplication getInstance() {
        return mInstance;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }
}
