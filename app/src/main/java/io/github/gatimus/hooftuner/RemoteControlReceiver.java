package io.github.gatimus.hooftuner;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.view.KeyEvent;

//delete
public class RemoteControlReceiver extends BroadcastReceiver {

    private MediaBrowser mediaBrowser;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            mediaBrowser = new MediaBrowser(
                    context,
                    new ComponentName(context, PVLMediaBrowserService.class),
                    new MediaBrowserConnectionCallback(context, intent),
                    null);
        }
    }

    private class MediaBrowserConnectionCallback extends MediaBrowser.ConnectionCallback{
        Context context;
        Intent intent;
        public MediaBrowserConnectionCallback(Context context, Intent intent){
            this.context = context;
            this.intent = intent;
        }
        @Override
        public void onConnected() {
            MediaController mediaController =  new MediaController(context, mediaBrowser.getSessionToken());
            MediaController.TransportControls transportControls = mediaController.getTransportControls();
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MEDIA_PAUSE :
                    transportControls.pause();
                    break;
                case  KeyEvent.KEYCODE_MEDIA_PLAY :
                    transportControls.play();
                    break;
                case  KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE :
                    if(mediaController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING){
                        transportControls.pause();
                    }else {
                        transportControls.play();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP :
                    transportControls.stop();
                    break;
            }
        }
    }

}
