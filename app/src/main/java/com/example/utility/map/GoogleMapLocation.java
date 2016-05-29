package com.example.utility.map;

import android.support.annotation.NonNull;

import com.example.utility.jsonizer.JSONAble;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMapLocation implements JSONAble{
    @NonNull
    String name = "";

    boolean isCoordinateSet = false;

    double latitude = 0;
    double longitude = 0;

    @NonNull
    String placeid = "";

    public GoogleMapLocation() {

    }

    public GoogleMapLocation(@NonNull String name) {
        this.name = name;
    }

    public GoogleMapLocation(@NonNull String name, double latitude, double longitude, @NonNull String placeid) {
        this.name = name;
        this.isCoordinateSet = true;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeid = placeid;
    }

    // name of google map location
    public void setName(@NonNull String name) {
        this.name = name;
    }
    @NonNull
    public String getName() {
        return this.name;
    }


    // specific information set by googlemap api
    public boolean isCoordinateSet() { return isCoordinateSet; }

    public double getLatitude() {
        if(!isCoordinateSet)
            return 0;
        return latitude;
    }
    public double getLongitude() {
        if(!isCoordinateSet)
            return 0;
        return longitude;
    }

    public String getPlaceid() {
        if(!isCoordinateSet)
            return null;
        return placeid;
    }

    @Override
    public JSONObject ToJSON() {
        JSONObject result = new JSONObject();

        try {
            result.put("name", name);
            result.put("latitude", latitude);
            result.put("longitude", longitude);
            result.put("isCoordinateSet", isCoordinateSet);
            result.put("placeid", placeid);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

        return result;
    }

    @Override
    public boolean FromJSON(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.latitude = json.getDouble("latitude");
            this.longitude = json.getDouble("longitude");
            this.isCoordinateSet = json.getBoolean("isCoordinateSet");
            this.placeid = json.getString("placeid");

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
