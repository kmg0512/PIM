package com.example.managers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.data.ScheduleItemData;

/**
 * Created by tigri on 2016-06-07.
 */
public class BackgroundManager extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("BackgroundManager", "Updating Schedule Item");

        PIMAlarmService.startActionClearAlarm(context);

        SharedDataManager.Inst(context).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {
                int length = manager.getItemsize();

                manager.addUpdateListener(new AlarmAdder((context)));

                // update schedule item
                for(int i = 0; i < length; ++i)
                {
                    manager.updateScheduleItem(manager.getItemData(i));
                }
            }
        });
    }

    class AlarmAdder implements ScheduleItemManager.ScheduleItemUpdateCallBack {

        Context context;

        AlarmAdder(Context context) {
            this.context = context;
        }

        @Override
        public void onUpdate(ScheduleItemData data, ScheduleItemManager.ScheduleItemUpdateType type, ScheduleItemManager manager) {
            Log.d("BackgroundManager", "Set New Alarm");

            PIMAlarmService.startActionAddAlarm(context, 10000, "ASDF", 100);
        }
    }
}
