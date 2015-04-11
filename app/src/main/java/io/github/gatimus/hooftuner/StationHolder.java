package io.github.gatimus.hooftuner;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class StationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView stationImage;
    private TextView name;
    private TextView genre;
    private IconTextView category;

    public StationHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        stationImage = (ImageView) itemView.findViewById(R.id.stationImage);
        name = (TextView) itemView.findViewById(android.R.id.text1);
        genre = (TextView) itemView.findViewById(android.R.id.text2);
        category = (IconTextView) itemView.findViewById(R.id.category);
    }

    public void setUI(Context context, MediaBrowser.MediaItem mediaItem){
        name.setText(mediaItem.getDescription().getTitle());
        genre.setText(mediaItem.getDescription().getSubtitle());
        Picasso.with(context).load(mediaItem.getDescription().getIconUri()).into(stationImage);
        PicassoWrapper.getStationPicasso(context, mediaItem.getDescription().getIconUri())
                .into(stationImage);
    }

    @Override
    public void onClick(View v) {

    }

}
