package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.example.kevin.mapapplication.utils.CustomListItem;
import com.example.kevin.mapapplication.utils.CustomListViewAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AddFriendsActivity extends AppCompatActivity {


    private EditText searchinput;
    private ImageButton searchbtn;
    private ProgressBar loading;
    private ListView result;
    private Map<String, String> usernametoid;

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

        usernametoid = new HashMap<>();

        final AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                JSONArray get_list = res.optJSONArray("users");
                final List<CustomListItem> user_list = new ArrayList<>();

                for(int i = 0;i < get_list.length(); i++) {
                    JSONObject user_item = get_list.optJSONObject(i);
                    user_list.add(new CustomListItem(user_item.optString("username")));
                    usernametoid.put(user_item.optString("username"), user_item.optString("uid"));
                }

                CustomListViewAdapter user_listAdapter = new CustomListViewAdapter(AddFriendsActivity.this, R.layout.listview_item_accepted_friends, user_list) {
                    @Override
                    public View setViewDetail(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi;
                            vi = LayoutInflater.from(AddFriendsActivity.this);
                            v = vi.inflate(R.layout.listview_item_accepted_friends, parent, false);
                        }
                        CustomListItem item = getItem(position);
                        if(item!=null) {
                            TextView username = (TextView) v.findViewById(R.id.friends_accepted_username);
                            username.setText(item.text);
                        }
                        return v;
                    }
                };
                result.setAdapter(user_listAdapter);
                result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(AddFriendsActivity.this, FriendsDetailActivity.class);
                        intent.putExtra("uid", usernametoid.get(user_list.get(position).text));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                });
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(AddFriendsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };

        searchinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loading.setVisibility(View.VISIBLE);
                ConnectionManager.getInstance().SearchUser(getSharedPreferences("User_info", MODE_PRIVATE).getString("token", null), searchinput.getText().toString(), handler);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
