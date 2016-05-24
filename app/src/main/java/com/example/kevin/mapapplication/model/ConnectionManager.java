package com.example.kevin.mapapplication.model;

import android.content.Context;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kevin on 5/23/16.
 */
public class ConnectionManager {

    private static final String SERVER_ADDR = "http://intime.halcyons.org:3000/api";

    private static ConnectionManager mInstance;

    private AsyncHttpClient client;

    private ConnectionManager() {
        client = new AsyncHttpClient();
    }

    public static synchronized ConnectionManager getInstance() {
        if(mInstance == null) {
            mInstance = new ConnectionManager();
            return mInstance;
        }
        return mInstance;
    }

    public void Login(Context context, String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        client.post(context, SERVER_ADDR + "/login", params,  handler);
    }

    public void Register(Context context, String username, String password, String phone, String email, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("email", email);
        client.post(context, SERVER_ADDR + "/user", params, handler);
    }


}
