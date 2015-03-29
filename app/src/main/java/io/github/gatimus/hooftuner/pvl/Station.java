package io.github.gatimus.hooftuner.pvl;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

public class Station {

    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";

    public int id = 0;
    public String name;
    public String shortcode;
    public String genre;
    public String category;
    public String affiliation;
    public String type;  //?
    @SerializedName("image_url")
    public Uri imageUri;
    public URL web_url;
    public URL stream_url;
    public URL twitter_url;
    public String irc;
    public int default_stream_id;
    public URL player_url;
    public URL request_url;
    public List<Stream> streams;

    @Override
    public String toString(){
        return name;
    } //toString

} //class
