package com.boldman.cooperuser.Activities.Wallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Activities.Payment.AddCardActivity;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Model.CardInfo;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
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

public class WalletActivity extends AppCompatActivity implements View.OnClickListener{


    private final int ADD_CARD_CODE = 435;
    private final int SELECT_CARD = 5555;
    private static final int WALLET_TOPUP_REQUEST_CODE = 231;

    private Button add_fund_button;
    private ProgressDialog loadingDialog;

    private Button add_money_button;
    private EditText money_et;
    private TextView balance_tv;
    private String session_token;
    private Button one, two, three, add_money;
    private double update_amount = 0;
    private ArrayList<CardDetails> cardDetailsArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol, lblPaymentChange, lblCardNumber;
    private LinearLayout lnrAddmoney, lnrClose, lnrWallet;
    private int selectedPosition = 0;
    private CardInfo cardInfo = new CardInfo();
    private ImageView backArrow;
    //List<CardDetails> mListCardDetails;
    boolean loading;
    String strBTToken = "";
    private static final int BRAINTREE_REQUEST_CODE = 4949;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);

        findViewByIds();
        defineClickListeners();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(context.getResources().getString(R.string.please_wait));

        session_token = SharedHelper.getKey(this, "access_token");

    }

    private void findViewByIds() {

        cardDetailsArrayList = new ArrayList<>();
        add_fund_button = (Button) findViewById(R.id.add_fund_button);
        balance_tv = (TextView) findViewById(R.id.balance_tv);
        currencySymbol = (TextView) findViewById(R.id.currencySymbol);
        add_money = (Button) findViewById(R.id.add_money);
        context = this;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = (EditText) findViewById(R.id.money_et);
        lblPaymentChange = (TextView) findViewById(R.id.lblPaymentChange);
        lblCardNumber = (TextView) findViewById(R.id.lblCardNumber);
        lnrClose = (LinearLayout) findViewById(R.id.lnrClose);
        lnrAddmoney = (LinearLayout) findViewById(R.id.lnrAddmoney);
        lnrWallet = (LinearLayout) findViewById(R.id.lnrWallet);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        backArrow = (ImageView) findViewById(R.id.backArrow);
    }

    private void defineClickListeners() {

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        one.setText(SharedHelper.getKey(context, "currency") + "10");
        two.setText(SharedHelper.getKey(context, "currency") + "20");
        three.setText(SharedHelper.getKey(context, "currency") + "100");

        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddmoney.setVisibility(View.VISIBLE);
            }
        });

        lblPaymentChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardDetailsArrayList.size() > 0) {
                    showChooser();
                } else {
                    gotoAddCard();
                }
            }
        });

        lnrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddmoney.setVisibility(View.GONE);
            }
        });

        lnrWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        add_fund_button.setOnClickListener(this);

        getBalance();
    }

    private void getBalance(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(WalletActivity.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetWalletBalance(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            //currency = data.getString("currency");
                            currency = "$";
                            balance_tv.setText(currency + data.getString("balance"));
                            SharedHelper.putKey(context, "wallet_balance", data.getString("balance"));

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(WalletActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showChooser() {

        final String[] cardsList = new String[cardDetailsArrayList.size()];

        for (int i = 0; i < cardDetailsArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardDetailsArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(context.getResources().getString(R.string.add_money_using));
        builderSingle.setSingleChoiceItems(cardsList, selectedPosition, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_cv);

        for (int j = 0; j < cardDetailsArrayList.size(); j++) {
            String card = "";
            card = "XXXX-XXXX-XXXX-" + cardDetailsArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                Log.e("Items clicked===>", "" + selectedPosition);

                cardInfo.setCardId(cardDetailsArrayList.get(selectedPosition).getId());
                cardInfo.setCardType(cardDetailsArrayList.get(selectedPosition).getBrand());
                cardInfo.setSelected(cardDetailsArrayList.get(selectedPosition).getIsDefault());
                cardInfo.setLastFour(cardDetailsArrayList.get(selectedPosition).getLastFour());
                lblCardNumber.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
                addMoney(cardInfo);
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addMoney(cardInfoArrayList.get(which));
//                    }
//                });
        builderSingle.show();
    }

    private void addMoney(CardInfo cardInfo){

        if (customDialog != null)
            customDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("card_id", cardInfo.getCardId());
            paramObject.put("amount", money_et.getText().toString());

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSendNonce(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        JSONObject data = object.getJSONObject("data");
                        if (object.getString("status").equals("1")){

                            balance_tv.setText(currency + data.getString("wallet_balance"));
                            SharedHelper.putKey(context, "wallet_balance", data.getString("wallet_balance"));
                            money_et.setText("");
                            lnrAddmoney.setVisibility(View.GONE);

                        } else{
                            Utils.displayMessage(WalletActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCardActivity.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                }
            }
        }

        if (requestCode == SELECT_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                addMoney(cardInfo);
            }
        }

        if(requestCode == BRAINTREE_REQUEST_CODE){
            if (RESULT_OK == resultCode){
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentNonce = result.getPaymentMethodNonce().getNonce();
                String type = result.getPaymentMethodType().toString();

                Log.i("Braintree", "Boldman----->" + type);
                //send to your server
                Log.d("WalletActivity", "Testing the app here");

                sendPaymentNonceToServer(paymentNonce, type);
            }else if(resultCode == Activity.RESULT_CANCELED){
                Log.d("WalletActivity", "User cancelled payment");
            }else {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("WalletActivity", " error exception");
                error.printStackTrace();
            }
        }
    }

    private void sendPaymentNonceToServer(String paymentNonce, String type) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(WalletActivity.this, "api_token"));
            paramObject.put("nonce", paymentNonce);
            paramObject.put("amount", update_amount);
            paramObject.put("type", type);
            paramObject.put("type_value", "");

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSendNonce(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {

                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(WalletActivity.this, Utils.getMessageForKey(data.getString("message")));

                            balance_tv.setText(currency + data.getString("balance"));
                            SharedHelper.putKey(context, "wallet_balance", data.getString("balance"));
                            money_et.setText("");
                            lnrAddmoney.setVisibility(View.GONE);

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(WalletActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    update_amount = Double.parseDouble(money_et.getText().toString());
//                    if (cardDetailsArrayList.size() > 0) {
//                        // Goto payment screen
//                        Intent intent = new Intent(WalletActivity.this, PaymentActivity.class);
//                        intent.putExtra("type", "wallet");
//                        startActivityForResult(intent, SELECT_CARD);
//                    } else {
//                        gotoAddCard();
//                    }

                    addMoneyWithBT();
                }

                break;

            case R.id.one:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("10");
                break;
            case R.id.two:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("20");
                break;
            case R.id.three:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                money_et.setText("100");
                break;
        }
    }

    /*  Get BrainTree Token from CooperServer.*/
    private void addMoneyWithBT() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(WalletActivity.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetBTToken(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            strBTToken = data.getString("token");
                            if (strBTToken != null && !strBTToken.equals("")) {
                                SharedHelper.putKey(WalletActivity.this, "braintree_token", data.getString("token"));
                                goToBrainTreeActivity(strBTToken, update_amount);
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(WalletActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(WalletActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*  Go to the BrainTree Server and Add money*/
    private void goToBrainTreeActivity(String strBTToken, double update_amount) {

        Toast.makeText(WalletActivity.this, strBTToken.substring(10, 20) + "  " + update_amount, Toast.LENGTH_SHORT).show();

        onBraintreeSubmit();
    }

    public void onBraintreeSubmit(){
        DropInRequest dropInRequest = new DropInRequest().clientToken(strBTToken);
        startActivityForResult(dropInRequest.getIntent(this), BRAINTREE_REQUEST_CODE);
    }

    @Override
    public void onBackPressed(){

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
