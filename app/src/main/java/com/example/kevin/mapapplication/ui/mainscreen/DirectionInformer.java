package com.example.kevin.mapapplication.ui.mainscreen;

import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Kevin on 3/12/16.
 */
public class DirectionInformer {

    private final MapsActivity handle;

    private final GoogleMap map;

    private LinearLayout taskinformer;
    private Button task_cancel;

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

    public void SetTaskInformer() {
        task_cancel = (Button) handle.findViewById(R.id.btn_direction_hide);
        taskinformer = (LinearLayout) handle.findViewById(R.id.ll_task_informer);
        task_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideTaskInformer();
                map.setInfoWindowAdapter(handle);
            }
        });
    }

    public void HideTaskInformer() {
        if(isShown) {
            onTaskCancelClicker.ClearMapAndSetInfoWindow();
            TranslateAnimation shiftright = new TranslateAnimation(0, 700, 0, 0);
            shiftright.setDuration(700);
            taskinformer.startAnimation(shiftright);
            taskinformer.setVisibility(View.GONE);
            task_cancel.setEnabled(false);
            isShown = false;
        }
    }

    public void ShowTaskInformer() {
        if(!isShown) {
            Button task_cancel = (Button) handle.findViewById(R.id.btn_direction_hide);
            LinearLayout taskinformer = (LinearLayout) handle.findViewById(R.id.ll_task_informer);
            TranslateAnimation shiftleft = new TranslateAnimation(700, 0, 0, 0);
            shiftleft.setDuration(700);
            shiftleft.setFillAfter(true);
            taskinformer.setVisibility(View.VISIBLE);
            taskinformer.startAnimation(shiftleft);
            task_cancel.setEnabled(true);
            isShown = true;
        }
    }

}
