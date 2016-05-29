package com.example.kevin.mapapplication.ui.startup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;

import com.example.kevin.mapapplication.ui.mainscreen.MapsActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;


public class StartUpActivity extends AppCompatActivity {


    private TextView title;
    private TextView subtitle;
    private RelativeLayout login_panel;
    private RelativeLayout register_panel;
    private Button login_btn;
    private Button register_btn;
    private Button reg_cancel_btn;
    private Button reg_confirm_btn;
    private EditText login_username;
    private EditText login_password;
    private EditText reg_username;
    private EditText reg_password;
    private EditText reg_repassword;
    private EditText reg_phone;
    private EditText reg_email;
    private ProgressBar loading;

    private SharedPreferences userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        loading = (ProgressBar) findViewById(R.id.loading);

        String sp_username = userinfo.getString("username", null);


        if(sp_username != null) {
            String sp_password = userinfo.getString("password", null);
            Login(sp_username, sp_password, new Runnable(){
                @Override
                public void run() {
                    SharedPreferences.Editor editor = userinfo.edit();
                    editor.putString("username", null);
                    editor.putString("password", null);
                    editor.putString("uid", null);
                    editor.putString("token", null);
                    editor.apply();
                    ShowLoginInterface();
                }
            });
        }
        else {
            ShowLoginInterface();
        }
    }

    private void ShowLoginInterface() {
        //Toast.makeText(StartUpActivity.this, "Error!", Toast.LENGTH_LONG).show();

        login_panel = (RelativeLayout) findViewById(R.id.login_panel);
        register_panel = (RelativeLayout) findViewById(R.id.register_panel);

        DisableLoginPanel();
        DisableRegisterPanel();

        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow);
        title = (TextView) findViewById(R.id.login_title);
        subtitle = (TextView) findViewById(R.id.subtitle);
        title.startAnimation(fadein);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    TitleSlideUpHandler.sendMessage(new Message());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        login_btn = (Button) findViewById(R.id.login_btn);
        register_btn = (Button) findViewById(R.id.register_btn);

        reg_cancel_btn = (Button) findViewById(R.id.reg_cancel);
        reg_confirm_btn = (Button) findViewById(R.id.reg_confirm);

        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
        reg_username = (EditText) findViewById(R.id.reg_username);
        reg_password = (EditText) findViewById(R.id.reg_password);
        reg_repassword = (EditText) findViewById(R.id.reg_repassword);
        reg_phone = (EditText) findViewById(R.id.reg_phone);
        reg_email = (EditText) findViewById(R.id.reg_email);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideSoftInfoKeyword();
                Login(login_username.getText().toString(), login_password.getText().toString(), null);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeinF = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.fade_in_fast);
                fadeinF.setFillAfter(true);
                Animation fadeoutF = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.fade_out_fast);
                fadeoutF.setFillAfter(true);
                login_panel.startAnimation(fadeoutF);
                register_panel.startAnimation(fadeinF);
                DisableLoginPanel();
                EnableRegisterPanel();
            }
        });

        reg_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeinF = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.fade_in_fast);
                fadeinF.setFillAfter(true);
                Animation fadeoutF = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.fade_out_fast);
                login_panel.startAnimation(fadeinF);
                register_panel.startAnimation(fadeoutF);
                DisableRegisterPanel();
                EnableLoginPanel();
            }
        });

        reg_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reg_password.getText().toString().equals(reg_repassword.getText().toString())) {
                    HideSoftInfoKeyword();
                    Register(reg_username.getText().toString(), reg_password.getText().toString(), reg_phone.getText().toString(), reg_email.getText().toString());
                }
                else {
                    Toast.makeText(StartUpActivity.this,"Password and Re-password not match.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void HideSoftInfoKeyword() {
        View view = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void StartMainScreen() {
        Intent intent = new Intent(StartUpActivity.this, MapsActivity.class);
        StartUpActivity.this.startActivity(intent);
        finish();
    }

    private void Login(final String username, final String password, final Runnable doFailure){
        loading.setVisibility(View.VISIBLE);

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers,  byte[] responseBody) throws JSONException {
                JSONObject res = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                loading.setVisibility(View.INVISIBLE);
                SharedPreferences.Editor editor = userinfo.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("uid", res.optString("uid"));
                editor.putString("token", res.optString("token"));
                editor.apply();
                StartMainScreen();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                if(res == null) {
                    Toast.makeText(StartUpActivity.this, "Cannot Connect to Server.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(StartUpActivity.this, error, Toast.LENGTH_LONG).show();
                }
                if (doFailure != null)
                    doFailure.run();
            }
        };

        ConnectionManager.getInstance().Login(this.getBaseContext(), username, password, handler);
    }

    private void Register(final String username, final String password, String phone, String email) {
        loading.setVisibility(View.VISIBLE);

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers,  byte[] responseBody) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(StartUpActivity.this,"Register Success.", Toast.LENGTH_LONG).show();
                Login(username, password, null);
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                if(res == null) {
                    Toast.makeText(StartUpActivity.this, "Cannot Connect to Server.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(StartUpActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        };
        ConnectionManager.getInstance().Register(this.getBaseContext(), username, password, phone, email, handler);
    }

    private void DisableLoginPanel() {
        for (int i = 0; i < login_panel.getChildCount(); i++) {
            View child = login_panel.getChildAt(i);
            child.setEnabled(false);
        }
    }
    private void EnableLoginPanel() {
        for (int i = 0; i < login_panel.getChildCount(); i++) {
            View child = login_panel.getChildAt(i);
            child.setEnabled(true);
        }
    }
    private void DisableRegisterPanel() {
        for (int i = 0; i < register_panel.getChildCount(); i++) {
            View child = register_panel.getChildAt(i);
            child.setEnabled(false);
        }
        register_panel.setVisibility(View.GONE);
    }
    private void EnableRegisterPanel() {
        for (int i = 0; i < register_panel.getChildCount(); i++) {
            View child = register_panel.getChildAt(i);
            child.setEnabled(true);
        }
        register_panel.setVisibility(View.VISIBLE);
    }

    Handler TitleSlideUpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Animation slideup = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.slide_up);
            slideup.setFillAfter(true);
            title.startAnimation(slideup);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1100);
                        PanelFadeInHandler.sendMessage(new Message());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
    Handler PanelFadeInHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Animation fadeinF = AnimationUtils.loadAnimation(StartUpActivity.this, R.anim.fade_in_fast);
            fadeinF.setFillAfter(true);
            login_panel.startAnimation(fadeinF);
            subtitle.startAnimation(fadeinF);
            EnableLoginPanel();
        }
    };
}
