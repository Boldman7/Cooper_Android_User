package com.boldman.cooperuser.Activities.Trips;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.BookingRides;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;
import com.boldman.cooperuser.Utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingTripDetailActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    AlertDialog mReasonDialog;
    BookingRides trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upcoming_trip_detail);

        setData();

        defineClickListeners();
    }

    private void setData() {

        mContext = this;
        mActivity = this;

        Intent intent = getIntent();
        trip = (BookingRides) intent.getSerializableExtra("trip");

        ((TextView) findViewById(R.id.tv_driver_name)).setText(trip.getFirstname() + " " + trip.getLastname());
        ((RatingBar) findViewById(R.id.rb_driver_rating)).setRating((float)trip.getDriver_avg_rating());

        try{
            ((TextView) findViewById(R.id.tv_datetime)).setText(DateUtil.convertDateToString24HoursFormat(trip.getBook_at()));
        } catch (Exception e){
            ((TextView) findViewById(R.id.tv_datetime)).setText("");
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.tv_payment_method)).setText(trip.getPayment_method());
        ((TextView) findViewById(R.id.tv_price)).setText("$" + trip.getPay_amount());
    }

    private void defineClickListeners() {

        findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_booking_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayCancelReasonDialog();
            }
        });
    }

    private void displayCancelReasonDialog() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(getResources().getDrawable(R.drawable.cooper_logo))
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setView(view)
                .setCancelable(true);
        mReasonDialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reasonEtxt.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(mContext, "Please input the reason.", Toast.LENGTH_SHORT).show();
                else {
                    sendCancelRequest(reasonEtxt.getText().toString());
                }
            }
        });

        mReasonDialog.show();
    }

    private void sendCancelRequest(String strReason) {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            long time = System.currentTimeMillis() / 1000;

            paramObject.put("api_token", SharedHelper.getKey(mActivity, "api_token"));
            paramObject.put("ride_id", trip.getId());
            paramObject.put("cancel_at", time);
            paramObject.put("cancel_reason", strReason);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doCancelRide(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")){

                            Utils.displayMessage(mActivity, data.getString("message"));
                            mReasonDialog.dismiss();

                            finish();

                        } else{
                            Utils.displayMessage(mActivity, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
