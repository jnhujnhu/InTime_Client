package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.example.kevin.mapapplication.utils.HorizontalPicker;

import junit.framework.Test;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

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
    protected Button submit, cancel;
    protected HorizontalPicker enrollment;
    protected CheckBox Privacy;
    protected CheckBox Template;
    protected ProgressBar loading;
    protected Intent intent;
    protected TextView customtype;

    protected String Placename, Class, oid, tid, b_title, b_category, b_exptime, b_content, type;
    protected int b_enrollment, b_price;
    protected boolean b_privacy, b_template, Special;
    protected double Latitude, Longitude;
    protected int Year, Month, Day, Hour, Minute;
    protected long t_time;
    protected long DateSelectorClickDelta;
    protected SharedPreferences userinfo;

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
        setOnBackIntent();
        setResult(RESULT_CODE, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
    protected void setOnBackIntent() {}

    protected void setDateSelector(final int Theme) {
        DateSelectorClickDelta = 0;
        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if((DateSelectorClickDelta == 0 || System.currentTimeMillis() - DateSelectorClickDelta > 800)) {
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
                                            DateSelector.setText(String.format(Locale.ENGLISH, "%d-%d-%d %02d:%02d", Year, Month + 1, Day, Hour, Minute));
                                            t_time = componentTimeToTimestamp(Year, Month, Day, Hour, Minute);
                                        }
                                    }
                                    , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                    true).show();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                }
                DateSelectorClickDelta = System.currentTimeMillis();
            }
        });
    }

    protected String setType() { return null;}

    protected void chooseDateSelectorTheme() {}

    protected void setTagView() {}

    protected void setMapBtnIntent(Intent map_intent) {}

    private boolean checkForm() {
        if(shorttitle.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Title cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(customtype.getVisibility() == View.VISIBLE && customtype.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Custom Type cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Price != null && Price.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Price cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(DateSelector.getText().toString().equals("Pick a Time")) {
            Toast.makeText(TagInfoActivity.this, "Please choose a time.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Place.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Place cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(DetailedDcpt.getText().toString().equals("")) {
            Toast.makeText(TagInfoActivity.this, "Content cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createTemplate(String t_shorttile, String t_detail, String t_category, int t_price, int number, long time, boolean privacy) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TagInfoActivity.this, "Successfully created template", Toast.LENGTH_LONG).show();
                TagInfoActivity.this.onBackPressed();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                submit.setEnabled(true);
                Toast.makeText(TagInfoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().CreateTemplate(type, t_shorttile, t_detail, t_category, t_price, number, time, Placename, Latitude, Longitude, privacy, userinfo.getString("token", null), handler);
    }

    private void modifyTemplate(String tid, String t_shorttile, String t_detail, String t_category, int t_price, int number, long time, boolean privacy) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TagInfoActivity.this, "Successfully modified template", Toast.LENGTH_LONG).show();
                TagInfoActivity.this.onBackPressed();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                submit.setEnabled(true);
                Toast.makeText(TagInfoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().ModifyTemplate(tid, type, t_shorttile, t_detail, t_category, t_price, number, time, Placename, Latitude, Longitude, privacy, userinfo.getString("token", null), handler);
    }

    private void createOrder(String t_shorttile, String t_detail, String t_category, int t_price, int number, long time, boolean privacy) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TagInfoActivity.this, "Successfully created " + type, Toast.LENGTH_SHORT).show();
                TagInfoActivity.this.onBackPressed();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                submit.setEnabled(true);
                Toast.makeText(TagInfoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().CreateOrder(type, t_shorttile, t_detail, t_category, t_price, number, time, Placename, Latitude, Longitude, privacy, userinfo.getString("token", null), handler);
    }

    private void modifyOrder(String oid, String t_shorttile, String t_detail, String t_category, int t_price, int number, long time, boolean privacy) {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TagInfoActivity.this, "Successfully modified " + type, Toast.LENGTH_SHORT).show();
                TagInfoActivity.this.onBackPressed();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                submit.setEnabled(true);
                Toast.makeText(TagInfoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().ModifyOrder(oid, type, t_shorttile, t_detail, t_category, t_price, number, time, Placename, Latitude, Longitude, privacy, userinfo.getString("token", null), handler);
    }

    private long componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private void SetButtonListener() {
        submit = (Button) findViewById(R.id.tag_submit_button);
        cancel = (Button) findViewById(R.id.tag_cancel_button);

        shorttitle = (EditText) findViewById(R.id.tag_short_title);
        category = (Spinner) findViewById(R.id.tag_type_spinner);
        customtype = (TextView) findViewById(R.id.tag_custom_type);
        enrollment = (HorizontalPicker) findViewById(R.id.tag_enrollment_numberpicker);
        Price = (EditText) findViewById(R.id.tag_price);
        DetailedDcpt = (EditText) findViewById(R.id.tag_detaildcpt);
        DateSelector = (Button) findViewById(R.id.tag_date_picker);
        Privacy = (CheckBox) findViewById(R.id.tag_isprivate_checkbox);
        Template = (CheckBox) findViewById(R.id.tag_create_template);
        Place = (EditText) findViewById(R.id.tag_place);
        MapPlace = (Button) findViewById(R.id.tag_map_btn);
        loading = (ProgressBar) findViewById(R.id.tag_loading);
        t_time = 0;

        Bundle bundle = getIntent().getExtras();
        Placename = bundle.getString("place", "");
        Latitude = bundle.getDouble("latitude", 200);
        Longitude = bundle.getDouble("longitude", 200);

        ////////////////////////Get from Intent/////////////////////
        Class = bundle.getString("class", "error");
        oid = bundle.getString("oid", "");
        tid = bundle.getString("tid", "");

        b_category = bundle.getString("category", "null");
        b_title = bundle.getString("title", "");
        b_content = bundle.getString("content", "");
        b_privacy = bundle.getBoolean("isPrivate", false);
        b_template = bundle.getBoolean("isTemplate", false);
        b_price = bundle.getInt("points", -1);
        b_enrollment = bundle.getInt("number", 1);
        b_exptime = bundle.getString("time", "");
        Special = bundle.getBoolean("special", false);

        if(Special) {
            shorttitle.setEnabled(false);
            category.setEnabled(false);
            Price.setEnabled(false);
            DetailedDcpt.setEnabled(false);
            Place.setEnabled(false);
            MapPlace.setEnabled(false);
        }

        if(!b_exptime.equals("")) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            try {
                Date date = inputFormat.parse(b_exptime);
                b_exptime = outputFormat.format(date);
                t_time = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            b_exptime = "Pick a Time";
        }
        /////////////////////////////////////////////////////////////


        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        type = setType();
        if (Class.equals("template")) {
            Template.setVisibility(View.GONE);
        }

        ///////////////Set Default Content//////////////////////////
        shorttitle.setText(b_title);
        final TextView customtypelabel = (TextView) findViewById(R.id.tag_custom_type_label);
        if(!b_category.equals("null")) {
            if(((ArrayAdapter<String>) category.getAdapter()).getPosition(b_category) == -1) {
                customtypelabel.setVisibility(View.VISIBLE);
                customtype.setVisibility(View.VISIBLE);
                category.setSelection(((ArrayAdapter<String>) category.getAdapter()).getPosition("Other"));
                customtype.setText(b_category);
            }
            else {
                category.setSelection(((ArrayAdapter<String>) category.getAdapter()).getPosition(b_category));
            }
        }
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Other")) {
                    customtypelabel.setVisibility(View.VISIBLE);
                    customtype.setVisibility(View.VISIBLE);
                }
                else {
                    customtypelabel.setVisibility(View.GONE);
                    customtype.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Place.setText(Placename);
        DetailedDcpt.setText(b_content);
        Privacy.setChecked(b_privacy);
        if(Price != null && b_price != -1) {
            Price.setText(Integer.toString(b_price));
        }
        if(enrollment != null) {
            enrollment.setSelectedItem(b_enrollment - 1);
        }
        DateSelector.setText(b_exptime);

        ///////////////////////////////////////////////////////////

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
                    number = enrollment.getSelectedItem() + 1;
                }
                int t_price = 0;
                if(Price!=null) {
                    if(Price.getText().toString().equals("")) {
                        t_price = 0;
                    }
                    else {
                        t_price = Integer.parseInt(Price.getText().toString());
                    }
                }
                String t_category = category.getSelectedItem().toString();
                if(t_category.equals("Other")) {
                    t_category = customtype.getText().toString();
                }
                String t_detail = DetailedDcpt.getText().toString();
                boolean istemplate = Template.isChecked();
                Placename = Place.getText().toString();

                if(Class.equals("template") && tid.equals("")) {
                    submit.setEnabled(false);
                    createTemplate(t_shorttile, t_detail, t_category, t_price, number, t_time, privacy);
                }
                else if(Class.equals("template")) {
                    submit.setEnabled(false);
                    modifyTemplate(tid, t_shorttile, t_detail, t_category, t_price, number, t_time, privacy);
                }
                else if(Class.equals("order") && oid.equals("")) {
                    if(checkForm()) {
                        submit.setEnabled(false);
                        createOrder(t_shorttile, t_detail, t_category, t_price, number, t_time, privacy);
                        if (istemplate) {
                            createTemplate(t_shorttile, t_detail, t_category, t_price, number, t_time, privacy);
                        }
                    }
                }
                else if(Class.equals("order")) {
                    if(checkForm()) {
                        submit.setEnabled(false);
                        modifyOrder(oid, t_shorttile, t_detail, t_category, t_price, number, t_time, privacy);
                    }
                }

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
