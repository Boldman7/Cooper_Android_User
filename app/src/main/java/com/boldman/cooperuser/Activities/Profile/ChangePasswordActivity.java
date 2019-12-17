package com.boldman.cooperuser.Activities.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Activities.Sign.LoginActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.CustomDialog;
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

public class ChangePasswordActivity extends AppCompatActivity {

    String TAG = "ChangePasswordActivity";
    public Context context = ChangePasswordActivity.this;
    public Activity activity = ChangePasswordActivity.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    EditText current_password, new_password, confirm_new_password;
    AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_password_value = current_password.getText().toString();
                String new_password_value = new_password.getText().toString();
                String confirm_password_value = confirm_new_password.getText().toString();
                if (current_password_value == null || current_password_value.equalsIgnoreCase("")) {
                    displayMessage(getString(R.string.please_enter_current_pass));
                }else if(!current_password_value.equals(SharedHelper.getKey(context, "access_password"))){
                    displayMessage(getString(R.string.password_old_not_same));
                }else if(new_password_value == null || new_password_value.equalsIgnoreCase("")){
                    displayMessage(getString(R.string.please_enter_new_pass));
                }else if(confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")){
                    displayMessage(getString(R.string.please_enter_confirm_pass));
                }else if(new_password_value.length() < 8 || new_password_value.length() > 16){
                    displayMessage(getString(R.string.password_validation1));
                }else if(current_password_value.equalsIgnoreCase(new_password_value)){
                    displayMessage(getString(R.string.new_password_validation));
                }else if(!new_password_value.equals(confirm_password_value)){
                    displayMessage(getString(R.string.different_passwords));
                }else{
                    if (helper.isConnectingToInternet()) {
                        changePassword(new_password_value);
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

    }

    public void findViewByIdandInitialization(){
        current_password = (EditText)findViewById(R.id.current_password);
        new_password = (EditText)findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_password);
        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
    }


    private void changePassword(final String new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if(customDialog != null)
            customDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("password", new_pass);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doChangePassword(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(customDialog != null)
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getResources().getString(R.string.change_pwd_dialog))
                                    .setCancelable(false)
                                    .setPositiveButton(context.getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            GoToBeginActivity();
                                        }
                                    });
                            if (alert == null) {
                                alert = builder.create();
                                alert.show();
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            displayMessage(Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayMessage(getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    if(customDialog != null)
                        customDialog.dismiss();
                    displayMessage(getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GoToBeginActivity(){

        Intent mainIntent;
        mainIntent = new Intent(activity, LoginActivity.class);

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

}
