package io.github.gatimus.hooftuner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class IconBitmap {

    public static Bitmap getIconBitmap(Context context, Iconify.IconValue iconValue){
        IconDrawable drawable = new IconDrawable(context, iconValue).sizePx(36);
        drawable.setStyle(Paint.Style.FILL);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
