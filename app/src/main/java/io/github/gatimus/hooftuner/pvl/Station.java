package io.github.gatimus.hooftuner.pvl;

public class Station {
    public int id;
    public String name;
    public String shortcode;
    public String genre;
    public String category;
    public String type;
    public String image_url;
    public String web_url;
    public String stream_url;
    public String twitter_url;
    public String irc;
    public int default_stream_id;
    public String player_url;
    public String request_url;
    public Stream[] streams;

    @Override
    public String toString(){
        return name;
    }
}
