package io.github.gatimus.hooftuner;

import android.content.res.Configuration;
import android.graphics.Typeface;
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
    private TextView songArtist;
    private TextView songTitle;
    //private TextView songDescription;
    //private TextView songLyrics;
    private ImageView songImage;
    private ImageView stationBG;
    private Station selectedStation;
    private APIWorker api;
    //private BGW bgw;
    private ScheduledExecutorService updateScheduler;
    private ScheduledFuture scheduledUpdate;
    private Updater updater;
    private List<Station> stations;
    private ArrayAdapter<Station> stationAdapter;
    private TweetFragment tweetFragment;

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
                tweetFragment = TweetFragment.newInstance(selectedStation);
                getFragmentManager().beginTransaction().replace(R.id.tweetFragmentContainer, tweetFragment).commit();
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
        });
        songArtist = (TextView) findViewById(R.id.songArtist);
        songArtist.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        songTitle = (TextView) findViewById(R.id.songTitle);
        songTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        //songDescription = (TextView) findViewById(R.id.songDescription);
        //songLyrics = (TextView) findViewById(R.id.songLyrics);
        songImage = (ImageView) findViewById(R.id.songImage);
        stationBG = (ImageView) findViewById(R.id.stationBG);
        api = new APIWorker();
        //bgw = new BGW();
        updateScheduler = Executors.newScheduledThreadPool(1);
        updater = new Updater();
        //bgw.execute();
        stations = Global.stations;
        stationAdapter = new StationAdapter(getApplicationContext(), stations);
        stationAdapter.setNotifyOnChange(true);
        listView.setAdapter(stationAdapter);



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
        scheduledUpdate = updateScheduler.scheduleWithFixedDelay(updater, 0 , 2000, TimeUnit.MILLISECONDS);
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
                Log.d(getClass().getSimpleName(), selectedStation.shortcode);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songArtist.setText(nowPlaying.current_song.artist);
                        songTitle.setText(nowPlaying.current_song.title);


                        Picasso picasso = Picasso.with(getApplicationContext());
                        if(BuildConfig.DEBUG){
                            picasso.setIndicatorsEnabled(true);
                        }
                        if(nowPlaying.current_song.external != null){
                            if(nowPlaying.current_song.external.bronytunes != null){
                                //songDescription.setText(nowPlaying.current_song.external.bronytunes.description);
                                //songLyrics.setText(nowPlaying.current_song.external.bronytunes.lyrics);
                                picasso.load(nowPlaying.current_song.external.bronytunes.image_url)
                                        .placeholder(android.R.drawable.stat_sys_download)
                                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                                        .into(songImage);
                            }else if (nowPlaying.current_song.external.ponyfm != null){
                                //songDescription.setText(nowPlaying.current_song.external.ponyfm.description);
                                //songLyrics.setText(nowPlaying.current_song.external.ponyfm.lyrics);
                                picasso.load(nowPlaying.current_song.external.ponyfm.image_url)
                                        .placeholder(android.R.drawable.stat_sys_download)
                                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                                        .into(songImage);
                            }else if(nowPlaying.current_song.external.eqbeats != null) {
                                picasso.load(nowPlaying.current_song.external.eqbeats.image_url)
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
                });
            }
        }
    }
}
