package com.example.utility.net;

import com.example.data.ScheduleItemData;
import com.example.managers.DataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Hoon on 5/13/2016.
 */
public class GoogleMapAPI {
    public static void UpdateScheduleItem(final ScheduleItemData data){
        // get destination name
        String dest = data.loc_destination.getName();
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
                    DataManager.Inst().getScheduleDataManager().notifyUpdate(data);
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

    public void UpdateGoogleMapLocation(GoogleMapLocation loc) {

    }

}
