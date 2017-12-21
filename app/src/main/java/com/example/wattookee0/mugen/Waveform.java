package com.example.wattookee0.mugen;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    static final int MINIMUM_AUDIBLE_FREQ = 20;
    static final int MAXIMUM_AUDIBLE_FREQ = 20000;

    public float amplitude;
    public float waveform[];
    private HashSet<Integer> harmonics = new HashSet<>();
    private float m_freq;
    private float m_min_freq;
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

    public boolean set_harmonic(int harmonic_n) {
        //check validity of harmonic_n
        if (harmonic_n >= -1 && harmonic_n <= 1)  { //don't allow -1, 0, or 1
            return false;
        } else if (harmonic_n < -1) {   //check to see if the harmonic will be audible
            if (m_freq/Math.abs(harmonic_n) < MINIMUM_AUDIBLE_FREQ) {
                Log.d("WAVEFORM", "harmonic inaudible! (low):" + m_freq/Math.abs(harmonic_n) + " harm:" + harmonic_n + " m_freq:" + m_freq);
                return false;
            }
        } else if (harmonic_n > 1) {    //check to see if the harmonic will be audible
            if (m_freq*harmonic_n > MAXIMUM_AUDIBLE_FREQ) {
                Log.d("WAVEFORM", "harmonic inaudible! (high):" + m_freq*harmonic_n + " harm:" + harmonic_n + " m_freq:" + m_freq);
                return false;
            }
        }

        harmonics.add(harmonic_n);

        Log.d("WAVEFORM", "(add)harmonics: " + harmonics);
        return true;
    }

    public boolean remove_harmonic(int harmonic_n) {
        if (harmonics.contains(harmonic_n)) {
            harmonics.remove(harmonic_n);
        }
        Log.d("WAVEFORM", "(remove)harmonics: " + harmonics);
        return true;
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

    private float calculate_freq_from_n(int n) {
        return (float)(440*Math.pow(2.0, (double)n/12.0));
    }

    private float calculate_minimum_freq(Set<Integer> harmonics) {
        int min_harmonic = Integer.MAX_VALUE;
        float min_freq = Float.MAX_VALUE;
        Integer[] harmonics_array = harmonics.toArray(new Integer[harmonics.size()]);

        int i = 0;
        while (i < harmonics.size()) {
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
        return min_freq;
    }

    public void set_waveform(int n, shape_e shape) {
        //calculate frequency from n
        m_freq = (float)(440*Math.pow(2.0, (double)n/12.0));

        //use the lowest harmonic (longest period wave) to set the floats_per_cycle
        int divisor = harmonic_shift-(max_harmonic_shift/2);
        if (divisor != 0) {
            m_min_freq = m_freq/Math.abs(divisor);
        } else {
            m_min_freq = m_freq;
        }
        floats_per_cycle = (int)(sample_rate/m_min_freq);
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
        int j = 0;
        int divisor = 1;
        float multiplier = (float)1.0;
        float A = amplitude / 2;
        Log.d("WAVEFORM", "generating sine wave with amplitude " + A);

        Integer[] harmonics_array = {};
        if (harmonics != null) {
            if (!harmonics.isEmpty()) {
                harmonics_array = harmonics.toArray(new Integer[harmonics.size()]);
            }
        }

        while (j <= harmonics_array.length) {
            if (harmonics_array != null && j != harmonics_array.length) {
                if (harmonics_array[j] < 0) {
                    divisor = Math.abs(2 * harmonics_array[j]);
                    multiplier = (float) 1.0 / Math.abs(harmonics_array[j]);
                } else {    //must be positive, we don't allow 0
                    multiplier = harmonics_array[j];
                }
            }

            if (j == harmonics_array.length) {
                //when you're through the array, do the fundamental frequency
                divisor = 1;
                multiplier = (float)1.0;
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
