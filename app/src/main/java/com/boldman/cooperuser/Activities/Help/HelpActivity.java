package com.boldman.cooperuser.Activities.Help;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.boldman.cooperuser.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
