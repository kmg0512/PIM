package com.example.managers;

import android.content.Context;
import android.util.Log;

import com.example.data.SocialItemData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoon on 5/8/2016.
 */
public class DataManager {
    // singleton
    public static void Init(Context context){
        inst = new DataManager(context);
    }

    public static DataManager Inst() {
        if(inst == null)
            inst = new DataManager();

        return inst;
    }
    private static DataManager inst;

    // refer
    private Context context;

    // data
    private ScheduleItemManager scheduleItemManager;
    private ArrayList<SocialItemData> socialItemDatas;

    // ctor
    private DataManager() {
    }
    private DataManager(Context context) {
        this.context = context;
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
        if (context == null)
            return;
        Log.d("DataManager", "loading data...");

        String filename = "schedules.json";
        StringBuilder schedulesb = new StringBuilder();

        try {
            FileInputStream inputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // read to string
            String line;
            while((line = reader.readLine()) != null) {
                schedulesb.append(line).append('\n');
            }
            reader.close();

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(schedulesb.toString());
            if(!scheduleItemManager.fromJSON(obj))
                Log.d("DataManager", "Cannot make schedulemanager from json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("DataManager", "loading data done");
    };

    private void saveData() {
        if (context == null)
            return;
        Log.d("DataManager", "saving data...");

        String filename = "schedules.json";

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(scheduleItemManager.toJSON().toString(1).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("DataManager", "saving data done");
    }

    // info

    public ScheduleItemManager getScheduleDataManager() {
        return scheduleItemManager;
    }

    public List<SocialItemData> getSocialDataList() {
        return socialItemDatas;
    }




}
