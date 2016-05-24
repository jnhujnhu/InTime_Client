package com.example.kevin.mapapplication.ui.mainscreen;

import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;

/**
 * Created by Kevin on 3/12/16.
 */
public class TaskInformer {

    private final MapsActivity handle;

    public TaskInformer(MapsActivity hand) {
        handle = hand;
        onTaskCancelClicker = hand;
    }

    public interface OnTaskCancelClicker {
        void ClearMapAndSetInfoWindow();
    }

    private OnTaskCancelClicker onTaskCancelClicker;

    public void SetTaskInformer() {
        final Button task_cancel = (Button) handle.findViewById(R.id.btn_task_cancel);
        final LinearLayout taskinformer = (LinearLayout) handle.findViewById(R.id.ll_task_informer);
        task_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTaskCancelClicker.ClearMapAndSetInfoWindow();
                TranslateAnimation shiftright = new TranslateAnimation(0, 700, 0, 0);
                shiftright.setDuration(700);
                shiftright.setFillAfter(true);
                taskinformer.startAnimation(shiftright);
                taskinformer.setVisibility(View.INVISIBLE);
                task_cancel.setEnabled(false);
            }
        });
    }

    public void ShowTaskInformer() {
        Button task_cancel = (Button)handle.findViewById(R.id.btn_task_cancel);
        LinearLayout taskinformer = (LinearLayout) handle.findViewById(R.id.ll_task_informer);
        TranslateAnimation shiftleft = new TranslateAnimation(700, 0, 0, 0);
        shiftleft.setDuration(700);
        shiftleft.setFillAfter(true);
        taskinformer.setVisibility(View.VISIBLE);
        taskinformer.startAnimation(shiftleft);
        task_cancel.setEnabled(true);
    }

}
