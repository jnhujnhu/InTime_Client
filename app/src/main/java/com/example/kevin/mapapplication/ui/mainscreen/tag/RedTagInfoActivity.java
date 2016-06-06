package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.kevin.mapapplication.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Locale;

public class RedTagInfoActivity extends TagInfoActivity {

    @Override
    protected void chooseDateSelectorTheme() {
        setDateSelector(R.style.RedPickerDialogTheme);
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


