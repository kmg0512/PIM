package com.example.managers;

import com.example.data.ScheduleItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores all schedule item data.
 *
 * add listener to this to notify data changes.
 */
public class ScheduleItemManager {
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

}
