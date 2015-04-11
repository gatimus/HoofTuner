package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import io.github.gatimus.hooftuner.pvl.Station;

public class NavigationDrawerFragment extends Fragment {

    private NavigationDrawerCallbacks callbackActivity;
    private MediaBrowser mediaBrowser;
    private RecyclerView recyclerView;

    private MediaBrowser.ConnectionCallback connectionCallback = new MediaBrowser.ConnectionCallback(){
        @Override
        public void onConnected() {
            mediaBrowser.subscribe(PVLMediaBrowserService.MEDIA_ID_ROOT, new MediaBrowser.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(String parentId, List<MediaBrowser.MediaItem> children) {
                    //StationAdapter stationAdapter = new StationAdapter(getActivity().getApplicationContext(), Cache.stations);
                    if(getActivity() != null){
                        StationAdapter stationAdapter = new StationAdapter(getActivity(), children);
                        recyclerView.setAdapter(stationAdapter);

                    }

                }
            });
        }
    };

    public NavigationDrawerFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowser = new MediaBrowser(getActivity(),
                new ComponentName(getActivity(), PVLMediaBrowserService.class),
                connectionCallback, null);
        mediaBrowser.connect();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView)inflater.inflate(R.layout.fragment_navigation_drawer, container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
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

    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Station selectedStation = Cache.stations.get(position);
        callbackActivity.onNavigationDrawerItemSelected(selectedStation);
    }
    */

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(Station station);
    }

}
