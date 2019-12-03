package com.boldman.cooperuser.Activities.Help;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.R;

public class HelpActivity extends AppCompatActivity {

    Button btnGetMoney;
    EditText eTAmount;
    double amount;
//    HyperwalletAuthenticationTokenProvider hyperwalletAuthenticationTokenProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        eTAmount = findViewById(R.id.eTAmount);
        btnGetMoney = findViewById(R.id.btnGetMoney);

        initHyperWallet();

        btnGetMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eTAmount.getText().toString().isEmpty()) {
                    amount = 0;
                    Toast.makeText(HelpActivity.this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    amount = Double.parseDouble(eTAmount.getText().toString());
                    getMoneyWithBT();
                }
            }
        });

    }

    private void initHyperWallet() {

        // initialize UI SDK
        //Hyperwallet.getInstance(hyperwalletAuthenticationTokenProvider);

        // use UI SDK functions
//        Hyperwallet.getDefault().getIntentListTransferMethodActivity(HelpActivity.this);

    }

    private void getMoneyWithBT() {



    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
