package com.example.kevin.mapapplication.ui.mainscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.example.kevin.mapapplication.ui.userinfo.OrderDetailActivity;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kevin on 3/12/16.
 */
public class TaginfoDialog {

    private MapsActivity handle;

    public TaginfoDialog (MapsActivity con) {
        handle = con;
    }


    public void BuildDialog (Marker marker) throws JSONException{

        AlertDialog.Builder builder = new AlertDialog.Builder(handle);
        LayoutInflater inflater = LayoutInflater.from(handle);
        View layout = inflater.inflate(R.layout.dialog_tag_detail, null);

        String id = marker.getId();
        final JSONObject data = MarkerManager.getInstance().Get(id);
        final String oid = data.optString("oid");
        String type = data.optString("type");
        String title = data.optString("title");
        String category = data.optString("category");
        int point = data.optInt("points");
        int enrollment = data.optInt("number");
        String exptime = data.optString("time");
        String description = data.optString("content");
        String place = data.optString("place");

        builder.setTitle(type.toUpperCase())
                .setView(layout);
        final DialogInterface dialog = builder.show();

        final TextView u_title = (TextView) layout.findViewById(R.id.dialog_title);
        final TextView u_category = (TextView) layout.findViewById(R.id.dialog_category);
        TextView u_dcpt = (TextView) layout.findViewById(R.id.dialog_description);
        TextView u_points = (TextView) layout.findViewById(R.id.dialog_points);
        TextView u_enrollment = (TextView) layout.findViewById(R.id.dialog_enrollment);
        TextView u_exptime = (TextView) layout.findViewById(R.id.dialog_exptime);
        final TextView u_place = (TextView) layout.findViewById(R.id.dialog_destination);
        ImageView u_image = (ImageView) layout.findViewById(R.id.dialog_imagetag);
        Button details = (Button) layout.findViewById(R.id.dialog_accept);
        Button cancel = (Button) layout.findViewById(R.id.dialog_cancel);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                u_title.setSelected(true);
                u_category.setSelected(true);
                u_place.setSelected(true);
            }
        }, 2000);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(handle, OrderDetailActivity.class);
                intent.putExtra("oid", oid);
                handle.startActivityForResult(intent, MapsActivity.REQUEST_CODE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        u_title.setText(title);
        u_category.setText(category);
        u_dcpt.setText("                      " + description);
        u_points.setText(Integer.toString(point));
        u_enrollment.setText(Integer.toString(enrollment));
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        try {
            Date date = inputFormat.parse(exptime);
            u_exptime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        u_place.setText(place);

        if (type.equals("offer")) {
            u_image.setImageDrawable(ContextCompat.getDrawable(handle.getApplicationContext(), R.drawable.ic_greentag_large));
        } else if (type.equals("prompt")) {
            u_image.setImageDrawable(ContextCompat.getDrawable(handle.getApplicationContext(), R.drawable.ic_bluetag_large));
        } else {
            u_image.setImageDrawable(ContextCompat.getDrawable(handle.getApplicationContext(), R.drawable.ic_redtag_large));
        }
    }

}
