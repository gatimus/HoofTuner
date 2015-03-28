package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.github.gatimus.hooftuner.pvl.Station;

public class NavigationDrawerFragment extends ListFragment {

    private NavigationDrawerCallbacks callbackActivity;

    public NavigationDrawerFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StationAdapter stationAdapter = new StationAdapter(getActivity().getApplicationContext(), Global.stations);
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
        callbackActivity.onNavigationDrawerItemSelected(Global.stations.get(position));
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(Station station);
    }

}
