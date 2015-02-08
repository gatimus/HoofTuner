package io.github.gatimus.hooftuner.pvl;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {

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
    } //toString

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(id),
                name,
                shortcode,
                genre,
                category,
                type,
                image_url,
                web_url,
                stream_url,
                twitter_url,
                irc,
                String.valueOf(default_stream_id),
                player_url,
                request_url,
                streams.toString()
        });

    }

    public Station(Parcel in){
        String[] data = new String[15];
        in.readStringArray(data);
        id = Integer.parseInt(data[0]);
        name = data[1];
        shortcode = data[2];
        genre = data[3];
        category = data[4];
        type = data[5];
        image_url = data[6];
        web_url = data[7];
        stream_url = data[8];
        twitter_url = data[9];
        irc = data[10];
        default_stream_id = Integer.parseInt(data[11]);
        player_url = data[12];
        request_url = data[13];

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

} //class
