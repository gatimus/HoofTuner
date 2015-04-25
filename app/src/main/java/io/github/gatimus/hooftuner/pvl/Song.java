package io.github.gatimus.hooftuner.pvl;

import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Song {

    public static String KEY_SCORE = "KEY_SCORE";

    public String id = "";
    public String text;
    public String artist;
    public String title;
    @SerializedName("image_url")
    public Uri imageUri;
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
        return text;
    } //toString

    public MediaMetadata toMediaMetadata(){
        return new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, artist)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, artist)
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_ART_URI, imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, imageUri.toString())
                .putRating(MediaMetadata.METADATA_KEY_RATING, android.media.Rating.newThumbRating(false))
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, text)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 100L)
                .build();
    }

    public MediaBrowser.MediaItem toMediaItem(){
        return new MediaBrowser.MediaItem(new MediaDescription.Builder()
                .setMediaId(id)
                .setTitle(title)
                .setSubtitle(artist)
                .setIconUri(imageUri)
                .setDescription(text)
                .build(), MediaBrowser.MediaItem.FLAG_PLAYABLE);
    }

} //class
