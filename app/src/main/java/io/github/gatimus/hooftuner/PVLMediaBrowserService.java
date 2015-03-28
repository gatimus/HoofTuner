package io.github.gatimus.hooftuner;

import android.annotation.TargetApi;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.gatimus.hooftuner.utils.API2MediaItem;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PVLMediaBrowserService extends MediaBrowserService {

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final String ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead";
    public static final String ANDROID_AUTO_SIMULATOR_PACKAGE_NAME = "com.google.android.mediasimulator";

    @Override
    public void onCreate() {
        super.onCreate();
        MediaSession mSession = new MediaSession(this, "session tag");
        setSessionToken(mSession.getSessionToken());
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        if(getPackageName().equals(clientPackageName)){
            Log.i(getClass().getSimpleName(), "Native app");
            //if native app
        }
        if (ANDROID_AUTO_PACKAGE_NAME.equals(clientPackageName)) {
            Log.i(getClass().getSimpleName(), "Android Auto");
            // Optional: if your app needs to adapt ads, music library or anything
            // else that needs to run differently when connected to the car, this
            // is where you should handle it.
        }
        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {
        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MEDIA_ID_ROOT.equals(parentId)) {
            API2MediaItem.loadStations(result);
            // build the MediaItem objects for the top level,
            // and put them in the <result> list
        } else {

            // examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the <result> list
        }

        result.detach();
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            Log.i(getClass().getSimpleName(), "Play");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.i(getClass().getSimpleName(), "Pause");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(getClass().getSimpleName(), "Stop");
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.i(getClass().getSimpleName(), "play" + mediaId);
        }
    }

}