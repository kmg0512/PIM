package com.example.data;

/**
 * Created by Hoon on 5/6/2016.
 */
public class GoogleMapLocation {
    String majorname;
    String minorname;


    public GoogleMapLocation() {
        majorname = "Not Assigned";
        minorname = "";
    }

    public void setMajorName(String major) {
        this.majorname = major;
    }

    public String getMajorName() {
        return this.majorname;
    }

    public void setMinorName(String minor) {
        this.minorname = minor;
    }

    public String getMinorName() {
        return this.minorname;
    }

}
