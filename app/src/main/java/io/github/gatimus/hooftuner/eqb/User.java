package io.github.gatimus.hooftuner.eqb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 3/8/2015.
 */
public class User {

    private int id;
    private String name;
    private String avatar;
    private String description;
    private String htmlDescription;
    private List<Track> tracks = new ArrayList<Track>();
    private List<Playlist> playlists = new ArrayList<Playlist>();
    private int numFavorites;
    private int numFollowers;
    private String link;

    @Override
    public String toString(){
        return name;
    }

}
