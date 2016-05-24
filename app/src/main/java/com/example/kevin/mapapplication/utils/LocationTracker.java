package com.example.kevin.mapapplication.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Kevin on 2/27/16.
 */
public class LocationTracker implements LocationListener {

    private Context mContext;

    private LocationManager mLocationManager;


    private double Latitude;

    private double Longtitude;

    public LocationTracker(Context context) {
        mContext = context;
        getLocation();
    }

    public LatLng getCurrentLatlng() {
        return new LatLng(Latitude, Longtitude);
    }

    private void getLocation(){
        Location location;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    Latitude = location.getLatitude();
                    Longtitude = location.getLongitude();
                    Log.i("Location", "From GPS");
                }
                else {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null) {
                        Latitude = location.getLatitude();
                        Longtitude = location.getLongitude();
                        Log.i("Location", "From Network");
                    }
                }
            }catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        else if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null) {
                    Latitude = location.getLatitude();
                    Longtitude = location.getLongitude();
                    Log.i("Location", "From Network");
                }
            }catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        else {
            Log.i("Location", "Error!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
