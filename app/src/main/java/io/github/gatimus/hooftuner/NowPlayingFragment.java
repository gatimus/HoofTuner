package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.joanzapata.android.iconify.Iconify;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.gatimus.hooftuner.customviews.VisualizerView;
import io.github.gatimus.hooftuner.pvl.NowPlaying;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;

public class NowPlayingFragment extends Fragment implements Callback<Response<NowPlaying>> {

    private static final String SHORT_CODE = "SHORT_CODE";

    private TextView listeners, songArtist, songTitle, event, eventUpComing, score;
    private ImageView songImage;
    private ToggleButton play;

    private NowPlaying nowPlaying = new NowPlaying();
    private ScheduledExecutorService updateScheduler;
    private ScheduledFuture scheduledUpdate;
    private Updater updater;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //TODO

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            stationShortcode = getArguments().getString(SHORT_CODE);
        }
        //threading
        updateScheduler = Executors.newScheduledThreadPool(1);
        updater = new Updater();
        scheduledUpdate = updateScheduler.scheduleWithFixedDelay(updater, 0 , 5_000, TimeUnit.MILLISECONDS);
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
        //listeners = (TextView) view.findViewById(R.id.listeners);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songTitle = (TextView) view.findViewById(R.id.song_title);
        event = (TextView) view.findViewById(R.id.event);
        eventUpComing = (TextView) view.findViewById(R.id.event_upcoming);
        songImage = (ImageView) view.findViewById(R.id.song_image);
        play = (ToggleButton) view.findViewById(R.id.play);
        play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        });

        //vis
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.vis_container);
        frameLayout.addView(new VisualizerView(getActivity().getApplicationContext()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO
        scheduledUpdate.cancel(true);
    }

    @Override
    public void success(Response<NowPlaying> nowPlayingResponse, retrofit.client.Response response) {
        updateNowPlaying(nowPlayingResponse.result);
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(getClass().getSimpleName(), error.toString());
    }

    public class Updater implements Runnable {
        @Override
        public void run() {
            if(!stationShortcode.equals("")){
                PonyvilleLive.getPonyvilleLiveInterface().nowPlaying(stationShortcode, NowPlayingFragment.this);
            }
        }
    }

    public void updateNowPlaying(NowPlaying nowPlaying){
        if(!this.nowPlaying.current_song.id.equals(nowPlaying.current_song.id)){
            this.nowPlaying = nowPlaying;
            score.setText(String.valueOf(nowPlaying.current_song.score));
            //listeners.setText(String.valueOf(nowPlaying.listeners.current) + "{fa-user}");
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
    }

} //class
