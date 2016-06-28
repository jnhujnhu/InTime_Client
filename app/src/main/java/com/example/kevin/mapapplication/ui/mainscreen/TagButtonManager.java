package com.example.kevin.mapapplication.ui.mainscreen;

import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.example.kevin.mapapplication.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Kevin on 3/12/16.
 */
public class TagButtonManager {

    private MapsActivity handle;

    private Button redbutton;
    private Button greenbutton;
    private Button bluebutton;

    private boolean rebbtnState;
    private boolean bluebtnState;
    private boolean greenbtnState;

    public TagButtonManager (MapsActivity hand) {
        handle = hand;
        rebbtnState = false;
        bluebtnState = false;
        greenbtnState = false;
        redbutton = (Button) handle.findViewById(R.id.btn_redevent);
        greenbutton = (Button) handle.findViewById(R.id.btn_greenevent);
        bluebutton = (Button) handle.findViewById(R.id.btn_blueevent);
    }

    public void showTagButton() {
        rebbtnState = true;
        bluebtnState = true;
        greenbtnState = true;
        shiftLeftAnimation(R.id.btn_redevent, 900);
        shiftLeftAnimation(R.id.btn_blueevent, 700);
        shiftLeftAnimation(R.id.btn_greenevent, 800);
    }

    public void hideTagButton() {
        if(rebbtnState) {
            shiftRightAnimation(R.id.btn_redevent, 700);
            rebbtnState = false;
        }
        if(bluebtnState) {
            shiftRightAnimation(R.id.btn_blueevent, 900);
            bluebtnState = false;
        }
        if(greenbtnState) {
            shiftRightAnimation(R.id.btn_greenevent, 800);
            greenbtnState = false;
        }
    }

    public void hideOtherTagButton(String color) {
        switch (color) {
            case "Red":
                bluebtnState = false;
                greenbtnState = false;
                shiftRightAnimation(R.id.btn_blueevent, 900);
                shiftRightAnimation(R.id.btn_greenevent, 800);
                break;
            case "Blue":
                rebbtnState = false;
                greenbtnState = false;
                shiftRightAnimation(R.id.btn_redevent, 700);
                shiftRightAnimation(R.id.btn_greenevent, 800);
                break;
            case "Green":
                rebbtnState = false;
                bluebtnState = false;
                shiftRightAnimation(R.id.btn_blueevent, 900);
                shiftRightAnimation(R.id.btn_redevent, 700);
                break;
        }
    }

    private void shiftLeftAnimation(int id, int duration) {
        Button button = (Button) handle.findViewById(id);
        TranslateAnimation shiftleft = new TranslateAnimation(300, 0, 0, 0);
        shiftleft.setDuration(duration);
        shiftleft.setFillAfter(true);
        button.setVisibility(View.VISIBLE);
        button.startAnimation(shiftleft);
        button.setEnabled(true);
    }

    private void shiftRightAnimation(int id, int duration) {
        Button button = (Button) handle.findViewById(id);
        TranslateAnimation shiftright = new TranslateAnimation(0, 300, 0, 0);
        shiftright.setDuration(duration);
        button.setEnabled(false);
        button.startAnimation(shiftright);
        button.setVisibility(View.GONE);
    }

    public void setButtonListener(final boolean enable) {
        handle.getWindow().setExitTransition(new Slide(3));

        redbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enable) {
                    hideOtherTagButton("Red");
                    Bundle bundle = new Bundle();
                    bundle.putString("state", "add");
                    bundle.putFloat("color", BitmapDescriptorFactory.HUE_RED);
                    handle.setState(bundle);
                    setButtonListener(false);
                }
            }
        });
        bluebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enable) {
                    hideOtherTagButton("Blue");
                    Bundle bundle = new Bundle();
                    bundle.putString("state", "add");
                    bundle.putFloat("color", BitmapDescriptorFactory.HUE_BLUE);
                    handle.setState(bundle);
                    setButtonListener(false);
                }

            }
        });
        greenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enable) {
                    hideOtherTagButton("Green");
                    Bundle bundle = new Bundle();
                    bundle.putString("state", "add");
                    bundle.putFloat("color", BitmapDescriptorFactory.HUE_GREEN);
                    handle.setState(bundle);
                    setButtonListener(false);
                }
            }
        });
    }

}
