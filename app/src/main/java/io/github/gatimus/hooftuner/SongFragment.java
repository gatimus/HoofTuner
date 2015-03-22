package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.gatimus.hooftuner.pvl.Station;

public class SongFragment extends Fragment {

    private ImageView stationBG;
    private ImageView songImage;
    private TextView songArtist;
    private TextView songTitle;

    private Station station;

    public SongFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //TODO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        stationBG = (ImageView) view.findViewById(R.id.stationBG);
        songImage = (ImageView) view.findViewById(R.id.song_image);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songTitle = (TextView) view.findViewById(R.id.song_title);
        setBackground();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO
    }

    public void setBackground(){
        Picasso picasso = Picasso.with(getActivity());
        if (BuildConfig.DEBUG) picasso.setIndicatorsEnabled(true);
        switch (station.shortcode) {
            case "ponyvillefm":
                picasso.load("http://www.ponyvillefm.com/images/covers/aeriel-cover1.png")
                        .into(stationBG);
                break;
            case "luna_radio":
                picasso.load("http://www.lunaloves.us/cache/thumbs/2668ef27702f31d6d007ab88228f3a39-contain-800x500.png")
                        .into(stationBG);
                break;
            case "fillydelphia_radio":
                picasso.load("https://fillydelphiaradio.net/wp-content/uploads/2014/10/cityscape-day.jpg")
                        .into(stationBG);
                break;
            case "celestia_radio":
                picasso.load("http://celestiaradio.com/wp-content/themes/CelestiaRadio/Images/BG/MPC-Layer-for.png")
                        .into(stationBG);
                break;
            case "best_pony_radio" :
                picasso.load("http://www.bestponyradio.com/bprbg.jpg")
                        .resize(2048,2048)
                        .into(stationBG);
                break;
            case "sonic_radioboom" :
                picasso.load("http://sonicradioboom.co.uk/wp-content/uploads/2013/10/bodybg.jpg")
                        .into(stationBG);
                break;
            case "alicorn_radio" :
                picasso.load("http://alicornradio.com/wp-content/uploads/2015/01/copy-alicorn_radio_header_by_giratina3456-d6j5lxb.jpg")
                        .into(stationBG);
                break;
            case "the_hive_radio" :
                picasso.load("https://hiveradio.net/wp-content/themes/The%20Hive%20Radio/images/site-background.jpg")
                        .into(stationBG);
                break;
            case "wonderbolt_radio" :
                stationBG.setImageBitmap(null);
                break;
            case "everypony_radio" :
                picasso.load("http://www.everypony.com/forums/images/springbg.png")
                        .into(stationBG);
                break;
            case "bronydom_radio" :
                picasso.load("http://www.bronydom.net/resources/castle.png")
                        .into(stationBG);
                break;
            case "radio_brony" :
                picasso.load("http://www.radiobrony.fr/wp-content/uploads/2013/01/fondrb.png")
                        .into(stationBG);
                break;
            case "brony_radio_germany" :
                picasso.load("http://www.bronyradiogermany.com/wp-content/themes/brg_winter/image/header_1.png")
                        .into(stationBG);
                break;
            case "bronies_radio_la" :
                picasso.load("http://www.mlp-la.com/wp-content/themes/MLP/images/bg2.jpg")
                        .into(stationBG);
                break;
            case "powerponies_radio" :
                picasso.load("http://www.powerponies.cz/img/bg5.jpg")
                        .into(stationBG);
                break;
            case "radio_mybrony" :
                picasso.load("http://radio.mybrony.ru/wp-content/uploads/2015/01/artworks-000068981865-kma2ds-original.png")
                        .into(stationBG);
                break;
            default :
                stationBG.setImageBitmap(null);
                break;
        } //switch
    } //setBackground

} //class
