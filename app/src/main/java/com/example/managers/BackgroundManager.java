package com.example.managers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.data.ScheduleItemData;

import java.util.Calendar;

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

                // update schedule item
                for(int i = 0; i < length; ++i)
                {
                    manager.updateScheduleItem(manager.getItemData(i));
                }
            }
        });
    }


}
