package io.github.gatimus.hooftuner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.utils.PicassoWrapper;

public class NavigationDrawerFragment extends ListFragment {

    private NavigationDrawerCallbacks callbackActivity;

    public NavigationDrawerFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StationAdapter stationAdapter = new StationAdapter(getActivity().getApplicationContext(), Cache.stations);
        stationAdapter.setNotifyOnChange(true);
        setListAdapter(stationAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (ListView)inflater.inflate(R.layout.fragment_navigation_drawer, container);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackActivity = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), activity.toString() + " must implement NavigationDrawerCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbackActivity = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Station selectedStation = Cache.stations.get(position);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setTitle(selectedStation.name);
        PicassoWrapper.getStationPicasso(getActivity(), selectedStation.imageUri.toString())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        actionBar.setIcon(R.drawable.icon);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        actionBar.setIcon(R.drawable.icon);
                    }
                });
        callbackActivity.onNavigationDrawerItemSelected(selectedStation);
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(Station station);
    }

}
