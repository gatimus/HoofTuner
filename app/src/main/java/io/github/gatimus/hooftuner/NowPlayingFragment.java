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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import io.github.gatimus.hooftuner.customviews.VisualizerView;
import io.github.gatimus.hooftuner.pvl.Song;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.Stream;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class NowPlayingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private static final String SHORT_CODE = "SHORT_CODE";

    private TextView listeners, songArtist, songTitle, event, eventUpComing, score, buff;
    private ImageView songImage;
    private ToggleButton play;
    FrameLayout frameLayout;
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
                buff.clearAnimation();
                buff.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                play.setChecked(true);
            }
            if(state.getState() == PlaybackState.STATE_BUFFERING){
                play.setVisibility(View.INVISIBLE);
                buff.setVisibility(View.VISIBLE);
                if(getActivity() != null){
                    Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.buff);
                    buff.startAnimation(rotate);
                }

            }
            if(state.getState() == PlaybackState.STATE_PAUSED || state.getState() == PlaybackState.STATE_STOPPED){
                buff.clearAnimation();
                buff.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                play.setChecked(false);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            Log.v(getClass().getSimpleName(), metadata.toString());
            songArtist.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
            songTitle.setText(metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
            score.setText(metadata.getString(Song.KEY_SCORE));
            listeners.setText(metadata.getString(Stream.KEY_LISTENERS)+"{fa-user}");
            if(!metadata.getString(MediaMetadata.METADATA_KEY_ART_URI).isEmpty()){
                PicassoWrapper.getSongPicasso(getActivity(),metadata.getString(MediaMetadata.METADATA_KEY_ART_URI), stationShortcode).into(songImage);
            } else {
                PicassoWrapper.getSongPicasso(getActivity(), stationShortcode).into(songImage);
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
        play.setOnCheckedChangeListener(this);
        buff = (TextView) view.findViewById(R.id.buff);

        //vis
        frameLayout = (FrameLayout) view.findViewById(R.id.vis_container);
        frameLayout.addView(new VisualizerView(getActivity().getApplicationContext()));
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(transportControls != null){
            if(isChecked){
                transportControls.play();
            }else {
                transportControls.pause();
            }
        }
    }


} //class
