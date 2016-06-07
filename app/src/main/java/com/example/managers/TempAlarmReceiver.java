package com.example.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tigri on 2016-06-08.
 */
public class TempAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("YOOO", "YOOO");
        MyIntentService.startActionBaz(context, "", "");
    }
}
