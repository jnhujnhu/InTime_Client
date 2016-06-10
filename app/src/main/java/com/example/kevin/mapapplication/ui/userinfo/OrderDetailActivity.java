package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.ui.mainscreen.tag.TagInfoActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class OrderDetailActivity extends OrderAndTemplateDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text_header.setText("Order Detail");

        button_more.setVisibility(View.GONE);
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
    }

    @Override
    protected void refreshDetail() {
        super.refreshDetail();
        ConnectionManager.getInstance().GetOrderDetail(bundle.getString("oid"), userinfo.getString("token", null), detailHandler);
    }

    @Override
    protected void onRefreshDetail(JSONObject res) {
        super.onRefreshDetail(res);

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

        if (res.optString("status").equals("accepted") && res.optString("type").equals("request") && res.optString("uid").equals(userinfo.getString("uid", null))) {
            button_complete.setVisibility(View.VISIBLE);
        }
        else {
            button_complete.setVisibility(View.GONE);
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

        if (res.optString("status").equals("waiting") && !res.optString("type").equals("prompt") && (userStatus == null || userStatus.equals("canceled")) && !res.optString("uid").equals(userinfo.getString("uid", null))) {
            button_accept.setVisibility(View.VISIBLE);
        }
        else {
            button_accept.setVisibility(View.GONE);
        }
    }

    private void editOrder() {
        Intent intent = new Intent(OrderDetailActivity.this, tagClass);
        intent.putExtras(newBundle);
        intent.putExtra("class", "order");
        intent.putExtra("tid", bundle.getString("oid"));
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

        ConnectionManager.getInstance().SetOrderStatus(bundle.getString("oid"), "completed", userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
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
}
