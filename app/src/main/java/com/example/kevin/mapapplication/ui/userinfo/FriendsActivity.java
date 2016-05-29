package com.example.kevin.mapapplication.ui.userinfo;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.example.kevin.mapapplication.utils.CustomListItem;
import com.example.kevin.mapapplication.utils.CustomListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FriendsActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 233;

    private ProgressBar loading;
    private SharedPreferences userinfo;

    private ListView pendingListView, waitingListView, acceptedListView;
    private LinearLayout pendinglayout, waitinglayout, acceptedlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        loading = (ProgressBar) findViewById(R.id.friends_loading);
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);

        pendingListView = (ListView) findViewById(R.id.friends_pending_list);
        waitingListView = (ListView) findViewById(R.id.friends_waiting_list);
        acceptedListView = (ListView) findViewById(R.id.friends_accepted_list);

        pendinglayout = (LinearLayout) findViewById(R.id.layout_user_pending);
        waitinglayout = (LinearLayout) findViewById(R.id.layout_user_waiting);
        acceptedlayout = (LinearLayout) findViewById(R.id.layout_user_accepted);

        GetFriendsList();
    }

    private void GetFriendsList() {
        loading.setVisibility(View.VISIBLE);

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers,  byte[] responseBody) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                JSONArray friends_list = new JSONArray(new String(responseBody, StandardCharsets.UTF_8));

                List<CustomListItem> pendinglist = new ArrayList<>(), waitinglist = new ArrayList<>(), acceptedlist = new ArrayList<>();

                for(int i = 0; i <friends_list.length(); i++) {
                    JSONObject friends_item = friends_list.optJSONObject(i);
                    switch (friends_item.optString("status")) {
                        case "waiting":
                            waitinglist.add(new CustomListItem(friends_item.optString("username")));
                            break;
                        case "pending":
                            pendinglist.add(new CustomListItem(friends_item.optString("username")));
                            break;
                        case "accepted":
                            acceptedlist.add(new CustomListItem(friends_item.optString("username")));
                            break;
                    }
                }


                CustomListViewAdapter waitinglistAdapter = new CustomListViewAdapter(FriendsActivity.this, R.layout.listview_item_waiting_friends, waitinglist) {
                    @Override
                    public View setViewDetail(int position, View convertView) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi;
                            vi = LayoutInflater.from(FriendsActivity.this);
                            v = vi.inflate(R.layout.listview_item_waiting_friends, null);
                        }
                        CustomListItem item = getItem(position);

                        if(item!=null) {
                            TextView username = (TextView) v.findViewById(R.id.friends_waiting_username);
                            username.setText(item.text);
                        }
                        return v;
                    }
                };

                CustomListViewAdapter pendinglistAdapter = new CustomListViewAdapter(FriendsActivity.this, R.layout.listview_item_pending_friends, pendinglist) {
                    @Override
                    public View setViewDetail(int position, View convertView) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi;
                            vi = LayoutInflater.from(FriendsActivity.this);
                            v = vi.inflate(R.layout.listview_item_pending_friends, null);
                        }
                        CustomListItem item = getItem(position);
                        if(item!=null) {
                            TextView username = (TextView) v.findViewById(R.id.friends_pending_username);
                            Button accept = (Button) v.findViewById(R.id.friends_pending_accept_btn);
                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            username.setText(item.text);
                        }
                        return v;
                    }
                };

                CustomListViewAdapter acceptedlistAdapter = new CustomListViewAdapter(FriendsActivity.this, R.layout.listview_item_accepted_friends, acceptedlist) {
                    @Override
                    public View setViewDetail(int position, View convertView) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi;
                            vi = LayoutInflater.from(FriendsActivity.this);
                            v = vi.inflate(R.layout.listview_item_accepted_friends, null);
                        }
                        CustomListItem item = getItem(position);

                        if(item!=null) {
                            TextView username = (TextView) v.findViewById(R.id.friends_accepted_username);
                            username.setText(item.text);
                        }
                        return v;
                    }
                };

                if(waitinglist.isEmpty()) {
                    waitinglayout.setVisibility(View.GONE);
                }
                else {
                    waitingListView.setAdapter(waitinglistAdapter);
                }
                if(pendinglist.isEmpty()) {
                    pendinglayout.setVisibility(View.GONE);
                }
                else {
                    pendingListView.setAdapter(pendinglistAdapter);
                }
                if(acceptedlist.isEmpty()) {
                    acceptedlayout.setVisibility(View.GONE);
                }
                else {
                    acceptedListView.setAdapter(acceptedlistAdapter);
                }
            }
            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                if(res == null) {
                    Toast.makeText(FriendsActivity.this, "Cannot Connect to Server.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(FriendsActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        };
        ConnectionManager.getInstance().GetFriendsList(userinfo.getString("uid", null), userinfo.getString("token", null), handler);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra("Choosed","Item id");
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

}
