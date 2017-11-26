package com.example.wattookee0.mugen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Array;

import javax.security.auth.callback.Callback;

/**
 * Created by wattookee0 on 11/23/2017.
 */

public class Waveform extends SurfaceView implements SurfaceHolder.Callback {

    float waveform[];
    int waveform_length;
    SurfaceHolder holder;

    public Waveform(Context c, AttributeSet a) {
        super(c, a);
        holder = this.getHolder();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SHC", "FUCK");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("SHC", "SHIT");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("SHC", "ASS");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0;
        int j = 0;
        super.onDraw(canvas);
        if (waveform == null) {
            Log.d("WAVEFORM", "no waveform");
        } else {
            Log.d("WAVEFORM", "onDraw()");
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
            path.moveTo(0,canvas_middle-waveform[0]*100);
            Log.d("WAVEFORM", "length: "+ waveform_length);
            while (i + j < canvas.getWidth()) {
                while (i < waveform_length) {
                    path.lineTo(i+j, canvas_middle - waveform[i] * 100);
                    i++;
                }
                i = 0;
                j+=waveform_length;
            }
            canvas.drawPath(path, brush);
        }
    }

    public void set_Waveform(float[] data, int data_length) {
        waveform = data;
        waveform_length = data_length;
        this.setWillNotDraw(false);
        //this.setZOrderOnTop(true);
    }
}