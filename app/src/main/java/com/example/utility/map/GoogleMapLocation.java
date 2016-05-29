package com.example.utility.map;

/**
 * Created by Hoon on 5/6/2016.
 */
public class GoogleMapLocation {
    String name = "";

    boolean isCoordinateSet = false;

    double latitude = 0;
    double longitude = 0;

    String placeid = "";

    public GoogleMapLocation() {

    }

    public GoogleMapLocation(String name) {

    }

    public GoogleMapLocation(String name, double latitude, double longitude, String placeid) {

        this.isCoordinateSet = true;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeid = placeid;
    }

    // name of google map location
    public void setName(String major) {
        this.name = major;
    }
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


}
