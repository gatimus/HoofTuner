package io.github.gatimus.hooftuner;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationHolder> {

    private Context context;
    private List<MediaBrowser.MediaItem> mediaItems;
    private ListView.OnItemClickListener listener;

    public StationAdapter(Context context, List<MediaBrowser.MediaItem> objects, ListView.OnItemClickListener listener){
        this.context = context;
        this.mediaItems = objects;
        this.listener = listener;
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



    public class StationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView stationImage;
        private TextView name, genre;
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
            listener.onItemClick(null, v, getAdapterPosition(), getAdapterPosition());
        }

    }

}
