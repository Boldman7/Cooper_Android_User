package com.boldman.cooperuser.Activities.Sign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boldman.cooperuser.Activities.MainActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient mGoogleApiClient;

    ConnectionHelper helper;
    LinearLayout facebook_layout, google_layout;
    ImageView backArrow;
    CallbackManager callbackManager;

    public Context context = SocialLoginActivity.this;
    String TAG = "SocialLoginActivity";
    String device_token, device_UDID;

    UIManager uiManager;
    String accessToken = "";
    String loginBy = "";
    String mobileNumber= "";

    private static final int REQ_SIGN_IN_REQUIRED = 100;
    private static final int RC_SIGN_IN = 100;
    public static int APP_REQUEST_CODE = 99;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_social);

        helper = new ConnectionHelper(SocialLoginActivity.this);

        initIds();
        defineClickListeners();

        /*  Facebook Login*/
        callbackManager = CallbackManager.Factory.create();

    }

    private void initIds() {

        backArrow = findViewById(R.id.backArrow);

        facebook_layout  = findViewById(R.id.facebook_layout);
        google_layout = findViewById(R.id.google_layout);

        /*----------Google Login---------------*/
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void defineClickListeners() {

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        facebook_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (helper.isConnectingToInternet()) {

                    LoginManager.getInstance().logInWithReadPermissions(SocialLoginActivity.this,
                            Arrays.asList("email"));

                    Log.i("Before registerCallback", "Boldman------->" + AccessToken.getCurrentAccessToken() + "");

                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {

                                public void onSuccess(LoginResult loginResult) {

                                    Log.i("Callback Success","Boldman------>" + AccessToken.getCurrentAccessToken()+"");

                                    if (AccessToken.getCurrentAccessToken() != null) {

                                        Log.i("loginresult", "" + loginResult.getAccessToken().getToken());

                                        SharedHelper.putKey(SocialLoginActivity.this, "accessToken", loginResult.getAccessToken().getToken());
                                        accessToken = loginResult.getAccessToken().getToken();
                                        loginBy = "facebook";

                                        //login("facebook");

                                        phoneLogin();
                                    }else{
                                        Utils.displayMessage(SocialLoginActivity.this, getString(R.string.something_went_wrong));
                                    }
                                }

                                @Override
                                public void onCancel() {
                                    // App code
                                    Utils.displayMessage(SocialLoginActivity.this, getResources().getString(R.string.fb_cancel));
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    // App code
                                    Utils.displayMessage(SocialLoginActivity.this, getResources().getString(R.string.fb_error));
                                }
                            });
                } else {
                    //mProgressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SocialLoginActivity.this);
                    builder.setMessage("Check your Internet").setCancelable(false);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(NetworkAction);
                        }
                    });
                    builder.show();
                }
            }
        });

        google_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public void phoneLogin() {

        Log.e(TAG, "onActivityResult: phone Login Account Kit" + AccountKit.getCurrentAccessToken() + "");

        final Intent intent = new Intent(this, AccountKitActivity.class);

        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(this, R.color.cancel_ride_color), R.drawable.banner_fb, SkinManager.Tint.WHITE, 85);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Log.v("ACCESS GOOGLE `~~~~~~~",data+"  -  "+requestCode);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }

            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request

                if (resultCode == RESULT_OK) {

//                    if (loginBy.equalsIgnoreCase("facebook")) {
//                        login("facebook");
//                    } else {
//                        //Log.v("GOOGLE LOG IN ~~~~~~",URLHelper.base +""+ URLHelper.GOOGLE_LOGIN+"   "+accessToken+"   ");
//                        login("google");
//                    }

                    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                    //     Log.v("ACCESS TOKEN `~~~~~~~",AccessToken.getCurrentAccessToken().getToken());
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            try {
                                //   Log.v("ACCESS TOKEN `~~~~~~~",AccessToken.getCurrentAccessToken().getToken());
                                Log.e(TAG, "onSuccess: Account Kit" + account.getId());
                                Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());

                                if (AccountKit.getCurrentAccessToken().getToken() != null) {
                                    SharedHelper.putKey(SocialLoginActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());

                                    mobileNumber = account.getPhoneNumber().toString();
                                    if (loginBy.equalsIgnoreCase("facebook")) {
                                        login("facebook");
                                    } else {
                                        //Log.v("GOOGLE LOG IN ~~~~~~",URLHelper.base +""+ URLHelper.GOOGLE_LOGIN+"   "+accessToken+"   ");
                                        login("google");
                                    }
                                    //GoToMainActivity();
                                } else {
                                    SharedHelper.putKey(SocialLoginActivity.this, "account_kit_token", "");
                                    SharedHelper.putKey(SocialLoginActivity.this, "loggedIn", getString(R.string.False));
                                    SharedHelper.putKey(context, "email", "");
                                    SharedHelper.putKey(context, "login_by", "");
                                    SharedHelper.putKey(SocialLoginActivity.this, "account_kit_token", "");
                                    Intent goToLogin;
//                                if (AccessDetails.demo_build) {
//                                    goToLogin = new Intent(SocialLoginActivity.this, AccessKeyActivity.class);
//                                } else {
                                    goToLogin = new Intent(SocialLoginActivity.this, LoginActivity.class);
                                    // }
                                    goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(goToLogin);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e(TAG, "onError: Account Kit" + accountKitError);
//                        displayMessage(getResources().getString(R.string.social_cancel));
                        }
                    });

                }else {
                    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                    if (loginResult != null) {
                        SharedHelper.putKey(this, "account_kit", getString(R.string.True));
                    } else {
                        SharedHelper.putKey(this, "account_kit", getString(R.string.False));
                    }
                    if (loginResult.getError() != null) {
//                        displayMessage(getResources().getString(R.string.social_failed));
                    } else if (loginResult.wasCancelled()) {
//                        displayMessage(getResources().getString(R.string.social_cancel));
                    } else {
                        if (loginResult.getAccessToken() != null) {
                            Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken().toString());
                            SharedHelper.putKey(this, "account_kit", loginResult.getAccessToken().toString());
                        } else {
                            SharedHelper.putKey(this, "account_kit", "");
                        }
                    }
                }
            }
        }
    }

    private void login(final String socialLoginType){

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        if (progressDialog != null)
            progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("device_type", "android");
            paramObject.put("device_token", "0619");
            paramObject.put("accessToken", accessToken);
            paramObject.put("phone_number", mobileNumber);
            paramObject.put("login_by", socialLoginType);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSocialSignIn(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            SharedHelper.putKey(SocialLoginActivity.this, "api_token", data.getString("api_token"));
                            SharedHelper.putKey(SocialLoginActivity.this, "access_token", accessToken);
                            SharedHelper.putKey(SocialLoginActivity.this, "login_by", socialLoginType);
                            SharedHelper.putKey(SocialLoginActivity.this, "loggedIn", "true");

                            GlobalConstants.g_api_token = data.getString("api_token");

                            GoToMainActivity();


//                            if (!data.getString("currency").equalsIgnoreCase("") && data.getString("currency") != null)
//                                SharedHelper.putKey(context, "currency", data.getString("currency"));
//                            else
//                                SharedHelper.putKey(context, "currency", "$");

                        } else{

                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(SocialLoginActivity.this, Utils.parseErrorMessage(data));
                            GoToLoginActivity();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(SocialLoginActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    if (progressDialog != null)
                        progressDialog.dismiss();

                    t.printStackTrace();
                    Utils.displayMessage(SocialLoginActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void GoToLoginActivity() {
        Intent mainIntent;
        mainIntent = new Intent(SocialLoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        try{
            Log.d("Beginscreen GoogleLogin", "handleSignInResult:" + result.toString()+"   ");
            Log.d("Beginscreen", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d("Google", "display_name:" + acct.getDisplayName());
                Log.d("Google", "mail:" + acct.getEmail());
                Log.d("Google", "photo:" + acct.getPhotoUrl());

                new RetrieveTokenTask().execute(acct.getEmail());
            } else {
                Log.d("BeginscreenLogin Failed", "handleSignInResult:" + result.toString()+"   "+result.getStatus().getStatusMessage()+"   CODE :"+result.getStatus().getStatusCode());
                Utils.displayMessage(SocialLoginActivity.this, getResources().getString(R.string.google_login));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String GoogleaccessToken) {
            super.onPostExecute(GoogleaccessToken);
            Log.e("Token", GoogleaccessToken);
            accessToken = GoogleaccessToken;

            Log.i("SocialPostExecute", "Boldman----->accessToken---->" + accessToken);

            loginBy = "google";

            //login("google");
            phoneLogin();
        }
    }
}
