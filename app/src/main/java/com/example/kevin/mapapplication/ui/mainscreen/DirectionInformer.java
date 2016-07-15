package com.example.kevin.mapapplication.ui.mainscreen;

import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kevin.mapapplication.R;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Kevin on 3/12/16.
 */
public class DirectionInformer {

    private final MapsActivity handle;

    private final GoogleMap map;

    private LinearLayout directioninformer;
    private Button direction_cancel;

    public boolean isShown;

    public DirectionInformer(MapsActivity hand, GoogleMap mMap) {
        handle = hand;
        isShown = false;
        onTaskCancelClicker = hand;
        map = mMap;
    }

    public interface OnTaskCancelClicker {
        void ClearMapAndSetInfoWindow();
    }

    private OnTaskCancelClicker onTaskCancelClicker;

    public void SetDirectionInformer() {
        direction_cancel = (Button) handle.findViewById(R.id.btn_direction_hide);
        directioninformer = (LinearLayout) handle.findViewById(R.id.ll_direction_informer);
        direction_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideDirectionInformer();
            }
        });
    }

    public void HideDirectionInformer() {
        if(isShown) {
            onTaskCancelClicker.ClearMapAndSetInfoWindow();
            TranslateAnimation shiftright = new TranslateAnimation(0, 700, 0, 0);
            shiftright.setDuration(700);
            directioninformer.startAnimation(shiftright);
            directioninformer.setVisibility(View.GONE);
            direction_cancel.setEnabled(false);
            isShown = false;
        }
    }

    public void ShowDirectionInformer() {
        if(!isShown) {
            TranslateAnimation shiftleft = new TranslateAnimation(700, 0, 0, 0);
            shiftleft.setDuration(700);
            shiftleft.setFillAfter(true);
            directioninformer.setVisibility(View.VISIBLE);
            directioninformer.startAnimation(shiftleft);
            direction_cancel.setEnabled(true);
            isShown = true;
        }
    }

}
