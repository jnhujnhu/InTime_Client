package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NotificationActivity extends AppCompatActivity {

    private SharedPreferences userinfo;

    private ListView notificationList;
    private ProgressBar loading;
    private TextView text_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        notificationList = (ListView)findViewById(R.id.notification_list);
        loading = (ProgressBar)findViewById(R.id.notifications_loading);
        text_clear = (TextView)findViewById(R.id.notification_clear);

        text_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionManager.getInstance().MarkNotification("all", true, userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
                    @Override
                    public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                        GetNotificationList();
                    }

                    @Override
                    public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                        Toast.makeText(NotificationActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void GetNotificationList() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().GetNotificationList(userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                showNotificationList(res.optJSONArray("notifications"));
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(NotificationActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNotificationList(JSONArray notifications) {
        final List<Bundle> itemList = new ArrayList<>();
        for(int i = 0; i <notifications.length(); i++) {
            JSONObject notificationItem = notifications.optJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("nid", notificationItem.optString("nid"));
            bundle.putString("type", notificationItem.optString("type"));
            bundle.putString("message", notificationItem.optString("message"));
            bundle.putString("details", notificationItem.optJSONObject("details").toString());
            bundle.putBoolean("read", notificationItem.optBoolean("read"));
            itemList.add(bundle);
        }

        Collections.reverse(itemList);

        notificationList.setAdapter(new ArrayAdapter<Bundle>(NotificationActivity.this, R.layout.listview_item_default, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(NotificationActivity.this);
                    v = vi.inflate(R.layout.listview_item_default, parent, false);
                }
                Bundle item = getItem(position);

                if (item != null) {
                    ImageView icon = (ImageView)v.findViewById(R.id.listview_item_icon);
                    TextView text = (TextView)v.findViewById(R.id.listview_item_text);

                    if (item.getBoolean("read")) {
                        icon.setImageResource(R.drawable.ic_notification_read);
                    }
                    else {
                        icon.setImageResource(R.drawable.ic_notification_unread);
                        text.setTypeface(null, Typeface.BOLD);
                    }

                    text.setText(item.getString("message"));
                }
                return v;
            }
        });

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle item = itemList.get(position);
                Intent intent = new Intent(NotificationActivity.this, NotificationDetailActivity.class);
                intent.putExtras(item);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }

    @Override
    protected void onResume() {
        GetNotificationList();
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
