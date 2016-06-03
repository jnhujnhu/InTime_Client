package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TemplateDetailActivity extends AppCompatActivity {

    private Bundle bundle;
    private Bundle newBundle;
    private SharedPreferences userinfo;

    private PopupMenu popupMenu;
    private MenuInflater menuInflater;
    private ImageView button_more;
    private ProgressBar loading;
    private ImageView image_type;
    private TextView text_type;
    private TextView text_title;
    private TextView text_category;
    private TextView text_privacy;
    private TextView text_enrollment;
    private TextView text_rewards;
    private TextView text_location;
    private TextView text_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_detail);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bundle = getIntent().getExtras();
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        newBundle = new Bundle();

        button_more = (ImageView)findViewById(R.id.template_more);
        loading = (ProgressBar)findViewById(R.id.template_detail_loading);
        image_type = (ImageView)findViewById(R.id.template_detail_type_icon);
        text_type = (TextView)findViewById(R.id.template_detail_type);
        text_title = (TextView)findViewById(R.id.template_detail_title);
        text_category = (TextView)findViewById(R.id.template_detail_category);
        text_privacy = (TextView)findViewById(R.id.template_detail_privacy);
        text_enrollment = (TextView)findViewById(R.id.template_detail_enrollment);
        text_rewards = (TextView)findViewById(R.id.template_detail_rewards);
        text_location = (TextView)findViewById(R.id.template_detail_location);
        text_content = (TextView)findViewById(R.id.template_detail_content);

        button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupMenu==null){
                    popupMenu = new PopupMenu(TemplateDetailActivity.this, button_more);

                    menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.menu_more,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.action_edit:
                                    editTemplate();
                                    break;
                                case R.id.action_delete:
                                    deleteTemplate();
                                    break;
                            }
                            return true;
                        }
                    });
                }
                popupMenu.show();
            }
        });
    }

    private void GetTemplateDetail() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().GetTemplateDetail(bundle.getString("tid"), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);

                newBundle.putString("class", "template");
                newBundle.putString("type", res.optString("type"));
                newBundle.putString("title", res.optString("title"));
                newBundle.putBoolean("isPrivate", res.optBoolean("isPrivate"));
                newBundle.putInt("number", res.optInt("number"));
                newBundle.putInt("points", res.optInt("points"));
                newBundle.putString("place", res.optString("place"));
                newBundle.putDouble("latitude", res.optJSONObject("coordinate").optDouble("latitude"));
                newBundle.putDouble("longitude", res.optJSONObject("coordinate").optDouble("longitude"));
                newBundle.putString("content", res.optString("content"));

                switch (res.optString("type")) {
                    case "request":
                        text_type.setText("request");
                        text_type.setTextColor(ContextCompat.getColor(TemplateDetailActivity.this, R.color.colorRedEvent));
                        image_type.setImageResource(R.drawable.ic_redtag);
                        break;
                    case "offer":
                        text_type.setText("offer");
                        text_type.setTextColor(ContextCompat.getColor(TemplateDetailActivity.this, R.color.colorGreenEvent));
                        image_type.setImageResource(R.drawable.ic_greentag);
                        break;
                    case "notification":
                        text_type.setText("prompt");
                        text_type.setTextColor(ContextCompat.getColor(TemplateDetailActivity.this, R.color.colorBlueEvent));
                        image_type.setImageResource(R.drawable.ic_bluetag);
                        break;
                }

                text_title.setText(res.optString("title"));
                text_category.setText(res.optString("category"));
                text_privacy.setText(res.optBoolean("isPrivate") ? "Visible to friends only" : "Visible to everyone");
                text_enrollment.setText(String.format("%d Person%s", res.optInt("number"), res.optInt("number") > 1 ? "s" : ""));
                text_rewards.setText(String.format("%d Points/Person", res.optInt("points")));
                text_location.setText(res.optString("place"));
                text_content.setText(res.optString("content"));
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TemplateDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editTemplate() {
        Intent intent = new Intent(TemplateDetailActivity.this, TemplateEditActivity.class);
        intent.putExtras(newBundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void deleteTemplate() {
        ConnectionManager.getInstance().DeleteTemplate(bundle.getString("tid"), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TemplateDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        GetTemplateDetail();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
