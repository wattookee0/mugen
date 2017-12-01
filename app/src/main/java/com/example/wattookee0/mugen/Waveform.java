package com.example.wattookee0.mugen;

import android.util.Log;

/**
 * Created by wattookee0 on 11/28/2017.
 */

public class Waveform {

    public enum shape_e {
        SINE,
        SQUARE,
        SAW,
        RECTIFIED
    }

    static final int sample_rate = 192000;
    static final float two_pi = (float)Math.PI*2;

    public float waveform[];
    private int m_freq;
    public int floats_per_cycle;
    private shape_e m_shape;

    public int get_floats_per_cycle() {
        return floats_per_cycle;
    }

    public float[] get_waveform() {
        return waveform;
    }

    public void set_waveform(int new_frequency, shape_e shape) {
        floats_per_cycle = sample_rate/new_frequency;
        m_shape = shape;
        m_freq = new_frequency;
        waveform = new float[floats_per_cycle];
        switch(shape) {
            case SINE:
                sine_Wave();
                break;
            case SQUARE:
                square_Wave();
                break;
            case SAW:
                saw_Wave();
                break;
            case RECTIFIED:
                rectified_Wave();
                break;
            default:
                sine_Wave();
                break;
        }
    }

    public Waveform(int frequency, shape_e shape) {
        floats_per_cycle = sample_rate/frequency;
        m_shape = shape;
        m_freq = frequency;
        waveform = new float[floats_per_cycle];
        switch(shape) {
            case SINE:
                sine_Wave();
                break;
            case SQUARE:
                square_Wave();
                break;
            case SAW:
                saw_Wave();
                break;
            case RECTIFIED:
                rectified_Wave();
                break;
            default:
                sine_Wave();
                break;
        }
    }

    private void sine_Wave() {
        int i = 0;

        Log.d("WAVEFORM", "generating sine wave");
        while (i < floats_per_cycle) {
            waveform[i] = (float) Math.sin( (m_freq * two_pi * i) / sample_rate);
            i++;
        }
    }

    private void square_Wave() {
        int i = 0;
        while (i < floats_per_cycle) {
            if ((float) Math.sin( (m_freq * two_pi * i) / sample_rate) > 0) {
                waveform[i] = (float)1.0;
            } else {
                waveform[i] = (float)-1.0;
            }
            i++;
        }
    }

    private void saw_Wave() {
        int i = 0;
        while (i < floats_per_cycle) {
            waveform[i] = (float)(i/floats_per_cycle);
            i++;
        }
    }

    private void rectified_Wave() {
        int i = 0;
        while (i < floats_per_cycle) {
            waveform[i] = (float)( Math.abs( Math.sin( (m_freq * two_pi * i) / sample_rate) ) );
        }
    }
}
