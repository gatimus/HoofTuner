package io.github.gatimus.hooftuner;

import android.app.Fragment;
import android.content.ComponentName;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import io.github.gatimus.hooftuner.customviews.VisualizerView;
import io.github.gatimus.hooftuner.pvl.NowPlaying;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class NowPlayingFragment extends Fragment {

    private static final String SHORT_CODE = "SHORT_CODE";

    private TextView listeners, songArtist, songTitle, event, eventUpComing, score;
    private ImageView songImage;
    private ToggleButton play;
    private MediaBrowser mMediaBrowser;
    private MediaController.TransportControls transportControls;
    private String stationShortcode;

    public static NowPlayingFragment newInstance(Station station) {
        NowPlayingFragment fragment = new NowPlayingFragment();
        Bundle args = new Bundle();
        args.putString(SHORT_CODE, station.shortcode);
        fragment.setArguments(args);
        return fragment;
    }

    public NowPlayingFragment() {
        //empty constructor
    }

    private MediaBrowser.ConnectionCallback mConnectionCallback = new MediaBrowser.ConnectionCallback() {
        @Override
        public void onConnected() {
            Log.i(getClass().getSimpleName(), "Connected");
            MediaController mMediaController = new MediaController(getActivity(), mMediaBrowser.getSessionToken());
            transportControls = mMediaController.getTransportControls();
            transportControls.playFromMediaId(String.valueOf(Cache.stations.get(stationShortcode).default_stream_id), null);
            mMediaController.registerCallback(mSessionCallback);
            mMediaBrowser.subscribe(String.valueOf(Cache.stations.get(stationShortcode).default_stream_id), new MediaBrowser.SubscriptionCallback(){});
        }

        @Override
        public void onConnectionSuspended() {
            Log.e(getClass().getSimpleName(), "ConnectionSuspended");
            super.onConnectionSuspended();
        }

        @Override
        public void onConnectionFailed() {
            Log.e(getClass().getSimpleName(), "ConnectionFailed");
            super.onConnectionFailed();
        }
    };

    private MediaController.Callback mSessionCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            if(state.getState() == PlaybackState.STATE_PLAYING){
                play.setEnabled(true);
                play.setChecked(true);
            }
            if(state.getState() == PlaybackState.STATE_BUFFERING){
                play.setEnabled(false);
            }
            if(state.getState() == PlaybackState.STATE_PAUSED || state.getState() == PlaybackState.STATE_STOPPED){
                play.setEnabled(true);
                play.setChecked(false);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            Log.v(getClass().getSimpleName(), metadata.toString());
            songArtist.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
            songTitle.setText(metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
            if(!metadata.getString(MediaMetadata.METADATA_KEY_ART_URI).isEmpty()){
                PicassoWrapper.getSongPicasso(getActivity(),metadata.getString(MediaMetadata.METADATA_KEY_ART_URI), stationShortcode);
            } else {
                PicassoWrapper.getSongPicasso(getActivity(), stationShortcode);
            }
            Log.v(getClass().getSimpleName(), stationShortcode);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            stationShortcode = getArguments().getString(SHORT_CODE);
        }

        mMediaBrowser = new MediaBrowser(
                getActivity(),
                new ComponentName(getActivity(), PVLMediaBrowserService.class),
                mConnectionCallback, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        mMediaBrowser.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view){
        score = (TextView) view.findViewById(R.id.score);
        listeners = (TextView) view.findViewById(R.id.listeners);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songTitle = (TextView) view.findViewById(R.id.song_title);
        event = (TextView) view.findViewById(R.id.event);
        eventUpComing = (TextView) view.findViewById(R.id.event_upcoming);
        songImage = (ImageView) view.findViewById(R.id.song_image);
        play = (ToggleButton) view.findViewById(R.id.play);
        /*
        play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(nowPlaying.station != null){
                    Intent iStop = new Intent(getActivity(), MusicService.class)
                        .setAction(MusicService.ACTION_STOP);
                    getActivity().startService(iStop);
                    if(isChecked){
                        Intent iStart = new Intent(getActivity(), MusicService.class)
                                .setAction(MusicService.ACTION_PLAY)
                                .putExtra(MusicService.KEY_STREAM_URL, nowPlaying.station.stream_url.toString());
                        getActivity().startService(iStart);
                    }
                }
            }
        });
        */

        //vis
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.vis_container);
        frameLayout.addView(new VisualizerView(getActivity().getApplicationContext()));
    }





    public void updateNowPlaying(NowPlaying nowPlaying){
        /*
        if(!this.nowPlaying.current_song.id.equals(nowPlaying.current_song.id)){
            this.nowPlaying = nowPlaying;
            score.setText(String.valueOf(nowPlaying.current_song.score));
            listeners.setText(String.valueOf(nowPlaying.listeners.current) + "{fa-user}");
            songArtist.setText(nowPlaying.current_song.artist);
            songTitle.setText(nowPlaying.current_song.title);
            if(nowPlaying.event != null){
                Iconify.addIcons(event);
                event.setText("On Air: " + nowPlaying.event.toString());
            } else {
                event.setText("");
            }
            if(nowPlaying.event_upcoming != null){
                eventUpComing.setText("Upcoming: " + nowPlaying.event_upcoming.toString());
            } else {
                eventUpComing.setText("");
            }

            if(nowPlaying.current_song.external != null){
                if(nowPlaying.current_song.external.bronytunes != null){
                    PicassoWrapper.getSongPicasso(
                            getActivity().getApplicationContext(),
                            nowPlaying.current_song.external.bronytunes.image_url.toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }else if (nowPlaying.current_song.external.ponyfm != null){
                    PicassoWrapper.getSongPicasso(
                            getActivity().getApplicationContext(),
                            nowPlaying.current_song.external.ponyfm.image_url.toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }else if(nowPlaying.current_song.external.eqbeats != null) {
                    PicassoWrapper.getSongPicasso(
                            getActivity().getApplicationContext(),
                            nowPlaying.current_song.external.eqbeats.image_url.toString(),
                            nowPlaying.station.shortcode
                    ).into(songImage);
                }

            }else{
                PicassoWrapper.getSongPicasso(getActivity().getApplicationContext(), nowPlaying.station.shortcode).into(songImage);
            }
        }
        */
    }

} //class
