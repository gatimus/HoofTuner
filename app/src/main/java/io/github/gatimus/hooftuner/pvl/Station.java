package io.github.gatimus.hooftuner.pvl;

import java.net.URL;

public class Station {

    public int id;
    public String name;
    public String shortcode;
    public String genre;
    public String category;
    public String type;  //?
    public URL image_url;
    public URL web_url;
    public URL stream_url;
    public URL twitter_url;
    public String irc;
    public int default_stream_id;
    public URL player_url;
    public URL request_url;
    public Stream[] streams;

    @Override
    public String toString(){
        return name;
    } //toString

} //class
