package com.example.data;

/**
 * Created by Hoon on 5/6/2016.
 */
public class ScheduleItemData {
    String name;

    public GoogleMapLocation loc_origin;
    public GoogleMapLocation loc_destination;

    public String deltaTime;

    public ScheduleItemData() {
        name = "None";
        loc_origin = new GoogleMapLocation();
        loc_destination = new GoogleMapLocation();
    }


    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }



}
