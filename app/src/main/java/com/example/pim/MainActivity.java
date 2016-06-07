package com.example.pim;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.example.data.ScheduleItemData;
import com.example.managers.BackgroundManager;
import com.example.managers.PIMAlarmService;
import com.example.managers.ScheduleItemManager;
import com.example.managers.SharedDataManager;
import com.example.utility.map.GoogleMapAPI;
import com.example.utility.map.GoogleMapLocation;
import com.example.view.main.ScheduleItemAdapter;
import com.example.view.main.SocialItemAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ScheduleItemAdapter scheduleItemAdapter;
    GoogleApiClient googleClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // initialize google map api
        GoogleMapAPI.Init(this);

        // initialize data
        //DataManager.Init(this);
        //DataManager.Inst().onCreate();

        // add alarm service
        if(!isMyServiceRunning(PIMAlarmService.class)) {

            Intent alarmservice = new Intent(this, PIMAlarmService.class);
            startService(alarmservice);
        }


        // add sample alarm
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BackgroundManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent alarmReceiver = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 3 * 1000, 120 * 1000, alarmReceiver);
        

        // create layout manager
        LinearLayoutManager scheduleLayoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager socialLayoutManager = new LinearLayoutManager(getApplicationContext());

        // create schedule view
        RecyclerView scheduleRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Schedule);
        scheduleRecyclerView.setLayoutManager(scheduleLayoutManager);
        scheduleItemAdapter = new ScheduleItemAdapter(this, 0);
        scheduleRecyclerView.setAdapter(scheduleItemAdapter);

        // create social view
        RecyclerView socialRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Social);
        socialRecyclerView.setLayoutManager(socialLayoutManager);
        socialRecyclerView.setAdapter(new SocialItemAdapter(1));

        // log
        Log.d("MainActivity", "onCreate");
    }

    @Override
    public void onDestroy() {
        // log
        Log.d("MainActivity", "onDestroy");

        // destroy
        scheduleItemAdapter.onDestroy();

        // stop
        GoogleMapAPI.Destroy();

        // remove all data
        SharedDataManager.Inst(this).clear();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("MainActivity", "enabled add schedule dialog");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            // add dummy data
            final ScheduleItemData data = new ScheduleItemData();
            data.loc_origin = new GoogleMapLocation("연세대학교 본관", 37.56633970000001, 126.9387511, "ChIJN35ssI6YfDURAZzUunmlinI");
            data.loc_destination = new GoogleMapLocation("서울역", 37.554531, 126.970663, "ChIJM5xLpGaifDURb1sjwxADM-8");

            SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                @Override
                public void doWith(ScheduleItemManager scheduleItemManager) {
                    scheduleItemManager.addItemData(data);
                }
            });
        } else if (id == R.id.nav_send) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("일정 추가");

            final LinearLayout linearLayout = (LinearLayout)View.inflate(this, R.layout.dialog, null);
            dialog.setView(linearLayout);

            final EditText editTextName = (EditText) linearLayout.findViewById(R.id.editTextName);
            final EditText editTextDestination = (EditText) linearLayout.findViewById(R.id.editText3);
            final EditText editTextComment = (EditText) linearLayout.findViewById(R.id.editTextComment);

            final Calendar calendar = Calendar.getInstance();
            final Button buttonDate = (Button) linearLayout.findViewById(R.id.buttonDate);
            buttonDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int y = calendar.get(Calendar.YEAR);
                    int m = calendar.get(Calendar.MONTH);
                    int d = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            buttonDate.setText(String.valueOf(year) + "년 " + String.valueOf(monthOfYear+1) + "월 " + String.valueOf(dayOfMonth) + "일");
                        }
                    }, y, m, d);
                    dpd.show();
                }
            });
            final Button buttonTime = (Button) linearLayout.findViewById(R.id.buttonTime);
            buttonTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int h = calendar.get(Calendar.HOUR_OF_DAY);
                    int m = calendar.get(Calendar.MINUTE);

                    TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            buttonTime.setText(String.valueOf(hourOfDay) + "시 " + String.valueOf(minute) + "분");
                        }
                    }, h, m, false);
                    tpd.show();
                }
            });

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            final ScheduleItemData data = new ScheduleItemData();
                            data.name = editTextName.getText().toString();
                            data.time = String.valueOf(calendar.get(Calendar.YEAR)) +  String.valueOf((calendar.get(Calendar.MONTH) + 1) * 1000000 + calendar.get(Calendar.DAY_OF_MONTH) * 10000 + calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE));
                            Log.d("Calendar", data.time);
                            data.loc_destination.setName(editTextDestination.getText().toString());
                            data.comment = editTextComment.getText().toString();

                            SharedDataManager.Inst(MainActivity.this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                                @Override
                                public void doWith(ScheduleItemManager scheduleItemManager) {
                                    scheduleItemManager.addItemData(data);
                                }
                            });
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            break;
                    }
                }
            };

            dialog.setPositiveButton("Yes", listener);
            dialog.setNegativeButton("No", listener);
            dialog.setNeutralButton("Cancel", listener);

            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
