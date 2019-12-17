package com.boldman.cooperuser.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.boldman.cooperuser.Activities.Coupon.CouponActivity;
import com.boldman.cooperuser.Activities.Help.HelpActivity;
import com.boldman.cooperuser.Activities.Profile.EditProfileActivity;
import com.boldman.cooperuser.Activities.Service.ExploreActivity;
import com.boldman.cooperuser.Activities.Settings.ActivitySettings;
import com.boldman.cooperuser.Activities.Sign.LoginActivity;
import com.boldman.cooperuser.Activities.Trips.HistoryActivity;
import com.boldman.cooperuser.Activities.Wallet.WalletActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.CooperUserApplication;
import com.boldman.cooperuser.Fragments.MapFragment;
import com.boldman.cooperuser.Fragments.MenuFragment;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.QuickBlox.MessageChat.QBMgr;
import com.boldman.cooperuser.QuickBlox.VideoChat.services.LoginService;
import com.boldman.cooperuser.QuickBlox.VideoChat.utils.Consts;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.boldman.cooperuser.Utils.GlobalConstants;

/*
*   MainActivity
*   Created by Boldman. 2019.05.09
* */

public class MainActivity extends AppCompatActivity implements
        MapFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener{


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PAYMENT = "payments";
    private static final String TAG_YOURTRIPS = "yourtrips";
    private static final String TAG_COUPON = "coupon";
    private static final String TAG_WALLET = "wallet";
    private static final String TAG_HELP = "help";
    private static final String TAG_SHARE = "share";
    private static final String TAG_LOGOUT = "logout";

    private static final int EXPLORE_REQUEST_CODE = 196;

    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;

    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_MAIN_FRAGMENT;
    private Handler mHandler;
    private String notificationMsg;
    GoogleApiClient mGoogleApiClient;

    CustomDialog customDialog;

    MapFragment homeFragment;
    private final static String TAG_MAIN_FRAGMENT = "HOME_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getProfile();
        getBalance();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");

        mHandler = new Handler();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAIN_FRAGMENT;
            loadHomeFragment();
        }

    }

    private void goToLoginActivity() {

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void getProfile() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(MainActivity.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());
            apiInterface.doGetProfile(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            JSONObject profile = data.getJSONObject("profile");

                            setSharedHelper(profile);

                        } else {

                            Utils.displayMessage(MainActivity.this, Utils.parseErrorMessage(data));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Server connect error", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBalance(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(MainActivity.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetWalletBalance(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            SharedHelper.putKey(context, "wallet_balance", data.getString("balance"));
                            SharedHelper.putKey(context, "payment_mode", "WALLET");
                        } else{
                            JSONObject data = object.getJSONObject("data");
                            SharedHelper.putKey(context, "payment_mode", "CASH");
                            Utils.displayMessage(MainActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(MainActivity.this, getString(R.string.server_connect_error));
                        SharedHelper.putKey(context, "payment_mode", "CASH");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(MainActivity.this, getString(R.string.server_connect_error));
                    SharedHelper.putKey(context, "payment_mode", "CASH");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            SharedHelper.putKey(context, "payment_mode", "CASH");
        }
    }

    private void setSharedHelper(JSONObject profile) {

        try {
            SharedHelper.putKey(this, "id", profile.getString("id"));
            SharedHelper.putKey(this, "access_email", profile.getString("email"));
            SharedHelper.putKey(this, "first_name", profile.getString("first_name"));
            SharedHelper.putKey(this, "last_name", profile.getString("last_name"));
            SharedHelper.putKey(this, "phone_number", profile.getString("phone_number"));
            SharedHelper.putKey(this, "api_token", profile.getString("api_token"));
            SharedHelper.putKey(this, "avatar", profile.getString("avatar"));
            SharedHelper.putKey(this, "gender", profile.getString("gender"));
            SharedHelper.putKey(this, "login_by", profile.getString("login_by"));
//            SharedHelper.putKey(this, "loggedIn", "true");
            SharedHelper.putKey(this, "birthday", profile.getString("birthday"));


            int quickblox_id = 0;

            try{

                quickblox_id = profile.getInt("quickblox_id");

            } catch(Exception e){

                quickblox_id = 0;
            }

            if (quickblox_id == 0){

                QBMgr.createNewUser(profile.getInt("id"), GlobalConstants.USER_QB_DEFAULT_PASSWORD);

                QBMgr.signUp(new QBEntityCallback<QBSession>() {
                    @Override
                    public void onSuccess(QBSession session, Bundle params) {

                        updateQBInfo();
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        // TODO : relogin ???
                    }
                });
            }
            else {
                QBMgr.setUser(profile.getString("quickblox_username"), quickblox_id, profile.getString("quickblox_password"));

                QBMgr.login(new QBEntityCallback<QBSession>() {
                    @Override
                    public void onSuccess(QBSession session, Bundle params) {

                        Intent tempIntent = new Intent(MainActivity.this, LoginService.class);
                        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
                        LoginService.start(MainActivity.this, QBMgr.getUser(), pendingIntent);

                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        // TODO : relogin ???
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateQBInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(MainActivity.this, "api_token"));
            paramObject.put("quickblox_id", QBMgr.getUser().getId());
            paramObject.put("quickblox_username", QBMgr.getUser().getLogin());
            paramObject.put("quickblox_password", QBMgr.getUser().getPassword());

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doUpdateQuickBlox(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    QBMgr.setUser(QBMgr.getUser().getLogin(), QBMgr.getUser().getId(), QBMgr.getUser().getPassword());

                    QBMgr.login(new QBEntityCallback<QBSession>() {
                        @Override
                        public void onSuccess(QBSession session, Bundle params) {

                        }

                        @Override
                        public void onError(QBResponseException errors) {
                            // TODO : relogin ???
                        }
                    });
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadHomeFragment() {

        SharedHelper.putKey(context, "current_status", "");

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // home
                homeFragment = MapFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Notification", notificationMsg);
                homeFragment.setArguments(bundle);
                return homeFragment;

//            case 1:
//                // Payment fragment
//                Payment paymentFragment = new Payment();
//                return paymentFragment;
//            case 2:
//                // Your Trips
//                YourTrips yourTripsFragment = new YourTrips();
//                return yourTripsFragment;
//            case 3:
//                // Coupon
//                Coupon couponFragment = new Coupon();
//                return couponFragment;
//            case 4:
//                // wallet fragment
//                Wallet walletFragment = new Wallet();
//                return walletFragment;
//
//            case 5:
//                // Help fragment
//                Help helpFragment = new Help();
//                return helpFragment;

            default:
                return new MapFragment();
        }

    }

    private void logout() {

        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            LoginManager.getInstance().logOut();
        if (SharedHelper.getKey(context, "login_by").equals("google"))
            googleSignOut();

//        if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
//            Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));
//            AccountKit.logOut();
//            SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
//        }

        SharedHelper.putKey(context, "current_status", "");
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        SharedHelper.putKey(context, "access_email", "");
        SharedHelper.putKey(context, "access_password", "");
        SharedHelper.putKey(context, "login_by", "");
        SharedHelper.clearSharedPreferences(context);

        Intent goToLogin;
//        if (AccessDetails.demo_build) {
//            goToLogin = new Intent(activity, AccessKeyActivity.class);
//        } else {
        goToLogin = new Intent(activity, LoginActivity.class);
//        }
        goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLogin);
        finish();
    }


    private void googleSignOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

//                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void addNewFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onMenuPressed(double latitude, double longitude) {
        addNewFragment( MenuFragment.newInstance(latitude,longitude),"MENU_FRAGMENT");
    }

    @Override
    public void paymentMenuPressed() {
//        SharedHelper.putKey(context, "current_status", "");
//        startActivity(new Intent(MainActivity.this, PaymentActivity.class));
    }

    @Override
    public void yourTripsMenuPressed() {
        SharedHelper.putKey(context, "current_status", "");
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        intent.putExtra("tag", "past");
        startActivity(intent);
    }

    @Override
    public void exploreMenuPressed(double latitude, double longitude) {
        SharedHelper.putKey(context, "current_status", "");
        Intent intent1 = new Intent(homeFragment.getContext(), ExploreActivity.class);
        intent1.putExtra("latitude", latitude);
        intent1.putExtra("longitude", longitude);

        homeFragment.startActivityForResult(intent1, EXPLORE_REQUEST_CODE);
    }

    @Override
    public void freeRidesMenuPressed() {
        SharedHelper.putKey(context, "current_status", "");
        startActivity(new Intent(MainActivity.this, CouponActivity.class));
    }

    @Override
    public void helpMenuPressed() {

        SharedHelper.putKey(context, "current_status", "");
        startActivity(new Intent(MainActivity.this, HelpActivity.class));
    }

    @Override
    public void settingsMenuPressed() {
        SharedHelper.putKey(context, "current_status", "");
        startActivity(new Intent(MainActivity.this, ActivitySettings.class));
    }

    @Override
    public void logoutMenuPressed() {

        showLogoutDialog();

    }

    @Override
    public void menuImagePressed() {
        SharedHelper.putKey(context, "current_status", "");
        startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
    }

    @Override
    public void walletMenuPressed() {
        SharedHelper.putKey(context, "current_status", "");
        startActivity(new Intent(MainActivity.this, WalletActivity.class));
    }

    @Override
    public void passbookMenuPressed() {

    }

    @Override
    public void onBackPressed() {
//        final MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
//
//        if (fragment.allowBackPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
//            finish();
//        }

        Log.d("MainActivity","onBackPressed");
        Toast.makeText(this,"onBackPressed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LoginService.logout(this);
        CooperUserApplication.getInstance().getQbResRequestExecutor().signOut();
    }

    private void showLogoutDialog() {

        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("LOGOUT");
            builder.setMessage(getString(R.string.exit_confirm));

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

}