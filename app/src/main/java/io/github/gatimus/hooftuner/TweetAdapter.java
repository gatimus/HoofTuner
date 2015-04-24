package io.github.gatimus.hooftuner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import twitter4j.HashtagEntity;
import twitter4j.Status;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetHolder> {

    private Context context;
    private List<Status> tweets;

    public TweetAdapter(Context context, List<Status> objects) {
        this.context = context;
        tweets = objects;
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tweet_list_item, parent, false);
        return new TweetHolder(view);
    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        Status status = tweets.get(position);
        holder.setUI(status);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(tweets != null){
            count = tweets.size();
        }
        return count;
    }

    class TweetHolder extends RecyclerView.ViewHolder{

        TextView text, hashTags, timeStamp;

        public TweetHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
            text.setAutoLinkMask(Linkify.ALL);
            text.setLinksClickable(true);
            hashTags = (TextView) itemView.findViewById(android.R.id.text2);
            timeStamp = (TextView) itemView.findViewById(R.id.userName);
        }

        public void setUI(Status status){
            text.setText(status.getText().toString());
            HashtagEntity[] hashtagEntities = status.getHashtagEntities();
            StringBuilder stringBuilder = new StringBuilder();
            for(HashtagEntity hashTag : hashtagEntities){
                stringBuilder.append(" #" + hashTag.getText());
            }
            hashTags.setText(stringBuilder.toString());
            Date date = status.getCreatedAt();
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT, Locale.getDefault());
            timeStamp.setText(dateFormat.format(date));
        }

    }

}