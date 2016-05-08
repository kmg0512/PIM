package com.example.data;

/**
 * Created by Hoon on 5/6/2016.
 */
public class ScheduleItemData {
    String name;

    public GoogleMapLocation loc_origin;
    public GoogleMapLocation loc_destination;


    public ScheduleItemData() {
        name = "None";
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }



}
