package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Button cancel_btn;
    private TextView username, phone, email;
    private String mode;
    private ProgressBar loading;

    private SharedPreferences userinfo;


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
        cancel_btn = (Button) findViewById(R.id.friend_detail_cancel);
        username = (TextView) findViewById(R.id.friend_detail_username);
        phone = (TextView) findViewById(R.id.friend_detail_phone);
        email = (TextView) findViewById(R.id.friend_detail_email);
        loading = (ProgressBar) findViewById(R.id.friend_detail_loading);

        loading.setVisibility(View.VISIBLE);
        modify_btn.setEnabled(false);
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);


        GetUserInfo();

    }

    private void GetUserInfo() {

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                username.setText(res.optString("username"));
                phone.setText(res.optString("phone"));
                email.setText(res.optString("email"));
                mode = res.optString("status");
                modify_btn.setEnabled(true);
                modify_btn.setVisibility(View.VISIBLE);
                modify_btn.setBackground(getDrawable(R.drawable.bg_pending_friends_accept_btn));


                switch(mode) {
                    case "accepted":
                        modify_btn.setVisibility(View.GONE);
                        cancel_btn.setVisibility(View.VISIBLE);
                        cancel_btn.setText("Delete");
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest("accepted");
                                finish();
                            }
                        });
                        break;
                    case "pending":
                        cancel_btn.setVisibility(View.VISIBLE);
                        modify_btn.setText("Accept Friend Invitation");
                        modify_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AddOrAcceptFriendRequest("accept");
                            }
                        });
                        cancel_btn.setText("Ignore Friend Request");
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest("pending");
                            }
                        });
                        break;
                    case "waiting":
                        cancel_btn.setVisibility(View.VISIBLE);
                        modify_btn.setText("Waiting for Response");
                        modify_btn.setBackground(getDrawable(R.drawable.bg_waiting_friends_btn));
                        modify_btn.setEnabled(false);
                        cancel_btn.setText("Cancel Friend Request");
                        cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest("waiting");
                            }
                        });
                        break;
                    case "none":
                        cancel_btn.setVisibility(View.GONE);
                        modify_btn.setText("Add as Friend");
                        modify_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {AddOrAcceptFriendRequest("add");
                                }
                            });
                        break;
                    default:
                        modify_btn.setVisibility(View.GONE);
                        cancel_btn.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);

                Toast.makeText(FriendsDetailActivity.this, error, Toast.LENGTH_LONG).show();

            }
        };

        ConnectionManager.getInstance().GetUserInfo(uid, getSharedPreferences("User_info", MODE_PRIVATE).getString("token", null), handler);

    }

    public void CancelFriendRequest(final String mode) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                switch (mode) {
                    case "accepted":
                        Toast.makeText(FriendsDetailActivity.this, "Delete Success.", Toast.LENGTH_LONG).show();
                        break;
                    case "pending":
                        Toast.makeText(FriendsDetailActivity.this, "Friend Request Ignored.", Toast.LENGTH_LONG).show();
                        break;
                    case "waiting":
                        Toast.makeText(FriendsDetailActivity.this, "Friend Request Canceled.", Toast.LENGTH_LONG).show();
                        break;
                }
                GetUserInfo();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(FriendsDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().CancelFriendRequest(userinfo.getString("uid", null), uid, userinfo.getString("token", null), handler);
    }

    public void AddOrAcceptFriendRequest(final String mode) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                switch (mode) {
                    case "accept":
                        Toast.makeText(FriendsDetailActivity.this, "Friend Request Accepted.", Toast.LENGTH_LONG).show();
                        break;
                    case "add":
                        Toast.makeText(FriendsDetailActivity.this, "Friend Request Successfully Sent.", Toast.LENGTH_LONG).show();
                        break;
                }
                GetUserInfo();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(FriendsDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().AddOrAcceptFriendRequest(userinfo.getString("uid", null), uid, userinfo.getString("token", null), handler);
    }


    @Override
    public void onBackPressed() {
        //Intent intent = new Intent();
        //intent.putExtra("Choosed","Item id");
        //setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
