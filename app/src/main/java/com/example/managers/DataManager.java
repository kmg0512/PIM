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
    private ScheduleItemManager scheduleItemManager;
    private ArrayList<SocialItemData> socialItemDatas;

    // ctor
    private DataManager() {
    }

    // android lifecycle
    public void onCreate() {
        scheduleItemManager = new ScheduleItemManager();
        socialItemDatas = new ArrayList<>();

        loadData();
    }

    public void onDestroy() {
        saveData();

        scheduleItemManager.destroy();
        socialItemDatas.clear();
    }


    // load / save
    private void loadData() {

    };

    private void saveData() {

    }

    // info

    public ScheduleItemManager getScheduleDataManager() {
        return scheduleItemManager;
    }

    public List<SocialItemData> getSocialDataList() {
        return socialItemDatas;
    }




}
