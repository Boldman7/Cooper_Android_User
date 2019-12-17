package com.boldman.cooperuser.Activities.Payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class AddCardActivity extends Activity {

    Activity activity;
    Context context;
    ImageView backArrow, help_month_and_year, help_cvv;
    Button addCard;
    //EditText cardNumber, cvv, month_and_year;
    CardForm cardForm;
    String Card_Token = "";
    CustomDialog customDialog;

    static final Pattern CODE_PATTERN = Pattern
            .compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");

    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};

    private SupportedCardTypesView mSupportedCardTypesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        findViewByIdAndInitialize();

        cardForm.setOnCardTypeChangedListener(new CardEditText.OnCardTypeChangedListener() {
            @Override
            public void onCardTypeChanged(CardType cardType) {

                if (cardType == CardType.EMPTY) {
                    mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
                } else {
                    mSupportedCardTypesView.setSelected(cardType);
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialog(AddCardActivity.this);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                if( cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null ){
                    if ((customDialog != null)&& (customDialog.isShowing()))
                        customDialog.dismiss();
                    displayMessage(context.getResources().getString(R.string.enter_card_details));
                }else {
                    if (cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("") || cardForm.getCvv().equals("")) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        displayMessage(context.getResources().getString(R.string.enter_card_details));
                    } else {
                        final String cardNumber = cardForm.getCardNumber();
                        final int month = Integer.parseInt(cardForm.getExpirationMonth());
                        final int year = Integer.parseInt(cardForm.getExpirationYear());
                        final String cvv = cardForm.getCvv();

                        /*  Check card details.*/
//                        Card card = new Card(cardNumber, month, year, cvv);
//                        try {
//                            Stripe stripe = new Stripe(GlobalConstants.stripePublishableKey);
//                            stripe.createToken(
//                                    card,
//                                    new TokenCallback() {
//                                        public void onSuccess(Token token) {
//                                            // Send token to your server
//                                            Card_Token = token.getId();
//                                            addCardToAccount(Card_Token, "", cardNumber, year, month, cvv);
//                                        }
//
//                                        public void onError(Exception error) {
//                                            // Show localized error message
//                                            displayMessage(context.getResources().getString(R.string.enter_card_details));
//                                            Log.wtf("CARD NOT VALID : ~~~ "," CARD NOT VALID : ~~~ ");
//                                            if ((customDialog != null) && (customDialog.isShowing()))
//                                                customDialog.dismiss();
//                                        }
//                                    }
//                            );
//                        } catch (AuthenticationException e) {
//                            e.printStackTrace();
//                            if ((customDialog != null) && (customDialog.isShowing()))
//                                customDialog.dismiss();
//                        }

                        addCardToAccount(Card_Token, "", cardNumber, year, month, cvv);
                    }

                }
            }
        });

    }


    public void findViewByIdAndInitialize(){
        backArrow =findViewById(R.id.backArrow);
//        help_month_and_year = (ImageView)findViewById(R.id.help_month_and_year);
//        help_cvv = (ImageView)findViewById(R.id.help_cvv);
        addCard = findViewById(R.id.addCard);
//        cardNumber = (EditText) findViewById(R.id.cardNumber);
//        cvv = (EditText) findViewById(R.id.cvv);
//        month_and_year = (EditText) findViewById(R.id.monthAndyear);
        context = this;
        activity = this;
        cardForm =  findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add CardDetails")
                .setup(this);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
    }

    public void addCardToAccount(final String cardToken, String brand, String cardNumber, int year, int month, String cvv) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);
            paramObject.put("brand", "Visa");
            paramObject.put("card_no", cardNumber);
            paramObject.put("expiry_year", year);
            paramObject.put("expiry_month", month);
            paramObject.put("cvc", cvv);
            paramObject.put("atype", "user");

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doAddCard(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isAdded", true);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(AddCardActivity.this, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(AddCardActivity.this, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(AddCardActivity.this, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void displayMessage(String toastString) {
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
