package io.github.gatimus.hooftuner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import com.squareup.picasso.Target;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.gatimus.hooftuner.bt.BronyTunes;
import io.github.gatimus.hooftuner.pvl.APIWorker;
import io.github.gatimus.hooftuner.pvl.NowPlaying;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Station;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Main extends Activity implements Callback<io.github.gatimus.hooftuner.pvl.Response<NowPlaying>>{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBar actionBar;
    private ListView listView;
    private TextView stationGenre, songArtist, songTitle, event, eventUpComing;
    //private TextView songDescription;
    //private TextView songLyrics;
    private ImageView songImage, stationBG;
    private Station selectedStation = new Station();
    private NowPlaying nowPlaying = new NowPlaying();
    private APIWorker api;
    private ScheduledExecutorService updateScheduler;
    private ScheduledFuture scheduledUpdate;
    private Updater updater;
    private List<Station> stations;
    private ArrayAdapter<Station> stationAdapter;
    private TweetFragment tweetFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stations = Global.stations;
        setContentView(R.layout.activity_main);


        //setup drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //setup drawer list
        listView = (ListView) findViewById(android.R.id.list);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                updateStation(stations.get(position));
            }
        });
        //ui ref
        songArtist = (TextView) findViewById(R.id.song_artist);
        songTitle = (TextView) findViewById(R.id.song_title);
        event = (TextView) findViewById(R.id.event);
        eventUpComing = (TextView) findViewById(R.id.event_upcoming);
        stationGenre = (TextView) findViewById(R.id.station_genre);
        //songDescription = (TextView) findViewById(R.id.songDescription);
        //songLyrics = (TextView) findViewById(R.id.songLyrics);
        songImage = (ImageView) findViewById(R.id.song_image);

        //threading
        updateScheduler = Executors.newScheduledThreadPool(1);
        updater = new Updater();
        //adaptor
        stationAdapter = new StationAdapter(getApplicationContext(), stations);
        stationAdapter.setNotifyOnChange(true);
        listView.setAdapter(stationAdapter);

        tweetFragment = TweetFragment.newInstance(stations.get(1)); //temp
        //viewPager
        //viewPager = (ViewPager) findViewById(R.id.viewPager);
        //viewPager.setAdapter(new InfoPageAdapter(getFragmentManager()));
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
        scheduledUpdate = updateScheduler.scheduleWithFixedDelay(updater, 0 , 30_000, TimeUnit.MILLISECONDS);
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

    @Override
    public void success(io.github.gatimus.hooftuner.pvl.Response<NowPlaying> nowPlayingResponse, Response response) {
        updateNowPlaying(nowPlayingResponse.result);
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(getClass().getSimpleName(), error.toString());
    }


    public class Updater implements Runnable {

        @Override
        public void run() {
            Log.v(getClass().getSimpleName(), "update");
            if(selectedStation != null){
                //nowPlaying = api.getNowPlaying(selectedStation.shortcode);
                PonyvilleLive.getPonyvilleLiveInterface().nowPlaying(selectedStation.shortcode, Main.this);

            }
        }
    }


    public void updateStation(Station station){
        if(selectedStation.id != station.id){
            selectedStation = station;
            Intent iStop = new Intent(Main.this, MusicService.class)
                    .setAction(MusicService.ACTION_STOP);
            Main.this.startService(iStop);
            actionBar.setTitle(selectedStation.name);
            stationGenre.setText(selectedStation.genre);
            PicassoWrapper.getStationPicasso(getApplicationContext(),selectedStation.image_url.toString())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            actionBar.setIcon(new BitmapDrawable(getResources(),bitmap));
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
            if(selectedStation.category.equals(Station.AUDIO)){

                Intent iStart = new Intent(Main.this, MusicService.class)
                        .setAction(MusicService.ACTION_PLAY)
                        .putExtra(MusicService.KEY_STREAM_URL, selectedStation.stream_url.toString());
                Main.this.startService(iStart);

                //tweetFragment = TweetFragment.newInstance(selectedStation);
                //getFragmentManager().beginTransaction().replace(R.id.tweetFragmentContainer, tweetFragment).commit();

            }

            PonyvilleLive.getPonyvilleLiveInterface().nowPlaying(selectedStation.shortcode, Main.this);
        }
    }

    public void updateNowPlaying(NowPlaying nowPlaying){
        if(!this.nowPlaying.current_song.id.equals(nowPlaying.current_song.id)){
            this.nowPlaying = nowPlaying;

            songArtist.setText(nowPlaying.current_song.artist);
            songTitle.setText(nowPlaying.current_song.title);
            if(nowPlaying.event != null){
                event.setText(nowPlaying.event.toString());
            } else {
                event.setText("");
            }
            if(nowPlaying.event_upcoming != null){
                eventUpComing.setText(nowPlaying.event_upcoming.toString() + " in " + String.valueOf(nowPlaying.event_upcoming.minutes_until));
            } else {
                eventUpComing.setText("");
            }

            if(nowPlaying.current_song.external != null){
                if(nowPlaying.current_song.external.bronytunes != null){
                    PicassoWrapper.getSongPicasso(
                            getApplicationContext(),
                            BronyTunes.retrieveArtworkUri(nowPlaying.current_song.external.bronytunes.id, 150).toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }else if (nowPlaying.current_song.external.ponyfm != null){
                    PicassoWrapper.getSongPicasso(
                            getApplicationContext(),
                            nowPlaying.current_song.external.ponyfm.image_url.toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }else if(nowPlaying.current_song.external.eqbeats != null) {
                    PicassoWrapper.getSongPicasso(
                            getApplicationContext(),
                            nowPlaying.current_song.external.eqbeats.image_url.toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }

            }else{
                PicassoWrapper.getSongPicasso(getApplicationContext(), nowPlaying.station.shortcode).into(songImage);
            }

        }
    }

    public void onPlay(View view){

    }

}
