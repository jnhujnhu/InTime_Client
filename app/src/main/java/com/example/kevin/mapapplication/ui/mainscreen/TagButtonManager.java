package com.example.kevin.mapapplication.ui.mainscreen;

import android.app.ActivityOptions;
import android.content.Intent;
import android.transition.Slide;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.tag.BlueTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.GreenTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.RedTagInfoActivity;

/**
 * Created by Kevin on 3/12/16.
 */
public class TagButtonManager {

    private MapsActivity handle;

    private Button redbutton;
    private Button greenbutton;
    private Button bluebutton;

    public TagButtonManager (MapsActivity hand) {
        handle = hand;

        redbutton = (Button) handle.findViewById(R.id.btn_redevent);
        greenbutton = (Button) handle.findViewById(R.id.btn_greenevent);
        bluebutton = (Button) handle.findViewById(R.id.btn_blueevent);
    }

    public void ShowTagButton() {
        shiftleftanimation(R.id.btn_redevent, 900);
        shiftleftanimation(R.id.btn_blueevent, 700);
        shiftleftanimation(R.id.btn_greenevent, 800);
    }

    public void HideTagButton() {
        shiftrightanimation(R.id.btn_redevent, 700);
        shiftrightanimation(R.id.btn_blueevent, 900);
        shiftrightanimation(R.id.btn_greenevent, 800);
    }

    private void shiftleftanimation(int id, int duration) {
        Button button = (Button) handle.findViewById(id);
        TranslateAnimation shiftleft = new TranslateAnimation(300, 0, 0, 0);
        shiftleft.setDuration(duration);
        shiftleft.setFillAfter(true);
        button.setVisibility(View.VISIBLE);
        button.startAnimation(shiftleft);
        button.setEnabled(true);
    }

    private void shiftrightanimation(int id, int duration) {
        Button button = (Button) handle.findViewById(id);
        TranslateAnimation shiftright = new TranslateAnimation(0, 300, 0, 0);
        shiftright.setDuration(duration);
        shiftright.setFillAfter(true);
        button.setEnabled(false);
        button.startAnimation(shiftright);
        button.setVisibility(View.INVISIBLE);
    }


    public void SetButtonListener() {
        handle.getWindow().setExitTransition(new Slide(3));
        redbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(handle, RedTagInfoActivity.class);
                intent.putExtra("EventType", "Red");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(handle
                        , handle.findViewById(R.id.btn_redevent), "redtag");
                handle.startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());
            }
        });
        bluebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(handle, BlueTagInfoActivity.class);
                intent.putExtra("EventType", "Blue");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(handle
                        , handle.findViewById(R.id.btn_blueevent), "bluetag");
                handle.startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());

            }
        });
        greenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(handle, GreenTagInfoActivity.class);
                intent.putExtra("EventType", "Green");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(handle
                        , handle.findViewById(R.id.btn_greenevent), "greentag");
                handle.startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());
            }
        });
    }

}
