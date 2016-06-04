package com.example.kevin.mapapplication.ui.userinfo;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;

public class MyOrdersFragment extends OrdersFragment {
    public void GetOrderList() {
        super.GetOrderList();
        ConnectionManager.getInstance().GetOrderList(userinfo.getString("uid", null), "", "", "", userinfo.getString("token", null), responseHandler);
    }

    @Override
    protected void showItem(View v, final Bundle item) {
        super.showItem(v, item);

        TextView text_status = (TextView)v.findViewById(R.id.listview_item_status);
        TextView text_points = (TextView)v.findViewById(R.id.listview_item_points);
        TextView text_number = (TextView)v.findViewById(R.id.listview_item_number);
        RelativeLayout layout_user = (RelativeLayout)v.findViewById(R.id.listview_item_user);
        RelativeLayout layout_details = (RelativeLayout)v.findViewById(R.id.listview_item_details);

        switch (item.getString("status")) {
            case "waiting":
                text_status.setTextColor(ContextCompat.getColor(getActivity(), R.color.status_waiting));
                text_status.setText("Waiting");
                layout_details.setAlpha(1f);
                break;
            case "accepted":
                text_status.setTextColor(ContextCompat.getColor(getActivity(), R.color.status_accepted));
                text_status.setText("Accepted");
                layout_details.setAlpha(1f);
                break;
            case "completed":
                text_status.setTextColor(ContextCompat.getColor(getActivity(), R.color.status_completed));
                text_status.setText("Completed");
                layout_details.setAlpha(1f);
                break;
            case "canceling":
                text_status.setTextColor(ContextCompat.getColor(getActivity(), R.color.status_canceling));
                text_status.setText("Canceling");
                layout_details.setAlpha(1f);
                break;
            case "canceled":
                text_status.setTextColor(ContextCompat.getColor(getActivity(), R.color.status_canceled));
                text_status.setText("Canceled");
                layout_details.setAlpha(0.3f);
                break;
        }

        layout_user.setVisibility(View.GONE);

        switch (item.getString("type")) {
            case "request":
            case "offer":
                text_points.setVisibility(View.VISIBLE);
                text_number.setVisibility(View.VISIBLE);
                text_points.setText(String.format("%s Points", item.getInt("points")));
                text_number.setText(String.format("x%d", item.getInt("number")));
                break;
            case "notification":
                text_points.setVisibility(View.GONE);
                text_number.setVisibility(View.GONE);
                break;
        }
    }
}
