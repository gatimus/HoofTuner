package io.github.gatimus.hooftuner.pvl;

public class NowPlaying {
    public Station station;
    public Song current_song;
    public Listeners listeners;
    public class Listeners {
        public int current;
        public int unique;
        public int total;
    }
}
