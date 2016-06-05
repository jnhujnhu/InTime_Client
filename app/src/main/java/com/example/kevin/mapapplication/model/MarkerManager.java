package com.example.kevin.mapapplication.model;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kevin on 3/11/16.
 */
public class MarkerManager {

    private static MarkerManager mInstance;

    private Map<Integer, Marker> mMarkerMap;

    public int index;

    private JSONArray mMarkerData;


    private MarkerManager() {
        index = 1;
        mMarkerData = new JSONArray();
        mMarkerMap = new HashMap<>();
        try {
            InitData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject BuildData(int id,String uid,  String Type, String Title, String Category, int Points, int Number,  String Content, String Time, boolean isPrivate) throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Id", id);
        jsonObject.put("uid", uid);
        jsonObject.put("type", Type);
        jsonObject.put("title", Title);
        jsonObject.put("category", Category);
        jsonObject.put("points", Points);
        jsonObject.put("number", Number);
        jsonObject.put("content", Content);
        jsonObject.put("time", Time);
        jsonObject.put("isPrivate", isPrivate);
        //Put(jsonObject);
        return jsonObject;
    }

    /*public void ConfirmLatLng (String markerid, double Lat, double Lng, String Place) throws JSONException{
        JSONObject coordinate = new JSONObject();
        coordinate.put("longitude", Lng);
        coordinate.put("latitude", Lat);
        Get(markerid).put("coordinate", coordinate);
        Get(markerid).put("place", Place);
    }*/

    public int getID() {
        return index;
    }

    public void Restore() {
        mMarkerData = new JSONArray();
        index = 0;
        try {
            InitData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void InitData() throws JSONException{
        JSONObject LocationMarker = new JSONObject();

        LocationMarker.put("Id", 0);
        LocationMarker.put("Dcpt", "Your Location");
        mMarkerData.put(index, LocationMarker);

        //////////////////TEST_ONLY(Could change to HTTP_GET from server)//////////////
        /*JSONObject ExampleGreen = new JSONObject(), ExampleBlue = new JSONObject(), ExampleRed = new JSONObject();
        ExampleGreen.put("Id", 1);
        ExampleGreen.put("Color", "Green");
        ExampleGreen.put("Lat", 31.1925720582);
        ExampleGreen.put("Lng", 121.593649797);
        ExampleGreen.put("ShortTitle", "Move Bricks");
        ExampleGreen.put("Type", "Physical Work");
        ExampleGreen.put("Reward", 12);
        ExampleGreen.put("DetailedDcpt", "I am strong, I can move bricks.");
        Put(ExampleGreen);

        ExampleBlue.put("Id", 2);
        ExampleBlue.put("Color", "Blue");
        ExampleBlue.put("Lat", 31.1939948907);
        ExampleBlue.put("Lng", 121.593390963);
        ExampleBlue.put("ShortTitle", "Ground Sweep");
        ExampleBlue.put("Type", "Volunteers");
        ExampleBlue.put("Reward", 30);
        ExampleBlue.put("DetailedDcpt", "We need several people to clean the Ren Min Square.");
        Put(ExampleBlue);

        ExampleRed.put("Id", 3);
        ExampleRed.put("Color", "Red");
        ExampleRed.put("Lat", 31.1933948907);
        ExampleRed.put("Lng", 121.593689797);
        ExampleRed.put("ShortTitle", "Give me a ride");
        ExampleRed.put("Type", "Car Service");
        ExampleRed.put("Reward", 100);
        ExampleRed.put("DetailedDcpt", "I am so nervous, I need to go to Hangzhou this weekend, but my car broke down. Could any" +
                "one give me a ride?");
        Put(ExampleRed);*/

    }

    public void Put (Marker marker, JSONObject markerdetail) throws JSONException {
        mMarkerData.put(index, markerdetail);
        mMarkerMap.put(index, marker);
        index ++;
    }


    public JSONObject Get(String markerid) {
        int markerindex = Integer.parseInt(markerid.substring(1, markerid.length()));
        return mMarkerData.optJSONObject(markerindex);
    }

    public Marker GetMarker(String markerid) {
        int markerindex = Integer.parseInt(markerid.substring(1, markerid.length()));
        return  mMarkerMap.get(markerindex);
    }

    public static synchronized MarkerManager getInstance() {
        if(mInstance == null) {
            mInstance = new MarkerManager();
            return mInstance;
        }
        return mInstance;
    }

}
