package io.github.gatimus.hooftuner.pvl;

import java.net.URL;

public class Event {

    public int id;
    public int station_id;
    public String guid;
    //public Date start_time;
    //public Date end_time;
    public boolean is_all_day;
    public String title;
    public String location;
    public String body;
    //@SerializedName("banner_url")
    //public Uri bannerUri;
    public URL web_url;
    public boolean is_promoted;
    public int minutes_until;
    public String range;
    public URL image_url;

    @Override
    public String toString(){
        return title;
    }

}
