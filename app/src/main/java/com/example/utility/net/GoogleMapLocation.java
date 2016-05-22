package com.example.utility.net;

/**
 * Created by Hoon on 5/6/2016.
 */
public class GoogleMapLocation {
    String majorname = "";

    boolean isLocationSet = false;
    String latitude = "";
    String longitude = "";

    public GoogleMapLocation() {
        majorname = "";
    }

    public void setName(String major) {
        this.majorname = major;
    }
    public String getName() {
        return this.majorname;
    }

    public void setLocation(String latitude, String longtitude)
    {
        if(latitude == null || longtitude == null
                || latitude == "" || longtitude == "")
        {
            this.isLocationSet = false;
            this.latitude = "";
            this.longitude = "";
        }

        this.isLocationSet = true;
        this.latitude = latitude;
        this.longitude = longtitude;
    }
    public boolean isLocationSet() { return isLocationSet; }

    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }

}
