package com.example.kevin.mapapplication.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 3/11/16.
 */
public class MarkerManager {

    private static MarkerManager mInstance;

    public int index;

    private JSONArray mMarkerData;

    private MarkerManager() {
        index = 0;
        mMarkerData = new JSONArray();
        try {
            InitData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject BuildData(int id, String Color, String shorttitle, String type, int reward, String DetailedDcpt) throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Id", id);
        jsonObject.put("Color", Color);
        jsonObject.put("ShortTitle", shorttitle);
        jsonObject.put("Type", type);
        jsonObject.put("Reward", reward);
        jsonObject.put("DetailedDcpt", DetailedDcpt);
        Put(jsonObject);
        return jsonObject;
    }

    public void ConfirmLatLng (String markerid, double Lat, double Lng) throws JSONException{
        Get(markerid).put("Lat", Lat);
        Get(markerid).put("Lng", Lng);
    }

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
        Put(LocationMarker);

        //////////////////TEST_ONLY(Could change to HTTP_GET from server)//////////////
        JSONObject ExampleGreen = new JSONObject(), ExampleBlue = new JSONObject(), ExampleRed = new JSONObject();
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
        Put(ExampleRed);

    }

    public void Put (JSONObject markerdetail) throws JSONException {
        mMarkerData.put(index, markerdetail);
        index ++;
    }

    public JSONObject Get(String markerid) throws JSONException{
        int markerindex = Integer.parseInt(markerid.substring(1, markerid.length()));
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
