package com.boldman.cooperuser.Activities.Payment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Activities.Coupon.CouponActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.Model.CardInfo;
import com.boldman.cooperuser.Adapters.NewPaymentListAdapter;
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

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
*   Manage Payment Activity
*   Created By Boldman. 2019.05.21*/

public class PaymentActivity extends AppCompatActivity {

    private final int ADD_CARD_CODE = 435;
    TextView tVAddCard, tVPromoCode;
    ImageView imgBack;
    ListView listViewPayment;
    LinearLayout cashLayout;
    LinearLayout walletLayout;

    List<CardDetails> mListCardDetails;
    NewPaymentListAdapter mPaymentListAdapter;

    boolean check = true;

    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findViewByIds();
        defineClickListeners();

        /*  Get card list you registered.*/
        getCardList();

    }

    /*  Init ids*/
    private void findViewByIds() {

        tVAddCard = findViewById(R.id.addCard);
        tVPromoCode = findViewById(R.id.addPromoCode);
        imgBack = findViewById(R.id.backArrow);
        listViewPayment = findViewById(R.id.payment_list_view);
        cashLayout = findViewById(R.id.cash_layout);
        walletLayout = findViewById(R.id.wallet_layout);
    }

    /*  Define all clicklisteners*/
    private void defineClickListeners() {

        tVAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(PaymentActivity.this, AddCardActivity.class);
                startActivityForResult(mainIntent, ADD_CARD_CODE);
            }
        });

        tVPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, CouponActivity.class));
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*  Set Card to pay*/
//        listViewPayment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//
//                    CardInfo cardInfo=new CardInfo();
//                    cardInfo.setLastFour(mListCardDetails.get(position).getLastFour());
//                    cardInfo.setCardId(mListCardDetails.get(position).getId());
//                    cardInfo.setCardType(mListCardDetails.get(position).getBrand());
//
//                    if (check){
//                        Intent intent=new Intent();
//                        intent.putExtra("card_info",cardInfo);
//                        setResult(RESULT_OK,intent);
//                        finish();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        /*  Delete*/
        listViewPayment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    check = false;
                    DeleteCardDailog(mPaymentListAdapter.getItem(i).getCardNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("payment_type","CASH");
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("payment_type","WALLET");
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    /*  Show alertdialog to confirm delete*/
    private void DeleteCardDailog(final String cardNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        check = true;
                        sendDeleteCard(cardNo);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        check = true;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*  Send request for deleting card*/
    private void sendDeleteCard(String cardNo) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("card_no", cardNo);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doDeleteCard(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            getCardList();

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(PaymentActivity.this, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(PaymentActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(PaymentActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCardList();
                }
            }
        }
    }

    public void getCardList() {

        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("atype", "user");

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetCardList(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            JSONArray cards = data.getJSONArray("cards");

                            mListCardDetails = new Gson().fromJson(cards.toString(), new TypeToken<List<CardDetails>>() {
                            }.getType());

                            mPaymentListAdapter = new NewPaymentListAdapter(PaymentActivity.this, mListCardDetails, PaymentActivity.this);
                            listViewPayment.setAdapter(mPaymentListAdapter);

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(PaymentActivity.this, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(PaymentActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(PaymentActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
