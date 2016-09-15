package com.example.kevin.mapapplication.ui.startup;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kevin.mapapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(SplashActivity.this,StartUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
