package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class FriendsDetailActivity extends AppCompatActivity {

    private String uid;
    private Button modify_btn;
    private TextView username, phone, email;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);
        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        Bundle bundle = getIntent().getExtras();

        uid = bundle.getString("uid");

        modify_btn = (Button) findViewById(R.id.friend_detail_modify_btn);
        username = (TextView) findViewById(R.id.friend_detail_username);
        phone = (TextView) findViewById(R.id.friend_detail_phone);
        email = (TextView) findViewById(R.id.friend_detail_email);
        loading = (ProgressBar) findViewById(R.id.friend_detail_loading);
        loading.setVisibility(View.VISIBLE);

        switch(bundle.getString("mode")) {
            case "accepted":
                modify_btn.setVisibility(View.GONE);
                break;
            case "pending":
                modify_btn.setText("Accept Friend Invitation");
                modify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case "waiting":
                modify_btn.setText("Waiting for Response");
                modify_btn.setBackground(getDrawable(R.drawable.bg_waiting_friends_btn));
                modify_btn.setEnabled(false);
                break;
            case "stranger":
                modify_btn.setText("Add as Friend");
                modify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
        }


        GetUserInfo();

    }

    private void GetUserInfo() {

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, byte[] responseBody) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                JSONObject res = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                username.setText(res.optString("username"));
                phone.setText(res.optString("phone"));
                email.setText(res.optString("email"));
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                if(res == null) {
                    Toast.makeText(FriendsDetailActivity.this, "Cannot Connect to Server.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(FriendsDetailActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        };

        ConnectionManager.getInstance().GetUserInfo(uid, getSharedPreferences("User_info", MODE_PRIVATE).getString("token", null), handler);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra("Choosed","Item id");
        //setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
