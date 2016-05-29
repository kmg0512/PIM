package com.example.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.utility.jsonizer.JSONAble;
import com.example.utility.map.GoogleMapLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleItemData implements JSONAble{
    @NonNull
    public String name;
    @NonNull
    public String time;
    @NonNull
    public String comment;

    public GoogleMapLocation loc_origin;
    public GoogleMapLocation loc_destination;

    @NonNull
    public String deltaTime;

    public ScheduleItemData() {
        name = "";
        time = "";
        comment = "";
        deltaTime = "";
        loc_origin = new GoogleMapLocation();
        loc_destination = new GoogleMapLocation();
    }

    // JSON
    @Override
    public JSONObject ToJSON() {
        JSONObject result = new JSONObject();

        try {
            result.put("name", name);
            result.put("time", time);
            result.put("comment", comment);
            result.put("loc_origin", loc_origin.ToJSON());
            result.put("loc_destination", loc_destination.ToJSON());
            result.put("deltaTime", deltaTime);
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
            this.time = json.getString("time");
            this.comment = json.getString("comment");
            this.deltaTime = json.getString("deltaTime");

            this.loc_origin = new GoogleMapLocation();
            this.loc_origin.FromJSON(json.getJSONObject("loc_origin"));
            this.loc_destination = new GoogleMapLocation();
            this.loc_destination.FromJSON(json.getJSONObject("loc_destination"));

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
