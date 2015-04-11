package io.github.gatimus.hooftuner;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationHolder> {

    private Context context;
    private List<MediaBrowser.MediaItem> mediaItems;

    public StationAdapter(Context context, List<MediaBrowser.MediaItem> objects){
        this.context = context;
        this.mediaItems = objects;
    }

    @Override
    public StationHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.station_list_item, viewGroup, false);
        return new StationHolder(view);
    }

    @Override
    public void onBindViewHolder(StationHolder stationHolder, int i) {
        MediaBrowser.MediaItem station = mediaItems.get(i);
        stationHolder.setUI(context, station);
    }


    @Override
    public int getItemCount() {
        int count = 0;
        if(mediaItems != null){
            count = mediaItems.size();
        }
        return count;
    }

}
