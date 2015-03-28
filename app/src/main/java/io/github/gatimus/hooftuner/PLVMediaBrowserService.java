package io.github.gatimus.hooftuner;

import android.annotation.TargetApi;
import android.media.browse.MediaBrowser;
import android.os.Build;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PLVMediaBrowserService extends MediaBrowserService {

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final String ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead";
    public static final String ANDROID_AUTO_SIMULATOR_PACKAGE_NAME = "com.google.android.mediasimulator";

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        if(getPackageName().equals(clientPackageName)){
            //if native app
        }
        if (ANDROID_AUTO_PACKAGE_NAME.equals(clientPackageName)) {
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

            // build the MediaItem objects for the top level,
            // and put them in the <result> list
        } else {

            // examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the <result> list
        }

        result.detach();
    }

}