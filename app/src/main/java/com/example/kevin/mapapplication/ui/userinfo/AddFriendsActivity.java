package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class AddFriendsActivity extends AppCompatActivity {


    private EditText searchinput;
    private ImageButton searchbtn;
    private ProgressBar loading;
    private ListView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        searchbtn = (ImageButton) findViewById(R.id.add_friend_search_btn);
        searchinput = (EditText) findViewById(R.id.add_friend_input);
        loading = (ProgressBar) findViewById(R.id.add_friend_loading);
        result = (ListView) findViewById(R.id.add_friend_listview);

        final AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);

              //  for(int i = 0;i < res.)

            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(AddFriendsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchinput.getText().toString().equals("")) {
                    Toast.makeText(AddFriendsActivity.this, "Username cannnot be empty", Toast.LENGTH_LONG).show();
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    ConnectionManager.getInstance().SearchUser(getSharedPreferences("User_info", MODE_PRIVATE).getString("token", null), searchinput.getText().toString(), handler);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra("Choosed","Item id");
        //setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
