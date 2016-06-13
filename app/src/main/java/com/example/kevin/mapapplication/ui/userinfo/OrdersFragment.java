package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OrdersFragment extends Fragment {

    protected SharedPreferences userinfo;

    private ListView orderList;
    private ProgressBar loading;

    protected AsyncHttpResponseHandler responseHandler = new AsyncJSONHttpResponseHandler() {
        @Override
        public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
            loading.setVisibility(View.INVISIBLE);
            showOrderList(res.optJSONArray("orders"));
        }

        @Override
        public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
            loading.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userinfo = getActivity().getSharedPreferences("User_info", getActivity().MODE_PRIVATE);

        orderList = (ListView)getActivity().findViewById(R.id.order_list);
        loading = (ProgressBar)getActivity().findViewById(R.id.loading);
    }

    public void GetOrderList() {
        loading.setVisibility(View.VISIBLE);
    }

    private void showOrderList(JSONArray orders) {
        final List<Bundle> itemList = new ArrayList<>();
        for(int i = 0; i < orders.length(); i++) {
            JSONObject orderItem = orders.optJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("oid", orderItem.optString("oid"));
            bundle.putString("uid", orderItem.optString("uid"));
            bundle.putString("username", orderItem.optString("username"));
            bundle.putString("type", orderItem.optString("type"));
            bundle.putString("title", orderItem.optString("title"));
            bundle.putString("content", orderItem.optString("content"));
            bundle.putString("status", orderItem.optString("status"));
            bundle.putInt("points", orderItem.optInt("points"));
            bundle.putInt("number", orderItem.optInt("number"));
            bundle.putString("time", orderItem.optString("time"));

            JSONArray acceptUsers = orderItem.optJSONArray("accept_users");
            Boolean isAccepted = false;
            Integer acceptCount = 0;
            for (int j = 0; j < acceptUsers.length(); j++) {
                JSONObject acceptUser = acceptUsers.optJSONObject(j);
                if (acceptUser.optString("uid").equals(userinfo.getString("uid", null))) {
                    bundle.putString("userStatus", acceptUser.optString("status"));
                }
                if (acceptUser.optString("status").equals("accepted") || acceptUser.optString("status").equals("canceling")) {
                    isAccepted = true;
                }
                if (!acceptUser.optString("status").equals("canceled")) {
                    acceptCount++;
                }
            }

            bundle.putBoolean("isAccepted", isAccepted);
            bundle.putInt("acceptCount", acceptCount);

            itemList.add(bundle);
        }

        Collections.sort(itemList, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle lhs, Bundle rhs) {
                return -lhs.getString("oid").compareTo(rhs.getString("oid"));
            }
        });

        orderList.setAdapter(new ArrayAdapter<Bundle>(getActivity(), R.layout.listview_item_orders, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(getActivity());
                    v = vi.inflate(R.layout.listview_item_orders, parent, false);
                }
                Bundle item = getItem(position);

                if (item != null) {
                    showItem(v, item);
                }
                return v;
            }
        });

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle item = itemList.get(position);
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtras(item);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }

    protected void showItem(View v, final Bundle item) {
        ImageView icon = (ImageView)v.findViewById(R.id.listview_item_icon);
        TextView text_title = (TextView)v.findViewById(R.id.listview_item_title);
        TextView text_content = (TextView)v.findViewById(R.id.listview_item_content);
        RelativeLayout layout_user = (RelativeLayout)v.findViewById(R.id.listview_item_user);

        switch (item.getString("type")) {
            case "request":
                icon.setImageResource(R.drawable.ic_redtag);
                break;
            case "offer":
                icon.setImageResource(R.drawable.ic_greentag);
                break;
            case "prompt":
                icon.setImageResource(R.drawable.ic_bluetag);
                break;
        }

        text_title.setText(item.getString("title"));
        text_content.setText(item.getString("content"));
    }

    @Override
    public void onResume() {
        GetOrderList();
        super.onResume();
    }
}
