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

    private Paint wave_brush(int color) {
        Paint brush = new Paint();
        switch(color) {
            case 0: brush.setColor(Color.BLUE);
            break;
            case 1: brush.setColor(Color.RED);
            break;
            case 2: brush.setColor(Color.GREEN);
            break;
            case 3: brush.setColor(Color.BLACK);
            break;
            case 4: brush.setColor(Color.CYAN);
            break;
            case 5: brush.setColor(Color.MAGENTA);
            break;
            case 6: brush.setColor(Color.DKGRAY);
            break;
            default: brush.setColor(Color.GRAY);
            break;
        }
        brush.setStyle(Paint.Style.STROKE);
        if (color < 0) {
            brush.setStrokeWidth(10);
        } else {
            brush.setStrokeWidth(2);
        }
        return brush;
    }

    private Paint line_brush() {
        Paint brush = new Paint();
        brush.setColor(Color.GRAY);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(2);
        return brush;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 0, j = 0, k = -1;
        int iterations = 2;
        double y_scale = 0.0;
        double x_scale = 1.0;
        Paint brush = new Paint();
        super.onDraw(canvas);
        if (waveform == null) {
            Log.d("WAVEFORMVIEW", "no m_waveform");
        } else {
            Log.d("WAVEFORMVIEW", "onDraw()");
            canvas.drawColor(Color.WHITE);
            float[] wave;
            int floats;
            int canvas_middle = canvas.getHeight()/2;
            y_scale = calculate_y_scale(waveform, canvas);
            x_scale = calculate_x_scale(waveform, canvas);
            brush = line_brush();
            float y = 0;
            i = 1;
            while (i*y_scale < canvas.getHeight()) {
                y = (float)(i*y_scale);
                canvas.drawLine(0, y, canvas.getWidth(), y, brush);
                i+=10;
            }
            float x = 0;
            i = 1;
            while (i*x_scale < canvas.getWidth()) {
                x = (float)(i*x_scale);
                canvas.drawLine(x, 0, x, canvas.getHeight(), brush);
                i+=200;
            }
            while (k < waveform.m_num_harmonics) {
                if (k < 0) {
                    wave = waveform.m_waveform;
                } else {
                    wave = waveform.get_harmonic(k);
                }
                iterations = 2*waveform.m_floats_per_cycle/wave.length;
                brush = wave_brush(k);
                Path path = new Path();
                path.moveTo(0, canvas_middle - (float) y_scale * wave[0]);
                Log.d("WAVEFORMVIEW", "floats:" + waveform.m_floats_per_cycle + " length:" + wave.length + " iterations:" + iterations+" waveform:" + wave);
                i = 0;
                while (((i + j) * x_scale < canvas.getWidth()) && (i + j < iterations * wave.length)) {
                    while (i < wave.length) {
                        path.lineTo((float) ((i + j) * x_scale), canvas_middle - (float) y_scale * wave[i]);
                        i++;
                    }
                    j += wave.length;
                    i = 0;
                }
                canvas.drawPath(path, brush);
                i = 0;
                j = 0;
                k++;
            }
        }
    }

    private double calculate_x_scale(Waveform waveform, Canvas canvas) {
        double scale;
            scale = (double) canvas.getWidth() / (2*waveform.m_floats_per_cycle);
        Log.d("XSCALE", "xscale=" + scale + " canwidth=" + canvas.getWidth() + " floats=" + waveform.m_floats_per_cycle);
        return scale;
    }

    private double calculate_y_scale(Waveform waveform, Canvas canvas) {
        float max_positive = (float) 0.0;
        float max_negative = (float) -0.0;
        float amplitude = (float) 0.0;
        int i = 0;
        while (i < waveform.m_floats_per_cycle) {
            if (waveform.m_waveform[i] > (float) 0.0) {
                if (waveform.m_waveform[i] > max_positive) {
                    max_positive = waveform.m_waveform[i];
                }
            } else if (waveform.m_waveform[i] < (float) 0.0) {
                if (waveform.m_waveform[i] < max_negative) {
                    max_negative = waveform.m_waveform[i];
                }
            }
            i++;
        }
        amplitude = Math.abs(max_negative) + max_positive;
        Log.d("YSCALE:", "Amplitude: " + amplitude + " yscale: " + 0.8 * canvas.getHeight() / (double) amplitude);
        return (0.8 * canvas.getHeight() / (double) amplitude);
    }

    public void set_Waveform(Waveform new_waveform) {
        waveform = new_waveform;
        Log.d("WAVEFORMVIEW", "m_waveform: " + waveform);
    }
}