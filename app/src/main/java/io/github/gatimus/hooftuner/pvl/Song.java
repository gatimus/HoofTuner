package io.github.gatimus.hooftuner.pvl;

import java.net.URL;

public class Song {

    public String id;
    public String text;
    public String artist;
    public String title;
    public int sh_id;
    public int score;
    public int created_at;
    public String rate_likes;
    public String rate_dislikes;
    public String rate_score;
    public Rating rating;
    public VoteURLs vote_urls;
    public External external;

    public class Rating {
        public int likes;
        public int dislikes;
        public int score;
    }

    public class VoteURLs {
        public URL like;
        public URL dislike;
        public URL clearvote;
    }

    @Override
    public String toString(){
        return title;
    } //toString

} //class
