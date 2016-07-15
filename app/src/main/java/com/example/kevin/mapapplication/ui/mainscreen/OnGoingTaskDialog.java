package com.example.kevin.mapapplication.ui.mainscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.userinfo.OrderDetailActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kevin on 7/15/16.
 */
public class OnGoingTaskDialog {

    private MapsActivity handle;
    private GoogleMap map;
    private static final float ZoomLevel = 18f;
    private ListView ongoing_list;

    public OnGoingTaskDialog (MapsActivity con, GoogleMap map) {
        handle = con;
        this.map = map;
    }

    public void BuildDialog(JSONArray orders) {

        AlertDialog.Builder builder = new AlertDialog.Builder(handle);
        builder.setTitle("OnGoing Orders");
        final AlertDialog dialog;
        LayoutInflater inflater = LayoutInflater.from(handle);
        View v = inflater.inflate(R.layout.dialog_ongoing_task, null);
        builder.setView(v);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog = builder.show();
        ongoing_list = (ListView) v.findViewById(R.id.listview_ongoing_tasks);

        final List<Bundle> itemList = new ArrayList<>();
        for(int i = 0; i <orders.length(); i++) {
            JSONObject orderItem = orders.optJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("oid", orderItem.optString("oid"));
            bundle.putString("type", orderItem.optString("type"));
            bundle.putString("title", orderItem.optString("title"));
            bundle.putDoubleArray("coordinate", new double[]{orderItem.optJSONObject("coordinate").optDouble("latitude"), orderItem.optJSONObject("coordinate").optDouble("longitude")});
            itemList.add(bundle);
        }

        ongoing_list.setAdapter(new ArrayAdapter<Bundle>(handle, R.layout.listview_item_ongoing_tasks, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(handle);
                    v = vi.inflate(R.layout.listview_item_ongoing_tasks, parent, false);
                }
                final Bundle item = getItem(position);

                if (item != null) {
                    ImageView icon = (ImageView)v.findViewById(R.id.listview_item_icon);
                    TextView text = (TextView)v.findViewById(R.id.listview_item_text);
                    Button locate = (Button) v.findViewById(R.id.listview_item_locate);

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

                    locate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(item.getDoubleArray("coordinate")[0], item.getDoubleArray("coordinate")[1]), ZoomLevel));
                        }
                    });

                    text.setText(item.getString("title"));
                }
                ongoing_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        Intent intent = new Intent(handle, OrderDetailActivity.class);
                        intent.putExtra("oid", itemList.get(position).getString("oid"));
                        handle.startActivityForResult(intent, MapsActivity.REQUEST_CODE);
                    }
                });
                return v;
            }
        });
    }

}
