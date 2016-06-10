package com.example.kevin.mapapplication.ui.userinfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.ui.mainscreen.tag.BlueTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.GreenTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.RedTagInfoActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class OrderAndTemplateDetailActivity extends AppCompatActivity {

    protected Bundle bundle;
    protected Bundle newBundle;
    protected SharedPreferences userinfo;
    protected Class tagClass;

    protected PopupMenu popupMenu;
    protected MenuInflater menuInflater;
    protected TextView text_header;
    protected ImageView button_more;
    protected ProgressBar loading;
    protected ImageView image_type;
    protected TextView text_type;
    protected TextView text_title;
    protected TextView text_category;
    protected TextView text_privacy;
    protected TextView text_number;
    protected TextView text_points;
    protected TextView text_place;
    protected TextView text_time;
    protected TextView text_status;
    protected TextView text_content;
    protected Button button_create;
    protected Button button_complete;
    protected Button button_edit;
    protected Button button_cancel;
    protected Button button_accept;
    protected RelativeLayout layout_number;
    protected RelativeLayout layout_points;
    protected RelativeLayout layout_time;
    protected RelativeLayout layout_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_and_template_detail);

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

        text_header = (TextView)findViewById(R.id.detail_nav_header);
        button_more = (ImageView)findViewById(R.id.template_more);
        loading = (ProgressBar)findViewById(R.id.template_detail_loading);
        image_type = (ImageView)findViewById(R.id.detail_item_type_icon);
        text_type = (TextView)findViewById(R.id.detail_item_type);
        text_title = (TextView)findViewById(R.id.detail_item_title);
        text_category = (TextView)findViewById(R.id.detail_item_category);
        text_privacy = (TextView)findViewById(R.id.detail_item_privacy);
        text_number = (TextView)findViewById(R.id.detail_item_number);
        text_points = (TextView)findViewById(R.id.detail_item_points);
        text_place = (TextView)findViewById(R.id.detail_item_place);
        text_time = (TextView)findViewById(R.id.detail_item_time);
        text_status = (TextView)findViewById(R.id.detail_item_status);
        text_content = (TextView)findViewById(R.id.detail_item_content);
        button_create = (Button)findViewById(R.id.detail_button_create);
        button_complete = (Button)findViewById(R.id.detail_button_complete);
        button_edit = (Button)findViewById(R.id.detail_button_edit);
        button_cancel = (Button)findViewById(R.id.detail_button_cancel);
        button_accept = (Button)findViewById(R.id.detail_button_accept);
        layout_number = (RelativeLayout)findViewById(R.id.detail_item_container_number);
        layout_points = (RelativeLayout)findViewById(R.id.detail_item_container_points);
        layout_time = (RelativeLayout)findViewById(R.id.detail_item_container_time);
        layout_status = (RelativeLayout)findViewById(R.id.detail_item_container_status);
    }

    protected AsyncHttpResponseHandler detailHandler = new AsyncJSONHttpResponseHandler() {
        @Override
        public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
            loading.setVisibility(View.INVISIBLE);
            onRefreshDetail(res);
        }

        @Override
        public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
            loading.setVisibility(View.INVISIBLE);
            Toast.makeText(OrderAndTemplateDetailActivity.this, error, Toast.LENGTH_LONG).show();
        }
    };

    protected void onRefreshDetail(JSONObject res) {
        newBundle = new Bundle();
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
                text_type.setTextColor(ContextCompat.getColor(OrderAndTemplateDetailActivity.this, R.color.colorRedEvent));
                image_type.setImageResource(R.drawable.ic_redtag);
                tagClass = RedTagInfoActivity.class;
                break;
            case "offer":
                text_type.setText("offer");
                text_type.setTextColor(ContextCompat.getColor(OrderAndTemplateDetailActivity.this, R.color.colorGreenEvent));
                image_type.setImageResource(R.drawable.ic_greentag);
                tagClass = GreenTagInfoActivity.class;
                break;
            case "prompt":
                text_type.setText("prompt");
                text_type.setTextColor(ContextCompat.getColor(OrderAndTemplateDetailActivity.this, R.color.colorBlueEvent));
                image_type.setImageResource(R.drawable.ic_bluetag);
                layout_number.setVisibility(View.GONE);
                layout_points.setVisibility(View.GONE);
                tagClass = BlueTagInfoActivity.class;
                break;
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        try {
            Date date = inputFormat.parse(res.optString("time"));
            text_time.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        text_title.setText(res.optString("title"));
        text_category.setText(res.optString("category"));
        text_privacy.setText(res.optBoolean("isPrivate") ? "Visible to friends only" : "Visible to everyone");
        text_number.setText(String.format("%d Person%s", res.optInt("number"), res.optInt("number") > 1 ? "s" : ""));
        text_points.setText(String.format("%d Points/Person", res.optInt("points")));
        text_place.setText(res.optString("place"));
        text_content.setText(res.optString("content"));
    }

    protected void refreshDetail() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        refreshDetail();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
