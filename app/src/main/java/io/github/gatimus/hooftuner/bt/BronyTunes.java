package io.github.gatimus.hooftuner.bt;

import io.github.gatimus.hooftuner.BuildConfig;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public class BronyTunes {

    public static interface BronyTunesInterface{

        @GET("/retrieve_artwork.php")
        void retrieveArtwork(@Query("song_id")int song_id, Callback<Response> cb);
        @GET("/retrieve_artwork.php")
        void retrieveArtwork(@Query("song_id")int song_id, @Query("size")int size,Callback<Response> cb);

        @GET("/retrieve_song.php")
        void retrieveSong(@Query("song_id")int song_id, Callback<Response> cb);
        @GET("/retrieve_song.php")
        void retrieveSong(@Query("song_id")int song_id, @Query("client_type")String client_type, Callback<Response> cb);

    }

    public static BronyTunesInterface getBronyTunesInterface(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://bronytunes.com")
                .build();

        if(BuildConfig.DEBUG){
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(BronyTunesInterface.class);
    }
}
