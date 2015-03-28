package io.github.gatimus.hooftuner.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import io.github.gatimus.hooftuner.R;

public class PVLTextView extends TextView{
    public PVLTextView(Context context) {
        super(context);
        init(context);
    }

    public PVLTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PVLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    /*
    public PonyvilleLiveTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    */

    private void init(Context context) {
        if (!isInEditMode()){
            setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/SourceSansPro-Regular.ttf"));
            setShadowLayer(5f,2f,2f,getContext().getResources().getColor(R.color.abc_primary_text_material_light));
            setPaddingRelative(5,0,5,0);
        }
    }

}
