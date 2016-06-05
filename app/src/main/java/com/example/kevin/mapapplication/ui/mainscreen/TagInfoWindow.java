package com.example.kevin.mapapplication.ui.mainscreen;

import android.content.Context;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 3/12/16.
 */
public class TagInfoWindow {

    private Context context;

    public TagInfoWindow (Context con) {
        context = con;
    }

    public View BuildTagContent(Marker marker) {
        LinearLayout hor_layout = new LinearLayout(context), ver_layout = new LinearLayout(context);
        ImageView infoimage = new ImageView(context);
        TextView shorttitle = new TextView(context);
        TextView type = new TextView(context);
        TextView cost = new TextView(context);


        JSONObject mdata = MarkerManager.getInstance().Get(marker.getId());
        if(mdata.optInt("Id") == 0) {
            return null;
        }
        else {
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
        }

        infoimage.setPadding(0, 15, 0, 0);

        shorttitle.setTextSize(16);
        shorttitle.setTypeface(Typeface.DEFAULT_BOLD);

        hor_layout.setOrientation(LinearLayout.HORIZONTAL);
        hor_layout.setPadding(0, 10, 10, 10);
        ver_layout.setOrientation(LinearLayout.VERTICAL);
        hor_layout.addView(infoimage);
        ver_layout.addView(shorttitle);
        ver_layout.addView(type);
        ver_layout.addView(cost);
        hor_layout.addView(ver_layout);

        return hor_layout;

    }

}
