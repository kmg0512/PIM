package com.example.managers;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by tigri on 2016-06-07.
 */
public class SharedDataManager {
    public static SharedDataManager Inst(Context context) {
        if(inst == null)
            inst = new SharedDataManager(context);
        if(inst.context == null)
            inst.context = context;

        return inst;
    }

    public static Context getContext(){
        if(inst == null)
            return null;

        return inst.context;
    }
    private static SharedDataManager inst;


    public interface Task<T> {
        void doWith(T t);
    }

    private Context context;

    private final Object scheduleLock = new Object();
    private ScheduleItemManager scheduleItemManager;

    private GoogleApiClient googleApiClient;

    private SharedDataManager(Context context) {
        this.context = context;

        this.scheduleItemManager = null;

        this.googleApiClient = null;
    }



    public void giveScheduleTask(Task<ScheduleItemManager> task) {
        synchronized (scheduleLock) {
            if (scheduleItemManager == null)
                scheduleItemManager = ScheduleItemManager.Load(context);

            task.doWith(scheduleItemManager);
            scheduleItemManager.Save(context);
        }
    }

    public void giveScheduleTaskConst(Task<ScheduleItemManager> task) {
        synchronized (scheduleLock) {
            if (scheduleItemManager == null)
                scheduleItemManager = ScheduleItemManager.Load(context);

            task.doWith(scheduleItemManager);
        }
    }




    public synchronized void clear() {
        context = null;

        scheduleItemManager = null;
    }
}


