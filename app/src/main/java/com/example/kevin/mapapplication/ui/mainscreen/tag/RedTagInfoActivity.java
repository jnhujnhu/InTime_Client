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

import org.json.JSONException;

public class RedTagInfoActivity extends AppCompatActivity {

    public final static int RESULT_CODE = 235;

    private EditText shorttitle;
    private Spinner type;
    private EditText Price;
    private EditText DetailedDcpt;
    private Button DateSelector;
    private HorizontalPicker enrollment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_tag_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setEnterTransition(new Slide(5));
        SetButtonListener();

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void SetButtonListener() {
        Button submit, cancel;
        submit = (Button) findViewById(R.id.tag_submit_button);
        cancel = (Button) findViewById(R.id.tag_cancel_button);

        shorttitle = (EditText) findViewById(R.id.tag_short_title);
        type = (Spinner) findViewById(R.id.tag_type_spinner);
        enrollment = (HorizontalPicker) findViewById(R.id.tag_enrollment_numberpicker);
        Price = (EditText) findViewById(R.id.tag_price);
        DetailedDcpt = (EditText) findViewById(R.id.tag_detaildcpt);
        DateSelector = (Button) findViewById(R.id.tag_date_picker);

        enrollment.setSelectedItem(0);

        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RedTagInfoActivity.this, R.style.RedDatePickerDialogTheme, null, 2016, 5, 27).show();
            }
        });

        assert submit != null;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t_shorttile = shorttitle.getText().toString();
                String t_price = Price.getText().toString();
                String t_type = type.getSelectedItem().toString();
                String t_detail = DetailedDcpt.getText().toString();
                if(t_shorttile.equals("")) {
                    Toast.makeText(RedTagInfoActivity.this, "Short Title cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else if(t_price.equals("")) {
                    Toast.makeText(RedTagInfoActivity.this, "Reward cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else if(t_detail.equals("")) {
                    Toast.makeText(RedTagInfoActivity.this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        int id = MarkerManager.getInstance().getID();
                        MarkerManager.getInstance().BuildData(id, "Red", t_shorttile, t_type, Integer.parseInt(t_price), t_detail);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("Form", "Confirmed");
                    setResult(RESULT_CODE, intent);
                    RedTagInfoActivity.this.onBackPressed();
                }
            }
        });
        assert cancel != null;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedTagInfoActivity.this.onBackPressed();
            }
        });
    }

}
