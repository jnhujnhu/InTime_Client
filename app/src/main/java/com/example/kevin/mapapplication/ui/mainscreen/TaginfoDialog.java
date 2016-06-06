package com.example.kevin.mapapplication.ui.mainscreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kevin on 3/12/16.
 */
public class TaginfoDialog {

    private Context context;

    public TaginfoDialog (Context con) {
        context = con;
    }

    public interface OnAcceptClickedCallBack {
        void DrawDirectionAndSet(JSONObject data);
    }

    public void SetCallBack(OnAcceptClickedCallBack onAcceptClickedCallBack) {
        onaccept = onAcceptClickedCallBack;
    }

    private OnAcceptClickedCallBack onaccept;

    public void BuildDialog (Marker marker, final TaskInformer taskInformer) throws JSONException{

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_tag_detail, null);

        builder.setTitle("Details")
                .setView(layout);
        final DialogInterface dialog = builder.show();
        String id = marker.getId();
        final JSONObject data = MarkerManager.getInstance().Get(id);
        String type = data.optString("type");
        String title = data.optString("title");
        String category = data.optString("category");
        int point = data.optInt("points");
        int enrollment = data.optInt("number");
        String exptime = data.optString("time");
        String description = data.optString("content");
        String place = data.optString("place");


        TextView u_title = (TextView) layout.findViewById(R.id.dialog_title);
        TextView u_category = (TextView) layout.findViewById(R.id.dialog_category);
        TextView u_dcpt = (TextView) layout.findViewById(R.id.dialog_description);
        TextView u_points = (TextView) layout.findViewById(R.id.dialog_points);
        TextView u_enrollment = (TextView) layout.findViewById(R.id.dialog_enrollment);
        TextView u_exptime = (TextView) layout.findViewById(R.id.dialog_exptime);
        TextView u_place = (TextView) layout.findViewById(R.id.dialog_destination);
        //ImageView u_image = (ImageView) layout.findViewById(R.id.dialog_imagetag);
        Button accept = (Button) layout.findViewById(R.id.dialog_accept);
        Button cancel = (Button) layout.findViewById(R.id.dialog_cancel);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onaccept.DrawDirectionAndSet(data);
                taskInformer.ShowTaskInformer();
                dialog.dismiss();
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
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(exptime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ////
        u_exptime.setText(String.format(Locale.ENGLISH, "%d-%d-%d %02d-%02d", Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE));
        u_place.setText(place);

        /*if (type.equals("offer")) {
            u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_greentag));
        } else if (type.equals("prompt")) {
            u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_bluetag));
        } else {
            u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_redtag));
        }*/
    }

}
