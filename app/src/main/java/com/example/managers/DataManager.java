package com.example.managers;

import android.util.JsonReader;
import android.util.Log;

import com.example.data.ScheduleItemData;
import com.example.data.SocialItemData;
import com.example.utility.net.HttpsGetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoon on 5/8/2016.
 */
public class DataManager {
    // singleton
    public static DataManager Inst() {
        if(inst == null)
            inst = new DataManager();

        return inst;
    }

    private static DataManager inst;

    // data
    private ArrayList<ScheduleItemData> scheduleItemDatas;
    private ArrayList<SocialItemData> socialItemDatas;

    // ctor
    private DataManager() {
        scheduleItemDatas = new ArrayList<>();
        socialItemDatas = new ArrayList<>();
    }


    // load / save
    public void loadData() {

    };

    public void saveData() {

    }

    // data manipulation

    /**
     * get schedule item data list
     * @return sorted schedule list
     */
    public List<ScheduleItemData> getScheduleDataList() {
        return scheduleItemDatas;
    }

    public List<SocialItemData> getSocialDataList() {
        return socialItemDatas;
    }

    public void RequestDeltaTimeUpdate(final ScheduleItemData data){
        // get destination name
        String dest = data.loc_destination.getMajorName();
        if(dest == null || dest == "")
            return;
        try {
            dest = URLEncoder.encode(dest, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // create https getter
        HttpsGetter getter = new HttpsGetter();
        HttpsGetter.HttpsGetListener getListener = new HttpsGetter.HttpsGetListener() {
            @Override
            public void OnGet(HttpsGetter.HttpsGetResult result) {
                if(!result.success)
                    return;

                // parse result
                try {
                    JSONObject root = new JSONObject(result.result);
                    JSONObject route = root.getJSONArray("routes").getJSONObject(0);
                    JSONObject duration = route.getJSONArray("legs").getJSONObject(0).getJSONObject("duration");


                    data.deltaTime = duration.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // url
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=%EA%B3%A0%EB%A0%A4%EB%8C%80%ED%95%99%EA%B5%90&" +
                "destination=" + dest + "&" +
                "key=AIzaSyAwT2mGH1pGz1RMuPfB_tKE9fF3wnpIJz0&" +
                "mode=transit";

        // request
        getter.Get(url, getListener);
    }


}
