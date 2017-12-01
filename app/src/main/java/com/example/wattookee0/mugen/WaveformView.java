package com.example.wattookee0.mugen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by wattookee0 on 11/23/2017.
 */

public class WaveformView extends SurfaceView implements SurfaceHolder.Callback {

    Waveform waveform;
    SurfaceHolder holder;

    public WaveformView(Context c, AttributeSet a) {
        super(c, a);
        holder = this.getHolder();
        this.setWillNotDraw(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        int j = 0;
        super.onDraw(canvas);
        if (waveform == null) {
            Log.d("WAVEFORMVIEW", "no waveform");
        } else {
            Log.d("WAVEFORMVIEW", "onDraw()");
            //canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            int canvas_middle = canvas.getHeight()/2;
            Paint brush = new Paint();
            brush.setColor(Color.GRAY);
            brush.setStyle(Paint.Style.STROKE);
            brush.setStrokeWidth(5);
            canvas.drawLine(0, canvas_middle, canvas.getWidth(), canvas_middle, brush);
            brush.setColor(Color.BLUE);
            brush.setStrokeWidth(10);
            Path path = new Path();
            path.moveTo(0,canvas_middle-waveform.waveform[0]*100);
            Log.d("WAVEFORMVIEW", "length: "+ waveform.floats_per_cycle);
            while (i + j < canvas.getWidth()*10) {
                while (i < waveform.floats_per_cycle) {
                    path.lineTo(i+j, canvas_middle - waveform.waveform[i] * 100);
                    i+=10;
                }
                i = 0;
                j+=waveform.floats_per_cycle;
            }
            canvas.drawPath(path, brush);
        }
    }

    public void set_Waveform(Waveform new_waveform) {
        waveform = new_waveform;
        Log.d("WAVEFORMVIEW", "waveform: " + waveform);
    }
}