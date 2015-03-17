package io.github.gatimus.hooftuner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.joanzapata.android.iconify.Iconify;

public class IconToggleButton extends ToggleButton {

    public IconToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IconToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconToggleButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode())
            Iconify.addIcons(this);
        else{
            this.setText(this.getText());
            this.setTextOn(this.getTextOn());
            this.setTextOff(this.getTextOff());
        }
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Iconify.compute(text), type);
    }

    @Override
    public void setTextOn(CharSequence textOn) {
        super.setTextOn(Iconify.compute(textOn));
    }

    @Override
    public void setTextOff(CharSequence textOff) {
        super.setTextOff(Iconify.compute(textOff));
    }


}
