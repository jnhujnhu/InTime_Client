package com.example.kevin.mapapplication.model;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Kevin on 3/11/16.
 */
public class MarkerManager {

    private static MarkerManager mInstance;

    private JSONArray mMarkerData;

    private int locationmarkerid;

    private MarkerManager() {
        locationmarkerid = 0;
        mMarkerData = new JSONArray();
    }

    public void setLocationMarkerid(String locationMarkerid) {
        locationmarkerid = Integer.parseInt(locationMarkerid.substring(1, locationMarkerid.length()));
    }

    public String getLocationMarkerid() {
        return "m" + locationmarkerid;
    }


    public void Put (String markerid, JSONObject markerdetail) throws JSONException {
        int markerindex = Integer.parseInt(markerid.substring(1, markerid.length()));
        mMarkerData.put(markerindex, markerdetail);
    }

    public JSONObject Get(String markerid) throws JSONException{
        int markerindex = Integer.parseInt(markerid.substring(1, markerid.length()));
        if (markerindex == locationmarkerid) {
            return null;
        }
        return (JSONObject) mMarkerData.get(markerindex);
    }

    public static synchronized MarkerManager getInstance() {
        if(mInstance == null) {
            mInstance = new MarkerManager();
            return mInstance;
        }
        return mInstance;
    }

}
