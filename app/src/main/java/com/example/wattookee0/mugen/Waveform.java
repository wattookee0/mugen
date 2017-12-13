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

    public float amplitude;
    public float waveform[];
    private float m_freq;
    private int harmonic_shift;
    private final int max_harmonic_shift = 6;
    public int floats_per_cycle;
    private shape_e m_shape;

    public int get_floats_per_cycle() {
        return floats_per_cycle;
    }

    public float[] get_waveform() {
        return waveform;
    }

    public void set_amplitude(float new_amplitude) { amplitude = new_amplitude;}

    public void set_waveform(float new_frequency, shape_e shape) {
        floats_per_cycle = (int)(sample_rate/new_frequency);
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

    public void set_waveform(int n, shape_e shape) {
        m_freq = (float)(440*Math.pow(2.0, (double)n/12.0));
        floats_per_cycle = (int)(sample_rate/m_freq);
        m_shape = shape;
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

    public Waveform(int n, shape_e shape) {
        amplitude = (float)100.0;
        m_freq = (float)(440*Math.pow(2.0,(double)n));
        floats_per_cycle = (int)(sample_rate/m_freq);
        m_shape = shape;
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

    public Waveform(float frequency, shape_e shape) {
        amplitude = (float)1.0;
        floats_per_cycle = (int)(sample_rate/frequency);
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

    public void set_harmonic_shift(int shift) {
        harmonic_shift = shift;
    }

    private void sine_Wave() {
        int half_max_shift = max_harmonic_shift/2;
        int i = 0;
        int j = harmonic_shift - half_max_shift;
        int divisor = 1;
        float multiplier = (float)1.0;
        float A = amplitude / 2;
        Log.d("WAVEFORM", "generating sine wave with amplitude " + A);
        while (j < (harmonic_shift + half_max_shift)) {
            if (j == 0) {
                divisor = 1;
                multiplier = (float)1.0;
            } else {
                divisor = Math.abs(2*j);
                if (j < 0) {
                    multiplier = (float)1.0/Math.abs(j-1);
                } else {
                    multiplier = j+1;
                }
            }
            Log.d("WAVEFORM", "j=" + j + " mult=" + multiplier + " div=" + divisor + " A/div=" + A/divisor + " freq*mult =" + m_freq*multiplier);
            while (i < floats_per_cycle) {
                waveform[i] += (float) ( (A) * Math.sin((multiplier * m_freq * two_pi * i) / sample_rate));
                i++;
            }
            i = 0;
            j++;
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
