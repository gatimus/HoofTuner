package io.github.gatimus.hooftuner.utils;

import android.annotation.TargetApi;
import android.media.MediaDescription;
import android.media.browse.MediaBrowser;
import android.os.Build;
import android.service.media.MediaBrowserService;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import retrofit.Callback;
import retrofit.RetrofitError;

public class API2MediaItem {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void loadStations(final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result){
        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        PonyvilleLive.getPonyvilleLiveInterface().listStations(new Callback<Response<List<Station>>>() {
            @Override
            public void success(Response<List<Station>> listResponse, retrofit.client.Response response) {
                for(Station station : listResponse.result){

                        MediaBrowser.MediaItem mediaItem = new MediaBrowser.MediaItem(new MediaDescription.Builder()
                                .setMediaId(station.shortcode)
                                .setTitle(station.name)
                                .setSubtitle(station.genre)
                                //.setIconUri(station.image_url)
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

    }

    public static void loadStreams(String parentId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result){

    }

}
