[1mdiff --git a/app/src/main/java/com/example/managers/PIMAlarmService.java b/app/src/main/java/com/example/managers/PIMAlarmService.java[m
[1mindex 298ba25..e80b54d 100644[m
[1m--- a/app/src/main/java/com/example/managers/PIMAlarmService.java[m
[1m+++ b/app/src/main/java/com/example/managers/PIMAlarmService.java[m
[36m@@ -180,8 +180,8 @@[m [mpublic class PIMAlarmService extends Service {[m
         AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);[m
 [m
         // add intent[m
[31m-        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (after - deltatime) * 1000, alarmReceiver);[m
[31m-        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, alarmReceiver);[m
[32m+[m[32m        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (after - deltatime) * 1000, alarmReceiver);[m[41m[m
[32m+[m[32m        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, alarmReceiver);[m[41m[m
         intents.add(alarmReceiver);[m
     }[m
 [m
[1mdiff --git a/app/src/main/java/com/example/pim/MainActivity.java b/app/src/main/java/com/example/pim/MainActivity.java[m
[1mindex abaf486..aae789e 100644[m
[1m--- a/app/src/main/java/com/example/pim/MainActivity.java[m
[1m+++ b/app/src/main/java/com/example/pim/MainActivity.java[m
[36m@@ -2,6 +2,7 @@[m [mpackage com.example.pim;[m
 [m
 import android.app.ActivityManager;[m
 import android.app.AlarmManager;[m
[32m+[m[32mimport android.app.Dialog;[m
 import android.app.PendingIntent;[m
 import android.content.BroadcastReceiver;[m
 import android.content.Context;[m
[36m@@ -11,6 +12,7 @@[m [mimport android.content.DialogInterface;[m
 import android.content.Intent;[m
 import android.os.Bundle;[m
 import android.os.SystemClock;[m
[32m+[m[32mimport android.support.design.widget.FloatingActionButton;[m
 import android.support.v7.app.AlertDialog;[m
 import android.support.v7.widget.LinearLayoutManager;[m
 import android.support.v7.widget.RecyclerView;[m
[36m@@ -29,6 +31,7 @@[m [mimport android.widget.Button;[m
 import android.widget.DatePicker;[m
 import android.widget.EditText;[m
 import android.widget.LinearLayout;[m
[32m+[m[32mimport android.widget.SearchView;[m
 import android.widget.TimePicker;[m
 [m
 import com.example.data.ScheduleItemData;[m
[36m@@ -60,15 +63,24 @@[m [mpublic class MainActivity extends AppCompatActivity[m
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);[m
         setSupportActionBar(toolbar);[m
 [m
[31m-        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);[m
[31m-        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle([m
[31m-                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);[m
[31m-        drawer.setDrawerListener(toggle);[m
[31m-        toggle.syncState();[m
[32m+[m[32m        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);[m
[32m+[m[32m        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle([m
[32m+[m[32m        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);[m
[32m+[m[32m        //drawer.setDrawerListener(toggle);[m
[32m+[m[32m       // toggle.syncState();[m
 [m
         NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);[m
         navigationView.setNavigationItemSelectedListener(this);[m
 [m
[32m+[m[32m        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);[m
[32m+[m[32m        fab.setOnClickListener(new View.OnClickListener() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onClick(View v) {[m
[32m+[m[32m                CreateItemAddingPopUp();[m
[32m+[m[32m            }[m
[32m+[m[32m        });[m
[32m+[m
[32m+[m
         // initialize google map api[m
         GoogleMapAPI.Init(this);[m
 [m
[36m@@ -83,7 +95,7 @@[m [mpublic class MainActivity extends AppCompatActivity[m
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);[m
         PendingIntent alarmReceiver = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);[m
         alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3 * 1000, 180 * 1000, alarmReceiver);[m
[31m-        [m
[32m+[m
 [m
         // create layout manager[m
         LinearLayoutManager scheduleLayoutManager = new LinearLayoutManager(getApplicationContext());[m
[36m@@ -166,11 +178,27 @@[m [mpublic class MainActivity extends AppCompatActivity[m
         } else if (id == R.id.nav_gallery) {[m
 [m
         } else if (id == R.id.nav_slideshow) {[m
[32m+[m[32m        } else if (id == R.id.nav_manage) {[m
[32m+[m
[32m+[m[32m        } else if (id == R.id.nav_share) {[m
[32m+[m
[32m+[m[32m        } else if (id == R.id.nav_send) {[m
[32m+[m[32m            CreateSampleItems();[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);[m
[32m+[m[32m        drawer.closeDrawer(GravityCompat.START);[m
[32m+[m[32m        return true;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    private void CreateSampleItems() {[m
[32m+[m[32m        {[m
[32m+[m[32m            // add dummy data[m
             final ScheduleItemData data = new ScheduleItemData();[m
[31m-            data.name = "고급 시계 수리";[m
[31m-            data.loc_destination = new GoogleMapLocation("청담동", 37.5175829, 127.0416468, "ChIJgxsKDHWkfDUR1YdLXxblctM");[m
[31m-            data.comment = "";[m
[31m-            data.time = new GregorianCalendar(2016, 5, 8, 11, 45);[m
[32m+[m[32m            data.name = "종강파티";[m
[32m+[m[32m            data.loc_destination = new GoogleMapLocation("안암역", 37.586296, 127.029137, "ChIJ35b3zLq8fDURvog9-eIQPBg");[m
[32m+[m[32m            data.comment = "아 종강이다!!!";[m
[32m+[m[32m            data.time = new GregorianCalendar(2016, 5, 24, 18, 00);[m
 [m
             SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {[m
                 @Override[m
[36m@@ -178,7 +206,9 @@[m [mpublic class MainActivity extends AppCompatActivity[m
                     scheduleItemManager.addItemData(data);[m
                 }[m
             });[m
[31m-        } else if (id == R.id.nav_manage) {[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        {[m
             // add dummy data[m
             final ScheduleItemData data = new ScheduleItemData();[m
             data.name = "용던레이드";[m
[36m@@ -192,13 +222,14 @@[m [mpublic class MainActivity extends AppCompatActivity[m
                     scheduleItemManager.addItemData(data);[m
                 }[m
             });[m
[31m-        } else if (id == R.id.nav_share) {[m
[31m-            // add dummy data[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        {[m
             final ScheduleItemData data = new ScheduleItemData();[m
[31m-            data.name = "종강파티";[m
[31m-            data.loc_destination = new GoogleMapLocation("안암역", 37.586296, 127.029137, "ChIJ35b3zLq8fDURvog9-eIQPBg");[m
[31m-            data.comment = "아 종강이다!!!";[m
[31m-            data.time = new GregorianCalendar(2016, 5, 24, 18, 00);[m
[32m+[m[32m            data.name = "고급 시계 수리";[m
[32m+[m[32m            data.loc_destination = new GoogleMapLocation("청담동", 37.5175829, 127.0416468, "ChIJgxsKDHWkfDUR1YdLXxblctM");[m
[32m+[m[32m            data.comment = "";[m
[32m+[m[32m            data.time = new GregorianCalendar(2016, 5, 8, 11, 45);[m
 [m
             SharedDataManager.Inst(this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {[m
                 @Override[m
[36m@@ -206,115 +237,100 @@[m [mpublic class MainActivity extends AppCompatActivity[m
                     scheduleItemManager.addItemData(data);[m
                 }[m
             });[m
[31m-        } else if (id == R.id.nav_send) {[m
[31m-            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);[m
[31m-[m
[31m-            dialog.setTitle("일정 추가");[m
[31m-[m
[31m-            final LinearLayout linearLayout = (LinearLayout)View.inflate(this, R.layout.dialog, null);[m
[31m-            dialog.setView(linearLayout);[m
[31m-[m
[31m-            final EditText editTextName = (EditText) linearLayout.findViewById(R.id.editTextName);[m
[31m-            final EditText editTextDestination = (EditText) linearLayout.findViewById(R.id.editText3);[m
[31m-            final EditText editTextComment = (EditText) linearLayout.findViewById(R.id.editTextComment);[m
[31m-[m
[31m-            final Calendar calendar = Calendar.getInstance();[m
[31m-            final Button buttonDate = (Button) linearLayout.findViewById(R.id.buttonDate);[m
[31m-            buttonDate.setOnClickListener(new View.OnClickListener() {[m
[31m-                @Override[m
[31m-                public void onClick(View v) {[m
[31m-                    int y = calendar.get(Calendar.YEAR);[m
[31m-                    int m = calendar.get(Calendar.MONTH);[m
[31m-                    int d = calendar.get(Calendar.DAY_OF_MONTH);[m
[31m-[m
[31m-                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {[m
[31m-                        @Override[m
[31m-                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {[m
[31m-                            calendar.set(Calendar.YEAR, year);[m
[31m-                            calendar.set(Calendar.MONTH, monthOfYear);[m
[31m-                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);[m
[31m-                            buttonDate.setText(String.valueOf(year) + "년 " + String.valueOf(monthOfYear+1) + "월 " + String.valueOf(dayOfMonth) + "일");[m
[31m-                        }[m
[31m-                    }, y, m, d);[m
[31m-                    dpd.show();[m
[31m-                }[m
[31m-            });[m
[31m-            final Button buttonTime = (Button) linearLayout.findViewById(R.id.buttonTime);[m
[31m-            buttonTime.setOnClickListener(new View.OnClickListener() {[m
[31m-                @Override[m
[31m-                public void onClick(View v) {[m
[31m-                    int h = calendar.get(Calendar.HOUR_OF_DAY);[m
[31m-                    int m = calendar.get(Calendar.MINUTE);[m
[31m-[m
[31m-                    TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {[m
[31m-                        @Override[m
[31m-                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {[m
[31m-                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);[m
[31m-                            calendar.set(Calendar.MINUTE, minute);[m
[31m-                            buttonTime.setText(String.valueOf(hourOfDay) + "시 " + String.valueOf(minute) + "분");[m
[31m-                        }[m
[31m-                    }, h, m, false);[m
[31m-                    tpd.show();[m
[31m-                }[m
[31m-            });[m
[31m-[m
[31m-            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {[m
[31m-                @Override[m
[31m-                public void onClick(DialogInterface dialog, int which) {[m
[31m-                    switch (which) {[m
[31m-                        case DialogInterface.BUTTON_POSITIVE:[m
[31m-                            final ScheduleItemData data = new ScheduleItemData();[m
[31m-                            data.name = editTextName.getText().toString();[m
[31m-                            data.loc_destination.setName(editTextDestination.getText().toString());[m
[31m-                            data.comment = editTextComment.getText().toString();[m
[31m-[m
[31m-                            GregorianCalendar gregorianCalendar = new GregorianCalendar([m
[31m-                                    calendar.get(Calendar.YEAR),[m
[31m-                                    calendar.get(Calendar.MONTH),[m
[31m-                                    calendar.get(Calendar.DAY_OF_MONTH),[m
[31m-                                    calendar.get(Calendar.HOUR_OF_DAY),[m
[31m-                                    calendar.get(Calendar.MINUTE)[m
[31m-                            );[m
[31m-[m
[31m-                            data.time = gregorianCalendar;[m
[31m-[m
[31m-                            SharedDataManager.Inst(MainActivity.this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {[m
[31m-                                @Override[m
[31m-                                public void doWith(ScheduleItemManager scheduleItemManager) {[m
[31m-                                    scheduleItemManager.addItemData(data);[m
[31m-                                }[m
[31m-                            });[m
[31m-                            break;[m
[31m-                        case DialogInterface.BUTTON_NEGATIVE:[m
[31m-                            break;[m
[31m-                        case DialogInterface.BUTTON_NEUTRAL:[m
[31m-                            break;[m
[31m-                    }[m
[31m-                }[m
[31m-            };[m
[31m-[m
[31m-            dialog.setPositiveButton("Yes", listener);[m
[31m-            dialog.setNegativeButton("No", listener);[m
[31m-            dialog.setNeutralButton("Cancel", listener);[m
[31m-[m
[31m-            dialog.show();[m
         }[m
[31m-[m
[31m-        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);[m
[31m-        drawer.closeDrawer(GravityCompat.START);[m
[31m-        return true;[m
     }[m
 [m
[31m-    // http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android[m
[31m-    private boolean isMyServiceRunning(Class<?> serviceClass) {[m
[31m-        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);[m
[31m-        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {[m
[31m-            if (serviceClass.getName().equals(service.service.getClassName())) {[m
[31m-                return true;[m
[32m+[m[32m    private void CreateItemAddingPopUp() {[m
[32m+[m[32m        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);[m
[32m+[m
[32m+[m[32m        dialog.setTitle("일정 추가");[m
[32m+[m
[32m+[m[32m        final LinearLayout linearLayout = (LinearLayout)View.inflate(this, R.layout.dialog, null);[m
[32m+[m[32m        dialog.setView(linearLayout);[m
[32m+[m
[32m+[m[32m        final EditText editTextName = (EditText) linearLayout.findViewById(R.id.editTextName);[m
[32m+[m[32m        final EditText editTextDestination = (EditText) linearLayout.findViewById(R.id.editText3);[m
[32m+[m[32m        final EditText editTextComment = (EditText) linearLayout.findViewById(R.id.editTextComment);[m
[32m+[m
[32m+[m[32m        final Calendar calendar = Calendar.getInstance();[m
[32m+[m[32m        final Button buttonDate = (Button) linearLayout.findViewById(R.id.buttonDate);[m
[32m+[m[32m        buttonDate.setOnClickListener(new View.OnClickListener() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onClick(View v) {[m
[32m+[m[32m                int y = calendar.get(Calendar.YEAR);[m
[32m+[m[32m                int m = calendar.get(Calendar.MONTH);[m
[32m+[m[32m                int d = calendar.get(Calendar.DAY_OF_MONTH);[m
[32m+[m
[32m+[m[32m                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {[m
[32m+[m[32m                        calendar.set(Calendar.YEAR, year);[m
[32m+[m[32m                        calendar.set(Calendar.MONTH, monthOfYear);[m
[32m+[m[32m                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);[m
[32m+[m[32m                        buttonDate.setText(String.valueOf(year) + "년 " + String.valueOf(monthOfYear+1) + "월 " + String.valueOf(dayOfMonth) + "일");[m
[32m+[m[32m                    }[m
[32m+[m[32m                }, y, m, d);[m
[32m+[m[32m                dpd.show();[m
             }[m
[31m-        }[m
[31m-        return false;[m
[31m-    }[m
[32m+[m[32m        });[m
[32m+[m[32m        final Button buttonTime = (Button) linearLayout.findViewById(R.id.buttonTime);[m
[32m+[m[32m        buttonTime.setOnClickListener(new View.OnClickListener() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onClick(View v) {[m
[32m+[m[32m                int h = calendar.get(Calendar.HOUR_OF_DAY);[m
[32m+[m[32m                int m = calendar.get(Calendar.MINUTE);[m
[32m+[m
[32m+[m[32m                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {[m
[32m+[m[32m                    @Override[m
[32m+[m[32m                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {[m
[32m+[m[32m                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);[m
[32m+[m[32m                        calendar.set(Calendar.MINUTE, minute);[m
[32m+[m[32m                        buttonTime.setText(String.valueOf(hourOfDay) + "시 " + String.valueOf(minute) + "분");[m
[32m+[m[32m                    }[m
[32m+[m[32m                }, h, m, false);[m
[32m+[m[32m                tpd.show();[m
[32m+[m[32m            }[m
[32m+[m[32m        });[m
[32m+[m
[32m+[m[32m        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public void onClick(DialogInterface dialog, int which) {[m
[32m+[m[32m                switch (which) {[m
[32m+[m[32m                    case DialogInterface.BUTTON_POSITIVE:[m
[32m+[m[32m                        final ScheduleItemData data = new ScheduleItemData();[m
[32m+[m[32m                        data.name = editTextName.getText().toString();[m
[32m+[m[32m                        data.loc_destination.setName(editTextDestination.getText().toString());[m
[32m+[m[32m                        data.comment = editTextComment.getText().toString();[m
[32m+[m
[32m+[m[32m                        GregorianCalendar gregorianCalendar = new GregorianCalendar([m
[32m+[m[32m                                calendar.get(Calendar.YEAR),[m
[32m+[m[32m                                calendar.get(Calendar.MONTH),[m
[32m+[m[32m                                calendar.get(Calendar.DAY_OF_MONTH),[m
[32m+[m[32m                                calendar.get(Calendar.HOUR_OF_DAY),[m
[32m+[m[32m                                calendar.get(Calendar.MINUTE)[m
[32m+[m[32m                        );[m
[32m+[m
[32m+[m[32m                        data.time = gregorianCalendar;[m
[32m+[m
[32m+[m[32m                        SharedDataManager.Inst(MainActivity.this).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {[m
[32m+[m[32m                            @Override[m
[32m+[m[32m                            public void doWith(ScheduleItemManager scheduleItemManager) {[m
[32m+[m[32m                                scheduleItemManager.addItemData(data);[m
[32m+[m[32m                            }[m
[32m+[m[32m                        });[m
[32m+[m[32m                        break;[m
[32m+[m[32m                    case DialogInterface.BUTTON_NEGATIVE:[m
[32m+[m[32m                        break;[m
[32m+[m[32m                    case DialogInterface.BUTTON_NEUTRAL:[m
[32m+[m[32m                        break;[m
[32m+[m[32m                }[m
[32m+[m[32m            }[m
[32m+[m[32m        };[m
 [m
[32m+[m[32m        dialog.setPositiveButton("Yes", listener);[m
[32m+[m[32m        dialog.setNegativeButton("No", listener);[m
[32m+[m[32m        dialog.setNeutralButton("Cancel", listener);[m
 [m
[32m+[m[32m        dialog.show();[m
[32m+[m[32m    }[m
 }[m
[1mdiff --git a/app/src/main/res/layout/app_bar_main.xml b/app/src/main/res/layout/app_bar_main.xml[m
[1mindex b18f706..37f8265 100644[m
[1m--- a/app/src/main/res/layout/app_bar_main.xml[m
[1m+++ b/app/src/main/res/layout/app_bar_main.xml[m
[36m@@ -29,6 +29,6 @@[m
         android:layout_height="wrap_content"[m
         android:layout_gravity="bottom|end"[m
         android:layout_margin="@dimen/fab_margin"[m
[31m-        android:src="@android:drawable/ic_dialog_email" />[m
[32m+[m[32m        android:src="@android:drawable/ic_menu_add" />[m
 [m
 </android.support.design.widget.CoordinatorLayout>[m
[1mdiff --git a/app/src/main/res/layout/dialog.xml b/app/src/main/res/layout/dialog.xml[m
[1mindex cdd1c35..61b5b04 100644[m
[1m--- a/app/src/main/res/layout/dialog.xml[m
[1m+++ b/app/src/main/res/layout/dialog.xml[m
[36m@@ -1,7 +1,8 @@[m
 <?xml version="1.0" encoding="utf-8"?>[m
 <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"[m
     android:layout_width="match_parent" android:layout_height="match_parent"[m
[31m-    android:orientation="vertical">[m
[32m+[m[32m    android:orientation="vertical"[m
[32m+[m[32m    android:weightSum="1">[m
     <LinearLayout[m
         android:layout_width="match_parent"[m
         android:layout_height="wrap_content"[m
