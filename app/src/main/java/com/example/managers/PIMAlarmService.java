package com.example.managers;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.data.AlarmReceiver;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PIMAlarmService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CLEAR_ALARM = "com.example.managers.action.CLEARALARM";
    private static final String ACTION_ADD_ALARM = "com.example.managers.action.ADDALARM";

    // TODO: Rename parameters
    private static final String EXTRA_ALARM_NAME = "com.example.managers.extra.ALARMNAME";
    private static final String EXTRA_ALARM_TIME = "com.example.managers.extra.ALARMTIME";
    private static final String EXTRA_DELTA_TIME = "com.example.managers.extra.DELTATIME";

    ArrayList<PendingIntent> intents;

    public PIMAlarmService() {
        super("PIMAlarmService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intents = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        for (PendingIntent intent :intents) {
            alarmManager.cancel(intent);
        }

        intents.clear();

        super.onDestroy();
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
    public static void startActionAddAlarm(Context context, long deltatime, String name, long time) {
        Intent intent = new Intent(context, PIMAlarmService.class);
        intent.setAction(ACTION_ADD_ALARM);
        intent.putExtra(EXTRA_DELTA_TIME, deltatime);
        intent.putExtra(EXTRA_ALARM_NAME, name);
        intent.putExtra(EXTRA_ALARM_TIME, time);
        context.startService(intent);
    }

    @Override
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

        Log.d("AlarmService", "Clear Alarm");

        intents.clear();

    }

    public void handleActionAddAlarm(long deltatime, String name, long time) {
        if(deltatime <= 0)
            return;

        Log.d("AlarmService", "Add Alarm : " + name);

        // create intent
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_NAME, name);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_TIME, time);
        PendingIntent alarmReceiver = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // find alarm manager
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        // add intent
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, deltatime, alarmReceiver);
        intents.add(alarmReceiver);
    }
}
