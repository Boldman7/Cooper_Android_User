package com.boldman.cooperuser.Activities.Sign;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.boldman.cooperuser.Utils.Utils.displayMessage;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView btnBackArrow;
    Button btnResetPass;
    EditText email, newPass, confirmPass, verifyCode;
    TextView tVTitle;
    TextInputLayout newPassLyt,confirmPassLyt,verifyCodeLyt;
    ImageView imgBahamas;

    String strEmail, strNewPass, strConfirmPass, strVerifyCode;
    String validation = "";
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        tVTitle = findViewById(R.id.title_txt);
        newPass = findViewById(R.id.new_password);
        newPassLyt = findViewById(R.id.new_password_lay);
        confirmPass = findViewById(R.id.confirm_password);
        confirmPassLyt = findViewById(R.id.confirm_password_lay);
        verifyCode = findViewById(R.id.otp);
        verifyCodeLyt = findViewById(R.id.otp_lay);
        imgBahamas = findViewById(R.id.bahamas_image);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.getExtras().getString("email") != null){
                strEmail = intent.getExtras().getString("email");
            }
        }

        email.setText(strEmail);

        btnBackArrow = findViewById(R.id.backArrow);
        btnResetPass = findViewById(R.id.reset_password_button);

        helper = new ConnectionHelper(this);

        define_setOnClickListeners();

    }

    private void define_setOnClickListeners() {

        btnResetPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                strEmail = email.getText().toString();

                if(validation.equalsIgnoreCase("")){
                    if (strEmail.equals("")) {
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.email_validation));
                    }
                    else if (!Utils.isValidEmail(strEmail)) {
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.not_valid_email));
                    }
                    else{
                        if (helper.isConnectingToInternet()) {
                            Utils.hideKeyboard(ForgotPasswordActivity.this);
                            sendForgotPasswordRequest();
                        } else {
                            displayMessage(ForgotPasswordActivity.this, getString(R.string.something_went_wrong_net));
                        }
                    }
                } else {
                    strNewPass = newPass.getText().toString();
                    strConfirmPass = confirmPass.getText().toString();
                    strVerifyCode = verifyCode.getText().toString();
                    if (strVerifyCode.equals("") ) {
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.otp_validation));
                    } else if (strNewPass.equals("") || strNewPass.equalsIgnoreCase(getString(R.string.new_password))) {
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.password_validation));
                    }else if (strNewPass.length() < 8 || strNewPass.length() > 16){
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.password_validation1));
//                    }else if(!Utils.isValidPassword(strNewPass)){
//                        displayMessage(ForgotPasswordActivity.this, getString(R.string.password_validation2));
                    }else if(strConfirmPass.equals("") || strConfirmPass.equalsIgnoreCase(getString(R.string.confirm_password))||
                            !strNewPass.equalsIgnoreCase(strConfirmPass)){
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.confirm_password_validation));
                    }else if (strConfirmPass.length()<6) {
                        displayMessage(ForgotPasswordActivity.this, getString(R.string.password_size));
                    }else {
                        if (helper.isConnectingToInternet()) {
                            sendResetPasswordRequest();
                        } else {
                            displayMessage(ForgotPasswordActivity.this, getString(R.string.something_went_wrong_net));
                        }
                    }
                }

            }
        });

        btnBackArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendResetPasswordRequest() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("email", strEmail);
            paramObject.put("reset_code", strVerifyCode);
            paramObject.put("password", strNewPass);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doResetPassword(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getString("status").equals("1")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                            builder.setMessage(getString(R.string.reset_pwd_dialog))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });

                            builder.show();

                        } else{
                            JSONObject data = object.getJSONObject("data");

                            displayMessage(ForgotPasswordActivity.this, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayMessage(ForgotPasswordActivity.this, "Server connect error");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    displayMessage(ForgotPasswordActivity.this, "Server connect error");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*  If user presses the reset password btn, send forgotpassword request to server.*/
    private void sendForgotPasswordRequest() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("email", strEmail);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doForgotPassword(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getString("status").equals("1")){

                            validation = "reset";

                            email.setClickable(false);
                            email.setFocusable(false);
                            email.setFocusableInTouchMode(false);

                            tVTitle.setVisibility(View.GONE);
                            imgBahamas.setVisibility(View.GONE);
                            newPassLyt.setVisibility(View.VISIBLE);
                            confirmPassLyt.setVisibility(View.VISIBLE);
                            verifyCodeLyt.setVisibility(View.VISIBLE);
                            //verifyCode.performClick();

                        } else{
                            JSONObject data = object.getJSONObject("data");

                            displayMessage(ForgotPasswordActivity.this, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayMessage(ForgotPasswordActivity.this, "Server connect error");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    displayMessage(ForgotPasswordActivity.this, "Server connect error");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
