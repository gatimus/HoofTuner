package io.github.gatimus.hooftuner.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import java.io.IOException;

public class MediaPlayerHandler implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnBufferingUpdateListener{

    private MediaSession mediaSession;
    private MediaPlayer player;
    private WifiManager.WifiLock wifiLock;
    private Context context;
    private Uri currentUri;

    public MediaPlayerHandler(MediaSession mediaSession, Context context){
        this.mediaSession = mediaSession;
        this.context = context;
    }

    private void constructPlayer() {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
    }

    public void playRemoteResource(Uri url) {
        currentUri = url;
        constructPlayer();
        requestAudioFocus();
        try {
            player.setDataSource(context, url);
        } catch (IllegalStateException | IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
        player.prepareAsync();
        wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, getClass().getSimpleName());
    }

    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            requestAudioFocus();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mediaSession.setPlaybackState(new PlaybackState.Builder().setState(
                PlaybackState.STATE_PLAYING,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                1).build());
    }

    public void stopPlayback(){
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
        mediaSession.setPlaybackState(new PlaybackState.Builder().setState(
                PlaybackState.STATE_STOPPED,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                0).build());
    }

    public boolean playerExists(){
        return player != null;
    };

    public void start(){
        if(playerExists()){
            if(!player.isPlaying()){
                player.start();
                mediaSession.setPlaybackState(new PlaybackState.Builder().setState(
                        PlaybackState.STATE_BUFFERING,
                        PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                        0).build());
            }
        }
    }

    public void pause(){
        if(playerExists()){
            if(player.isPlaying()){
                player.pause();
                mediaSession.setPlaybackState(new PlaybackState.Builder().setState(
                        PlaybackState.STATE_PAUSED,
                        PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                        0).build());
            }
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if(player != null) {
                    if(!player.isPlaying()) start();
                    player.setVolume(1.0f, 1.0f);
                } else {
                    constructPlayer();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopPlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(player != null) {
                    player.setVolume(0.3f, 0.3f);
                }
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mediaSession.setPlaybackState(new  PlaybackState.Builder()
                .setBufferedPosition((long)percent)
                .setState(
                        PlaybackState.STATE_BUFFERING,
                        PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                        0)
                .build());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        boolean handled = false;
        if(currentUri != null){
            playRemoteResource(currentUri);
            handled = true;
        }
        return handled;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        boolean handled = false;
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                mediaSession.setPlaybackState(new  PlaybackState.Builder()
                        .setBufferedPosition(0L)
                        .setState(
                                PlaybackState.STATE_BUFFERING,
                                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                                0)
                        .build());
                handled = true;
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mediaSession.setPlaybackState(new  PlaybackState.Builder()
                        .setBufferedPosition(100L)
                        .setState(
                                PlaybackState.STATE_PLAYING,
                                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                                1)
                        .build());
                handled = true;
                break;
            default:
                handled = false;
        }
        return handled;
    }
}
