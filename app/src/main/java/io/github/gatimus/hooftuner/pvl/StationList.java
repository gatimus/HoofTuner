package io.github.gatimus.hooftuner.pvl;

import java.util.ArrayList;

public class StationList extends ArrayList<Station> {

    public Station get(String shortcode) {
        for(Station station : this){
            if(station.shortcode.equals(shortcode)){
                return station;
            }
        }
        return null;
    }

}
