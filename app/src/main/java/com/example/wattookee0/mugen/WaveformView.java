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
    Canvas view_canvas;

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
        double y_scale = 0.0;
        super.onDraw(canvas);
        if (waveform == null) {
            Log.d("WAVEFORMVIEW", "no waveform");
        } else {
            Log.d("WAVEFORMVIEW", "onDraw()");
            canvas.drawColor(Color.WHITE);
            int canvas_middle = canvas.getHeight()/2;
            y_scale = calculate_y_scale(waveform, canvas);
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
                    path.lineTo(i+j, canvas_middle - (float)y_scale*waveform.waveform[i]);
                    i+=10;
                }
                i = 0;
                j+=waveform.floats_per_cycle;
            }
            canvas.drawPath(path, brush);
        }
    }

    private double calculate_y_scale(Waveform waveform, Canvas canvas) {
        float max_positive = (float)0.0;
        float max_negative = (float)-0.0;
        float amplitude = (float)0.0;
        int i = 0;
        while (i < waveform.floats_per_cycle) {
            if (waveform.waveform[i] > (float)0.0) {
                if (waveform.waveform[i] > max_positive) {
                    max_positive = waveform.waveform[i];
                }
            } else if (waveform.waveform[i] < (float)0.0) {
                if (waveform.waveform[i] < max_negative) {
                    max_negative = waveform.waveform[i];
                }
            }
            i++;
        }
        amplitude = Math.abs(max_negative) + max_positive;
        Log.d("YSCALE:", "Amplitude: " + amplitude + " yscale: " + 0.8*canvas.getHeight()/(double)amplitude);
        return (0.8*canvas.getHeight()/(double)amplitude);
    }

    public void set_Waveform(Waveform new_waveform) {
        waveform = new_waveform;
        Log.d("WAVEFORMVIEW", "waveform: " + waveform);
    }
}