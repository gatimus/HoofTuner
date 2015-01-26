package io.github.gatimus.hooftuner;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.gatimus.hooftuner.pvl.APIWorker;
import io.github.gatimus.hooftuner.pvl.NowPlaying;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.StationAdapter;


public class Main extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBar actionBar;
    private ListView listView;
    private TextView songText;
    private ImageView songImage;
    private Station selectedStation;
    private APIWorker api;
    private BGW bgw;
    private ScheduledExecutorService updateScheduler;
    private ScheduledFuture scheduledUpdate;
    private Updater updater;
    private List<Station> stations;
    private ArrayAdapter<Station> stationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ){
            //TODO
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                selectedStation = (Station) parent.getItemAtPosition(position);
                actionBar.setTitle(selectedStation.name);
            }
        });
        songText = (TextView) findViewById(R.id.songText);
        songImage = (ImageView) findViewById(R.id.songImage);
        api = new APIWorker();
        bgw = new BGW();
        updateScheduler = Executors.newScheduledThreadPool(1);
        updater = new Updater();
        bgw.execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.v(getClass().getSimpleName(), "start");
        scheduledUpdate = updateScheduler.scheduleWithFixedDelay(updater, 0 , 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.v(getClass().getSimpleName(), "pause");
        scheduledUpdate.cancel(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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

    public class BGW extends AsyncTask<Void , Void, List<Station>>{

        @Override
        protected List<Station> doInBackground(Void... params) {
            return api.listStations();
        }

        @Override
        protected void onPostExecute(List<Station> stationList){
            stations = stationList;
            stationAdapter = new StationAdapter(getApplicationContext(), stations);
            stationAdapter.setNotifyOnChange(true);
            listView.setAdapter(stationAdapter);
        }
    }


    public class Updater implements Runnable {

        NowPlaying nowPlaying;

        @Override
        public void run() {
            Log.v(getClass().getSimpleName(), "update");
            if(selectedStation != null){
                nowPlaying = api.getNowPlaying(selectedStation.shortcode);
                Log.v(getClass().getSimpleName(), nowPlaying.current_song.text);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songText.setText(nowPlaying.current_song.text);
                        Picasso picasso = Picasso.with(getApplicationContext());
                        if(BuildConfig.DEBUG){
                            picasso.setIndicatorsEnabled(true);
                        }
                        if(nowPlaying.current_song.external.bronytunes != null){
                            picasso.load(nowPlaying.current_song.external.bronytunes.image_url)
                                    .placeholder(android.R.drawable.stat_sys_download)
                                    .error(android.R.drawable.stat_sys_download)
                                    .into(songImage);
                        }else if(nowPlaying.current_song.external.eqbeats != null){
                            picasso.load(nowPlaying.current_song.external.eqbeats.image_url)
                                    .placeholder(android.R.drawable.stat_sys_download)
                                    .error(android.R.drawable.stat_sys_download)
                                    .into(songImage);
                        }else if (selectedStation.shortcode == "ponyvillefm"){
                            picasso.load("http://ponyvillefm.com/images/music/default.png")
                                    .placeholder(android.R.drawable.stat_sys_download)
                                    .error(android.R.drawable.stat_sys_download)
                                    .into(songImage);
                        }else{
                            picasso.load("http://bronytunes.com/images/song-white.png")
                                    .placeholder(android.R.drawable.stat_sys_download)
                                    .error(android.R.drawable.stat_sys_download)
                                    .into(songImage);
                        }

                    }
                });
            }
        }
    }
}
