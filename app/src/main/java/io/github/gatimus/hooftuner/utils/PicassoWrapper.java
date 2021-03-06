package io.github.gatimus.hooftuner.utils;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import io.github.gatimus.hooftuner.R;

public class PicassoWrapper {

    public static Picasso getPicasso(Context context){
        Picasso picasso = Picasso.with(context);
        //if(BuildConfig.DEBUG)picasso.setIndicatorsEnabled(true);
        return picasso;
    }

    public static RequestCreator getStationPicasso(Context context, Uri uri){
        RequestCreator requestCreator = getPicasso(context).load(uri)
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon);
        return requestCreator;
    }

    public static RequestCreator getSongPicasso(Context context, String uri, String shortCode){
        RequestCreator requestCreator = getPicasso(context).load(uri);
        if(shortCode.equals("ponyvillefm")){
            requestCreator.placeholder(R.drawable.pvfm_default)
                    .error(R.drawable.pvfm_default);
        } else {
            requestCreator.placeholder(R.drawable.song_generic)
                    .error(R.drawable.song_generic);
        }
        return requestCreator;
    }

    public static RequestCreator getSongPicasso(Context context, String shortCode){
        Picasso picasso = getPicasso(context);
        RequestCreator requestCreator;
        if(shortCode.equals("ponyvillefm")){
            requestCreator = picasso.load(R.drawable.pvfm_default)
                    .placeholder(R.drawable.pvfm_default)
                    .error(R.drawable.pvfm_default);
        } else {
            requestCreator = picasso.load(R.drawable.song_generic)
                    .placeholder(R.drawable.song_generic)
                    .error(R.drawable.song_generic);
        }
        return requestCreator;
    }
}
