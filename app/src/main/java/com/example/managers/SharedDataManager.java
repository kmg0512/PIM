package com.example.managers;

import android.content.Context;

/**
 * Created by tigri on 2016-06-07.
 */
public class SharedDataManager {
    public synchronized static SharedDataManager Inst(Context context) {
        if(inst == null)
            inst = new SharedDataManager(context);
        return inst;
    }

    private static SharedDataManager inst;


    public interface Task<T> {
        void doWith(T t);
    }

    private Context context;

    private ScheduleItemManager scheduleItemManager;

    private SharedDataManager(Context context) {
        this.context = context;
    }


    public synchronized void giveTask(Task<ScheduleItemManager> task) {
        if(scheduleItemManager == null)
            scheduleItemManager = ScheduleItemManager.Load(context);

        task.doWith(scheduleItemManager);
        scheduleItemManager.Save(context);
    }


    public synchronized void clear() {
        context = null;

        scheduleItemManager = null;
    }
}


