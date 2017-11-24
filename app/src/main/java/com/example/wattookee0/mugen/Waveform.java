package com.example.wattookee0.mugen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Array;

import javax.security.auth.callback.Callback;

/**
 * Created by wattookee0 on 11/23/2017.
 */

public class Waveform {

    float waveform[];
    int i = 0;
    int waveform_length;

    public Waveform() {

    }

    public Waveform(float[] sample, int sample_length) {
        waveform = sample;
        waveform_length = sample_length;
        Log.d("SAMPLE", ": "+sample);
        Log.d("WAVEFORM", ": "+waveform);
    }

    public void display_Waveform(SurfaceView surface) {
        Paint brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        SurfaceHolder holder = surface.getHolder();
        Log.d("WAVEFORM", "holder: " + holder);
        SurfaceHolder.Callback surfaceholdercallback = new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("SURFACEHOLDER", "surfaceCreated");
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                Log.d("SURFACEHOLDER", "surfaceChanged");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("SURFACEHOLDER", "surfaceDestroyed");
            }
        };
        holder.addCallback(surfaceholdercallback);
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.d("WAVEFORM", "cannot create surface?");
        } else {
            Log.d("WAVEFORM", "canvas returned");
            canvas.drawColor(Color.BLUE);
            brush.setStyle(Paint.Style.STROKE);
            brush.setStrokeWidth(3);
            brush.setColor(Color.GREEN);
            Path path = new Path();
            path.moveTo(0, 0);
            i = 0;
            Log.d("WAVEFORM", "length: " + waveform_length);
            while (i < waveform_length) {
                path.lineTo(i, waveform[i]);
                Log.d("WAVEFORM:", "i[" + i + "][" + waveform[i] + "]");
                i++;
            }
            canvas.drawPath(path, brush);
            holder.unlockCanvasAndPost(canvas);
            Log.d("WAVEFORM", "unlocked canvas...");
            //surface.draw(canvas);
            //surface.invalidate();
        }
    }
}