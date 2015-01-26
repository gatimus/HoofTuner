package io.github.gatimus.hooftuner.pvl;

public class Stream {
    public int id;
    public String name;
    public String url;
    public String type;
    public boolean is_default;

    @Override
    public String toString(){
        return name;
    }
}
