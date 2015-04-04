package io.github.gatimus.hooftuner;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.KeyEvent;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.StationList;
import io.github.gatimus.hooftuner.pvl.Stream;
import io.github.gatimus.hooftuner.utils.API2Media;
import retrofit.Callback;
import retrofit.RetrofitError;

public class PVLMediaBrowserService extends MediaBrowserService implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener{

    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private MediaSession mSession;
    private MediaPlayer player;
    private WifiManager.WifiLock wifiLock;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSession(this, getClass().getSimpleName());
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
    }

    @Override
    public void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        //?
        PonyvilleLive.getPonyvilleLiveInterface().listStations(Station.AUDIO, new Callback<Response<StationList>>() {
            @Override
            public void success(Response<StationList> stationListResponse, retrofit.client.Response response) {
                Cache.stations = stationListResponse.result;
                notifyChildrenChanged(MEDIA_ID_ROOT);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return new BrowserRoot(MEDIA_ID_ROOT, rootHints);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {
        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        // Check if this is the root menu:
        if (MEDIA_ID_ROOT.equals(parentId)) {
            //API2Media.loadStations(result);
            for(Station station : Cache.stations){
                mediaItems.add(station.toMediaItem());
            }
        } else if(!StringUtils.isNumeric(parentId)) {
            //API2Media.loadStreams(parentId, result, getApplicationContext());
            for(Stream stream : Cache.stations.get(parentId).streams){
                mediaItems.add(stream.toMediaItem(getApplicationContext()));
            }
        }
        result.sendResult(mediaItems);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // TODO: We've gained audio focus, handle it
                if(player != null) {
                    if(!player.isPlaying()) player.start();
                    player.setVolume(1.0f, 1.0f);
                } else {
                    // TODO: Init the player?
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopPlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(player != null) {
                    player.setVolume(0.3f, 0.3f);
                }
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_PLAYING, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1).build());
    }

    private void constructPlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
    }

    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // TODO: We need to handle the case where we can't get audio focus
        }
    }

    private void stopPlayback(){
        if(player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if(wifiLock != null)
            if(wifiLock.isHeld()){
                wifiLock.release();
                wifiLock = null;
            }
        mSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0).build());
    }


    private void playRemoteResource(String url) {
        constructPlayer();
        requestAudioFocus();
        try {
            player.setDataSource(url);
        } catch (IllegalStateException | IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
        player.prepareAsync();
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, getClass().getSimpleName());
    }

    private void playRemoteResource(Uri url) {
        constructPlayer();
        requestAudioFocus();
        try {
            player.setDataSource(getApplicationContext(), url);
        } catch (IllegalStateException | IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
        player.prepareAsync();
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, getClass().getSimpleName());
    }


    private final class MediaSessionCallback extends MediaSession.Callback implements API2Media.CallBack{
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
            if(player != null){
                if(!player.isPlaying()){
                    player.start();
                    mSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_BUFFERING,PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0).build());
                }
            }
        }

        @Override
        public void onPause() {
            if(player != null){
                if(player.isPlaying()){
                    player.pause();
                    mSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_PAUSED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0).build());
                }
            }
        }

        @Override
        public void onStop() {
            API2Media.stopNowPlayingStream();
            stopPlayback();

        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if(player != null){
                onStop();
            }
            API2Media.startNowPlayingStream(Integer.parseInt(mediaId), this);
            mSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_BUFFERING,PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0).build());
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
            if(player == null){
                playRemoteResource((Uri)stream.getDescription().getExtras().getParcelable(Stream.KEY_STREAM_URI));
            }
        }

    }

}