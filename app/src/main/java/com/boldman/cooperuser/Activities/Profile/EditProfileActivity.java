package com.boldman.cooperuser.Activities.Profile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.boldman.cooperuser.Activities.MainActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.CooperUserApplication;
import com.boldman.cooperuser.Helper.AppHelper;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Helper.VolleyMultipartRequest;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class EditProfileActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final int SELECT_PHOTO = 100;
    private static String TAG = "EditProfile";

    public Context mContext = EditProfileActivity.this;
    public Activity mActivity = EditProfileActivity.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button saveBTN;
    ImageView backArrow;
    TextView changePasswordTxt;
    private RelativeLayout passwordLayout;
    EditText first_name, last_name, mobile_no, date_birth;
    TextView email;
    ImageView profile_Image;
    Boolean isImageChanged = false;
    public static int deviceHeight;
    public static int deviceWidth;
    Uri uri;
    Boolean isPermissionGivenAlready = false;
    RadioGroup genderGrp;
    RadioButton maleRbtn;
    RadioButton femaleRbtn;
    int gender;
    Button btnSelectDate;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        findViewByIds();
        defineClickListeners();

        setProfileContent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        
        if (SharedHelper.getKey(mContext, "login_by").equals("facebook") ||
                SharedHelper.getKey(mContext, "login_by").equals("google")) {
            //  changePasswordTxt.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.GONE);
        } else {
            // changePasswordTxt.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
        }

    }

    private void defineClickListeners() {


        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(mContext,
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

                                date_birth.setText(year + "-" + choosedMonth + "-" + choosedDate);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //GoToMainActivity();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());

                if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                    displayMessage(mContext.getResources().getString(R.string.email_validation));
                } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                    displayMessage(mContext.getResources().getString(R.string.mobile_number_empty));
                } else if (mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20) {
                    displayMessage(mContext.getResources().getString(R.string.mobile_number_validation));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                    displayMessage(mContext.getResources().getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                    displayMessage(mContext.getResources().getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(mContext.getResources().getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(mContext.getResources().getString(R.string.last_name_no_number));
                } else if (date_birth.getText().toString().equals("") || !date_birth.getText().toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
                    displayMessage(getString(R.string.birthday_validation_error));
                } else {
                    if (isInternet) {
                        updateProfile();
                    } else {
                        displayMessage(mContext.getResources().getString(R.string.something_went_wrong_net));
                    }
                }


            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ChangePasswordActivity.class));
                //Toast.makeText(mContext, "You clicked changePassword!", Toast.LENGTH_SHORT).show();
            }
        });

        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ChangePasswordActivity.class));
                Toast.makeText(mContext, "You clicked passwordLayout!", Toast.LENGTH_SHORT).show();
            }
        });

        profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        goToImageIntent();
                    }
                } else {
                    goToImageIntent();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if(!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
    }

    /*  Set Profile content from SharedHelper*/
    private void setProfileContent() {

        try {
            first_name.setText(SharedHelper.getKey(mContext, "first_name"));
            last_name.setText(SharedHelper.getKey(mContext, "last_name"));
            mobile_no.setText(SharedHelper.getKey(mContext, "phone_number"));
            email.setText(SharedHelper.getKey(mContext, "access_email"));
            date_birth.setText(SharedHelper.getKey(mContext, "birthday"));
            String avatarUrl = SharedHelper.getKey(mContext, "avatar");

            // password???
            if (!avatarUrl.equalsIgnoreCase("")
                    && !avatarUrl.equalsIgnoreCase(null)
                    && avatarUrl != null) {

                SharedHelper.putKey(mContext, "avatar", avatarUrl);

                profile_Image.setBackground(null);
                if (avatarUrl.startsWith("http"))
                    Picasso.with(mContext)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_dummy_user)
                            .error(R.drawable.ic_dummy_user)
                            .into(profile_Image);
                else
                    Picasso.with(mContext)
                            .load(GlobalConstants.SERVER_HTTP_URL + "/storage/user/avatar/" + avatarUrl)
                            .placeholder(R.drawable.ic_dummy_user)
                            .error(R.drawable.ic_dummy_user)
                            .into(profile_Image);
            } else {

                Picasso.with(mContext)
                        .load(R.drawable.ic_dummy_user)
                        .placeholder(R.drawable.ic_dummy_user)
                        .error(R.drawable.ic_dummy_user)
                        .into(profile_Image);
            }

            int nGender = Integer.valueOf(SharedHelper.getKey(mContext, "gender"));

            if (nGender != 0) {
                if (nGender == 1) {
                    maleRbtn.setChecked(true);
                    gender = 1;
                } else if (nGender == 2) {
                    femaleRbtn.setChecked(true);
                    gender = 2;
                }
            }

            //first_name.requestFocus();

        } catch (Exception e){
            e.printStackTrace();
        }

    }


    public void goToImageIntent() {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check (API 23)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }else{
                    isImageChanged = true;
                    Bitmap resizeImg = getBitmapFromUri(this, uri);
                    if (resizeImg != null) {
                        Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                        profile_Image.setImageBitmap(reRotateImg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    private void updateProfileWithImage() {
        isImageChanged = false;
        customDialog = new CustomDialog(mContext);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, GlobalConstants.SERVER_HTTP_URL + "/api/user/update_profile",
                new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject data = jsonObject.getJSONObject("data");

                    if (jsonObject.getString("status").equals("1")) {
                        Utils.displayMessage(mActivity, data.getString("message"));
                        getProfile();

                    } else {
                        Utils.displayMessage(mActivity, data.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.server_connect_error));
                            }
                        } else {
                            displayMessage(getString(R.string.server_connect_error));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.server_connect_error));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        //updateProfileWithoutImage();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", SharedHelper.getKey(mContext, "api_token"));
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("gender", Integer.toString(gender));
                params.put("birthday", date_birth.getText().toString());
                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(mContext, "token_type") + " " + SharedHelper.getKey(mContext, "access_token"));
//                return headers;
//            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("avatar", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };

        CooperUserApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }


    private void updateProfileWithoutImage() {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));
            paramObject.put("first_name", first_name.getText().toString());
            paramObject.put("last_name", last_name.getText().toString());
            paramObject.put("gender", gender);
            paramObject.put("birthday", date_birth.getText().toString());

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());
            apiInterface.doUpdateProfile(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {
                            Utils.displayMessage(mActivity, data.getString("message"));
                            getProfile();

                        } else {
                            Utils.displayMessage(mActivity, data.getString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, getString(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(mContext, "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findViewByIds() {
        email =findViewById(R.id.email);
        first_name =  findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile_no =  findViewById(R.id.mobile_no);
        saveBTN = findViewById(R.id.saveBTN);
        changePasswordTxt =  findViewById(R.id.changePasswordTxt);
        passwordLayout = findViewById(R.id.password_layout_edit_profile);
        backArrow = findViewById(R.id.backArrow);
        profile_Image = findViewById(R.id.img_profile);

        helper = new ConnectionHelper(mContext);
        isInternet = helper.isConnectingToInternet();
        genderGrp = findViewById(R.id.gender_group);
        maleRbtn = findViewById(R.id.male_btn);
        femaleRbtn = findViewById(R.id.female_btn);
        genderGrp.setOnCheckedChangeListener(this);

        date_birth = findViewById(R.id.birthday);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        //Assign current profile values to the edittext
        //Glide.with(mActivity).load(SharedHelper.getKey(mContext, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);
        if (!SharedHelper.getKey(mContext, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(mContext, "picture").equalsIgnoreCase(null)
                && SharedHelper.getKey(mContext, "picture") != null) {
            profile_Image.setBackground(null);
            if (SharedHelper.getKey(mContext, "picture").startsWith("http"))
                Picasso.with(mContext)
                        .load(SharedHelper.getKey(mContext, "picture"))
                        .placeholder(R.drawable.ic_dummy_user)
                        .error(R.drawable.ic_dummy_user)
                        .into(profile_Image);
            else
                Picasso.with(mContext)
                        .load(GlobalConstants.SERVER_HTTP_URL + "/storage/user/avatar/" + SharedHelper.getKey(mContext, "picture"))
                        .placeholder(R.drawable.ic_dummy_user)
                        .error(R.drawable.ic_dummy_user)
                        .into(profile_Image);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        }
        email.setText(SharedHelper.getKey(mContext, "access_email"));
        first_name.setText(SharedHelper.getKey(mContext, "first_name"));
        last_name.setText(SharedHelper.getKey(mContext, "last_name"));

        changePasswordTxt.setText(SharedHelper.getKey(mContext, "access_password"));

        if (SharedHelper.getKey(mContext, "mobile") != null
                && !SharedHelper.getKey(mContext, "mobile").equals("null")
                && !SharedHelper.getKey(mContext, "mobile").equals("")) {
            mobile_no.setText(SharedHelper.getKey(mContext, "mobile"));
        }
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(mActivity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        mActivity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(mContext,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //GoToMainActivity();
    }
    
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.male_btn:
                gender = 1;
                break;
            case R.id.female_btn:
                gender = 2;
                break;
        }
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

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));

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

                            Utils.displayMessage(mActivity, data.getString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, R.string.server_connect_error, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(mContext, "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSharedHelper(JSONObject profile) {

        try {
            SharedHelper.putKey(this, "access_email", profile.getString("email"));
            SharedHelper.putKey(this, "first_name", profile.getString("first_name"));
            SharedHelper.putKey(this, "last_name", profile.getString("last_name"));
            SharedHelper.putKey(this, "phone_number", profile.getString("phone_number"));
            SharedHelper.putKey(this, "api_token", profile.getString("api_token"));
            SharedHelper.putKey(this, "avatar", profile.getString("avatar"));
            SharedHelper.putKey(this, "gender", profile.getString("gender"));
            SharedHelper.putKey(this, "login_by", profile.getString("login_by"));
            SharedHelper.putKey(this, "loggedIn", "true");
            SharedHelper.putKey(this, "birthday", profile.getString("birthday"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
