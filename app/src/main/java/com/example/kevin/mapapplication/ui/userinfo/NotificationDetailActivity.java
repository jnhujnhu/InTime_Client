package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class NotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_notification_detail);

        Bundle bundle = getIntent().getExtras();

        markAsRead(bundle.getString("nid"));

        try {
            JSONObject details = new JSONObject(bundle.getString("details"));
            switch (bundle.getString("type")) {
                case "friend":
                    Intent friendIntent = new Intent(this, FriendsDetailActivity.class);
                    friendIntent.putExtra("uid", details.getString("uid"));
                    friendIntent.putExtra("mode", "stranger");
                    startActivity(friendIntent);
                    finish();
                    break;
                case "order":
                    Intent orderIntent = new Intent(this, OrderDetailActivity.class);
                    orderIntent.putExtra("oid", details.getString("oid"));
                    startActivity(orderIntent);
                    finish();
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void markAsRead(String nid) {
        SharedPreferences userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        ConnectionManager.getInstance().MarkNotification(nid, true, userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {

            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
