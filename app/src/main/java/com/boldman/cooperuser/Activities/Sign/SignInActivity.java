package com.boldman.cooperuser.Activities.Sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Activities.MainActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {


    ConnectionHelper helper;

    ImageView btnBackArrow;
    Button btnSignIn, btnSignUp;
    TextView btnForgotPass;
    TextView tVTermsCondition;
    CheckBox chkRememberPasswd;
    String strEmail, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnBackArrow = findViewById(R.id.backArrow);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnForgotPass = findViewById(R.id.forgotPassword);
        tVTermsCondition = findViewById(R.id.tv_term_condition);
        chkRememberPasswd = findViewById(R.id.chkRemember);

        setTermCoditionText();

        helper = new ConnectionHelper(this);

        define_setOnClickListeners();
    }

    public void setTermCoditionText() {
        //Terms & Condition
        SpannableString termsSpannableString = new SpannableString("By signing up, you agree to CoOper's Terms and Conditions and Privacy Policy.");

        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

        };
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        termsSpannableString.setSpan(termsSpan, 37, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsSpannableString.setSpan(privacySpan, 62, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVTermsCondition.setText(termsSpannableString);
        tVTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
        tVTermsCondition.setHighlightColor(Color.TRANSPARENT);
    }

    /*  define setOnClickListerners*/
    private void define_setOnClickListeners() {

        btnSignIn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkInputFields();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                gotoSignUpActivity();
            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                gotoForgotPasswordActivity();
            }
        });

        btnBackArrow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void gotoSignUpActivity() {

        startActivity(new Intent(SignInActivity.this, SignUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
    }

    private void gotoForgotPasswordActivity(){

        startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class)
                .putExtra("email", strEmail)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
    }

    private void checkInputFields() {

        strEmail = ((EditText) findViewById(R.id.email)).getText().toString();
        strPassword = ((EditText) findViewById(R.id.password)).getText().toString();

        if (strEmail.equals("") || strEmail.equalsIgnoreCase(getString(R.string.sample_mail_id))) {
            Utils.displayMessage(this, getString(R.string.email_validation));
        }
        else if (!Utils.isValidEmail(strEmail)) {
            Utils.displayMessage(this, getString(R.string.not_valid_email));
        }
        else if (strPassword.equals("") || strPassword.equalsIgnoreCase(getString(R.string.password_txt))) {
            Utils.displayMessage(this, getString(R.string.password_validation));
        }
        else if (strPassword.length() < 8 || strPassword.length() > 16) {
            Utils.displayMessage(this, getString(R.string.password_validation2));
        }
//        else if (!Utils.isValidPassword(strPassword.trim())) {
//            displayMessage(getString(R.string.password_validation2));
//        }
        else {
            //  check the internet connection
            if (helper.isConnectingToInternet()) {

                SharedHelper.putKey(SignInActivity.this, "access_email", strEmail);
                SharedHelper.putKey(SignInActivity.this, "access_password", strPassword);

                //  send signup request to server
                sendSignInRequest();

            } else {
                Utils.displayMessage(this, getString(R.string.something_went_wrong_net));
            }
        }

    }

    private void sendSignInRequest() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("device_token", "boldman123");
            paramObject.put("email", strEmail);
            paramObject.put("password", strPassword);
            paramObject.put("fcm_token", SharedHelper.getKey(SignInActivity.this, "fcm_token"));

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

                            SharedHelper.putKey(SignInActivity.this, "access_email", strEmail);
                            SharedHelper.putKey(SignInActivity.this, "access_password", strPassword);
                            SharedHelper.putKey(SignInActivity.this, "api_token", data.getString("api_token"));

                            if (chkRememberPasswd.isChecked()){
                                SharedHelper.putKey(SignInActivity.this, "loggedIn", getString(R.string.True));
                            } else{
                                SharedHelper.putKey(SignInActivity.this, "loggedIn", getString(R.string.False));
                            }
                            gotoMainActivity();


                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(SignInActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(SignInActivity.this, getString(R.string.something_went_wrong));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(SignInActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*  You logged in. Go to MainActivity */
    public void gotoMainActivity() {

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

}
