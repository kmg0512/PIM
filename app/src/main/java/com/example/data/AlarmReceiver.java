package com.example.data;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tigri on 2016-06-07.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_ALARM_NAME = "com.example.data.extra.ALARMNAME";
    public static final String EXTRA_ALARM_TIME = "com.example.data.extra.ALARMTIME";

    @Override
    public void onReceive(Context context, Intent intent) {
        // announce alarm
        Log.d("AlarmReceiver", "Announce alarm : " + intent.getStringExtra(EXTRA_ALARM_NAME));
    }
}
