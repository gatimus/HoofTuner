package io.github.gatimus.hooftuner;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class MediaItemAdapter extends ArrayAdapter<MediaBrowser.MediaItem>{

    private Context context;
    private List<MediaBrowser.MediaItem> mediaItems;

    public MediaItemAdapter(Context context, List<MediaBrowser.MediaItem> objects) {
        super(context, R.layout.station_list_item, objects);
        this.context = context;
        this.mediaItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.station_list_item, parent, false);
        MediaBrowser.MediaItem mediaItem = mediaItems.get(position);

        ImageView stationImage = (ImageView) rowView.findViewById(R.id.stationImage);
        TextView name = (TextView) rowView.findViewById(android.R.id.text1);
        TextView genre = (TextView) rowView.findViewById(android.R.id.text2);
        IconTextView category = (IconTextView) rowView.findViewById(R.id.category);

        name.setText(mediaItem.getDescription().getTitle());
        genre.setText(mediaItem.getDescription().getSubtitle());
        Picasso.with(context).load(mediaItem.getDescription().getIconUri()).into(stationImage);
        PicassoWrapper.getStationPicasso(context, mediaItem.getDescription().getIconUri())
                .into(stationImage);
        //if(station.category.equals(Station.AUDIO)) category.setText(Iconify.compute("{fa-music}"));
        //if(station.category.equals(Station.VIDEO)) category.setText(Iconify.compute("{fa-video-camera}"));

        return rowView;
    }

}
