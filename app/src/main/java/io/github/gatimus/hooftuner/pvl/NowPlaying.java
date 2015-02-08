package io.github.gatimus.hooftuner.pvl;

import com.google.gson.JsonElement;

public class NowPlaying {

    public Station station;
    public Song current_song;
    public Listeners listeners;
    public JsonElement[] event;
    public JsonElement[] event_upcoming;
    public String cache;

} //class
