package io.github.gatimus.hooftuner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Main extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String STATION_NAME = "STATION_NAME";
    private static final String STATION_IMAGE = "STATION_IMAGE";

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
        if(savedInstanceState != null){
            updateActionBar(savedInstanceState.getCharSequence(STATION_NAME), (Uri)savedInstanceState.getParcelable(STATION_IMAGE));
        }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATION_NAME, actionBar.getTitle());
        outState.putParcelable(STATION_IMAGE, selectedStation.imageUri);
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
        switch(id){
            case R.id.action_about :
                new About().show(getFragmentManager(), getResources().getString(R.string.action_about));
                break;
            case R.id.action_settings :
                //TODO
                break;
            case R.id.action_quit :
                System.exit(0);
                break;
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
        updateActionBar(station.name, station.imageUri);
    }

    public void updateActionBar(CharSequence name, Uri image){
        actionBar.setTitle(name);
        PicassoWrapper.getStationPicasso(getApplicationContext(), image)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        actionBar.setIcon(R.drawable.icon);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        actionBar.setIcon(R.drawable.icon);
                    }
                });
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

    public void onLike(View view){
        //TODO
    }

    public void onDisLike(View view){
        //TODO
    }

}
