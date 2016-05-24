package com.example.kevin.mapapplication.ui.mainscreen.tag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.MarkerManager;

import org.json.JSONException;

public class BlueTagInfoActivity extends AppCompatActivity {

    public final static int RESULT_CODE = 237;

    private EditText shorttitle;
    private Spinner type;
    private EditText Reward;
    private EditText DetailedDcpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tag_info);
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
        Reward = (EditText) findViewById(R.id.tag_reward);
        DetailedDcpt = (EditText) findViewById(R.id.tag_detaildcpt);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t_shorttile = shorttitle.getText().toString();
                String t_reward = Reward.getText().toString();
                String t_type = type.getSelectedItem().toString();
                String t_detail = DetailedDcpt.getText().toString();
                if(t_shorttile.equals("")) {
                    Toast.makeText(BlueTagInfoActivity.this, "Short Title cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else if(t_reward.equals("")) {
                    Toast.makeText(BlueTagInfoActivity.this, "Reward cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else if(t_detail.equals("")) {
                    Toast.makeText(BlueTagInfoActivity.this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        int id = MarkerManager.getInstance().getID();
                        MarkerManager.getInstance().BuildData(id, "Blue", t_shorttile, t_type, Integer.parseInt(t_reward), t_detail);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("Form", "Confirmed");
                    setResult(RESULT_CODE, intent);
                    BlueTagInfoActivity.this.onBackPressed();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueTagInfoActivity.this.onBackPressed();
            }
        });
    }

}
