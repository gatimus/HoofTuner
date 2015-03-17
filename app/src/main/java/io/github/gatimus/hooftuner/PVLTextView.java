package io.github.gatimus.hooftuner;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PVLTextView extends TextView{
    public PVLTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/SourceSansPro-Regular.ttf"));
    }

    public PVLTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/SourceSansPro-Regular.ttf"));
    }

    public PVLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/SourceSansPro-Regular.ttf"));
    }
    /*
    public PonyvilleLiveTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    */
}
