package com.example.kevin.mapapplication.ui.userinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserDetailActivity extends AppCompatActivity {

    private static int ActivityCount = 0;
    public static final int RESULT_CODE = 238;

    private EditText username;
    private EditText password;
    private EditText repassword;
    private EditText phone;
    private EditText email;
    private Button modify;

    private ProgressBar loading;

    private SharedPreferences userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCount++;
        if(ActivityCount > 1)
            finish() ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        username = (EditText) findViewById(R.id.user_detail_username);
        password = (EditText) findViewById(R.id.user_detail_password);
        repassword = (EditText) findViewById(R.id.user_detail_repassword);
        email = (EditText) findViewById(R.id.user_detail_email);
        phone = (EditText) findViewById(R.id.user_detail_phone);

        loading = (ProgressBar) findViewById(R.id.user_detail_loading);

        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);

        //Toast.makeText(UserDetailActivity.this, "Error!", Toast.LENGTH_LONG).show();

        GetUserInfo();

        modify = (Button) findViewById(R.id.user_detail_modify_btn);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!password.getText().toString().equals(repassword.getText().toString())) {
                    Toast.makeText(UserDetailActivity.this, "Password and Re-password not match.", Toast.LENGTH_LONG).show();
                }
                else {

                    if(password.getText().toString().equals("")) {
                        ModifyUserInfo("");
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
                        final AlertDialog dialog = builder.setTitle("Please enter the OLD password")
                                .setCancelable(false)
                                .setView(R.layout.dialog_input_old_password)
                                .show();
                        View oldpassword_dialog = dialog.findViewById(R.id.user_detail_oldpassword_dialog);
                        final EditText oldpassword = (EditText) oldpassword_dialog.findViewById(R.id.user_detail_dialog_oldpassword);
                        Button confirm = (Button) oldpassword_dialog.findViewById(R.id.user_detail_dialog_confirm_btn);
                        Button cancel = (Button) oldpassword_dialog.findViewById(R.id.user_detail_dialog_cancel_btn);

                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ModifyUserInfo(oldpassword.getText().toString());
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(oldpassword.getWindowToken(), 0);
                                dialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });

    }

    private void ModifyUserInfo(String oldPassword) {
        loading.setVisibility(View.VISIBLE);
        String s_username = username.getText().toString();
        if(!s_username.equals(userinfo.getString("username", null))){
        }

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers,  JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                SharedPreferences.Editor editor = userinfo.edit();
                editor.putString("username", username.getText().toString());
                if (!password.getText().toString().equals("")) {
                    editor.putString("password", password.getText().toString());
                }
                editor.apply();
                Toast.makeText(UserDetailActivity.this, "Modification success.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(UserDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };

        ConnectionManager.getInstance().ModifyUserInfo(userinfo.getString("token", null), userinfo.getString("uid", null), s_username, oldPassword, password.getText().toString(), phone.getText().toString(), email.getText().toString(), handler);

    }

    private void GetUserInfo(){

        loading.setVisibility(View.VISIBLE);

        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers,  JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                username.setText(res.optString("username"));
                email.setText(res.optString("email"));
                phone.setText(res.optString("phone"));
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(UserDetailActivity.this, error, Toast.LENGTH_LONG).show();

            }
        };

        ConnectionManager.getInstance().GetUserInfo(userinfo.getString("uid", null), userinfo.getString("token", null), handler);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void onDestroy() {
        ActivityCount --;
        super.onDestroy();
    }
}
