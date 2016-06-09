package com.example.pim;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.SearchView;
import android.widget.TimePicker;

import com.example.data.ScheduleItemData;
import com.example.managers.BackgroundManager;
import com.example.managers.MyIntentService;
import com.example.managers.PIMAlarmService;
import com.example.managers.ScheduleItemManager;
import com.example.managers.SharedDataManager;
import com.example.managers.TempAlarmReceiver;
import com.example.utility.map.GoogleMapAPI;
import com.example.utility.map.GoogleMapLocation;
import com.example.view.main.ScheduleItemAdapter;
import com.example.view.main.SocialItemAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
       // toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateItemAddingPopUp();
            }
        });

        // start service
        Intent alarmService = new Intent(this, PIMAlarmService.class);
        startService(alarmService);


        // add background updating routine
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BackgroundManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent alarmReceiver = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3 * 1000, 180 * 1000, alarmReceiver);


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
        SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {

            }
        });
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

        } else if (id == R.id.nav_send) {
            CreateSampleItems();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CreateSampleItems() {
        {
            // add dummy data
            final ScheduleItemData data = new ScheduleItemData();
            data.name = "종강파티";
            data.loc_destination = new GoogleMapLocation("안암역", 37.586296, 127.029137, "ChIJ35b3zLq8fDURvog9-eIQPBg");
            data.comment = "아 종강이다!!!";
            data.time = new GregorianCalendar(2016, 5, 24, 18, 00);

            SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                @Override
                public void doWith(ScheduleItemManager scheduleItemManager) {
                    scheduleItemManager.addItemData(data);
                }
            });
        }

        {
            // add dummy data
            final ScheduleItemData data = new ScheduleItemData();
            data.name = "용던레이드";
            data.loc_destination = new GoogleMapLocation("용산역", 37.529626, 126.96347, "ChIJUcFjVAOifDUR0CAVWLo7ZOg");
            data.comment = "메모리 8G, SSD 256GB, 파워 700W";
            data.time = new GregorianCalendar(2016, 5, 13, 12, 45);

            SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                @Override
                public void doWith(ScheduleItemManager scheduleItemManager) {
                    scheduleItemManager.addItemData(data);
                }
            });
        }

        {
            final ScheduleItemData data = new ScheduleItemData();
            data.name = "고급 시계 수리";
            data.loc_destination = new GoogleMapLocation("청담동", 37.5175829, 127.0416468, "ChIJgxsKDHWkfDUR1YdLXxblctM");
            data.comment = "";
            data.time = new GregorianCalendar(2016, 5, 8, 11, 45);

            SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                @Override
                public void doWith(ScheduleItemManager scheduleItemManager) {
                    scheduleItemManager.addItemData(data);
                }
            });
        }
    }

    private void CreateItemAddingPopUp() {
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
                        data.loc_destination.setName(editTextDestination.getText().toString());
                        data.comment = editTextComment.getText().toString();

                        GregorianCalendar gregorianCalendar = new GregorianCalendar(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)
                        );

                        data.time = gregorianCalendar;

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
}
