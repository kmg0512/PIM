package com.example.data;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.pim.MainActivity;

import java.util.concurrent.TimeUnit;

/**
 * Created by tigri on 2016-06-07.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_ALARM_NAME = "com.example.data.extra.ALARMNAME";
    public static final String EXTRA_ALARM_TIME = "com.example.data.extra.ALARMTIME";

    private static int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // announce alarm
        Log.d("AlarmReceiver", "Announce alarm : " + intent.getStringExtra(EXTRA_ALARM_NAME));

        String name = intent.getStringExtra(EXTRA_ALARM_NAME);
        long deltatime = intent.getLongExtra(EXTRA_ALARM_TIME, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(context.getResources().getIdentifier("ic_menu_send", "drawable", context.getPackageName()))
                        .setContentTitle("다음 일정이 대기중 : " + name)
                        .setContentText("지금 출발하면 " + TimeUnit.SECONDS.toMinutes(deltatime) + " 분 후에 도착합니다.");

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(++id, mBuilder.build());
    }
}
