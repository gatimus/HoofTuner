package io.github.gatimus.hooftuner;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
    private TextView songArtist, songTitle, event, eventUpComing;
    //private TextView songDescription;
    //private TextView songLyrics;
    private ImageView songImage, stationBG;
    private Station selectedStation;
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
        setContentView(R.layout.activity_main);


        //setup drawer
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
                selectedStation = (Station) parent.getItemAtPosition(position);
                Intent iStop = new Intent(Main.this, MusicService.class)
                        .setAction(MusicService.ACTION_STOP);
                Main.this.startService(iStop);
                actionBar.setTitle(selectedStation.name);
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

                    tweetFragment = TweetFragment.newInstance(selectedStation);


                    //getFragmentManager().beginTransaction().replace(R.id.tweetFragmentContainer, tweetFragment).commit();

                }

            }
        });
        //ui ref
        songArtist = (TextView) findViewById(R.id.songArtist);
        songArtist.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        songTitle = (TextView) findViewById(R.id.songTitle);
        songTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        event = (TextView) findViewById(R.id.event);
        event.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        eventUpComing = (TextView) findViewById(R.id.event_upcoming);
        eventUpComing.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        //songDescription = (TextView) findViewById(R.id.songDescription);
        //songLyrics = (TextView) findViewById(R.id.songLyrics);
        songImage = (ImageView) findViewById(R.id.songImage);
        stationBG = (ImageView) findViewById(R.id.stationBG);
        //api = new APIWorker();
        //threading
        updateScheduler = Executors.newScheduledThreadPool(1);
        updater = new Updater();
        //adaptor
        stations = Global.stations;
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
        NowPlaying nowPlaying = nowPlayingResponse.result;
        Log.v(getClass().getSimpleName(), nowPlaying.current_song.text);
        Log.d(getClass().getSimpleName(), selectedStation.shortcode);
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
        Picasso picasso = Picasso.with(getApplicationContext());
        if(BuildConfig.DEBUG){
            picasso.setIndicatorsEnabled(true);
        }
        if(nowPlaying.current_song.external != null){
            if(nowPlaying.current_song.external.bronytunes != null){
                //songDescription.setText(nowPlaying.current_song.external.bronytunes.description);
                //songLyrics.setText(nowPlaying.current_song.external.bronytunes.lyrics);
                picasso.load(nowPlaying.current_song.external.bronytunes.image_url.toString())
                        .placeholder(android.R.drawable.stat_sys_download)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(songImage);
            }else if (nowPlaying.current_song.external.ponyfm != null){
                //songDescription.setText(nowPlaying.current_song.external.ponyfm.description);
                //songLyrics.setText(nowPlaying.current_song.external.ponyfm.lyrics);
                picasso.load(nowPlaying.current_song.external.ponyfm.image_url.toString())
                        .placeholder(android.R.drawable.stat_sys_download)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(songImage);
            }else if(nowPlaying.current_song.external.eqbeats != null) {
                picasso.load(nowPlaying.current_song.external.eqbeats.image_url.toString())
                        .placeholder(android.R.drawable.stat_sys_download)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(songImage);
            }
        }else if (selectedStation.shortcode.equals("ponyvillefm")) {
            picasso.load("http://ponyvillefm.com/images/music/default.png")
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(songImage);
        }else if(selectedStation.shortcode.equals("fillydelphia_radio")){
            picasso.load("https://fillydelphiaradio.net/wp-content/themes/delphia_reimagined/player/nocover.jpg")
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(songImage);
        }else{
            picasso.load("http://ponyvillelive.com/static/images/song_generic.png")
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(songImage);
        }
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

    /*
    public class InfoPageAdapter extends FragmentPagerAdapter {

        public InfoPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragement = new Fragment();
            switch (position){
                case 0:
                    fragement = tweetFragment;
                    break;
                case 1:
                    fragement = new Fragment();
                    break;
                default:
                    fragement = new Fragment();
                    break;
            }
            return fragement;
            //return new TweetFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence charSequence = "";
            switch (position){
                case 0:
                    charSequence = "Tweets";
                    break;
                case 1:
                    charSequence = "Tab 2";
                    break;
                default:
                    charSequence = "default";
                    break;
            }
            return charSequence;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
    */


    public void setBackGround(){
        Picasso picasso = Picasso.with(getApplicationContext());
        if (BuildConfig.DEBUG) {
            picasso.setIndicatorsEnabled(true);
        }
        switch (selectedStation.shortcode) {
            case "ponyvillefm":
                picasso.load("http://www.ponyvillefm.com/images/covers/aeriel-cover1.png")
                        .into(stationBG);
                break;
            case "luna_radio":
                picasso.load("http://www.lunaloves.us/cache/thumbs/2668ef27702f31d6d007ab88228f3a39-contain-800x500.png")
                        .into(stationBG);
                break;
            case "fillydelphia_radio":
                picasso.load("https://fillydelphiaradio.net/wp-content/uploads/2014/10/cityscape-day.jpg")
                        .into(stationBG);
                break;
            case "celestia_radio":
                picasso.load("http://celestiaradio.com/wp-content/themes/CelestiaRadio/Images/BG/MPC-Layer-for.png")
                        .into(stationBG);
                break;
            case "best_pony_radio" :
                picasso.load("http://www.bestponyradio.com/bprbg.jpg")
                        .resize(2048,2048)
                        .into(stationBG);
                break;
            case "sonic_radioboom" :
                picasso.load("http://sonicradioboom.co.uk/wp-content/uploads/2013/10/bodybg.jpg")
                        .into(stationBG);
                break;
            case "alicorn_radio" :
                picasso.load("http://alicornradio.com/wp-content/uploads/2015/01/copy-alicorn_radio_header_by_giratina3456-d6j5lxb.jpg")
                        .into(stationBG);
                break;
            case "the_hive_radio" :
                picasso.load("https://hiveradio.net/wp-content/themes/The%20Hive%20Radio/images/site-background.jpg")
                        .into(stationBG);
                break;
            case "wonderbolt_radio" :
                //TODO
                break;
            case "everypony_radio" :
                picasso.load("http://www.everypony.com/forums/images/springbg.png")
                        .into(stationBG);
                break;
            case "bronydom_radio" :
                picasso.load("http://www.bronydom.net/resources/castle.png")
                        .into(stationBG);
                break;
            case "radio_brony" :
                picasso.load("http://www.radiobrony.fr/wp-content/uploads/2013/01/fondrb.png")
                        .into(stationBG);
                break;
            case "brony_radio_germany" :
                picasso.load("http://www.bronyradiogermany.com/wp-content/themes/brg_winter/image/header_1.png")
                        .into(stationBG);
                break;
            case "bronies_radio_la" :
                picasso.load("http://www.mlp-la.com/wp-content/themes/MLP/images/bg2.jpg")
                        .into(stationBG);
                break;
            case "powerponies_radio" :
                picasso.load("http://www.powerponies.cz/img/bg5.jpg")
                        .into(stationBG);
                break;
            case "radio_mybrony" :
                picasso.load("http://radio.mybrony.ru/wp-content/uploads/2015/01/artworks-000068981865-kma2ds-original.png")
                        .into(stationBG);
                break;
            default :
                //TODO
                break;
        }
    }

}
