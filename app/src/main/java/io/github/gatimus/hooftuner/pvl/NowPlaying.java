package io.github.gatimus.hooftuner.pvl;

import com.google.gson.JsonElement;

public class NowPlaying {

    public String status;
    public Station station;
    public Stream[] streams;
    public Song current_song;
    public SongHistory song_history;
    public Listeners listeners;
    public JsonElement[] event;
    public JsonElement[] event_upcoming;
    public String cache;

} //class
