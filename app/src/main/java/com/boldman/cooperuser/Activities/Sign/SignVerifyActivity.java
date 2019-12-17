package com.boldman.cooperuser.Activities.Sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Activities.MainActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignVerifyActivity extends AppCompatActivity {

    Button btnVerify;
    EditText eTEmailVCode;
    EditText eTPhoneVCode;
    ImageView backArrow;

    Boolean isInternet;
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_verify);

        btnVerify = findViewById(R.id.btn_verify);
        eTEmailVCode = findViewById(R.id.email_verify);
        eTPhoneVCode = findViewById(R.id.phone_verify);
        backArrow = findViewById(R.id.backArrow);

        helper = new ConnectionHelper(this);
        isInternet = helper.isConnectingToInternet();

        btnVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String emailVCode = eTEmailVCode.getText().toString();
                String phoneVCode = eTPhoneVCode.getText().toString();

                if (emailVCode.equals("") && phoneVCode.equals("")){
                    displayMessage(getString(R.string.email_or_phone_verify_required));
                } else{

                    if (isInternet) {
                        sendVerifyRequest(emailVCode, phoneVCode);
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void sendVerifyRequest(String emailVCode, String phoneVCode) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);

            paramObject.put("email", GlobalConstants.g_email);
            paramObject.put("email_verify_code", emailVCode);
            paramObject.put("sms_verify_code", phoneVCode);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSignVerify(gsonObject).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                progressDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response.body().string());

                    if (object.getString("status").equals("1")){

                        JSONObject data = object.getJSONObject("data");

//                        SharedHelper.putKey(SignVerifyActivity.this, "access_email", GlobalConstants.g_email);
//                        SharedHelper.putKey(SignVerifyActivity.this, "access_password", GlobalConstants.g_password);

                        gotoLoginActivity();

                        //sendSignInRequest();

                    } else{

                        JSONObject data = object.getJSONObject("data");
                        displayMessage(Utils.parseErrorMessage(data));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                displayMessage(getString(R.string.server_connect_error));
            }
        });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*  If verify success, sign in*/
    public void sendSignInRequest() {

        if (isInternet) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            JsonObject gsonObject = new JsonObject();
            try {
                JSONObject paramObject = new JSONObject();

                paramObject.put("device_token", "boldman123");
                paramObject.put("email", GlobalConstants.g_email);
                paramObject.put("password", GlobalConstants.g_password);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

                apiInterface.doSignIn(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            GlobalConstants.g_api_token = data.getString("api_token");

                            SharedHelper.putKey(SignVerifyActivity.this, "access_email", GlobalConstants.g_email);
                            SharedHelper.putKey(SignVerifyActivity.this, "access_password", GlobalConstants.g_password);
                            SharedHelper.putKey(SignVerifyActivity.this, "api_token", data.getString("api_token"));
                            SharedHelper.putKey(SignVerifyActivity.this, "loggedIn", getString(R.string.True));

                            gotoMainActivity();


                        } else{
                            JSONObject data = object.getJSONObject("data");
                            displayMessage(Utils.parseErrorMessage(data));

                            gotoLoginActivity();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.something_went_wrong));

                        gotoLoginActivity();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    displayMessage(getString(R.string.server_connect_error));
                }
            });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    /*  If you fail in login. Go to LoginActivity */
    private void gotoLoginActivity() {

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
        finish();
    }

    private void gotoMainActivity() {

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();

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
}
