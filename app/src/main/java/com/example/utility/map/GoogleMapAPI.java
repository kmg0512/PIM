package com.example.utility.map;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.data.ScheduleItemData;
import com.example.managers.DataManager;
import com.example.utility.net.HttpsGetter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
public class GoogleMapAPI implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public interface GoogleMapAPICallBack<T> {
        void OnGet(T parameters);
    }

    // singleton
    private static GoogleMapAPI inst;
    public static GoogleMapAPI Inst() {
        if(inst == null)
            inst = new GoogleMapAPI();
        return inst;
    }

    public static void Init(GoogleApiClient client) {
        Log.d("GoogleMapAPI", "Initializing");

    }
    private GoogleMapAPI() {

    }


    // data
    GoogleApiClient mGoogleApiClient;

    public void SetGoogleApiClient(GoogleApiClient client)
    {
        this.mGoogleApiClient = client;
    }

    public GoogleApiClient GetGoogleApiClient()
    {
        return mGoogleApiClient;
    }



    /**
     *
     * @param start should coordinate setted
     * @param dest should coordeinate setted
     * @param callback Time by seconds. null if cannot find.
     */
    public void GetDeltatTimeOf(GoogleMapLocation start, GoogleMapLocation dest, final GoogleMapAPICallBack<Integer> callback) {
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
                "destination=place_id:" + destplaceid + "&" +
                "key=AIzaSyAwT2mGH1pGz1RMuPfB_tKE9fF3wnpIJz0" + "&" +
                "mode=transit";

        Log.d("GoogleMapAPI", "Getting delta time of" + startplaceid + " " + destplaceid);

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
                    Log.d("GoogleMapAPI", "Getting deltatime : " + root.getString("status"));
                    if(root.getString("status").equals("OK")) {
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

    public void GetGeocodingsOf(String locname, final GoogleMapAPICallBack<Collection<GoogleMapLocation>> callback) {
        // change location into right format
        String loc = locname.replace(" ", "+");
        try {
            loc = URLEncoder.encode(loc, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }


        Log.d("GoogleMapAPI", "Getting geocodings of" + locname);

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
                    Log.d("GoogleMapAPI", "Getting geocodings : " + status);
                    if(status.equals("OK")) {
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
                    } else if(status.equals("ZERO_RESULTS")){
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


    public void GetCurrentLocation(final GoogleMapAPICallBack<GoogleMapLocation> callback) {
        // check success
        if(!mGoogleApiClient.isConnected())
        {
            callback.OnGet(null);
            return;
        }


        Log.d("GoogleMapAPI", "Getting current location");

        // get current location
        double lat;
        double lng;
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location == null)
            {
                callback.OnGet(null);
                return;
            }

            lat = location.getLatitude();
            lng = location.getLongitude();

        } catch (SecurityException e) {
            e.printStackTrace();
            callback.OnGet(null);
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
                    String status = root.getString("status");
                    Log.d("GoogleMapAPI", "Getting current location : " + status);
                    if(status.equals("OK")) {
                        JSONArray resultlist = root.getJSONArray("results");

                        // for each jsonobject
                        JSONObject obj = resultlist.getJSONObject(0);
                        String name = obj.getJSONArray("address_components").getJSONObject(0).getString("short_name");
                        double latitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double longitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        String placeid = obj.getString("place_id");

                        // create googlemaplocation
                        GoogleMapLocation loc = new GoogleMapLocation(name, latitude, longitude, placeid);

                        // callback
                        callback.OnGet(loc);
                    } else if(status.equals("ZERO_RESULTS")){
                        // callback
                        callback.OnGet(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.OnGet(null);
                }
            }
        };

        // url
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "latlng=" + lat + "," + lng + "&" +
                "key=AIzaSyAM9tioD2rCRF5bL9QGdPv1kNlAG3_EFO4";

        getter.Get(url, listener);
    }


    // connection info
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
