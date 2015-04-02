package io.github.gatimus.hooftuner.utils;

import android.content.Context;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.service.media.MediaBrowserService;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.gatimus.hooftuner.Cache;
import io.github.gatimus.hooftuner.pvl.NowPlaying;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.StationList;
import io.github.gatimus.hooftuner.pvl.Stream;
import retrofit.Callback;
import retrofit.RetrofitError;

public class API2Media {

    public static final String METADATA_KEY_STREAM_URI = "METADATA_KEY_STREAM_URI";
    public static final String METADATA_KEY_SHORTCODE = "METADATA_KEY_SHORTCODE";

    static ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
    static ScheduledFuture scheduledUpdate;
    static String lastSong = "";

    public static void loadStations(final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result){
        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        if(Cache.stations != null){
            PonyvilleLive.getPonyvilleLiveInterface().listStations(Station.AUDIO, new Callback<Response<StationList>>() {
                @Override
                public void success(Response<StationList> listResponse, retrofit.client.Response response) {
                    Cache.stations = listResponse.result;
                    for(Station station : listResponse.result){
                        mediaItems.add(station.toMediaItem());
                    }
                    result.sendResult(mediaItems);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                }
            });
            result.detach();
        } else {
            for(Station station : Cache.stations){
                mediaItems.add(station.toMediaItem());
            }
            result.sendResult(mediaItems);
        }

    }

    public static void loadStreams(final String parentId, final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result, Context context){
        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        if(Cache.stations != null){
            for(Stream stream : Cache.stations.get(parentId).streams){
                mediaItems.add(stream.toMediaItem(context));
            }
        }
        result.sendResult(mediaItems);
    }

    public static void startNowPlayingStream(final int streamID, final CallBack cb){
        stopNowPlayingStream();
         Runnable updater = new Runnable() {
             @Override
             public void run() {
                 PonyvilleLive.getPonyvilleLiveInterface().nowPlaying(new Callback<Response<HashMap<String, NowPlaying>>>() {
                     @Override
                     public void success(Response<HashMap<String, NowPlaying>> hashMapResponse, retrofit.client.Response response) {
                         List<NowPlaying> nowPlayingList = new ArrayList<NowPlaying>(hashMapResponse.result.values());
                         for(NowPlaying nowPlaying : nowPlayingList){
                             for(Stream stream : nowPlaying.streams){
                                 if(streamID == stream.id){
                                     if(!lastSong.equals(stream.current_song.id)){
                                         cb.onNowPlaying(stream.toMediaItem(), stream.current_song.toMediaMetadata());
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
