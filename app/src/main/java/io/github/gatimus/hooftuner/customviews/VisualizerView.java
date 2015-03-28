package io.github.gatimus.hooftuner.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.view.View;

import io.github.gatimus.hooftuner.R;

public class VisualizerView extends View implements Visualizer.OnDataCaptureListener {

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Visualizer visualizer;

    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mForePaint.setColor(getContext().getResources().getColor(R.color.ponyvillelive_color));

        try{
            visualizer = new Visualizer(0);
            visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate() / 2, true, true);
            visualizer.setEnabled(true);
        } catch (Exception e){
            Log.e(getClass().getSimpleName(), e.toString());
        }


    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        /*
        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }
        */

        //
        mPoints = new float[mBytes.length * 4];
        for (int i = 0; i < mBytes.length / 2; i++) {
            mPoints[i * 4] = i * 8;
            mPoints[i * 4 + 1] = 0;
            mPoints[i * 4 + 2] = i * 8;
            byte rfk = mBytes[2 * i];
            byte ifk = mBytes[2 * i + 1];
            float magnitude = (float) (rfk * rfk + ifk * ifk);
            int dbValue = (int) (5 * Math.log10(magnitude));
            mPoints[i * 4 + 3] = (float) (dbValue * 7);
        }
        //

        canvas.drawLines(mPoints, mForePaint);
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        //nothing
    }


    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        updateVisualizer(fft);


    }
}
