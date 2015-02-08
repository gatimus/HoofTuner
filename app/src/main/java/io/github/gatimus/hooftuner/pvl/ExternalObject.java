package io.github.gatimus.hooftuner.pvl;

import com.google.gson.JsonElement;

public abstract class ExternalObject {

    public String __initializer__;
    public String __cloner__;
    public JsonElement[] lazyPropertiesDefaults;
    public String id;
    public String hash;
    public String timestamp;
    public String artist;
    public String title;
    public String web_url;
    public String image_url;

    @Override
    public String toString(){
        return title;
    } //toString

    @Override
    public int hashCode(){
        return Integer.parseInt(hash);
    } //hashCode

} //class
