package io.github.gatimus.hooftuner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import io.github.gatimus.hooftuner.pvl.Station;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Main extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private Station selectedStation = new Station();

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        actionBar = getActionBar();

        //SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        //s.setSpan(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //actionBar.setTitle(s);

        //Set up Navigation Drawer

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
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
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateStation(Station station){
        if(selectedStation.id != station.id){
            selectedStation = station;

            if(selectedStation.category.equals(Station.AUDIO)){
                TweetFragment tweetFragment = TweetFragment.newInstance(selectedStation);
                getFragmentManager().beginTransaction().replace(R.id.tweet_fragment_container, tweetFragment).commit();
                NowPlayingFragment  nowPlayingFragment = NowPlayingFragment.newInstance(selectedStation);
                getFragmentManager().beginTransaction().replace(R.id.now_playing_fragment_container, nowPlayingFragment).commit();

            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(Station station) {
        drawerLayout.closeDrawers();
        updateStation(station);
    }

    /*
    public void onLike(View view){
        PonyvilleLive.getPonyvilleLiveInterface().likeSong(nowPlaying.current_song.sh_id, new Callback<Response<String>>() {
            @Override
            public void success(io.github.gatimus.hooftuner.pvl.Response<String> stringResponse, retrofit.client.Response response) {
                Toast.makeText(getApplicationContext(), stringResponse.result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(getClass().getSimpleName(), error.toString());
            }
        });
    }

    public void onDisLike(View view){
        PonyvilleLive.getPonyvilleLiveInterface().dislikeSong(nowPlaying.current_song.sh_id, new Callback<io.github.gatimus.hooftuner.pvl.Response<String>>() {
            @Override
            public void success(io.github.gatimus.hooftuner.pvl.Response<String> stringResponse, retrofit.client.Response response) {
                Toast.makeText(getApplicationContext(), stringResponse.result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(getClass().getSimpleName(), error.toString());
            }
        });
    }
    */

}
