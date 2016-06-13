package com.example.kevin.mapapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String PROJECT_NUMBER = "788165761397";
    private static final String[] TOPICS = {"global"};

    private  SharedPreferences userinfo;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            userinfo.edit().putString("regToken", token).apply();

            sendRegistrationToServer(token);

            subscribeTopics(token);

            userinfo.edit().putBoolean("sentTokenToServer", true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            userinfo.edit().putBoolean("sentTokenToServer", false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {
        ConnectionManager.getInstance().PostRegToken(userinfo.getString("uid", null), token, userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {

            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {

            }
        });
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}
