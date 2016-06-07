package com.example.managers;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.data.AlarmReceiver;
import com.example.data.ScheduleItemData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PIMAlarmService extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CLEAR_ALARM = "com.example.managers.action.CLEARALARM";
    private static final String ACTION_ADD_ALARM = "com.example.managers.action.ADDALARM";

    // TODO: Rename parameters
    private static final String EXTRA_ALARM_NAME = "com.example.managers.extra.ALARMNAME";
    private static final String EXTRA_ALARM_TIME = "com.example.managers.extra.ALARMTIME";
    private static final String EXTRA_DELTA_TIME = "com.example.managers.extra.DELTATIME";

    private final IBinder binder = new PIMAlarmBinder();

    public class PIMAlarmBinder extends Binder{
    }

    ArrayList<PendingIntent> intents;
    AlarmAdder adder;

    public PIMAlarmService() {
        //super("PIMAlarmService");
    }

    @Override
    public void onCreate() {
        Log.d("PIMAlarmService", "onCreate");
        super.onCreate();
        intents = new ArrayList<>();
        adder = new AlarmAdder(this);

        SharedDataManager.Inst(this).giveScheduleTaskConst(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {
                manager.addUpdateListener(adder);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        super.onStartCommand(intent, flags, startID);
        onHandleIntent(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("PIMAlarmService", "onDestroy");
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        for (PendingIntent intent :intents) {
            alarmManager.cancel(intent);
        }

        SharedDataManager.Inst(this).giveScheduleTaskConst(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {
                manager.removeUpdateListener(adder);
            }
        });
        adder = null;

        intents.clear();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionClearAlarm(Context context) {
        Intent intent = new Intent(context, PIMAlarmService.class);
        intent.setAction(ACTION_CLEAR_ALARM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionAddAlarm(Context context, long deltatime, String name, long after) {
        Log.d("PIMAlarmService", "Add New Alarm");
        Intent intent = new Intent(context, PIMAlarmService.class);
        intent.setAction(ACTION_ADD_ALARM);
        intent.putExtra(EXTRA_DELTA_TIME, deltatime);
        intent.putExtra(EXTRA_ALARM_NAME, name);
        intent.putExtra(EXTRA_ALARM_TIME, after);
        context.startService(intent);
    }

    //@Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLEAR_ALARM.equals(action)) {
                handleActionClearAlarm();
            } else if (ACTION_ADD_ALARM.equals(action)) {
                final long deltatime = intent.getLongExtra(EXTRA_DELTA_TIME, 0);
                final String name = intent.getStringExtra(EXTRA_ALARM_NAME);
                final long time = intent.getLongExtra(EXTRA_ALARM_TIME, 0);
                handleActionAddAlarm(deltatime, name, time);
            }
        }
    }



    public void handleActionClearAlarm() {
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        for (PendingIntent intent :intents) {
            alarmManager.cancel(intent);
        }

        Log.d("PIMAlarmService", "Clear Alarm");

        intents.clear();

    }

    public void handleActionAddAlarm(long deltatime, String name, long after) {
        if((after - deltatime) <= 0)
            return;

        Log.d("PIMAlarmService", "Add Alarm : " + name);
        Log.d("PIMAlarmService", "Alarm will be announced after : " + (after - deltatime) + " seconds");

        // create intent
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_NAME, name);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_TIME, deltatime);
        PendingIntent alarmReceiver = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // find alarm manager
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        // add intent
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (after - deltatime) * 1000, alarmReceiver);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, alarmReceiver);
        intents.add(alarmReceiver);
    }

    class AlarmAdder implements ScheduleItemManager.ScheduleItemUpdateCallBack {

        Context context;

        AlarmAdder(Context context) {
            this.context = context;
        }

        @Override
        public void onUpdate(ScheduleItemData data, ScheduleItemManager.ScheduleItemUpdateType type, ScheduleItemManager manager) {
            if(type == ScheduleItemManager.ScheduleItemUpdateType.REMOVED)
                return;

            if(data.time == null)
                return;

            //Log.d("PIMAlarmService", "current : " + GregorianCalendar.getInstance().getTimeInMillis());
            //Log.d("PIMAlarmService", "target : " + data.time.getTimeInMillis());
            long deltasecond = getDeltaSecond(data.time);

            if(deltasecond == -1)
                return;

            PIMAlarmService.startActionAddAlarm(context, data.deltaTime, data.name, deltasecond);
        }

        long getDeltaSecond(GregorianCalendar target)
        {
            Calendar current = GregorianCalendar.getInstance();

            // check year
            int yeardiff = target.get(Calendar.YEAR) - current.get(Calendar.YEAR);
            if(yeardiff >= 2 || yeardiff < 0)
                return -1;

            // check month
            int monthdiff = target.get(Calendar.MONTH) - current.get(Calendar.MONTH);
            if(monthdiff != 1 && monthdiff != 0 && monthdiff != -11)
                return -1;

            // check day
            int daydiff = target.get(Calendar.DAY_OF_MONTH) - current.get(Calendar.DAY_OF_MONTH);
            if(daydiff == 1 && monthdiff == 0) {
                return ((target.getTimeInMillis() - current.getTimeInMillis()) / 1000) + 86400;
            } else if (daydiff == 0 && monthdiff == 0) {
                return (target.getTimeInMillis() - current.getTimeInMillis()) / 1000;
            } else if (target.get(Calendar.DAY_OF_MONTH) == 1 &&
                    current.getActualMaximum(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH) &&
                    monthdiff != 0) {
                return ((target.getTimeInMillis() - current.getTimeInMillis()) / 1000) + 86400;
            }

            return -1;
        }
    }
}
