package io.github.gatimus.hooftuner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;

import java.util.List;

import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

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
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.station_list_item, parent, false);
        Station station = stations.get(position);

        ImageView stationImage = (ImageView) rowView.findViewById(R.id.stationImage);
        TextView name = (TextView) rowView.findViewById(android.R.id.text1);
        TextView genre = (TextView) rowView.findViewById(android.R.id.text2);
        IconTextView category = (IconTextView) rowView.findViewById(R.id.category);

        name.setText(station.name);
        genre.setText(station.genre);
        PicassoWrapper.getStationPicasso(context, station.image_url.toString())
                .into(stationImage);
        if(station.category.equals(Station.AUDIO)) category.setText(Iconify.compute("{fa-music}"));
        if(station.category.equals(Station.VIDEO)) category.setText(Iconify.compute("{fa-video-camera}"));

        return rowView;
    } //getView

}
