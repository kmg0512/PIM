package com.example.utility.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.data.ScheduleItemData;
import com.example.managers.DataManager;
import com.example.pim.MainActivity;
import com.example.utility.map.GoogleMapLocation;
import com.example.utility.net.HttpsGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Hoon on 5/13/2016.
 */
public class GoogleMapAPI {
    public interface GoogleMapAPICallBack<T> {
        void OnGet(T parameters);
    }

    public static void Init(LocationManager manager) {
        Log.d("GoogleMapAPI", "Initializing");

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LocationAAA", "lat " + location.getLatitude());
                Log.d("LocationAAA", "lng " + location.getLongitude());
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
        };

        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            manager.getLastKnownLocation()
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateScheduleItem(final ScheduleItemData data){
        // get destination name
        String dest = data.loc_destination.getName();
        if(dest == null || dest == "")
            return;
        try {
            dest = URLEncoder.encode(dest, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
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
                "key=AIzaSyAwT2mGH1pGz1RMuPfB_tKE9fF3wnpIJz0" + "&" +
                "mode=transit";

        // request
        getter.Get(url, getListener);
    }

    /**
     *
     * @param start should coordinate setted
     * @param dest should coordeinate setted
     * @param callback Time by seconds. null if cannot find.
     */
    public static void GetDeltatTimeOf(GoogleMapLocation start, GoogleMapLocation dest, final GoogleMapAPICallBack<Integer> callback) {
        // check locations are verified
        if(!start.isCoordinateSet() || !dest.isCoordinateSet()) {
            callback.OnGet(null);
            return;
        }

        // create url
        String startplaceid = start.getPlaceid();
        String destplaceid = dest.getPlaceid();
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=place_id:" + startplaceid + "&" +
                "destination=place_id" + destplaceid + "&" +
                "key=AIzaSyAwT2mGH1pGz1RMuPfB_tKE9fF3wnpIJz0" + "&" +
                "mode=transit";

        // create https getter
        HttpsGetter getter = new HttpsGetter();
        HttpsGetter.HttpsGetListener getListener = new HttpsGetter.HttpsGetListener() {
            @Override
            public void OnGet(HttpsGetter.HttpsGetResult result) {
                if(!result.success)
                    callback.OnGet(null);

                // parse json
                try {
                    JSONObject root = new JSONObject(result.result);

                    // check ok
                    if(root.getString("status") == "OK") {
                        // parse json object
                        JSONObject route = root.getJSONArray("routes").getJSONObject(0);
                        JSONObject duration = route.getJSONArray("legs").getJSONObject(0).getJSONObject("duration");

                        // create time
                        Integer time = duration.getInt("value");

                        // callback
                        callback.OnGet(time);
                    } else {
                        // callback
                        callback.OnGet(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.OnGet(null);
                }
            }
        };

        // request
        getter.Get(url, getListener);
    }

    public static void GetGeocodingsOf(String locname, final GoogleMapAPICallBack<Collection<GoogleMapLocation>> callback) {
        // change location into right format
        String loc = locname.replace(" ", "+");
        try {
            loc = URLEncoder.encode(loc, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        // create HTTPS requester
        HttpsGetter getter = new HttpsGetter();
        HttpsGetter.HttpsGetListener listener = new HttpsGetter.HttpsGetListener() {
            @Override
            public void OnGet(HttpsGetter.HttpsGetResult result) {
                // check ok
                if(!result.success) {
                    callback.OnGet(null);
                }

                // parse json
                try {
                    JSONObject root = new JSONObject(result.result);

                    // check ok
                    ArrayList<GoogleMapLocation> locations = new ArrayList<>();
                    String status = root.getString("status");
                    if(status == "OK") {
                        JSONArray resultlist = root.getJSONArray("results");

                        // for each jsonobject
                        int length = resultlist.length();
                        for (int i = 0; i < length; ++i) {
                            // parse each jsonobject
                            JSONObject obj = resultlist.getJSONObject(i);
                            String name = obj.getJSONArray("address_components").getJSONObject(0).getString("short_name");
                            double latitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double longitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            String placeid = obj.getString("place_id");

                            // create googlemaplocation
                            GoogleMapLocation loc = new GoogleMapLocation(name, latitude, longitude, placeid);
                            locations.add(loc);
                        }

                        // callback
                        callback.OnGet(locations);
                    } else if(status == "ZERO_RESULTS"){
                        // callback
                        callback.OnGet(locations);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.OnGet(null);
                }
            }
        };

        // create url
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + loc + "&" +
                "key=AIzaSyAM9tioD2rCRF5bL9QGdPv1kNlAG3_EFO4";


        // request
        getter.Get(url, listener);
    }


    public static void GetCurrentLocation(GoogleMapAPICallBack<GoogleMapLocation> callback) {


    }
}
