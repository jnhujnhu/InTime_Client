package com.example.kevin.mapapplication.ui.mainscreen;

import android.content.Context;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 3/12/16.
 */
public class TagInfoWindow {

    private MapsActivity context;

    public TagInfoWindow (MapsActivity con) {
        context = con;
    }

    public void setTagContent(View v, final Marker marker, final GoogleMap mMap) {
        ImageView infoimage = (ImageView) v.findViewById(R.id.markerinfowindow_image);
        TextView shorttitle = (TextView) v.findViewById(R.id.markerinfowindow_title);
        TextView type = (TextView) v.findViewById(R.id.markerinfowindow_category);
        TextView cost = (TextView) v.findViewById(R.id.markerinfowindow_points);
        try {
            JSONObject mdata = MarkerManager.getInstance().Get(marker.getId());

            if(mdata.optString("type").equals("offer")) {
                infoimage.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_greentag));
            }
            else if(mdata.optString("type").equals("prompt")) {
                infoimage.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_bluetag));
            }
            else {
                infoimage.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_redtag));
            }
            shorttitle.setText(mdata.optString("title"));
            type.setText(mdata.optString("category"));
            cost.setText("Points: " + Integer.toString(mdata.optInt("points")) + " Points");

            ImageButton direction = (ImageButton) v.findViewById(R.id.markerinfowindow_dir_btn);

            direction.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_UP:
                            try {
                                marker.hideInfoWindow();
                                context.loading.setVisibility(View.VISIBLE);
                                context.DrawDirectionAndSet(MarkerManager.getInstance().Get(marker.getId()));
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    return true;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public View BuildTagContent() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.infowindow_marker_normal, null);

        return v;

    }

}
