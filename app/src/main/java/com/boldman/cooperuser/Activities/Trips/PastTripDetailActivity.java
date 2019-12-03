package com.boldman.cooperuser.Activities.Trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;

public class PastTripDetailActivity extends AppCompatActivity {

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
        YourTrips trip = (YourTrips) intent.getSerializableExtra("trip");

        ((TextView) findViewById(R.id.tv_driver_name)).setText(trip.getFirstname() + " " + trip.getLastname());
        ((RatingBar) findViewById(R.id.rb_driver_rating)).setRating((float)trip.getDriver_avg_rating());
        ((TextView) findViewById(R.id.tv_datetime)).setText(!trip.getFinish_at().equalsIgnoreCase("null") ? DateUtil.convertDateToString24HoursFormat(trip.getFinish_at()):"");
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

    }
}
