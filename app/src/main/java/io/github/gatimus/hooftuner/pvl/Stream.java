package io.github.gatimus.hooftuner.pvl;

import java.net.URL;
import java.util.List;

public class Stream {

    public int id;
    public String name;
    public URL url;
    public String type;
    public boolean is_default;
    public String status;
    public String bitrate;
    public String format;
    public Listeners listeners;
    public Song current_song;
    public List<SongHistory> song_history;


    @Override
    public String toString(){
        return name;
    } //toString

} //class
