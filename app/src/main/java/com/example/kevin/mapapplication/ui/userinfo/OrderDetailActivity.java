package com.example.kevin.mapapplication.ui.userinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String oid = getIntent().getStringExtra("oid");

        TextView text_oid = (TextView)findViewById(R.id.order_oid);

        text_oid.setText(oid);
    }
}
