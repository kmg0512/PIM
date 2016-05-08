package com.example.managers;

import com.example.data.ScheduleItemData;
import com.example.data.SocialItemData;

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


}
