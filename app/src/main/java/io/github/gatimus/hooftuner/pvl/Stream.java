package io.github.gatimus.hooftuner.pvl;

import android.content.Context;
import android.media.MediaDescription;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.annotations.SerializedName;
import com.joanzapata.android.iconify.Iconify;

import java.util.List;

import io.github.gatimus.hooftuner.utils.IconBitmap;

public class Stream {

    public static final String KEY_STREAM_URI = "KEY_STREAM_URI";

    public int id;
    public String name;
    @SerializedName("url")
    public Uri uri;
    public String type;
    public boolean is_default;
    public String status;
    public String bitrate;
    public String format;
    public Listeners listeners;
    public Song current_song;
    public List<SongHistory> song_history;


    @Override
    public String toString(){
        return name;
    } //toString

    public MediaBrowser.MediaItem toMediaItem(Context context){
        Iconify.IconValue iconValue = Iconify.IconValue.fa_code_fork;
        if (is_default){
            iconValue = Iconify.IconValue.fa_star;
        }
        Bundle extras = new Bundle();
        extras.putParcelable(KEY_STREAM_URI, uri);
        return new MediaBrowser.MediaItem(new MediaDescription.Builder()
                .setMediaId(String.valueOf(id))
                .setTitle(name)
                .setIconBitmap(IconBitmap.getIconBitmap(context, iconValue))
                .setExtras(extras)
                .build(), MediaBrowser.MediaItem.FLAG_PLAYABLE);
    }

    public MediaBrowser.MediaItem toMediaItem(){
        Bundle extras = new Bundle();
        extras.putParcelable(KEY_STREAM_URI, uri);
        return new MediaBrowser.MediaItem(new MediaDescription.Builder()
                .setMediaId(String.valueOf(id))
                .setTitle(name)
                .setExtras(extras)
                .build(), MediaBrowser.MediaItem.FLAG_PLAYABLE);
    }

} //class
