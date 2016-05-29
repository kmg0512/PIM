package com.example.managers;

import android.util.Log;

import com.example.data.ScheduleItemData;
import com.example.utility.jsonizer.JSONAble;
import com.example.utility.map.GoogleMapAPI;
import com.example.utility.map.GoogleMapLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        scheduleItemDatas.add(data);
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.ADD);
        }
    }
    public void removeItemData(ScheduleItemData data) {
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.REMOVED);
        }
        scheduleItemDatas.remove(data);
    }

    public void updateScheduleItem(final ScheduleItemData data){

        Log.d("GoogleMapAPI", "Updating ScheduleItem");
        GoogleMapAPI.GoogleMapAPICallBack<GoogleMapLocation> getcurrent = new GoogleMapAPI.GoogleMapAPICallBack<GoogleMapLocation>() {
            @Override
            public void OnGet(GoogleMapLocation parameters) {
                data.loc_origin = parameters;
                GoogleMapAPI.GoogleMapAPICallBack<Integer> getdeltatime = new GoogleMapAPI.GoogleMapAPICallBack<Integer>() {
                    @Override
                    public void OnGet(Integer parameters) {
                        Log.d("GoogleMapAPI", "o : " + data.loc_origin.getPlaceid() + " d : " + data.loc_destination.getPlaceid());
                        Log.d("GoogleMapAPI", "deltatime : " + parameters);
                        if(parameters == null)
                            return;

                        data.deltaTime = parameters.toString();
                        DataManager.Inst().getScheduleDataManager().notifyUpdate(data);
                    }
                };

                if(data.loc_origin == null || data.loc_destination == null) {
                    return;
                }
                GoogleMapAPI.Inst().GetDeltatTimeOf(data.loc_origin, data.loc_destination, getdeltatime);
            }
        };
        GoogleMapAPI.Inst().GetCurrentLocation(getcurrent);
    }

    // listeners
    public enum ScheduleItemUpdateType {
        ADD, CHANGE, REMOVED
    }
    public static interface ScheduleItemUpdateCallBack {
        void onUpdate(ScheduleItemData data, ScheduleItemUpdateType type);
    }

    public void notifyUpdate(ScheduleItemData data) {
        for (ScheduleItemUpdateCallBack callBack : callBacks) {
            callBack.onUpdate(data, ScheduleItemUpdateType.CHANGE);
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
    public JSONObject ToJSON() {
        JSONObject result = new JSONObject();
        Collection<JSONObject> arrays = new ArrayList<>();

        for (ScheduleItemData data : scheduleItemDatas) {
            arrays.add(data.ToJSON());
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
    public boolean FromJSON(JSONObject json) {
        try {
            JSONArray arr = json.getJSONArray("scheduleItemDatas");

            ArrayList<ScheduleItemData> datas = new ArrayList<>();
            int len = arr.length();
            for (int i = 0; i < len; ++i) {
                ScheduleItemData data = new ScheduleItemData();

                if(!data.FromJSON(arr.getJSONObject(i)))
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


    // actions
    void refreshScheduleItems() {

    }
}
