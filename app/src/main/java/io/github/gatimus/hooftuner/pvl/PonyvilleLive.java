package io.github.gatimus.hooftuner.pvl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.Date;

import io.github.gatimus.hooftuner.BuildConfig;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

public class PonyvilleLive {

    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";

    public static interface PonyvilleLiveInterface{

        @GET("/nowplaying")
        void nowPlaying(Callback<Response<NowPlaying>> cb);
        @GET("/nowplaying/index/id/{id}")
        void nowPlaying(@Path("id")int id, Callback<Response<NowPlaying>> cb);
        @GET("/nowplaying/index/station/{shortcode}")
        void nowPlaying(@Path("shortcode")String shortcode, Callback<Response<NowPlaying>> cb);

        //Schedules

        //Songs
        @GET("/song/like/sh_id/{sh_id}")
        void likeSong(@Path("sh_id")int sh_id, Callback<Response<String>> cb);
        @GET("/song/dislike/sh_id/{sh_id}")
        void dislikeSong(@Path("sh_id")int sh_id, Callback<Response<String>> cb);
        @GET("/song/clearvote/sh_id/{sh_id}")
        void clearVote(@Path("sh_id")int sh_id, Callback<Response<String>> cb);

        @GET("/station/list")
        void listStations(Callback<Response<Station[]>> cb);
        @GET("/station/list/category/{audio|video}")
        void listStations(@Path("audio|video")String category, Callback<Response<Station[]>> cb);


        @GET("/index/status")
        void getStatus(Callback<Response<Status>> cb);


    }

    public static PonyvilleLiveInterface getPonyvilleLiveInterface(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(URL.class, new URLDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ponyvillelive.com/api")
                .setConverter(new GsonConverter(gson))
                .build();

        if(BuildConfig.DEBUG){
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(PonyvilleLiveInterface.class);
    }

}
