package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;
import com.example.kevin.mapapplication.utils.HorizontalPicker;

import org.json.JSONException;

import java.util.Calendar;

public class TagInfoActivity extends AppCompatActivity {

    public final static int RESULT_CODE = 235;
    public final static int REQUEST_CODE = 221;

    protected EditText shorttitle;
    protected Spinner category;
    protected EditText Price;
    protected EditText DetailedDcpt;
    protected EditText Place;
    protected Button DateSelector;
    protected Button MapPlace;
    protected HorizontalPicker enrollment;
    protected CheckBox Privacy;
    protected CheckBox Template;
    protected Intent intent;

    protected String Placename, Class, oid, tid, b_title, b_category, b_price, b_exptime, b_content;
    protected int b_enrollment;
    protected boolean b_privacy, b_template;
    protected double Latitude, Longitude;
    protected int Year, Month, Day, Hour, Minute;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case MapsActivity.RESULT_CODE_CANCEL:
                    break;
                case MapsActivity.RESULT_CODE_OK:
                    Bundle bundle = data.getExtras();
                    Placename = bundle.getString("place");
                    Latitude = bundle.getDouble("latitude");
                    Longitude = bundle.getDouble("longitude");
                    Place.setText(Placename);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTagView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setEnterTransition(new Slide(5));
        SetButtonListener();

    }
    @Override
    public void onBackPressed() {
        if(!intent.hasExtra("IsCanceled")) {
            intent.putExtra("IsCanceled", true);
        }
        setOnBackIntent();
        setResult(RESULT_CODE, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
    protected void setOnBackIntent() {}

    protected void setDateSelector(final int Theme) {
        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(TagInfoActivity.this, Theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Year = year;
                        Month = monthOfYear;
                        Day = dayOfMonth;
                        Calendar c = Calendar.getInstance();
                        new TimePickerDialog(TagInfoActivity.this, Theme,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Hour = hourOfDay;
                                        Minute = minute;
                                        DateSelector.setText(String.format("%d-%d-%d %02d:%02d", Year, Month, Day, Hour, Minute));
                                    }
                                }
                                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                true).show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }
    protected void chooseDateSelectorTheme() {}

    protected void setTagView() {}

    protected void setMapBtnIntent(Intent map_intent) {}

    protected void buildData(String title, String category, int enrollment, int point, String content, String time, boolean isprivate) throws JSONException {}

    private boolean checkForm() {
        if(shorttitle.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Title cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Place.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Price cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(DetailedDcpt.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Content cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Place.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Place cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void SetButtonListener() {
        Button submit, cancel;
        submit = (Button) findViewById(R.id.tag_submit_button);
        cancel = (Button) findViewById(R.id.tag_cancel_button);

        shorttitle = (EditText) findViewById(R.id.tag_short_title);
        category = (Spinner) findViewById(R.id.tag_type_spinner);
        enrollment = (HorizontalPicker) findViewById(R.id.tag_enrollment_numberpicker);
        Price = (EditText) findViewById(R.id.tag_price);
        DetailedDcpt = (EditText) findViewById(R.id.tag_detaildcpt);
        DateSelector = (Button) findViewById(R.id.tag_date_picker);
        Privacy = (CheckBox) findViewById(R.id.tag_isprivate_checkbox);
        Template = (CheckBox) findViewById(R.id.tag_create_template);
        Place = (EditText) findViewById(R.id.tag_place);
        MapPlace = (Button) findViewById(R.id.tag_map_btn);

        Bundle bundle = getIntent().getExtras();
        Placename = bundle.getString("place");
        Latitude = bundle.getDouble("latitude");
        Longitude = bundle.getDouble("longitude");

        Class = bundle.getString("class");
        oid = bundle.getString("oid");
        tid = bundle.getString("tid");
        b_category = bundle.getString("category");
        b_title = bundle.getString("title");
        b_content = bundle.getString("content");
        b_privacy = bundle.getBoolean("isPrivate");
        b_template = bundle.getBoolean("isTemplate");
        b_price = bundle.getString("points");
        b_enrollment = bundle.getInt("number");
        b_exptime = bundle.getString("time");

        Place.setText(Placename);
        if(enrollment!=null) {
            enrollment.setSelectedItem(0);
        }

        intent = new Intent();

        chooseDateSelectorTheme();

        MapPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map_intent = new Intent(TagInfoActivity.this, MapsActivity.class);
                map_intent.putExtra("state", "modify");
                map_intent.putExtra("latitude", Latitude);
                map_intent.putExtra("longitude", Longitude);
                setMapBtnIntent(map_intent);
                startActivityForResult(map_intent, REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        assert submit != null;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t_shorttile = shorttitle.getText().toString();
                boolean privacy = Privacy.isChecked();
                int number = 0;
                if(enrollment!=null) {
                    number = Integer.parseInt(enrollment.getValues()[enrollment.getSelectedItem()].toString());
                }
                String t_price = Price.getText().toString();
                String t_category = category.getSelectedItem().toString();
                String t_detail = DetailedDcpt.getText().toString();
                boolean template = Template.isChecked();

                if(Class.equals("template") && tid == null) {

                }

                intent.putExtra("IsCanceled", false);
                TagInfoActivity.this.onBackPressed();

            }
        });
        assert cancel != null;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagInfoActivity.this.onBackPressed();
            }
        });
    }

}