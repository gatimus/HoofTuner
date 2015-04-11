package io.github.gatimus.hooftuner.pvl;

import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.gatimus.hooftuner.BuildConfig;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

public class PonyvilleLive {

    static ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
    static ScheduledFuture scheduledUpdate;
    static String lastSong = "";

    public static interface PonyvilleLiveInterface{

        //Now Playing
        @GET("/nowplaying")
        void nowPlaying(Callback<Response<HashMap<String,NowPlaying>>> cb);
        @GET("/nowplaying/index/id/{id}")
        void nowPlaying(@Path("id")int id, Callback<Response<NowPlaying>> cb);
        @GET("/nowplaying/index/station/{shortcode}")
        void nowPlaying(@Path("shortcode")String shortcode, Callback<Response<NowPlaying>> cb);

        //Schedules
        //TODO

        //Songs
        @GET("/song/index/id/{song_id}")
        void getSong(@Path("id")String id, Callback<Song> cb);
        @GET("/song/like/sh_id/{sh_id}")
        void likeSong(@Path("sh_id")int sh_id, Callback<Response<String>> cb);
        @GET("/song/dislike/sh_id/{sh_id}")
        void dislikeSong(@Path("sh_id")int sh_id, Callback<Response<String>> cb);
        @GET("/song/clearvote/sh_id/{sh_id}")
        void clearVote(@Path("sh_id")int sh_id, Callback<Response<String>> cb);

        //Stations
        @GET("/station/list")
        void listStations(Callback<Response<StationList>> cb);
        @GET("/station/list/category/{category}")
        void listStations(@Path("category")String category, Callback<Response<StationList>> cb);
        @GET("/station/index/id/{id}")
        void getStation(@Path("id")int id, Callback<Station> cb);
        @GET("/station/index/station/{shortcode}")
        void getStation(@Path("shortcode")String shortcode, Callback<Station> cb);

        //Shows & Podcasts
        //TODO

        //Utilities
        void getAPI(Callback<String> cb);
        @GET("/index/status")
        void getStatus(Callback<Response<Status>> cb);

    }

    public static PonyvilleLiveInterface getPonyvilleLiveInterface(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(URL.class, new URLDeserializer())
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .registerTypeAdapter(Event.class, new EventDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ponyvillelive.com/api")
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();

        if(BuildConfig.DEBUG){
            //restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(PonyvilleLiveInterface.class);
    }

    public static void startNowPlayingStream(final String shortCode, final int streamID, final CallBack cb){
        stopNowPlayingStream();
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                getPonyvilleLiveInterface().nowPlaying(new Callback<Response<HashMap<String, NowPlaying>>>() {

                    @Override
                    public void success(Response<HashMap<String, NowPlaying>> hashMapResponse, retrofit.client.Response response) {
                        List<NowPlaying> nowPlayingList = new ArrayList<NowPlaying>(hashMapResponse.result.values());
                        for(NowPlaying nowPlaying : nowPlayingList){
                            for(Stream stream : nowPlaying.streams){
                                if(streamID == stream.id){
                                    if(!lastSong.equals(stream.current_song.id)){
                                        cb.onNowPlaying(stream.toMediaItem(), nowPlaying.toMediaMetadata(stream));
                                        lastSong = stream.current_song.id;
                                    }
                                }
                            }
                        }
                    }


                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(getClass().getSimpleName(), error.toString());
                    }
                });
            }
        };
        scheduledUpdate = updateScheduler.scheduleWithFixedDelay(updater, 0 , 5_000, TimeUnit.MILLISECONDS);
    }

    public static void stopNowPlayingStream(){
        if(scheduledUpdate != null){
            scheduledUpdate.cancel(true);
        }
    }

    public interface CallBack{
        public void onNowPlaying(MediaBrowser.MediaItem stream, MediaMetadata nowPlaying);
    }

}
