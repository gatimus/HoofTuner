package io.github.gatimus.hooftuner.pvl;

import android.media.MediaMetadata;

import java.util.List;

public class NowPlaying {

    public String status;
    public Station station;
    public List<Stream> streams;
    public Song current_song = new Song();
    public List<SongHistory> song_history;
    public Listeners listeners;
    public Event event;
    public Event event_upcoming;
    public String cache;

    public MediaMetadata toMediaMetadata(){
        return new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, current_song.id)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_TITLE, current_song.title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, current_song.title)
                .putString(MediaMetadata.METADATA_KEY_ART_URI, current_song.imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, current_song.imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, current_song.imageUri.toString())
                .putRating(MediaMetadata.METADATA_KEY_RATING, android.media.Rating.newThumbRating(false))
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, current_song.text)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 100L)
                .build();
    }

    public MediaMetadata toMediaMetadata(Stream selectedStream){
        return new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, selectedStream.current_song.id)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, selectedStream.current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, selectedStream.current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, selectedStream.current_song.artist)
                .putString(MediaMetadata.METADATA_KEY_TITLE, selectedStream.current_song.title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, selectedStream.current_song.title)
                .putString(MediaMetadata.METADATA_KEY_ART_URI, selectedStream.current_song.imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, selectedStream.current_song.imageUri.toString())
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, selectedStream.current_song.imageUri.toString())
                .putRating(MediaMetadata.METADATA_KEY_RATING, android.media.Rating.newThumbRating(false))
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, selectedStream.current_song.text)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 100L)
                .build();
    }



} //class
