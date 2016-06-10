package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;

import com.example.kevin.mapapplication.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class GreenTagInfoActivity extends TagInfoActivity {

    @Override
    protected void chooseDateSelectorTheme() {
        setDateSelector(R.style.GreenPickerDialogTheme);
    }

    @Override
    protected void setMapBtnIntent(Intent map_intent) {
        map_intent.putExtra("color", BitmapDescriptorFactory.HUE_GREEN);
    }
    @Override
    protected void setTagView() {
        setContentView(R.layout.activity_green_tag_info);
    }

    @Override
    protected void setOnBackIntent(){
        intent.putExtra("Color", "Green");
    }

    @Override
    protected String setType() {
        return "offer";
    }
}


