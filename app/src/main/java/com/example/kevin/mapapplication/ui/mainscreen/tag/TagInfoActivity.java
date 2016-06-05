package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;
import com.example.kevin.mapapplication.utils.HorizontalPicker;

import org.json.JSONException;

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

    protected String Placename;
    protected double Latitude, Longitude;


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

    protected void setDateSeletor() {}

    protected void setTagView() {}

    protected void setMapBtnIntent(Intent map_intent) {

    }


    protected void buildData(String title, String category, int enrollment, int point, String content, String time, boolean isprivate) throws JSONException {}

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

        Place.setText(Placename);
        if(enrollment!=null) {
            enrollment.setSelectedItem(0);
        }

        intent = new Intent();
        setDateSeletor();

        MapPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map_intent = new Intent(TagInfoActivity.this, MapsActivity.class);
                map_intent.putExtra("state", "add");
                map_intent.putExtra("forResult", true);
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


                if (t_shorttile.equals("")) {
                    Toast.makeText(TagInfoActivity.this, "Short Title cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (t_price.equals("")) {
                    Toast.makeText(TagInfoActivity.this, "Reward cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (t_detail.equals("")) {
                    Toast.makeText(TagInfoActivity.this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    /*try {
                        int id = MarkerManager.getInstance().getID();
                        buildData(t_shorttile, t_category, );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    intent.putExtra("IsCanceled", false);
                    TagInfoActivity.this.onBackPressed();
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
