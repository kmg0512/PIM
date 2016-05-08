package com.example.pim;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.data.ScheduleItemData;
import com.example.data.SocialItemData;
import com.example.managers.DataManager;
import com.example.view.main.ScheduleItemAdapter;
import com.example.view.main.SocialItemAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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


        // create layout manager
        LinearLayoutManager scheduleLayoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager socialLayoutManager = new LinearLayoutManager(getApplicationContext());

        // create schedule view
        RecyclerView scheduleRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Schedule);
        scheduleRecyclerView.setLayoutManager(scheduleLayoutManager);
        scheduleRecyclerView.setAdapter(new ScheduleItemAdapter(0));



        // create social view
        RecyclerView socialRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_Social);
        socialRecyclerView.setLayoutManager(socialLayoutManager);
        socialRecyclerView.setAdapter(new SocialItemAdapter(1));

        if(socialRecyclerView.getAdapter() == scheduleRecyclerView.getAdapter())
            DataManager.Inst().getScheduleDataList().get(0).setName("Bug!!");


        // add schedule data
        ScheduleItemData tmpdata = new ScheduleItemData();
        tmpdata.setName("BlaBla");
        DataManager.Inst().getScheduleDataList().add(tmpdata);
        DataManager.Inst().getScheduleDataList().add(new ScheduleItemData());

        // add social data
        DataManager.Inst().getSocialDataList().add(new SocialItemData());
        DataManager.Inst().getSocialDataList().add(new SocialItemData());

        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                HttpsGetter.HttpsGetListener listener = new HttpsGetter.HttpsGetListener() {
                    @Override
                    public void OnGet(HttpsGetter.HttpsGetResult result) {
                        if(result.success) {
                            TextView t = (TextView) findViewById(R.id.Testing);

                            try {
                                JSONObject obj = new JSONObject(result.result);
                                JSONObject obj2 = (JSONObject) obj.getJSONArray("routes").get(0);
                                JSONObject obj3 = (JSONObject) obj2.getJSONArray("legs").get(0);


                                t.setText(obj3.toString(1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        else
                            Snackbar.make(view, "Failed", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                    }
                };
                EditText edittext_from = (EditText) findViewById(R.id.From);
                String from = edittext_from.getText().toString();
                try {
                    from = URLEncoder.encode(from, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                EditText edittext_to = (EditText) findViewById(R.id.To);
                String to = edittext_to.getText().toString();
                try {
                    to = URLEncoder.encode(to, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + from + "&destination=" + to +
                        "&mode=transit&key=AIzaSyAwT2mGH1pGz1RMuPfB_tKE9fF3wnpIJz0";
                HttpsGetter getter = new HttpsGetter();
                getter.Get(url, listener);


            }
        });
        */
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
