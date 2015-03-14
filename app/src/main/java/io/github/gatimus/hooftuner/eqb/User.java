package io.github.gatimus.hooftuner.eqb;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {

    private int id;
    private String name;
    private String avatar;
    private String description;
    @SerializedName("html_description")
    private String htmlDescription;
    private List<Track> tracks;
    private List<Playlist> playlists;
    @SerializedName("num_favorites")
    private int numFavorites;
    @SerializedName("num_followers")
    private int numFollowers;
    private String link;

    @Override
    public String toString(){
        return name;
    }

}
