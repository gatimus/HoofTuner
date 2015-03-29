package io.github.gatimus.hooftuner.utils;

import android.content.Context;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;

import com.joanzapata.android.iconify.Iconify;

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
                        MediaBrowser.MediaItem mediaItem = new MediaBrowser.MediaItem(new MediaDescription.Builder()
                                .setMediaId(station.shortcode)
                                .setTitle(station.name)
                                .setSubtitle(station.genre)
                                .setIconUri(station.imageUri)
                                .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE);
                        mediaItems.add(mediaItem);
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
                MediaBrowser.MediaItem mediaItem = new MediaBrowser.MediaItem(new MediaDescription.Builder()
                        .setMediaId(station.shortcode)
                        .setTitle(station.name)
                        .setSubtitle(station.genre)
                        .setIconUri(station.imageUri)
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE);
                mediaItems.add(mediaItem);
            }
            result.sendResult(mediaItems);
        }

    }

    public static void loadStreams(final String parentId, final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result, Context context){
        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        final Bundle bundle = new Bundle();
        bundle.putString(METADATA_KEY_SHORTCODE, Cache.stations.get(parentId).shortcode);
        if(Cache.stations != null){
            for(Stream stream : Cache.stations.get(parentId).streams){
                Iconify.IconValue iconValue = Iconify.IconValue.fa_code_fork;
                if (stream.is_default){
                    iconValue = Iconify.IconValue.fa_star;
                }
                MediaBrowser.MediaItem mediaItem = new MediaBrowser.MediaItem(new MediaDescription.Builder()
                        .setMediaId(String.valueOf(stream.id))
                        .setTitle(stream.name)
                        .setIconBitmap(IconBitmap.getIconBitmap(context, iconValue))
                        .setExtras(bundle)
                        .build(), MediaBrowser.MediaItem.FLAG_PLAYABLE);
                mediaItems.add(mediaItem);
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
                                         MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                                                 .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, stream.current_song.id)
                                                 .putString(MediaMetadata.METADATA_KEY_ARTIST, stream.current_song.artist)
                                                 .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, stream.current_song.artist)
                                                 .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, stream.current_song.artist)
                                                 .putString(MediaMetadata.METADATA_KEY_TITLE, stream.current_song.title)
                                                 .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, stream.current_song.title)
                                                 .putString(MediaMetadata.METADATA_KEY_ART_URI, stream.current_song.imageUri.toString())
                                                 .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, stream.current_song.imageUri.toString())
                                                 .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, stream.current_song.imageUri.toString())
                                                 .putRating(MediaMetadata.METADATA_KEY_RATING, Rating.newThumbRating(false))
                                                 .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, stream.current_song.text)
                                                 .putString(METADATA_KEY_STREAM_URI, stream.uri.toString())
                                                 .build();
                                         cb.onNowPlaying(mediaMetadata);
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
        public void onNowPlaying(MediaMetadata nowPlaying);
    }

}
