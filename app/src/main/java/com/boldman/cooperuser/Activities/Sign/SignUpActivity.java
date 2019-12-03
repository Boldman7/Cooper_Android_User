package com.boldman.cooperuser.Activities.Sign;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


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
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    ConnectionHelper helper;
    Boolean isConnect;
    int gender = 1;

    EditText email, mobile_number, first_name, last_name, birthday, password, confirm_password;
    ImageView backArrow;
    Button btnSignUp, btnSelectDate;
    private TextView tVTermsConditions;
    RadioGroup radioGroupGender;
    ImageView imgMale, imgFemale;
    CountryCodePicker countryCodePicker;
    String strEmail;
    DatePickerDialog datePickerDialog;
    String strValidError = "";
    List<Map<String, Object>> strListValidError = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init_Ids();
        termsAndCondition();

        helper = new ConnectionHelper(this);
        isConnect = helper.isConnectingToInternet();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        define_ClickListener();

    }

    /*  define all clickListener*/
    private void define_ClickListener() {

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                // set day of month , month and year value in the edit text

                                String choosedMonth = "";
                                String choosedDate = "";

                                if ((monthOfYear + 1) < 10) {
                                    choosedMonth = "0" + (monthOfYear + 1);
                                } else {
                                    choosedMonth = "" + (monthOfYear + 1);
                                }

                                if (dayOfMonth < 10) {
                                    choosedDate = "0" + dayOfMonth;
                                } else {
                                    choosedDate = "" + dayOfMonth;
                                }

                                birthday.setText(year + "-" + choosedMonth + "-" + choosedDate);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  check Input fields and send signup request to server.
                checkAndSendRequest();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    /*  check input fields and send signup request to server*/
    private void checkAndSendRequest() {

        strEmail = email.getText().toString();
        String strPhoneNum = mobile_number.getText().toString();
        String strFirstName = first_name.getText().toString();
        String strLastName = last_name.getText().toString();
        String strPassword = password.getText().toString();
        String strConfirmPass = confirm_password.getText().toString();
        String strCountryCode = countryCodePicker.getSelectedCountryCode();
        String strBirthDay = birthday.getText().toString();
        String strFcmToken = SharedHelper.getKey(this, "fcm_token");

        Pattern ps = Pattern.compile(".*[0-9].*");
        Matcher firstName = ps.matcher(strFirstName);
        Matcher lastName = ps.matcher(strLastName);

        if (strEmail.equals("") || strEmail.equalsIgnoreCase(getString(R.string.sample_mail_id))) {
            displayMessage(getString(R.string.email_validation));
        }
        else if (!Utils.isValidEmail(strEmail)) {
            displayMessage(getString(R.string.not_valid_email));
        }
        else if (strCountryCode.equals("")){
            displayMessage(getString(R.string.country_code_empty));
        }
        else if (strPhoneNum.equals("")){
            displayMessage(getString(R.string.mobile_number_empty));
        }
        else if (strPhoneNum.length() < 10 || strPhoneNum.length() > 20){
            displayMessage(getString(R.string.mobile_number_validation));
        }
        else if (strFirstName.equals("") || strFirstName.equalsIgnoreCase(getString(R.string.first_name))) {
            displayMessage(getString(R.string.first_name_empty));
        }
        else if (firstName.matches()) {
            displayMessage(getString(R.string.first_name_no_number));
        }
        else if (strLastName.equals("") || strLastName.equalsIgnoreCase(getString(R.string.last_name))) {
            displayMessage(getString(R.string.last_name_empty));
        }
        else if (lastName.matches()) {
            displayMessage(getString(R.string.last_name_no_number));
        }
        else if (strBirthDay.equals("") || !strBirthDay.matches("\\d{4}-\\d{2}-\\d{2}")){
            displayMessage(getString(R.string.birthday_validation_error));
        }
        else if (strPassword.equals("") || strPassword.equalsIgnoreCase(getString(R.string.password_txt))) {
            displayMessage(getString(R.string.password_validation));
        }
        else if (strPassword.length() < 8 || strPassword.length() > 16) {
            displayMessage(getString(R.string.password_validation2));
        }
        else if ((strFcmToken == null) || strFcmToken.equals("")){
            displayMessage(getString(R.string.fcm_token_required));
        }
//                else if (!Utils.isValidPassword(strPassword.trim())) {
//                    displayMessage(getString(R.string.password_validation2));
//                }
        else if (!strPassword.equals(strConfirmPass)) {
            displayMessage(getString(R.string.confirm_password_validation));
        }
        else {
            //  check the internet connection
            if (isConnect) {

                strCountryCode = "+" + strCountryCode;
                strPhoneNum = strCountryCode + strPhoneNum;

                //  send signup request to server
                sendSignUpRequest(strEmail, strPhoneNum, strFirstName, strLastName, strBirthDay, strPassword, strFcmToken);
            } else {
                displayMessage(getString(R.string.something_went_wrong_net));
            }
        }
    }

    public void init_Ids() {

        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile_number = findViewById(R.id.mobile_number);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        backArrow = findViewById(R.id.backArrow);
        btnSignUp = findViewById(R.id.sign_up);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        birthday = findViewById(R.id.birhday);
        tVTermsConditions = findViewById(R.id.term_conditions);
        imgMale=  findViewById(R.id.male_img);
        imgFemale=  findViewById(R.id.female_img);
        radioGroupGender =  findViewById(R.id.gender_group);
        countryCodePicker = findViewById(R.id.country_picker);

        radioGroupGender.setOnCheckedChangeListener(this);

        //  initialize the gender color
        imgMale.setColorFilter(ContextCompat.getColor(this,R.color.theme));
        imgFemale.setColorFilter(ContextCompat.getColor(this,R.color.grey));


        Map<String, Object> mapError = new HashMap<>();
        mapError.put("strKey", "The fcm token field is required.");
        mapError.put("resStrKey", R.string.fcm_token_required);
        strListValidError.add(mapError);

    }

    /*  set bottom bar (term and condition)*/
    public void termsAndCondition() {

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
        tVTermsConditions.setText(termsSpannableString);
        tVTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        tVTermsConditions.setHighlightColor(Color.TRANSPARENT);
    }

    /*  send signup request to server*/
    public void sendSignUpRequest(final String strEmail, String strPhoneNum, String strFirstName, final String strLastName, String strBirthDay, final String strPassword, final String strFcmToken) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("device_type", "android");
            paramObject.put("device_token", "boldman123");
            paramObject.put("email", strEmail);
            paramObject.put("phone_number", strPhoneNum);
            paramObject.put("first_name", strFirstName);
            paramObject.put("last_name", strLastName);
            paramObject.put("gender", gender);
            paramObject.put("birthday", strBirthDay);
            paramObject.put("password", strPassword);
            paramObject.put("fcm_token", strFcmToken);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSignUp(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {

                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            if (!data.getString("message").equals("S_ALREADY_SIGNUP")){

                                GlobalConstants.g_api_token = data.getString("api_token");
                                GlobalConstants.g_email = strEmail;
                                GlobalConstants.g_password = strPassword;

                                SharedHelper.putKey(SignUpActivity.this, "api_token", data.getString("api_token"));
                                SharedHelper.putKey(SignUpActivity.this, "access_email", strEmail);

                                //  open sign verify mActivity
                                gotoSignVerify();
                            } else{
                                showReSignUpDialog();
                            }
                        } else{
                            JSONObject data = object.getJSONObject("data");
//                            if (data.getString("message").equalsIgnoreCase("E_VALIDATION_ERROR")){
//
//                                JSONObject validError = data.getJSONObject("validation_error");
//                                Iterator<String> itrErrorKey = validError.keys();
//
//                                int i = 0;
//                                int j = 0;
//                                while(itrErrorKey.hasNext()){
//                                    String json_key = (String)itrErrorKey.next();
//                                    String[] strEveryValidError = {""};
//                                    try{
//                                        for (i = 0; i < strListValidError.size(); i ++) {
//
//                                            JSONArray jsonArray = validError.getJSONArray(json_key);
//
//                                            for ( j = 0;  j < jsonArray.length(); j ++) {
//
//                                                if (((String)jsonArray.get(i)).equalsIgnoreCase((String)strListValidError.get(i).get("strKey"))) {
//                                                    strValidError += " ";
//                                                    strValidError += getString((int)strListValidError.get(i).get("resStrKey"));
//                                                }
//                                            }
//                                        }
//
//                                    }catch (Exception e){
//                                        // Build the path to the key
//                                    }
//                                }
//
//                                Log.i("SignUp", strValidError);
//                            }

                            Utils.displayMessage(SignUpActivity.this, data.getString("message"));
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

    private void showReSignUpDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.alert_re_signup))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.re_signup), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendReSignUpRequest();
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.show();
    }

    private void sendReSignUpRequest() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(SignUpActivity.this, "api_token"));
            paramObject.put("email", SharedHelper.getKey(SignUpActivity.this, "access_email"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doReSignUp(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {

                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            SharedHelper.putKey(SignUpActivity.this, "api_token", GlobalConstants.g_api_token);

                            //  open sign verify mActivity
                            gotoSignVerify();

                        } else{

                            JSONObject data = object.getJSONObject("data");
                            if (data.getString("message").equalsIgnoreCase("E_VALIDATION_ERROR")){

                                JSONObject validError = data.getJSONObject("validation_error");
                                Iterator<String> itrErrorKey = validError.keys();

                                int i = 0;
                                int j = 0;
                                while(itrErrorKey.hasNext()){
                                    String json_key = (String)itrErrorKey.next();
                                    String[] strEveryValidError = {""};
                                    try{
                                        for (i = 0; i < strListValidError.size(); i ++) {

                                            JSONArray jsonArray = validError.getJSONArray(json_key);

                                            for ( j = 0;  j < jsonArray.length(); j ++) {

                                                if (((String)jsonArray.get(i)).equalsIgnoreCase((String)strListValidError.get(i).get("strKey"))) {
                                                    strValidError += " ";
                                                    strValidError += getString((int)strListValidError.get(i).get("resStrKey"));
                                                }
                                            }
                                        }

                                    }catch (Exception e){
                                        // Build the path to the key
                                    }
                                }

                                Log.i("SignUp", strValidError);
                            }

                            Utils.displayMessage(SignUpActivity.this, strValidError);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    displayMessage("Server connect error");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*  open the SignVerify mActivity*/
    private void gotoSignVerify() {

        Intent mainIntent = new Intent(this, SignVerifyActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.btn_male:
                gender = 1;
                imgMale.setColorFilter(ContextCompat.getColor(this, R.color.theme));
                imgFemale.setColorFilter(ContextCompat.getColor(this, R.color.grey));
                break;
            case R.id.btn_female:
                gender = 2;
                imgFemale.setColorFilter(ContextCompat.getColor(this, R.color.theme));
                imgMale.setColorFilter(ContextCompat.getColor(this, R.color.grey));
                break;
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
}

