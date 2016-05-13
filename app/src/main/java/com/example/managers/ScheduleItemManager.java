package com.example.managers;

import com.example.data.ScheduleItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hoon on 5/13/2016.
 */
public class ScheduleItemManager {
    private ArrayList<ScheduleItemData> scheduleItemDatas;

    // ctor, dtor
    public ScheduleItemManager() {
        scheduleItemDatas = new ArrayList<>();
    }

    public void destroy() {
        scheduleItemDatas.clear();
    }

    // info
    public int getItemsize() {
        return scheduleItemDatas.size();
    }

    public ScheduleItemData getItemData(int position) {
        if(position < 0)
            return null;
        if(scheduleItemDatas.size() <= position)
            return null;

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
    }

    public void removeItemData(ScheduleItemData data) {
        scheduleItemDatas.remove(data);
    }

    // listeners
    public void notifyUpdate(ScheduleItemData data) { }


}
