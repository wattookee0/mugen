package com.example.wattookee0.mugen;

import android.util.Log;
import java.util.HashSet;

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

    static final int m_sample_rate = 192000;
    static final float TWO_PI = (float)Math.PI*2;
    static final int MINIMUM_AUDIBLE_FREQ = 20;
    static final int MAXIMUM_AUDIBLE_FREQ = 20000;
    static final int MAX_ALLOWED_HARMONICS = 6;

    public float m_amplitude;

    public float waveform[];
    private HashSet<harmonic> m_harmonics = new HashSet<harmonic>(MAX_ALLOWED_HARMONICS);

    private float m_freq;
    private float m_min_freq;
    public int m_floats_per_cycle;
    private shape_e m_shape;

    public int get_floats_per_cycle() {
        return m_floats_per_cycle;
    }

    public float[] get_waveform() {
        return waveform;
    }

    public boolean set_harmonic(int harmonic_n) {
        m_harmonics.add(new harmonic(m_freq, harmonic_n, (float)1.0, shape_e.SINE));
        Log.d("WAVEFORM", "(add)harmonics: " + m_harmonics);
        return true;
    }

    public boolean remove_harmonic(int harmonic_n) {
        if (m_harmonics.contains(harmonic_n)) {
            m_harmonics.remove(harmonic_n);
        }
        Log.d("WAVEFORM", "(remove)harmonics: " + m_harmonics);
        return true;
    }

    public void set_amplitude(float new_amplitude) { m_amplitude = new_amplitude;}

    public void set_waveform(float new_frequency, shape_e shape, HashSet<Integer> new_harmonics) {
        m_floats_per_cycle = (int)(m_sample_rate /new_frequency);
        m_shape = shape;
        m_freq = new_frequency;
        waveform = new float[m_floats_per_cycle];
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

    private float calculate_freq_from_n(int n) {
        return (float)(440*Math.pow(2.0, (double)n/12.0));
    }

    private float calculate_minimum_freq(HashSet<Integer> check_harmonics) {
        int min_harmonic = Integer.MAX_VALUE;
        float min_freq = Float.MAX_VALUE;
        Integer[] harmonics_array = check_harmonics.toArray(new Integer[check_harmonics.size()]);

        int i = 0;
        while (i < check_harmonics.size()) {
            if (harmonics_array[i] < min_harmonic) {
                min_harmonic = harmonics_array[i];
            }
            i++;
        }

        if (min_harmonic < 0) { //we're really only interested in "negative" harmonics, because they will divide the fundamental freq
            min_freq = m_freq/Math.abs(min_harmonic);
        } else {
            min_freq = m_freq;  //assume that "non-negative" harmonics will always be higher freq than fundamental freq
        }
        Log.d("WAVEFORM", "min freq = " + min_freq + " m_freq = " + m_freq);
        return min_freq;
    }

    public void set_waveform(int n, shape_e shape, HashSet<Integer> new_harmonics) {
        //calculate frequency from n
        m_freq = calculate_freq_from_n(n);

        m_floats_per_cycle = (int)(m_sample_rate /m_min_freq);
        m_shape = shape;
        waveform = new float[m_floats_per_cycle];
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

    public Waveform(int n, shape_e shape, HashSet<Integer> new_harmonics) {
        m_amplitude = (float)100.0;
        m_freq = (float)calculate_freq_from_n(n);
        m_floats_per_cycle = (int)(m_sample_rate /m_freq);
        m_shape = shape;
        waveform = new float[m_floats_per_cycle];

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
        m_amplitude = (float)1.0;
        m_floats_per_cycle = (int)(m_sample_rate /frequency);
        m_shape = shape;
        m_freq = frequency;
        waveform = new float[m_floats_per_cycle];
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

    private void sine_Wave(harmonic harmonic) {
        int i = 0;
        while (i < harmonic.h_floats_per_cycle) {
            harmonic.h_waveform[i] += (float) ( (harmonic.h_amplitude/2) * Math.sin((harmonic.h_freq * TWO_PI * i) / m_sample_rate));
            i++;
        }
    }

    private void sine_Wave() {
        int i = 0;
        float A = m_amplitude / 2;
        Log.d("WAVEFORM", "generating sine wave with amplitude " + A);

        while (i < m_floats_per_cycle) {
            waveform[i] += (float) ( (A) * Math.sin((m_freq * TWO_PI * i) / m_sample_rate));
            i++;
        }

    }

    private void square_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            if ((float) Math.sin( (m_freq * TWO_PI * i) / m_sample_rate) > 0) {
                waveform[i] = (float)1.0;
            } else {
                waveform[i] = (float)-1.0;
            }
            i++;
        }
    }

    private void saw_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            waveform[i] = (float)(i/m_floats_per_cycle);
            i++;
        }
    }

    private void rectified_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            waveform[i] = (float)( Math.abs( Math.sin( (m_freq * TWO_PI * i) / m_sample_rate) ) );
        }
    }

    private class harmonic {
        float h_freq;
        float h_amplitude;
        float[] h_waveform;
        int h_floats_per_cycle;
        shape_e h_shape;

        public harmonic(float fundamental_freq, int n, float new_amplitude, shape_e new_shape) throws IllegalArgumentException {
            //check for valid fundamentals
            //note: technically you could get valid harmonics from invalid fundamentals... but why try?
            if ((fundamental_freq < MINIMUM_AUDIBLE_FREQ) || (fundamental_freq > MAXIMUM_AUDIBLE_FREQ)) {
                throw new IllegalArgumentException("Invalid fundamental frequency in new harmonic, must be greater than "
                        + MINIMUM_AUDIBLE_FREQ + " and less than " + MAXIMUM_AUDIBLE_FREQ + ".");
            }

            //check for valid harmonics
            if (n < -1) {
                h_freq = fundamental_freq/n;
                if (h_freq < MINIMUM_AUDIBLE_FREQ) {
                    throw new IllegalArgumentException("Invalid n, resulting harmonic inaudible.");
                }
            } else if (n > 1) {
                h_freq = fundamental_freq*n;
                if (h_freq < MAXIMUM_AUDIBLE_FREQ) {
                    throw new IllegalArgumentException("Invalid n, resulting harmonic inaudible.");
                }
            } else {
                throw new IllegalArgumentException("Invalid n in new harmonic.  Must be < -1 or > 1");
            }

            if (new_amplitude > 0.0) {
                h_amplitude = new_amplitude;
            } else {
                throw new IllegalArgumentException("Invalid amplitude in new harmonic, must be > 0.0");
            }

            //everything checked out, let's get on it with
            h_floats_per_cycle = m_sample_rate /(int) h_freq;

            h_waveform = new float[h_floats_per_cycle];
            h_shape = new_shape;
            sine_Wave(this);
        }
    }
}