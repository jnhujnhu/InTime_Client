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

public class BlueTagInfoActivity extends TagInfoActivity {

    @Override
    public void setDateSeletor() {
        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BlueTagInfoActivity.this, R.style.BlueDatePickerDialogTheme, null, 2016, 5, 27).show();
            }
        });
    }

    @Override
    protected void setMapBtnIntent(Intent map_intent) {
        map_intent.putExtra("color", BitmapDescriptorFactory.HUE_BLUE);
    }

    @Override
    protected void setTagView() {
        setContentView(R.layout.activity_blue_tag_info);
    }

    @Override
    protected void setOnBackIntent(){
        intent.putExtra("Color", "Blue");
    }
}


