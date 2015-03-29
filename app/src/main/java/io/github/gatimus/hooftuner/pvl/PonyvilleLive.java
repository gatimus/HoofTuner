package io.github.gatimus.hooftuner.pvl;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import io.github.gatimus.hooftuner.BuildConfig;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

public class PonyvilleLive {



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

}
