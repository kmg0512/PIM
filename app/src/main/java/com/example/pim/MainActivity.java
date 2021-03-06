package com.example.pim;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.RelativeLayout;

import android.widget.TimePicker;
import android.widget.Toast;

import com.example.data.ScheduleItemData;
import com.example.managers.BackgroundManager;
import com.example.managers.PIMAlarmService;
import com.example.managers.ScheduleItemManager;
import com.example.managers.SharedDataManager;
import com.example.utility.map.GoogleMapAPI;
import com.example.utility.map.GoogleMapLocation;
import com.example.view.main.PlaceAutocompleteAdapter;
import com.example.view.main.ScheduleItemAdapter;
import com.example.view.main.SocialItemAdapter;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.UserTimeline;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JTeV0vgTTJ2FnMOixwnyCoKOg";
    private static final String TWITTER_SECRET = "QqcTDyG9d1BF8bWfcwbqdgCQH5qv1euCRHs02RMulBh2oyIpj8";


    ScheduleItemAdapter scheduleItemAdapter;
    PlaceAutocompleteAdapter placeAutocompleteAdapter;
    GoogleApiClient googleClient;

    // callback manager of facebook
    CallbackManager fbLoginManager;

    // Twitter
    TwitterLoginButton twitterLoginButton;
    TwitterCore core;
    TweetUi tweetUi;
    TweetComposer composer;
    String userName;

    boolean isTWLogining = false;
    boolean isFBLogining = false;


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
        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // initialize Facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbLoginManager = CallbackManager.Factory.create();

        // initialize Twitter kit
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        core = Twitter.getInstance().core;
        tweetUi = Twitter.getInstance().tweetUi;
        composer = Twitter.getInstance().tweetComposer;

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
        Log.d("MainActivity", "enabled add schedule dialog_schedule");

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_sample) {
            CreateSampleItems();
        } else if (id == R.id.nav_schedule) {
            CreateItemAddingPopUp();
        } else if (id == R.id.nav_facebook) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Facebook 계정 관리");

            final RelativeLayout relativeLayout = (RelativeLayout) View.inflate(this,R.layout.dialog_facebook, null);
            dialog.setView(relativeLayout);
            Log.d("Facebook", "set layout");

            LoginButton loginButton = (LoginButton) relativeLayout.findViewById(R.id.buttonFacebook);
            Log.d("Facebook", "connect button");
            loginButton.setReadPermissions("public_profile", "user_friends", "email");
            Log.d("Facebook", "set permission");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFBLogining = true;
                }
            });
            loginButton.registerCallback(fbLoginManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("Facebook", "Token" + loginResult.getAccessToken().getToken());
                    Log.d("Facebook", "User ID" + loginResult.getAccessToken().getUserId());
                    Log.d("Facebook", "Permission List" + loginResult.getAccessToken().getPermissions() + "");

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Log.d("Facebook", "User profile" + object.toString());
                                String msg = object.toString();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("Facebook", "User profile" + object.toString());
                                e.printStackTrace();
                            }
                        }
                    });

                    request.executeAsync();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("Facebook", "Error" + error.getMessage());
                }

                @Override
                public void onCancel() {
                    Log.e("Facebook", "Cancel");
                }
            });

            dialog.show();
        } else if (id == R.id.nav_twitter) {
            Log.d("Twitter", "Start");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Twitter 계정 관리");
            Log.d("Twitter", "setTitle");

            final RelativeLayout relativeLayout = (RelativeLayout) View.inflate(this, R.layout.dialog_twitter, null);
            dialog.setView(relativeLayout);
            Log.d("Twitter", "setView");

            twitterLoginButton = (TwitterLoginButton) relativeLayout.findViewById(R.id.buttonTwitter);
            twitterLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTWLogining = true;
                }
            });
            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // The TwitterSession is also available through:
                    // Twitter.getInstance().core.getSessionManager().getActiveSession()
                    TwitterSession session = result.data;
                    // TODO: Remove toast and use the TwitterSession's userID
                    // with your app's user model
                    String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                    Log.d("Twitter", msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                    userName = session.getUserName();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Login with Twitter failure", exception);
                }
            });

            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void CreateSampleItems()
    {
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
        final ScheduleItemData data = new ScheduleItemData();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("일정 추가");

        final RelativeLayout relativeLayout = (RelativeLayout) View.inflate(this, R.layout.dialog_schedule, null);
        dialog.setView(relativeLayout);

        final EditText editTextName = (EditText) relativeLayout.findViewById(R.id.editTextName);
        final EditText editTextComment = (EditText) relativeLayout.findViewById(R.id.editTextComment);

        final Calendar calendar = Calendar.getInstance();
        final Button buttonDate = (Button) relativeLayout.findViewById(R.id.buttonDate);
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
                        buttonDate.setText(String.valueOf(year) + "년 " + String.valueOf(monthOfYear + 1) + "월 " + String.valueOf(dayOfMonth) + "일");
                    }
                }, y, m, d);
                dpd.show();
            }
        });
        final Button buttonTime = (Button) relativeLayout.findViewById(R.id.buttonTime);
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

        final AutoCompleteTextView autoCompleteTextViewDestination = (AutoCompleteTextView) relativeLayout.findViewById(R.id.autoCompleteTextViewDestination);
        autoCompleteTextViewDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                CharSequence primaryText = item.getPrimaryText(null);

                Log.i("AutoCompleteTextView", "Autocomplete item selected: " + primaryText);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("AutoCompleteTextView", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        // Place to GoogleMapLocation
                        data.loc_destination = new GoogleMapLocation(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude, place.getId());

                        places.release();
                    }
                });

                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();
                Log.i("AutoCompleteTextView", "Called getPlaceById to get Place details for " + placeId);
            }
        });
        LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(MainActivity.this, googleClient, BOUNDS_GREATER_SYDNEY, null);
        autoCompleteTextViewDestination.setAdapter(placeAutocompleteAdapter);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        data.name = editTextName.getText().toString();
                        //data.loc_destination.setName(editTextDestination.getText().toString());
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if(isFBLogining) {
            fbLoginManager.onActivityResult(requestCode, resultCode, data);
            isFBLogining = false;
        }

        if(isTWLogining) {
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
            isTWLogining = false;
        }
    }

}
