package com.example.kevin.mapapplication.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

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

    public void Login(String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        client.post(SERVER_ADDR + "/login", params,  handler);
    }

    public void Register(String username, String password, String phone, String email, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("email", email);
        client.post(SERVER_ADDR + "/users", params, handler);
    }

    public void ModifyUserInfo(String token, String uid, String username, String oldpassword, String password, String phone, String email, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("oldPassword", oldpassword);
        params.put("phone", phone);
        params.put("email", email);
        client.addHeader("x-access-token", token);
        client.put(SERVER_ADDR + "/users/" + uid, params, handler);
    }

    public void GetUserInfo(String uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + "/users/" + uid, null, handler);
    }

    public void GetFriendsList(String uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + "/users/" + uid + "/friends", null, handler);
    }

    public void SearchUser(String token, String keyword, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + "/users?username_like=" + keyword, null, handler);
    }

    public void PostPromotionCode(String uid, String promotionCode, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.post(SERVER_ADDR + String.format("/users/%s/balance/promotion/%s", uid, promotionCode), null, handler);
    }

    public void PostRegToken(String uid, String regToken, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.post(SERVER_ADDR + String.format("/users/%s/reg_tokens/%s", uid, regToken), null, handler);
    }

    public void DeleteRegToken(String uid, String regToken, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.delete(SERVER_ADDR + String.format("/users/%s/reg_tokens/%s", uid, regToken), null, handler);
    }

    public void MarkNotification(String nid, Boolean read, String token, AsyncHttpResponseHandler handler) {
        try {
            JSONObject params = new JSONObject();
            params.put("read", read);
            StringEntity entity = new StringEntity(params.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.addHeader("x-access-token", token);
            client.put(null, SERVER_ADDR + String.format("/notifications/%s", nid), entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetNotificationList(String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + "/notifications", null, handler);
    }

    public void CancelFriendRequest(String m_uid, String f_uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.delete(SERVER_ADDR + String.format("/users/%s/friends/%s", m_uid, f_uid), null, handler);
    }

    public void AddOrAcceptFriendRequest(String m_uid, String f_uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.post(SERVER_ADDR + String.format("/users/%s/friends/%s", m_uid, f_uid), null, handler);
    }

    public void GetTemplateList(String uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + String.format("/templates?uid=%s", uid), null, handler);
    }
}
