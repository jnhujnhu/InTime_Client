package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class OrderDetailActivity extends OrderAndTemplateDetailActivity {

    private boolean isAccepted = false;
    private boolean isOwner = false;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text_header.setText("Order Detail");

        button_more.setVisibility(View.GONE);
        layout_user.setVisibility(View.VISIBLE);
        layout_time.setVisibility(View.VISIBLE);
        layout_status.setVisibility(View.VISIBLE);

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrder();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        button_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOrder();
            }
        });

        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder();
            }
        });

        button_stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stayOrder();
            }
        });

        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveOrder();
            }
        });
    }

    @Override
    protected void refreshDetail() {
        super.refreshDetail();
        ConnectionManager.getInstance().GetOrderDetail(bundle.getString("oid"), userinfo.getString("token", null), detailHandler);
    }

    @Override
    protected void onRefreshDetail(final JSONObject res) {
        super.onRefreshDetail(res);

        text_user.setText(res.optString("username"));

        if (!res.optString("uid").equals(userinfo.getString("uid", null))) {
            image_user.setVisibility(View.VISIBLE);

            layout_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailActivity.this, FriendsDetailActivity.class);
                    intent.putExtra("uid", res.optString("uid"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
            });
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        try {
            Date date = inputFormat.parse(res.optString("time"));
            text_time.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (res.optString("status")) {
            case "waiting":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_waiting));
                text_status.setText("Waiting");
                break;
            case "accepted":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_accepted));
                text_status.setText("Accepted");
                break;
            case "completed":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_completed));
                text_status.setText("Completed");
                break;
            case "canceling":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_canceling));
                text_status.setText("Canceling");
                break;
            case "canceled":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_canceled));
                text_status.setText("Canceled");
                break;
        }

        if ((res.optString("status").equals("waiting") || res.optString("status").equals("accepted")) && res.optString("uid").equals(userinfo.getString("uid", null))) {
            button_edit.setVisibility(View.VISIBLE);
            button_cancel.setVisibility(View.VISIBLE);
        }
        else {
            button_edit.setVisibility(View.GONE);
            button_cancel.setVisibility(View.GONE);
        }

        String userStatus = null;
        JSONArray acceptUsers = res.optJSONArray("accept_users");
        for (int j = 0; j < acceptUsers.length(); j++) {
            JSONObject acceptUser = acceptUsers.optJSONObject(j);
            if (acceptUser.optString("uid").equals(userinfo.getString("uid", null))) {
                userStatus = acceptUser.optString("status");
                break;
            }
        }

        if (res.optString("status").equals("accepted") && res.optString("type").equals("request") && res.optString("uid").equals(userinfo.getString("uid", null))
                || userStatus != null && userStatus.equals("accepted") && res.optString("type").equals("offer"))
        {
            button_complete.setVisibility(View.VISIBLE);
        }
        else {
            button_complete.setVisibility(View.GONE);
        }

        if (res.optString("status").equals("waiting") && !res.optString("type").equals("prompt") && (userStatus == null || userStatus.equals("canceled")) && !res.optString("uid").equals(userinfo.getString("uid", null))) {
            button_accept.setVisibility(View.VISIBLE);
        }
        else {
            button_accept.setVisibility(View.GONE);
        }

        if (userStatus != null && userStatus.equals("accepted")) {
            button_leave.setText("Cancel");
            button_leave.setVisibility(View.VISIBLE);
            button_stay.setVisibility(View.GONE);
        }
        else if (userStatus != null && userStatus.equals("canceling") && res.optString("type").equals("request")) {
            button_leave.setText("Leave");
            button_leave.setVisibility(View.VISIBLE);
            button_stay.setVisibility(View.VISIBLE);
        }
        else {
            button_leave.setVisibility(View.GONE);
            button_stay.setVisibility(View.GONE);
        }

        isOwner = res.optString("uid").equals(userinfo.getString("uid", null));
        isAccepted = false;
        type = res.optString("type");

        if (res.optJSONArray("accept_users").length() > 0) {
            layout_accept.setVisibility(View.VISIBLE);
            showAcceptUserList(res.optJSONArray("accept_users"));
        }
        else {
            layout_accept.setVisibility(View.GONE);
        }

        try {
            Date date = inputFormat.parse(res.optString("time"));
            Date now = new Date();
            if (!isAccepted && now.after(date)) {
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_canceled));
                text_status.setText("Expired");
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showAcceptUserList(JSONArray users) {
        final List<Bundle> itemList = new ArrayList<>();
        for(int i = 0; i < users.length(); i++) {
            JSONObject orderItem = users.optJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("uid", orderItem.optString("uid"));
            bundle.putString("username", orderItem.optString("username"));
            bundle.putString("status", orderItem.optString("status"));
            itemList.add(bundle);
        }

        list_accept.setAdapter(new ArrayAdapter<Bundle>(OrderDetailActivity.this, R.layout.listview_item_accept, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(OrderDetailActivity.this);
                    v = vi.inflate(R.layout.listview_item_accept, parent, false);
                }
                Bundle item = getItem(position);

                if (item != null) {
                    showItem(v, item);
                }
                return v;
            }
        });

        list_accept.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle item = itemList.get(position);
                Intent intent = new Intent(OrderDetailActivity.this, FriendsDetailActivity.class);
                intent.putExtras(item);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }

    private void showItem(View v, final Bundle item) {
        TextView text_accept = (TextView)v.findViewById(R.id.detail_item_accept);
        ImageView image_accept = (ImageView)v.findViewById(R.id.detail_item_accept_icon);
        TextView text_status = (TextView)v.findViewById(R.id.detail_item_accept_status);
        ImageView image_cancel = (ImageView)v.findViewById(R.id.detail_item_accept_cancel);
        LinearLayout layout_stay_or_leave = (LinearLayout)v.findViewById(R.id.detail_item_accept_stay_or_leave);
        Button button_stay = (Button)v.findViewById(R.id.detail_item_accept_stay);
        Button button_leave = (Button)v.findViewById(R.id.detail_item_accept_leave);

        text_accept.setText(item.getString("username"));

        if (!item.getString("uid").equals(userinfo.getString("uid", null))) {
            image_accept.setVisibility(View.VISIBLE);
        }
        else {
            image_accept.setVisibility(View.GONE);
        }

        switch (item.getString("status")) {
            case "accepted":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_accepted));
                text_status.setText("Accepted");
                text_accept.setAlpha(1f);
                isAccepted = true;
                break;
            case "completed":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_completed));
                text_status.setText("Completed");
                text_accept.setAlpha(1f);
                image_cancel.setVisibility(View.GONE);
                break;
            case "canceling":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_canceling));
                text_status.setText("Canceling");
                text_accept.setAlpha(1f);
                image_cancel.setVisibility(View.GONE);
                isAccepted = true;
                break;
            case "canceled":
                text_status.setTextColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.status_canceled));
                text_status.setText("Canceled");
                text_accept.setAlpha(0.3f);
                image_cancel.setVisibility(View.GONE);
                break;
        }

        if (isOwner && item.getString("status").equals("accepted")) {
            image_cancel.setVisibility(View.VISIBLE);
        }
        else if (isOwner) {
            image_cancel.setVisibility(View.INVISIBLE);
        }
        else {
            image_cancel.setVisibility(View.GONE);
        }

        if (isOwner && item.getString("status").equals("canceling") && type.equals("offer")) {
            layout_stay_or_leave.setVisibility(View.VISIBLE);
        }
        else {
            layout_stay_or_leave.setVisibility(View.GONE);
        }

        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserStatus(item.getString("uid"), "cancel");
            }
        });

        button_stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserStatus(item.getString("uid"), "accepted");
            }
        });

        button_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserStatus(item.getString("uid"), "cancel");
            }
        });
    }

    private void editOrder() {
        Intent intent = new Intent(OrderDetailActivity.this, tagClass);
        intent.putExtras(newBundle);
        intent.putExtra("class", "order");
        intent.putExtra("oid", bundle.getString("oid"));
        intent.putExtra("special", isAccepted);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void cancelOrder() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().SetOrderStatus(bundle.getString("oid"), "cancel", userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                switch (res.optString("status")) {
                    case "canceling":
                        refreshDetail();
                        break;
                    case "canceled":
                        Toast.makeText(OrderDetailActivity.this, "Order canceled", Toast.LENGTH_LONG).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        break;
                }
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void completeOrder() {
        loading.setVisibility(View.VISIBLE);

        AsyncHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                refreshDetail();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };

        if (isOwner) {
            ConnectionManager.getInstance().SetOrderStatus(bundle.getString("oid"), "completed", userinfo.getString("token", null), handler);
        }
        else {
            ConnectionManager.getInstance().SetOrderUserStatus(bundle.getString("oid"), userinfo.getString("uid", null), "completed", userinfo.getString("token", null), handler);
        }
    }

    private void acceptOrder() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().AcceptOrder(bundle.getString("oid"), userinfo.getString("uid", null), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                refreshDetail();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stayOrder() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().SetOrderUserStatus(bundle.getString("oid"), userinfo.getString("uid", null), "accepted", userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                refreshDetail();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void leaveOrder() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().SetOrderUserStatus(bundle.getString("oid"), userinfo.getString("uid", null), "cancel", userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                switch (res.optString("status")) {
                    case "canceling":
                        refreshDetail();
                        break;
                    case "canceled":
                        Toast.makeText(OrderDetailActivity.this, "Order canceled", Toast.LENGTH_LONG).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        break;
                }
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserStatus(String uid, String status) {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().SetOrderUserStatus(bundle.getString("oid"), uid, status, userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                refreshDetail();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
