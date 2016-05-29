package com.example.kevin.mapapplication.utils;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public abstract class AsyncJSONHttpResponseHandler extends AsyncHttpResponseHandler {
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {

            onSuccessWithJSON(statusCode, headers, responseBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        try {
            if (statusCode == 0) {
                onFailureWithJSON(statusCode, headers, null, error.getMessage());
            }
            else {
                JSONObject res = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                onFailureWithJSON(statusCode, headers, res, res.getString("error"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onSuccessWithJSON(int statusCode, Header[] headers, byte[] responseBody) throws JSONException;
    public abstract void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException;
}