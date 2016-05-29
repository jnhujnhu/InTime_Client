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
            String color = (String) data.get("Color");
            String title = (String) data.get("ShortTitle");
            String type = (String) data.get("Type");
            int reward = (int) data.get("Reward");
            String description = (String) data.get("DetailedDcpt");


            TextView u_title = (TextView) layout.findViewById(R.id.dialog_title);
            TextView u_type = (TextView) layout.findViewById(R.id.dialog_type);
            TextView u_dcpt = (TextView) layout.findViewById(R.id.dialog_description);
            TextView u_reward = (TextView) layout.findViewById(R.id.dialog_reward);
            ImageView u_image = (ImageView) layout.findViewById(R.id.dialog_imagetag);
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
            u_type.setText(type);
            u_dcpt.setText("                      " + description);
            u_reward.setText(Integer.toString(reward));

            if (color.equals("Green")) {
                u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_greentag));
            } else if (color.equals("Blue")) {
                u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_bluetag));
            } else {
                u_image.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.ic_redtag));
            }
    }

}
