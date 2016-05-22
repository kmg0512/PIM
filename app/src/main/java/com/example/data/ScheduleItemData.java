package com.example.data;

import android.util.JsonWriter;

import com.example.utility.jsonizer.JSONAble;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Hoon on 5/6/2016.
 */
public class ScheduleItemData implements JSONAble{
    public String name;
    public String time;
    public String comment;

    public GoogleMapLocation loc_origin;
    public GoogleMapLocation loc_destination;

    public String deltaTime;

    public ScheduleItemData() {
        loc_origin = new GoogleMapLocation();
        loc_destination = new GoogleMapLocation();
    }

    // JSON
    @Override
    public JSONObject ToJSON() {
        JSONObject result = new JSONObject();

        try {
            result.put("name", name);
        } catch (JSONException e) {
            return new JSONObject();
        }

        return result;
    }

    @Override
    public boolean FromJSON(JSONObject json) {
        try {
            this.name = json.getString("name");
        } catch (JSONException e) {
            return false;
        }

        return true;
    }
}
