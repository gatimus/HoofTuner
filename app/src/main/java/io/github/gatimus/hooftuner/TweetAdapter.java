package io.github.gatimus.hooftuner;

import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import twitter4j.HashtagEntity;
import twitter4j.Status;

public class TweetAdapter extends ArrayAdapter<Status> {

    private Context context;
    private List<Status> tweets;

    public TweetAdapter(Context context, List<Status> objects) {
        super(context, R.layout.tweet_list_item, objects);
        Log.v(getClass().getSimpleName(), "construct");
        this.context = context;
        tweets = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(getClass().getSimpleName(), "getView");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tweet_list_item, parent, false);
        TextView text = (TextView) rowView.findViewById(android.R.id.text1);
        text.setAutoLinkMask(Linkify.ALL);
        text.setLinksClickable(true);
        TextView hashTags = (TextView) rowView.findViewById(android.R.id.text2);
        TextView userName = (TextView) rowView.findViewById(R.id.userName);
        //ImageView userImage = (ImageView) rowView.findViewById(R.id.userImage);

        text.setText(tweets.get(position).getText().toString());
        HashtagEntity[] hashtagEntities = tweets.get(position).getHashtagEntities();
        StringBuilder stringBuilder = new StringBuilder();
        for(HashtagEntity hashTag : hashtagEntities){
            stringBuilder.append(" #" + hashTag.getText());
        }
        hashTags.setText(stringBuilder.toString());
        Date date = tweets.get(position).getCreatedAt();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT, Locale.getDefault());
        userName.setText(dateFormat.format(date));

        /*
        Picasso picasso = Picasso.with(context);
        if(BuildConfig.DEBUG){
            picasso.setIndicatorsEnabled(true);
        }

        picasso.load(tweets.get(position).getUser().getBiggerProfileImageURL())
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(userImage);
        */
        return rowView;
    } //getView

}