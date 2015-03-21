package io.github.gatimus.hooftuner;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoWrapper {

    public static Picasso getPicasso(Context context){
        Picasso picasso = Picasso.with(context);
        if(BuildConfig.DEBUG)picasso.setIndicatorsEnabled(true);
        return picasso;
    }

    public static RequestCreator getStationPicasso(Context context, String url){
        RequestCreator requestCreator = getPicasso(context).load(url)
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon);
        return requestCreator;
    }
}
