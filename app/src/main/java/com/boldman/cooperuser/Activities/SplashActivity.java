package com.boldman.cooperuser.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boldman.cooperuser.Activities.Sign.LoginActivity;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;

import com.boldman.cooperuser.Utils.GlobalConstants;
import com.google.android.material.snackbar.Snackbar;

/*
*   SplashActivity
*   Created by Boldman. 2019.05.09
* */

public class SplashActivity extends AppCompatActivity {

    Handler handleCheckStatus;
    ConnectionHelper helper;

    Boolean isConnect;
    String device_token;

    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedHelper.putKey(this, "app_name", getString(R.string.app_name));

        helper = new ConnectionHelper(this);
        isConnect = helper.isConnectingToInternet();

        handleCheckStatus = new Handler();

        //  ?? meaning?
        if (Build.VERSION.SDK_INT > 16) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA
                            , Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NOTIFICATION_POLICY
                            , Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS},
                    1);

        } else{

            runThread();
        }

    }

    private void runThread() {
        //  Check login state. If user didn't login, go to login page. If not, go to main page.
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (helper.isConnectingToInternet()) {
                    //  If logged in...
                    if (SharedHelper.getKey(SplashActivity.this, "loggedIn")
                            .equalsIgnoreCase( SplashActivity.this.getResources().getString(R.string.True))) {
                        if(SharedHelper.getKey(SplashActivity.this,"login_by").equals("google")){
                            GoToMainActivity();
                        } else if(SharedHelper.getKey(SplashActivity.this,"login_by").equals("facebook")){
                            GoToMainActivity();
                        } else {
                            GetToken();

                            GlobalConstants.g_api_token = SharedHelper.getKey(SplashActivity.this, "api_token");

                            GoToMainActivity();

//                            if (SharedHelper.getKey(SplashActivity.this, "loggedIn").equalsIgnoreCase(getResources().getString(R.string.True))) {
//                                GoToMainActivity();
//                            } else {
//                                goToLoginActivity();
//                            }

                            //sendSignInRequest();
                        }
                    } else {

                        goToLoginActivity();
                        handleCheckStatus.removeCallbacksAndMessages(null);
                    }
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                    }
                } else {
                    showConnectDialog();
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }
        }, 3000);
    }

    public void GetToken() {
        try {

            if (!SharedHelper.getKey(this, "device_token").equals("") && SharedHelper.getKey(this, "device_token") != null) {
                device_token = SharedHelper.getKey(this, "device_token");
            } else {
                device_token = "boldman123";
                SharedHelper.putKey(this, "device_token", "" + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d("SplashActivity", "Failed to complete token refresh", e);
        }
    }

    /*  If verify success, sign in*/
//    public void sendSignInRequest() {
//
//        if (isConnect) {
//
//            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//
//            JsonObject gsonObject = new JsonObject();
//            try {
//                JSONObject paramObject = new JSONObject();
//
//                paramObject.put("device_token", device_token);
//                paramObject.put("email", SharedHelper.getKey(SplashActivity.this, "access_email"));
//                paramObject.put("password", SharedHelper.getKey(SplashActivity.this, "access_password"));
//
//                JsonParser jsonParser = new JsonParser();
//                gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());
//
//                apiInterface.doSignIn(gsonObject).enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//
//                        try {
//                            JSONObject object = new JSONObject(response.body().string());
//
//                            if (object.getString("status").equals("1")){
//
//                                JSONObject data = object.getJSONObject("data");
////                                GlobalConstants.g_api_token_signin = data.getString("api_token");
////
////                                GoToMainActivity();
//
//                                if (SharedHelper.getKey(SplashActivity.this, "loggedIn").equalsIgnoreCase(getResources().getString(R.string.True))) {
//                                    GoToMainActivity();
//                                } else {
//                                    goToLoginActivity();
//                                }
//
//
//                            } else{
//                                JSONObject data = object.getJSONObject("data");
//                                displayMessage(data.getString("message"));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            displayMessage(getString(R.string.server_connect_error));
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.printStackTrace();
//                        displayMessage(getString(R.string.server_connect_error));
//                    }
//                });
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            displayMessage(getString(R.string.something_went_wrong_net));
//        }
//    }


    /*  You don't logged in. Go to LoginActivity */
    private void goToLoginActivity() {

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    /*  You logged in. Go to MainActivity */
    public void GoToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    /*  Show dialog asking you to turn on the WIFI*/
    private void showConnectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    public void displayMessage(String toastString) {

        try {

            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

        } catch (Exception e) {
            try {
                Toast.makeText(this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    runThread();

                } else {

                    Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

}
