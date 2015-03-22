package io.github.gatimus.hooftuner.pvl;

import java.util.List;

public class NowPlaying {

    public String status;
    public Station station;
    public List<Stream> streams;
    public Song current_song = new Song();
    public List<SongHistory> song_history;
    public Listeners listeners;
    public Event event;
    public Event event_upcoming;
    public String cache;

} //class
