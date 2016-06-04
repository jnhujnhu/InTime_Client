package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TemplatesActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 233;

    private SharedPreferences userinfo;

    private ListView templateList;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        templateList = (ListView)findViewById(R.id.template_list);
        loading = (ProgressBar)findViewById(R.id.templates_loading);
    }

    public void GetTemplateList() {
        loading.setVisibility(View.VISIBLE);

        ConnectionManager.getInstance().GetTemplateList(userinfo.getString("uid", null), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                showTemplateList(res.optJSONArray("templates"));
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TemplatesActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTemplateList(JSONArray templates) {
        final List<Bundle> itemList = new ArrayList<>();
        for(int i = 0; i <templates.length(); i++) {
            JSONObject templateItem = templates.optJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("tid", templateItem.optString("tid"));
            bundle.putString("type", templateItem.optString("type"));
            bundle.putString("title", templateItem.optString("title"));
            bundle.putString("content", templateItem.optString("content"));
            itemList.add(bundle);
        }

        Collections.sort(itemList, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle lhs, Bundle rhs) {
                return -lhs.getString("tid").compareTo(rhs.getString("tid"));
            }
        });

        templateList.setAdapter(new ArrayAdapter<Bundle>(TemplatesActivity.this, R.layout.listview_item_default, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi;
                    vi = LayoutInflater.from(TemplatesActivity.this);
                    v = vi.inflate(R.layout.listview_item_default, parent, false);
                }
                Bundle item = getItem(position);

                if (item != null) {
                    ImageView icon = (ImageView)v.findViewById(R.id.listview_item_icon);
                    TextView text = (TextView)v.findViewById(R.id.listview_item_text);

                    switch (item.getString("type")) {
                        case "request":
                            icon.setImageResource(R.drawable.ic_redtag);
                            break;
                        case "offer":
                            icon.setImageResource(R.drawable.ic_greentag);
                            break;
                        case "notification":
                            icon.setImageResource(R.drawable.ic_bluetag);
                            break;
                    }

                    text.setText(item.getString("title"));
                }
                return v;
            }
        });

        templateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle item = itemList.get(position);
                Intent intent = new Intent(TemplatesActivity.this, TemplateDetailActivity.class);
                intent.putExtras(item);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }

    @Override
    protected void onResume() {
        GetTemplateList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
