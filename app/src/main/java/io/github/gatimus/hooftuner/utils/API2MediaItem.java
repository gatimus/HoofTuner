package io.github.gatimus.hooftuner.utils;

import android.annotation.TargetApi;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.os.Build;
import android.service.media.MediaBrowserService;
import java.util.ArrayList;
import java.util.List;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import retrofit.Callback;
import retrofit.RetrofitError;

public class API2MediaItem {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void loadStations(MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result){
        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        PonyvilleLive.getPonyvilleLiveInterface().listStations(new Callback<Response<List<Station>>>() {
            @Override
            public void success(Response<List<Station>> listResponse, retrofit.client.Response response) {
                for(Station station : listResponse.result){
                    MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, station.shortcode)
                            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, station.name)
                            .putString(MediaMetadata.METADATA_KEY_TITLE, station.name)
                            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, station.genre)
                            .putString(MediaMetadata.METADATA_KEY_GENRE, station.genre)
                            .putString(MediaMetadata.METADATA_KEY_ART_URI, station.image_url.toString())
                            .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, station.image_url.toString())
                            .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, station.image_url.toString())
                            .build();
                    mediaItems.add(new MediaBrowser.MediaItem(mediaMetadata.getDescription(), MediaBrowser.MediaItem.FLAG_BROWSABLE));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        result.sendResult(mediaItems);
    }

    public static void loadStreams(String parentId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result){

    }

}
