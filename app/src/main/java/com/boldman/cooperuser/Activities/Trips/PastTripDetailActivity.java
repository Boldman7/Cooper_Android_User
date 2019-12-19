package com.boldman.cooperuser.Activities.Trips;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;

public class PastTripDetailActivity extends AppCompatActivity {

    private Context mContext;
    YourTrips trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_past_trip_detail);

        setData();

        defineClickListeners();
    }

    private void setData() {

        Intent intent = getIntent();
        trip = (YourTrips) intent.getSerializableExtra("trip");

        ((TextView) findViewById(R.id.tv_driver_name)).setText(trip.getFirst_name() + " " + trip.getLast_name());
        ((RatingBar) findViewById(R.id.rb_driver_rating)).setRating((float)trip.getDriver_avg_rating());

        try{
            ((TextView) findViewById(R.id.tv_datetime)).setText(DateUtil.convertDateToString24HoursFormat(trip.getFinish_at()));
        } catch (Exception e){
            ((TextView) findViewById(R.id.tv_datetime)).setText("");
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.tv_payment_method)).setText(trip.getPayment_method());
        ((TextView) findViewById(R.id.tv_price)).setText("$" + trip.getPay_amount());

        ((TextView) findViewById(R.id.tv_source)).setText(trip.getS_address());
        ((TextView) findViewById(R.id.tv_destination)).setText(trip.getD_address());

        mContext = this;
    }

    private void defineClickListeners() {

        findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_view_invoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_invoice_detail, null);

                double mSubTotal = 0, mSubTotal2 = 0, mBasePrice = 0, mEstimatedFair = 0
                        , mTaxPrice = 0, mTotalPrice = 0, mDiscountAmount = 0;

                mBasePrice = trip.getBase_fee();
                mEstimatedFair = trip.getDistance_fee();
                mSubTotal = mBasePrice + mEstimatedFair;
                mTaxPrice = trip.getTax_fee();
                mTotalPrice = trip.getPay_amount();

                ((TextView) view.findViewById(R.id.booking_id)).setText(trip.getId() + "");


                if (trip.getStatus() == 1) {

                    view.findViewById(R.id.lyt_main_ride_prices).setVisibility(View.GONE);

                    if (trip.getCancel_by().equalsIgnoreCase("user")) {

                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("Cancelled by Rider");

                    } else if (trip.getCancel_by().equalsIgnoreCase("driver")) {

                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("Cancelled by Driver");

                    } else if (trip.getCancel_by().equalsIgnoreCase("auto_user")) {
                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("Automatically cancelled.");
                    } else if (trip.getCancel_by().equalsIgnoreCase("auto_driver")) {
                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("Automatically cancelled.");
                    }
                } else {

                    view.findViewById(R.id.lyt_main_ride_prices).setVisibility(View.VISIBLE);

                    if (trip.getCoupon_id() == 0) {

                        view.findViewById(R.id.discountDetectionLayout).setVisibility(View.GONE);
                        view.findViewById(R.id.subtotal2Layout).setVisibility(View.GONE);

                        ((TextView) view.findViewById(R.id.lblBasePrice)).setText("$" + Utils.roundTwoDecimals(mBasePrice));
                        ((TextView) view.findViewById(R.id.lblDistancePrice)).setText("$" + Utils.roundTwoDecimals(mEstimatedFair));
                        ((TextView) view.findViewById(R.id.lblSubtotal)).setText("$" + Utils.roundTwoDecimals(mSubTotal));
                        ((TextView) view.findViewById(R.id.lblTaxTag)).setText("VAT (" + GlobalConstants.VAT_FEE + "%)");
                        ((TextView) view.findViewById(R.id.lblTaxPrice)).setText("$" + Utils.roundTwoDecimals(mTaxPrice));
                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("$" + Utils.roundTwoDecimals(mTotalPrice));

                    } else {

                        mDiscountAmount = trip.getDiscount_fee();
                        mSubTotal2 = mSubTotal - mDiscountAmount;

                        view.findViewById(R.id.discountDetectionLayout).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.subtotal2Layout).setVisibility(View.VISIBLE);

                        ((TextView) view.findViewById(R.id.lblBasePrice)).setText("$" + Utils.roundTwoDecimals(mBasePrice));
                        ((TextView) view.findViewById(R.id.lblDistancePrice)).setText("$" + Utils.roundTwoDecimals(mEstimatedFair));
                        ((TextView) view.findViewById(R.id.lblSubtotal)).setText("$" + Utils.roundTwoDecimals(mSubTotal));
                        ((TextView) view.findViewById(R.id.lblSubtotal2)).setText("$" + Utils.roundTwoDecimals(mSubTotal2));
                        ((TextView) view.findViewById(R.id.lblDiscountPrice)).setText("$" + Utils.roundTwoDecimals(mDiscountAmount));
                        ((TextView) view.findViewById(R.id.lblTaxTag)).setText("VAT (" + GlobalConstants.VAT_FEE + "%)");
                        ((TextView) view.findViewById(R.id.lblTaxPrice)).setText("$" + Utils.roundTwoDecimals(mTaxPrice));
                        ((TextView) view.findViewById(R.id.lblTotalPrice)).setText("$" + Utils.roundTwoDecimals(mTotalPrice));

                    }
                }

                builder.setView(view)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
    }
}
