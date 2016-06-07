package com.example.data;

import android.support.annotation.NonNull;

import com.example.utility.jsonizer.JSONAble;
import com.example.utility.map.GoogleMapLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.GregorianCalendar;

public class ScheduleItemData implements JSONAble{
    @NonNull
    public String name;
    public GregorianCalendar time;
    @NonNull
    public String comment;

    public GoogleMapLocation loc_origin;
    public GoogleMapLocation loc_destination;

    public long deltaTime;

    public ScheduleItemData() {
        name = "";
        time = null;
        comment = "";
        deltaTime = 0;
        loc_origin = new GoogleMapLocation();
        loc_destination = new GoogleMapLocation();
    }

    // JSON
    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        try {
            result.put("name", name);
            result.put("comment", comment);
            result.put("loc_origin", loc_origin.toJSON());
            result.put("loc_destination", loc_destination.toJSON());
            result.put("deltaTime", deltaTime);

            if(time == null) {
                result.put("time_setted", false);
                result.put("time_year", 0);
                result.put("time_month", 0);
                result.put("time_day", 0);
                result.put("time_hour", 0);
                result.put("time_minute", 0);
            } else {
                result.put("time_setted", true);
                result.put("time_year", time.get(GregorianCalendar.YEAR));
                result.put("time_month", time.get(GregorianCalendar.MONTH));
                result.put("time_day", time.get(GregorianCalendar.DAY_OF_MONTH));
                result.put("time_hour", time.get(GregorianCalendar.HOUR_OF_DAY));
                result.put("time_minute", time.get(GregorianCalendar.MINUTE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

        return result;
    }

    @Override
    public boolean fromJSON(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.comment = json.getString("comment");
            this.deltaTime = json.getLong("deltaTime");

            this.loc_origin = new GoogleMapLocation();
            this.loc_origin.fromJSON(json.getJSONObject("loc_origin"));
            this.loc_destination = new GoogleMapLocation();
            this.loc_destination.fromJSON(json.getJSONObject("loc_destination"));


            if(json.getBoolean("time_setted")) {
                int time_year = json.getInt("time_year");
                int time_month = json.getInt("time_month");
                int time_day = json.getInt("time_day");
                int time_hour = json.getInt("time_hour");
                int time_minute = json.getInt("time_minute");

                this.time = new GregorianCalendar(time_year, time_month,
                        time_day, time_hour, time_minute);
            } else {
                this.time = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
