package io.github.gatimus.hooftuner.pvl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.gatimus.hooftuner.BuildConfig;
import io.github.gatimus.hooftuner.R;

public class StationAdapter extends ArrayAdapter<Station> {

    private Context context;
    private List<Station> stations;

    public StationAdapter(Context context, List<Station> objects) {
        super(context, R.layout.station_list_item, objects);
        this.context = context;
        stations = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.station_list_item, parent, false);
        TextView name = (TextView) rowView.findViewById(android.R.id.text1);
        TextView genre = (TextView) rowView.findViewById(android.R.id.text2);
        ImageView stationImage = (ImageView) rowView.findViewById(R.id.stationImage);
        name.setText(stations.get(position).name);
        genre.setText(stations.get(position).genre);
        Picasso picasso = Picasso.with(context);
        if(BuildConfig.DEBUG){
            picasso.setIndicatorsEnabled(true);
        }
        picasso.load(stations.get(position).image_url)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.stat_sys_download)
                .into(stationImage);
        return rowView;
    } //getView

}
