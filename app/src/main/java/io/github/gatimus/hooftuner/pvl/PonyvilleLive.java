package io.github.gatimus.hooftuner.pvl;


import io.github.gatimus.hooftuner.BuildConfig;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

public class PonyvilleLive {

    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";

    public static interface PonyvilleLiveInterface{

        @GET("/nowplaying")
        void nowPlaying(Callback<APIResponse> cb);
        @GET("/nowplaying/index/id/{id}")
        void nowPlaying(@Path("id")int id, Callback<APIResponse> cb);
        @GET("/nowplaying/index/station/{shortcode}")
        void nowPlaying(@Path("shortcode")String shortcode, Callback<APIResponse> cb);

        //Schedules


        @GET("/index/status")
        void getStatus(Callback<APIResponse> cb);


    }

    public static PonyvilleLiveInterface getPoPonyvilleLiveInterface(){

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ponyvillelive.com/api")
                //.setConverter(new GsonConverter(gson))
                .build();

        if(BuildConfig.DEBUG){
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(PonyvilleLiveInterface.class);
    }

}
