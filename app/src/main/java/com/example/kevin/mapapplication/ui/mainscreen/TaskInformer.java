package com.example.kevin.mapapplication.ui.mainscreen;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kevin.mapapplication.R;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;

/**
 * Created by Kevin on 7/15/16.
 */
public class TaskInformer {

    private final MapsActivity handle;
    private final GoogleMap map;

    private LinearLayout taskinformer;
    private Button show_informer;
    private Button show_task;
    private JSONArray orders;

    public boolean isShown;

    public TaskInformer(MapsActivity handle, GoogleMap map, JSONArray orders) {
        this.handle = handle;
        this.map = map;
        this.orders = orders;
        isShown = false;
    }

    public void SetTaskInformer() {
        show_task = (Button) handle.findViewById(R.id.btn_show_ongoing_orders);
        show_informer = (Button) handle.findViewById(R.id.show_informer);
        taskinformer = (LinearLayout) handle.findViewById(R.id.ll_task_informer);

        show_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingTaskDialog dialog = new OnGoingTaskDialog(handle, map);
                dialog.BuildDialog(orders);
            }
        });
        show_informer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShown) {
                    HideTaskInformer();
                }
                else {
                    ShowTaskInformer();
                }
            }
        });
    }

    public void CloseTaskInformer() {
        taskinformer.setVisibility(View.GONE);
    }

    public void HideTaskInformer() {
        if(isShown) {
            show_informer.setText("<");
            ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(taskinformer, "translationX", 0, 587);
            objectAnimator.setDuration(700);
            objectAnimator.start();
            show_task.setEnabled(false);
            isShown = false;
        }
    }

    public void ShowTaskInformer() {
        if(!isShown) {
            show_informer.setText(">");
            taskinformer.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(taskinformer, "translationX", 587, 0);
            objectAnimator.setDuration(700);
            objectAnimator.start();
            show_task.setEnabled(true);
            isShown = true;
        }
    }

}
