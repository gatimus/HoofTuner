package io.github.gatimus.hooftuner;

import android.content.Intent;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.StationList;
import io.github.gatimus.hooftuner.pvl.Stream;
import io.github.gatimus.hooftuner.utils.MediaPlayerHandler;
import retrofit.Callback;
import retrofit.RetrofitError;

public class PVLMediaBrowserService extends MediaBrowserService {

    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private MediaSession mSession;
    private MediaPlayerHandler mediaPlayerHandler;
    private String lastStation;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSession(this, getClass().getSimpleName());
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mediaPlayerHandler = new MediaPlayerHandler(mSession, getApplicationContext());
    }

    @Override
    public void onDestroy() {
        mediaPlayerHandler.stopPlayback();
        super.onDestroy();
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        if(Cache.stations.isEmpty()){
            PonyvilleLive.getPonyvilleLiveInterface().listStations(Station.AUDIO, new Callback<Response<StationList>>() {
                @Override
                public void success(Response<StationList> stationListResponse, retrofit.client.Response response) {
                    Cache.stations = stationListResponse.result;
                    notifyChildrenChanged(MEDIA_ID_ROOT);
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                }
            });
        }
        return new BrowserRoot(MEDIA_ID_ROOT, rootHints);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {
        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        // Check if this is the root menu:
        if (MEDIA_ID_ROOT.equals(parentId)) {
            for(Station station : Cache.stations){
                mediaItems.add(station.toMediaItem());
            }
        } else if(Cache.stations.get(parentId) != null){
            lastStation = parentId;
            for(Stream stream : Cache.stations.get(parentId).streams){
                mediaItems.add(stream.toMediaItem(getApplicationContext()));
            }
        }
        result.sendResult(mediaItems);
    }

    private final class MediaSessionCallback extends MediaSession.Callback implements PonyvilleLive.CallBack{
        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            boolean handled = false;
            if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())) {
                handled = true;
                KeyEvent event = (KeyEvent)mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                switch (event.getKeyCode()){
                    case KeyEvent.KEYCODE_MEDIA_PAUSE :
                        mSession.getController().getTransportControls().play();
                        break;
                    case  KeyEvent.KEYCODE_MEDIA_PLAY :
                        mSession.getController().getTransportControls().play();
                        break;
                    case  KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE :
                        if(mSession.getController().getPlaybackState().getState() == PlaybackState.STATE_PLAYING){
                            mSession.getController().getTransportControls().pause();
                        }else {
                            mSession.getController().getTransportControls().play();
                        }
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP :
                        mSession.getController().getTransportControls().stop();
                        break;
                    default : handled = false;
                }
            }
            return handled;
        }

        @Override
        public void onPlay() {
            mediaPlayerHandler.start();
        }

        @Override
        public void onPause() {
            mediaPlayerHandler.pause();
        }

        @Override
        public void onStop() {
            PonyvilleLive.stopNowPlayingStream();
            mediaPlayerHandler.stopPlayback();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if(mediaPlayerHandler.playerExists()){
                onStop();
            }
            PonyvilleLive.startNowPlayingStream(lastStation, Integer.parseInt(mediaId), this);
            mSession.setPlaybackState(new PlaybackState.Builder().setState(
                    PlaybackState.STATE_BUFFERING,
                    PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                    0).build());
        }

        @Override
        public void onSetRating(Rating rating) {
            if(rating.isThumbUp()){
                //TODO likeSong
            } else {
                //TODO clearVote
            }
            super.onSetRating(rating);
        }

        @Override
        public void onNowPlaying(MediaBrowser.MediaItem stream, MediaMetadata nowPlaying) {
            mSession.setMetadata(nowPlaying);
            if(!mediaPlayerHandler.playerExists()){
                mediaPlayerHandler.playRemoteResource((Uri)stream.getDescription().getExtras().getParcelable(Stream.KEY_STREAM_URI));
            }
        }

    }

}