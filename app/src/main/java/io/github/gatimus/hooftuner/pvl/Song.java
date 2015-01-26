package io.github.gatimus.hooftuner.pvl;

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

    public class External {
        public EQBeats eqbeats;
        public BronyTunes bronytunes;

        public class EQBeats{
            public String id;
            public String hash;
            public String timestamp;
            public String artist;
            public String title;
            public String web_url;
            public String image_url;
        }

        public class BronyTunes{
            public String id;
            public String hash;
            public String timestamp;
            public String artist;
            public String title;
            public String album;
            public String description;
            public String lyrics;
            public String web_url;
            public String image_url;
            public String youtube_url;
            public String purchase_url;
        }
    }

    public class Rating {
        public int likes;
        public int dislikes;
        public int score;
    }

    public class VoteURLs {
        public String like;
        public String dislike;
        public String clearvote;
    }

    @Override
    public String toString(){
        return title;
    }
}
