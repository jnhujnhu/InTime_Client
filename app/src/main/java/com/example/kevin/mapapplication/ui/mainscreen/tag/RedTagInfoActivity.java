package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.example.kevin.mapapplication.utils.HorizontalPicker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;

public class RedTagInfoActivity extends TagInfoActivity {

    @Override
    protected void setDateSeletor() {
        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RedTagInfoActivity.this, R.style.RedDatePickerDialogTheme, null, 2016, 5, 27).show();
            }
        });
    }

    @Override
    protected void setMapBtnIntent(Intent map_intent) {
        map_intent.putExtra("color", BitmapDescriptorFactory.HUE_RED);
    }


    @Override
    protected void setTagView() {
        setContentView(R.layout.activity_red_tag_info);
    }

    @Override
    protected void buildData(String title, String category, int enrollment, int point, String content, String time, boolean isprivate) throws JSONException {}

    @Override
    protected void setOnBackIntent(){
        intent.putExtra("Color", "Red");
    }

}


