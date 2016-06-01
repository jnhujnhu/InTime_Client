package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PromotionActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 233;

    private String uid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        SharedPreferences userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        uid = userinfo.getString("uid", null);
        token = userinfo.getString("token", null);
        Log.i("uid", uid);
        Log.i("token", token);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        final Button button_apply = (Button)findViewById(R.id.promotion_apply);
        final TextView text_promotion = (TextView)findViewById(R.id.promotion_code);
        final ProgressBar progress_loading = (ProgressBar)findViewById(R.id.promotion_loading);

        assert button_apply != null;
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promotionCode = text_promotion.getText().toString();
                progress_loading.setVisibility(View.VISIBLE);

                ConnectionManager.getInstance().PostPromotionCode(uid, promotionCode, token, new AsyncJSONHttpResponseHandler() {
                    @Override
                    public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                        progress_loading.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(PromotionActivity.this, BalanceActivity.class);
                        intent.putExtra("increment", res.getInt("increment"));
                        intent.putExtra("balance", res.getInt("balance"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        finish();
                    }

                    @Override
                    public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                        progress_loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(PromotionActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        assert text_promotion != null;
        text_promotion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    button_apply.setEnabled(true);
                }
                else {
                    button_apply.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
