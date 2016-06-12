package com.example.kevin.mapapplication.model;


import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

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

    public void GetTemplateDetail(String tid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + String.format("/templates/%s", tid), null, handler);
    }

    public void DeleteTemplate(String tid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.delete(SERVER_ADDR + String.format("/templates/%s", tid), null, handler);
    }

    public void CreateTemplate(String type, String title, String content, String category, int price, int number, long time, String place, double latitude, double longitude, Boolean isPrivate, String token, AsyncHttpResponseHandler handler) {
        try {
            JSONObject params = new JSONObject();
            params.put("type", type);
            params.put("title", title);
            params.put("content", content);
            params.put("category", category);
            params.put("points", price);
            params.put("number", number);
            params.put("place", place);
            params.put("time", time);
            if(!(latitude == 200 && longitude == 200)) {
                JSONObject coordinate = new JSONObject();
                coordinate.put("latitude", latitude);
                coordinate.put("longitude", longitude);
                params.put("coordinate", coordinate);
            }
            params.put("isPrivate", isPrivate);

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.addHeader("x-access-token", token);
            client.post(null, SERVER_ADDR + "/templates", entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateOrder(String type, String title, String content, String category, int price, int number, long time, String place, double latitude, double longitude, Boolean isPrivate, String token, AsyncHttpResponseHandler handler) {
        try {
            JSONObject params = new JSONObject();
            params.put("type", type);
            params.put("title", title);
            params.put("content", content);
            params.put("category", category);
            params.put("points", price);
            params.put("number", number);
            params.put("place", place);
            params.put("time", time);
            JSONObject coordinate = new JSONObject();
            coordinate.put("latitude", latitude);
            coordinate.put("longitude", longitude);
            params.put("coordinate", coordinate);
            params.put("isPrivate", isPrivate);

            StringEntity entity = new StringEntity(params.toString(),"utf-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.addHeader("x-access-token", token);
            client.post(null, SERVER_ADDR + "/orders", entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ModifyTemplate(String tid, String type, String title, String content, String category, int price, int number, long time, String place, double latitude, double longitude, Boolean isPrivate, String token, AsyncHttpResponseHandler handler) {
        try {
            JSONObject params = new JSONObject();
            params.put("type", type);
            params.put("title", title);
            params.put("content", content);
            params.put("category", category);
            params.put("points", price);
            params.put("number", number);
            params.put("place", place);
            params.put("time", time);
            if(!(latitude == 200 && longitude == 200)) {
                JSONObject coordinate = new JSONObject();
                coordinate.put("latitude", latitude);
                coordinate.put("longitude", longitude);
                params.put("coordinate", coordinate);
            }
            params.put("isPrivate", isPrivate);

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.addHeader("x-access-token", token);
            client.put(null, SERVER_ADDR + String.format("/templates/%s", tid), entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ModifyOrder(String oid, String type, String title, String content, String category, int price, int number, long time, String place, double latitude, double longitude, Boolean isPrivate, String token, AsyncHttpResponseHandler handler) {
        try {
            JSONObject params = new JSONObject();
            params.put("type", type);
            params.put("title", title);
            params.put("content", content);
            params.put("category", category);
            params.put("points", price);
            params.put("number", number);
            params.put("place", place);
            params.put("time", time);
            JSONObject coordinate = new JSONObject();
            coordinate.put("latitude", latitude);
            coordinate.put("longitude", longitude);
            params.put("coordinate", coordinate);
            params.put("isPrivate", isPrivate);

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.addHeader("x-access-token", token);
            client.put(null, SERVER_ADDR + String.format("/orders/%s", oid), entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetOrderList(String uid, String status, String keyword, String acceptUser, boolean notExpired, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + String.format("/orders?uid=%s&status=%s&title_or_content_like=%s&accept_users_contains=%s&time_gte_now=%s", uid, status, keyword, acceptUser, notExpired ? "true" : "false"), null, handler);
    }

    public void GetDefaultOrdersList(String token, AsyncHttpResponseHandler handler) {
        GetOrderList("", "waiting", "", "", true, token, handler);
    }

    public void GetGeocodingPlace(LatLng coordinate, AsyncHttpResponseHandler handler) {
        client.get(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=AIzaSyD3e_Fju-Zpocen-WIW7-PWY8YNKczIgwA", coordinate.latitude, coordinate.longitude), null, handler);
    }

    public void GetOrderDetail(String oid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.get(SERVER_ADDR + String.format("/orders/%s", oid), null, handler);
    }

    public void SetOrderStatus(String oid, String status, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.put(SERVER_ADDR + String.format("/orders/%s", oid), new RequestParams("status", status), handler);
    }

    public void AcceptOrder(String oid, String uid, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.post(SERVER_ADDR + String.format("/orders/%s/accept_users/%s", oid, uid), null, handler);
    }

    public void SetOrderUserStatus(String oid, String uid, String status, String token, AsyncHttpResponseHandler handler) {
        client.addHeader("x-access-token", token);
        client.put(SERVER_ADDR + String.format("/orders/%s/accept_users/%s", oid, uid), new RequestParams("status", status), handler);
    }
}
