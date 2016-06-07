package com.example.managers;

import android.content.Context;
import android.util.Log;

import com.example.data.ScheduleItemData;
import com.example.utility.jsonizer.JSONAble;
import com.example.utility.map.GoogleMapAPI;
import com.example.utility.map.GoogleMapLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class stores all schedule item data.
 *
 * add listener to this to notify data changes.
 */
public class ScheduleItemManager implements JSONAble {
    private ArrayList<ScheduleItemData> scheduleItemDatas;
    private ArrayList<ScheduleItemUpdateCallBack> callBacks;

    // ctor, dtor
    public ScheduleItemManager() {
        scheduleItemDatas = new ArrayList<>();
        callBacks = new ArrayList<>();
    }

    public void destroy() {
        scheduleItemDatas.clear();
        callBacks.clear();
    }

    // info
    public int getItemsize() {
        return scheduleItemDatas.size();
    }

    public ScheduleItemData getItemData(int position) {
        return scheduleItemDatas.get(position);
    }

    public int getIndexof(ScheduleItemData data) {
        for (int indx = 0; indx < scheduleItemDatas.size(); ++indx) {
            if(scheduleItemDatas.get(indx) == data)
                return indx;
        }

        return -1;
    }

    // data manipulation
    public void addItemData(ScheduleItemData data) {
        Log.d("ScheduleItemManager", "Add ScheduleItem");
        scheduleItemDatas.add(data);
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.ADD, this);
        }
    }
    public void removeItemData(ScheduleItemData data) {
        Log.d("ScheduleItemManager", "Remove ScheduleItem");
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.REMOVED, this);
        }
        scheduleItemDatas.remove(data);
    }

    public void updateScheduleItem(final ScheduleItemData data){
        Log.d("ScheduleItemManager", "Updating ScheduleItem");
        GoogleMapAPI.GoogleMapAPICallBack<GoogleMapLocation> getcurrent = new GoogleMapAPI.GoogleMapAPICallBack<GoogleMapLocation>() {
            @Override
            public void OnGet(GoogleMapLocation parameters) {
                data.loc_origin = parameters;
                GoogleMapAPI.GoogleMapAPICallBack<Long> getdeltatime = new GoogleMapAPI.GoogleMapAPICallBack<Long>() {
                    @Override
                    public void OnGet(Long parameters) {
                        Log.d("ScheduleItemManager", "o : " + data.loc_origin.getPlaceid() + " d : " + data.loc_destination.getPlaceid());
                        Log.d("ScheduleItemManager", "deltatime : " + parameters);
                        if(parameters == null)
                            return;

                        data.deltaTime = parameters;
                        notifyUpdate(data);
                    }
                };

                if(data.loc_origin == null || data.loc_destination == null) {
                    return;
                }

                GoogleMapAPI.Inst().getDeltatTimeOf(data.loc_origin, data.loc_destination, getdeltatime);
            }
        };
        GoogleMapAPI.Inst().getCurrentLocation(getcurrent);
    }

    // listeners
    public enum ScheduleItemUpdateType {
        ADD, CHANGE, REMOVED
    }
    public static interface ScheduleItemUpdateCallBack {
        void onUpdate(ScheduleItemData data, ScheduleItemUpdateType type, ScheduleItemManager manager);
    }

    public void notifyUpdate(ScheduleItemData data) {
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.CHANGE, this);
        }
    }
    public void addUpdateListener(ScheduleItemUpdateCallBack callBack) {
        callBacks.add(callBack);
    }
    public void removeUpdateListener(ScheduleItemUpdateCallBack callBack) {
        callBacks.remove(callBack);
    }



    // JSON
    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        Collection<JSONObject> arrays = new ArrayList<>();

        for (ScheduleItemData data : scheduleItemDatas) {
            arrays.add(data.toJSON());
        }

        JSONArray arr = new JSONArray(arrays);
        try {
            result.put("scheduleItemDatas", arr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

        return result;
    }

    @Override
    public boolean fromJSON(JSONObject json) {
        try {
            JSONArray arr = json.getJSONArray("scheduleItemDatas");

            ArrayList<ScheduleItemData> datas = new ArrayList<>();
            int len = arr.length();
            for (int i = 0; i < len; ++i) {
                ScheduleItemData data = new ScheduleItemData();

                if(!data.fromJSON(arr.getJSONObject(i)))
                    return false;

                datas.add(data);
            }

            scheduleItemDatas = datas;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static ScheduleItemManager Load(Context context)
    {
        ScheduleItemManager result = new ScheduleItemManager();

        String filename = "schedules.json";
        Log.d("ScheduleItemManager", "Loading from :  " + filename);

        StringBuilder stringbuilder = new StringBuilder();

        // read json
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // read to string
            String line;
            while((line = reader.readLine()) != null) {
                stringbuilder.append(line).append('\n');
            }
            reader.close();

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // make json to object
        try {
            JSONObject obj = new JSONObject(stringbuilder.toString());
            if(!result.fromJSON(obj))
                Log.d("ScheduleItemManager", "Cannot make schedule item manager from json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("ScheduleItemManager", "Loading Complete");

        return result;
    }

    public void Save(Context context)
    {
        String filename = "schedules.json";
        Log.d("ScheduleItemManager", "Saving to :  " + filename);

        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(toJSON().toString(0).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ScheduleItemManager", "Saving Complete");
    }
}
