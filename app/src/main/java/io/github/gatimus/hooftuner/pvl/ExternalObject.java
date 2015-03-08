package io.github.gatimus.hooftuner.pvl;

import com.google.gson.JsonElement;

import java.net.URL;
import java.util.Date;

public abstract class ExternalObject {

    public String __initializer__;
    public String __cloner__;
    public JsonElement[] lazyPropertiesDefaults;
    public int id;
    public String hash;
    public Date timestamp;
    public Date created;
    public Date updated;
    public String artist;
    public String title;
    public URL web_url;
    public URL image_url;
    public URL download_url;

    @Override
    public String toString(){
        return title;
    } //toString

    @Override
    public int hashCode(){
        return Integer.parseInt(hash);
    } //hashCode

} //class
