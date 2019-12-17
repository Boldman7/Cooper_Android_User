package com.boldman.cooperuser.Activities.Coupon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Adapters.CouponListAdapter;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.Model.Coupon;
import com.boldman.cooperuser.Model.ServiceType;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponActivity extends AppCompatActivity {

    private EditText coupon_et;
    ImageView backArrow;
    private Button apply_button;
    LinearLayout couponListCardView;
    ListView coupon_list_view;

    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    CustomDialog customDialog;
    Context context;
    String mPromoCode;
    List<Coupon> mListCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coupon);

        findViewByIds();
        defineClickListeners();

        getCoupon();

    }

    private void findViewByIds() {

        context = this;

        backArrow = findViewById(R.id.backArrow);
        apply_button =  findViewById(R.id.apply_button);
        coupon_et =  findViewById(R.id.coupon_et);
        couponListCardView = findViewById(R.id.cardListViewLayout);
        coupon_list_view =  findViewById(R.id.coupon_list_view);
    }

    private void defineClickListeners() {

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(CouponActivity.this);

                mPromoCode = coupon_et.getText().toString();

                if (mPromoCode.isEmpty()) {
                    Utils.displayMessage(CouponActivity.this, getResources().getString(R.string.enter_coupon));
                } else {
                    Utils.displayMessage(CouponActivity.this, "Sending...");
                    sendToServer();
                }
            }
        });

        coupon_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle getBundle = CouponActivity.this.getIntent().getExtras();
                Boolean invoice = false;
                if (getBundle != null) {
                    invoice = getBundle.getBoolean("invoice");
                }

                if (invoice){

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("coupon_code", mListCoupon.get(position).getCoupon_code());
                    resultIntent.putExtra("discount", String.valueOf(mListCoupon.get(position).getDiscount()));
                    resultIntent.putExtra("discount_type", mListCoupon.get(position).getDiscount_type());

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }
            }
        });

    }

    private void getCoupon(){

//        couponListCardView.setVisibility(View.GONE);
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        if (customDialog != null)
//            customDialog.show();

        Log.i("getCoupon", "Boldman------->");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetCoupon(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")){

                            JSONArray arrayObject = data.getJSONArray("coupons");

                            //  Receive the reponse data from server.
                            mListCoupon = new Gson().fromJson(arrayObject.toString(), new TypeToken<List<Coupon>>() {
                            }.getType());

                            couponAdapter = new CouponListAdapter(context, R.layout.coupon_list_item, mListCoupon);
                            coupon_list_view.setAdapter(couponAdapter);
                            couponListCardView.setVisibility(View.VISIBLE);
                        }

//                        Utils.displayMessage(CouponActivity.this, data.getString("message"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(CouponActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(CouponActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*  Send promocode to Server.*/
    private void sendToServer() {

//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        if (customDialog != null)
//            customDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("coupon_code", mPromoCode);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doAddCoupon(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")){

                            couponListCardView.setVisibility(View.GONE);
                            getCoupon();
                        }

                        Utils.displayMessage(CouponActivity.this, Utils.parseErrorMessage(data));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(CouponActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(CouponActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
