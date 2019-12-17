package com.boldman.cooperuser.Activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.boldman.cooperuser.Activities.MainActivity;
import com.boldman.cooperuser.Activities.Profile.EditProfileActivity;
import com.boldman.cooperuser.Activities.Sign.LoginActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.AccessDetails;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.CircleTransformImage;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.LocaleUtils;
import com.boldman.cooperuser.Utils.Utils;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class ActivitySettings extends AppCompatActivity {

   // private RadioButton radioEnglish, radioArabic;
   public Activity activity = ActivitySettings.this;
    private LinearLayout lnrHome, lnrWork, lnrMoreSavedPlaces;
    private RelativeLayout layoutPrivacySettings;
   // private LinearLayout lnrEnglish, lnrArabic;

    private int UPDATE_HOME_WORK = 1;
    private ApiInterface mApiInterface;
    private TextView txtHomeLocation, txtWorkLocation, txtDeleteWork, txtDeleteHome,txtMoreSavedPlaces,txtDeleteMoreSavedPlaces,txtSignOut,txtEditProfile;
    private TextView userName,userContactNumber,userEmail;
    private ImageView userImage;

    private CustomDialog customDialog, customDialogNew;

    private ImageView backArrow;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {

//        radioEnglish =findViewById(R.id.radioEnglish);
//        radioArabic =findViewById(R.id.radioArabic);

//        lnrEnglish =  findViewById(R.id.lnrEnglish);
//        lnrArabic = findViewById(R.id.lnrArabic);
        lnrHome =  findViewById(R.id.lnrHome);
        lnrWork =  findViewById(R.id.lnrWork);
        lnrMoreSavedPlaces = findViewById(R.id.lnrMoreSavedPlaces);
        layoutPrivacySettings = findViewById(R.id.privacySettingsLayout);

        txtHomeLocation = findViewById(R.id.txtHomeLocation);
        txtWorkLocation = findViewById(R.id.txtWorkLocation);
        txtDeleteWork =  findViewById(R.id.txtDeleteWork);
        txtDeleteHome =  findViewById(R.id.txtDeleteHome);
        txtMoreSavedPlaces = findViewById(R.id.txtMoreSavedPlaces);
        txtDeleteMoreSavedPlaces = findViewById(R.id.txtDeleteMoreSavedPlaces);
        txtEditProfile = findViewById(R.id.txtEditProfile);
        txtSignOut = findViewById(R.id.txtSignOut);

        backArrow = findViewById(R.id.backArrow);

        userName = findViewById(R.id.user_name_settings);
        userContactNumber = findViewById(R.id.user_contact_number_settings);
        userEmail = findViewById(R.id.user_email_settings);
        userImage = findViewById(R.id.user_image_settings);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        customDialog = new CustomDialog(ActivitySettings.this);
        customDialog.setCancelable(false);
        customDialog.show();

        userName.setText(SharedHelper.getKey(getBaseContext(), "first_name") + " " + SharedHelper.getKey(getBaseContext(), "last_name"));
        userEmail.setText(SharedHelper.getKey(getBaseContext(),"email"));
        userContactNumber.setText(SharedHelper.getKey(getBaseContext(),"mobile"));
        // Loading profile image
        if (!SharedHelper.getKey(getBaseContext(),"avatar").equalsIgnoreCase("")
                && !SharedHelper.getKey(getBaseContext(),"avatar").equalsIgnoreCase(null) && SharedHelper.getKey(getBaseContext(),"avatar") != null) {
            userImage.setBackground(null);
            if (SharedHelper.getKey(getBaseContext(), "avatar").startsWith("http"))
                Picasso.with(getBaseContext()).load(SharedHelper.getKey(getBaseContext(), "avatar"))
                        .placeholder(R.drawable.user_default_sidebar)
                        .transform(new CircleTransformImage())
                        .error(R.drawable.user_default_sidebar)
                        .into(userImage);
            else
                Picasso.with(getBaseContext()).load(GlobalConstants.SERVER_HTTP_URL + "/storage/user/avatar/" + SharedHelper.getKey(getBaseContext(), "avatar"))
                        .placeholder(R.drawable.user_default_sidebar)
                        .transform(new CircleTransformImage())
                        .error(R.drawable.user_default_sidebar)
                        .into(userImage);
        }else {
            Picasso.with(getBaseContext()).load(R.drawable.user_default_sidebar)
                    .placeholder(R.drawable.user_default_sidebar)
                    .error(R.drawable.user_default_sidebar)
                    .transform(new CircleTransformImage())
                    .into(userImage);
        }

        getFavoriteLocations();

       txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, EditProfileActivity.class));
            }
        });
        lnrHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(ActivitySettings.this, "home").equalsIgnoreCase("")){
                    gotoHomeWork("home");
                }
            }
        });

        lnrWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(ActivitySettings.this, "work").equalsIgnoreCase("")){
                    gotoHomeWork("work");
                }
            }
        });

        lnrMoreSavedPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });


        txtDeleteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySettings.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                builder.setTitle(AccessDetails.siteTitle)
//                    .setIcon(R.mipmap.ic_launcher)
                        .setIcon(AccessDetails.site_icon)
                        .setMessage(getString(R.string.delete_loc));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFavoriteLocations("home");
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
                    }
                });
                dialog.show();

            }
        });

        txtDeleteWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySettings.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                builder.setTitle(AccessDetails.siteTitle)
//                    .setIcon(R.mipmap.ic_launcher)
                        .setIcon(AccessDetails.site_icon)
                        .setMessage(getString(R.string.delete_loc));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFavoriteLocations("work");
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ActivitySettings.this, R.color.colorPrimaryDark));
                    }
                });
                dialog.show();
            }
        });


        layoutPrivacySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLogoutDialog();
            }
        });

//        if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("en")){
//            radioEnglish.setChecked(true);
//        }else if (SharedHelper.getKey(ActivitySettings.this, "language").equalsIgnoreCase("ar")){
//            radioArabic.setChecked(true);
//        }else{
//            radioEnglish.setChecked(true);
//        }

//        lnrEnglish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                radioArabic.setChecked(false);
//                radioEnglish.setChecked(true);
//            }
//        });
//
//        lnrArabic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                radioEnglish.setChecked(false);
//                radioArabic.setChecked(true);
//            }
//        });

//        radioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked){
//                    radioEnglish.setChecked(false);
//                    SharedHelper.putKey(ActivitySettings.this, "language", "ar");
//                    setLanguage();
////                    recreate();
//                    GoToMainActivity();
//                }
//            }
//        });
//
//        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked){
//                    radioArabic.setChecked(false);
//                    SharedHelper.putKey(ActivitySettings.this, "language", "en");
//                    setLanguage();
////                    recreate();
//                    GoToMainActivity();
//                }
//            }
//        });
    }

    public void GoToMainActivity(){
        customDialogNew = new CustomDialog(ActivitySettings.this, getResources().getString(R.string.language_update));
        if (customDialogNew != null)
            customDialogNew.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialogNew.dismiss();
                Intent mainIntent = new Intent(ActivitySettings.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 3000);
    }


    private void gotoHomeWork(String strTag) {
        Intent intentHomeWork = new Intent(ActivitySettings.this, AddHomeWorkActivity.class);
        intentHomeWork.putExtra("tag", strTag);
        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySettings.this, "language");
        LocaleUtils.setLocale(this, languageCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_HOME_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                getFavoriteLocations();
            }
        }
    }

    private void getFavoriteLocations() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(ActivitySettings.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetFavouriteLocation(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            JSONArray homeArray = data.optJSONArray("home");
                            JSONArray workArray = data.optJSONArray("work");
                            JSONArray othersArray = data.optJSONArray("others");
                            JSONArray recentArray = data.optJSONArray("recent");

                            if (homeArray.length() > 0) {
                                Log.v("Home Address", "" + homeArray);
                                txtHomeLocation.setText(homeArray.optJSONObject(0).optString("address"));
                                txtDeleteHome.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            } else {
                                txtDeleteHome.setVisibility(View.GONE);
                                txtDeleteHome.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "home", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                            }
                            if (workArray.length() > 0) {
                                Log.v("Work Address", "" + workArray);
                                txtWorkLocation.setText(workArray.optJSONObject(0).optString("address"));
                                txtDeleteWork.setVisibility(View.VISIBLE);
                                SharedHelper.putKey(ActivitySettings.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(ActivitySettings.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            } else {
                                txtDeleteWork.setVisibility(View.GONE);
                                txtDeleteWork.setText(getResources().getString(R.string.delete));
                                SharedHelper.putKey(ActivitySettings.this, "work", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                            }
                            if (othersArray.length() > 0) {
                                Log.v("Others Address", "" + othersArray);
                            }
                            if (recentArray.length() > 0) {

                            }
                        } else{
                            Utils.displayMessage(ActivitySettings.this, Utils.parseErrorMessage(data));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(ActivitySettings.this, getString(R.string.server_connect_error));
                    }

                    customDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(ActivitySettings.this, getString(R.string.server_connect_error));
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    String strLatitude = "", strLongitude = "", strAddress = "", id = "";

    private void deleteFavoriteLocations(final String strTag) {

        if (strTag.equalsIgnoreCase("home")){
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "home_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "home_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "home");
            id = SharedHelper.getKey(ActivitySettings.this, "home_id");
        }else{
            strLatitude = SharedHelper.getKey(ActivitySettings.this, "work_lat");
            strLongitude = SharedHelper.getKey(ActivitySettings.this, "work_lng");
            strAddress = SharedHelper.getKey(ActivitySettings.this, "work");
            id = SharedHelper.getKey(ActivitySettings.this, "work_id");
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(ActivitySettings.this, "api_token"));
            paramObject.put("id", id);
            paramObject.put("type", strTag);
            paramObject.put("latitude", strLatitude);
            paramObject.put("longitude", strLongitude);
            paramObject.put("address", strAddress);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doDeleteFavouriteLocation(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            if (strTag.equalsIgnoreCase("home")) {
                                SharedHelper.putKey(ActivitySettings.this, "home", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "home_id", "");
                                txtHomeLocation.setText(getResources().getString(R.string.add_home_location));
                                txtDeleteHome.setVisibility(View.GONE);
                            } else {
                                SharedHelper.putKey(ActivitySettings.this, "work", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lat", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_lng", "");
                                SharedHelper.putKey(ActivitySettings.this, "work_id", "");
                                txtWorkLocation.setText(getResources().getString(R.string.add_work_location));
                                txtDeleteWork.setVisibility(View.GONE);
                            }
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("onFailure", "onFailure" + call.request().url());
                    customDialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showLogoutDialog() {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setTitle(AccessDetails.siteTitle)
//                    .setIcon(R.mipmap.ic_launcher)
                            .setIcon(AccessDetails.site_icon)
                            .setMessage(getString(R.string.logout_alert));
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(false);
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    public void logout() {

        JSONObject object = new JSONObject();

        Log.e("MainActivity", "logout: " + object);

        if (SharedHelper.getKey(getBaseContext(), "login_by").equals("facebook"))
            LoginManager.getInstance().logOut();
        if (SharedHelper.getKey(getBaseContext(), "login_by").equals("google"))
            signOut();
        if (!SharedHelper.getKey(ActivitySettings.this, "account_kit_token").equalsIgnoreCase("")) {
            Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(ActivitySettings.this, "account_kit_token"));
            AccountKit.logOut();
            SharedHelper.putKey(ActivitySettings.this, "account_kit_token", "");
        }
        SharedHelper.putKey(getBaseContext(), "current_status", "");
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        SharedHelper.putKey(getBaseContext(), "access_email", "");
        SharedHelper.putKey(getBaseContext(), "login_by", "");
        SharedHelper.clearSharedPreferences(getBaseContext());

        Intent goToLogin;
        goToLogin = new Intent(ActivitySettings.this, LoginActivity.class);
        goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToLogin);
        finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(getBaseContext(), "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void signOut() {
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

//    private void refreshAccessToken() {
//
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put("grant_type", "refresh_token");
//            object.put("client_id", AccessDetails.clientid);
//            object.put("client_secret", AccessDetails.passport);
//            object.put("refresh_token", SharedHelper.getKey(getBaseContext(), "refresh_token"));
//            object.put("scope", "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AccessDetails.serviceurl + URLHelper.login, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                Log.v("SignUpResponse", response.toString());
//                SharedHelper.putKey(getBaseContext(), "access_token", response.optString("access_token"));
//                SharedHelper.putKey(getBaseContext(), "refresh_token", response.optString("refresh_token"));
//                SharedHelper.putKey(getBaseContext(), "token_type", response.optString("token_type"));
//                logout();
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//
//                if (response != null && response.data != null) {
//                    SharedHelper.putKey(getBaseContext(), "loggedIn", getString(R.string.False));
//                    GoToBeginActivity();
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                return headers;
//            }
//        };
//
//        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);
//    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(ActivitySettings.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }


}
