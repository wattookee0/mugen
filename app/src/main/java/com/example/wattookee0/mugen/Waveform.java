package com.example.wattookee0.mugen;

import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by wattookee0 on 11/28/2017.
 */

public class Waveform {

    //enums

    /*
        pass in a shape for the fundamental or harmonic
     */
    public enum shape_e {
        SINE,
        SQUARE,
        SAW,
        RECTIFIED
    }

    //constants
    static final int SAMPLE_RATE = 192000;          //this may need to change app-wide eventually
    static final float TWO_PI = (float)Math.PI*2;   //used for generating waveforms
    static final int MINIMUM_AUDIBLE_FREQ = 20;     //approximate lowest freq audible to humans
    static final int MAXIMUM_AUDIBLE_FREQ = 20000;  //approximate highest freq audible to humans
    static final int MAX_ALLOWED_HARMONICS = 6;     //only allow so many harmonics to be added, for efficiency sake

    public float m_amplitude;   //base amplitude of the fundamental
    public float m_fundamental_waveform[];  //the fundamental waveform
    public float m_waveform[];  //compiled waveform (the one that gets played)
    //a Set of harmonics (multiples of the fundamental)
    private HashSet<harmonic> m_harmonics = new HashSet<harmonic>(MAX_ALLOWED_HARMONICS);

    private float m_freq;           //fundamental frequency
    public int m_floats_per_cycle;  //size of the waveform , if we're only going to fit a single cycle into it (which we are)
    private shape_e m_shape;        //the same of the waveform

    /*
        returns the size of the waveform array to the caller
     */
    public int get_floats_per_cycle() {
        return m_floats_per_cycle;
    }

    /*
        returns the fundamental waveform to the caller
     */
    public float[] get_waveform() {
        return m_waveform;
    }

    /*
        adds a harmonic of multiple harmonic_n to the fundamental
     */
    public boolean set_harmonic(int harmonic_n) {
        if (m_harmonics.size() < MAX_ALLOWED_HARMONICS) {
            m_harmonics.add(new harmonic(m_freq, harmonic_n, (float) 100.0, shape_e.SINE));
            Log.d("WAVEFORM", "(add)harmonics: " + m_harmonics);
            return true;
        } else {
            return false;
        }
    }

    /*
        removes a harmonic of multiple harmonic_n from the fundamental
     */
    public boolean remove_harmonic(int harmonic_n) {
        if (m_harmonics.contains(harmonic_n)) {
            Log.d("WAVEFORM", "(remove)harmonics: " + m_harmonics);
            m_harmonics.remove(harmonic_n);
            return true;
        }
        return false;
    }

    /*
        sets base amplitude of the fundamental
     */
    public void set_amplitude(float new_amplitude) { m_amplitude = new_amplitude;}

    /*
        calculates a frequency based on its step away from A4 (440Hz)
     */
    private float calculate_freq_from_n(int n) {
        return (float)(440*Math.pow(2.0, (double)n/12.0));
    }

    /*
        finds lowest harmonic and returns its frequency
     */
    private float calculate_minimum_freq(HashSet<harmonic> check_harmonics) {
        int min_harmonic = Integer.MAX_VALUE;
        float min_freq = Float.MAX_VALUE;
        harmonic i_harmonic;
        Iterator<harmonic> i = check_harmonics.iterator();

        //gather lowest freq from the set of harmonics
        while (i.hasNext()) {
            i_harmonic = i.next();
            if (i_harmonic.h_freq < min_freq) {
                min_freq = i_harmonic.h_freq;
            }
        }

        if (min_freq >= m_freq) {   //if you don't find a lower freq, then min freq is the fundamental
            min_freq = m_freq;
        }
        Log.d("WAVEFORM", "min freq = " + min_freq + " m_freq = " + m_freq);
        return min_freq;
    }

    /*
        generates the waveform
     */
    public void set_waveform(int n, shape_e shape, HashSet<harmonic> new_harmonics) {
        //calculate frequency from n
        m_freq = calculate_freq_from_n(n);

        m_floats_per_cycle = (int)(SAMPLE_RATE /calculate_minimum_freq(new_harmonics));
        m_shape = shape;
        m_waveform = new float[m_floats_per_cycle];
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

    public Waveform(int n, shape_e shape, Integer new_harmonics[]) {
        m_amplitude = (float)100.0;
        m_freq = (float)calculate_freq_from_n(n);
        float lowest_freq = m_freq;

        //add harmonics
        int i = 0;
        if (new_harmonics != null) {
            while ((i < MAX_ALLOWED_HARMONICS) && (i < new_harmonics.length)) { //could potentially cut off extra harmonics passed in
                set_harmonic(new_harmonics[i]);
                i++;
            }
        }

        //setup the waveform array to hold everything
        if (new_harmonics != null) {
            lowest_freq = calculate_minimum_freq(m_harmonics);
        }
        m_floats_per_cycle = (SAMPLE_RATE / (int)lowest_freq);
        m_waveform = new float[m_floats_per_cycle];
        m_fundamental_waveform = new float[m_floats_per_cycle];

        //generate the fundamental
        m_shape = shape;
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

        //put it all together!
        m_waveform = compile_waveforms(m_fundamental_waveform, m_harmonics);

    }

    private float[] compile_waveforms(float[] fundamental, HashSet<harmonic> harmonics) {
        float output[] = new float[fundamental.length];
        float max_amplitude = (float)0.0;
        float min_amplitude = (float)0.0;
        int j = 0;
        int k = 0;
        harmonic i_harmonic = null;
        //combine all the harmonics
        Iterator<harmonic> i = harmonics.iterator();
        while (i.hasNext()) {
            j = 0;
            k = 0;
            i_harmonic = i.next();
            while ((j+k) < fundamental.length) {        //the fundamental's length is set to the longest harmonic's length already
                j = 0;
                //Log.d("COMPILE", "HARMONIC LENGTH " + i_harmonic.h_floats_per_cycle + " k: " + k + " fdmntl lgth: " + fundamental.length);
                while (j < i_harmonic.h_floats_per_cycle) {
                    output[j+k] += i_harmonic.h_waveform[j];
                    //Log.d("COMPILE:","output J: " + output[j]);
                    j++;
                }
                k+=i_harmonic.h_floats_per_cycle;
            }
        }

        //throw the fundamental on top
        j = 0;
        while (j < fundamental.length) {
            output[j] += fundamental[j];
            //capture min/max amplitudes
            if (output[j] > 0) {
                if (output[j] > max_amplitude) {
                    max_amplitude = output[j];
                }
            } else if (output[j] < 0) {
                if (output[j] < min_amplitude) {
                    min_amplitude = output[j];
                }
            }
            j++;
        }

        //determine amplitude scale factor
        float amplitude_scale = m_amplitude/Math.max(Math.abs(max_amplitude), Math.abs(min_amplitude));

        if (amplitude_scale < 1.0) {    //if the resulting waveform is much larger than the specified amplitude
            //normalize amplitude
            j = 0;
            while (j < fundamental.length) {
                output[j]*=amplitude_scale;
                j++;
            }
        }

        return output;
    }

    //fill a harmonic with a sine wave
    private void sine_Wave(harmonic harmonic) {
        int i = 0;
        while (i < harmonic.h_floats_per_cycle) {
            harmonic.h_waveform[i] += (float) ( (harmonic.h_amplitude/2) * Math.sin((harmonic.h_freq * TWO_PI * i) / SAMPLE_RATE));
            i++;
        }
    }

    //fill the fundamental with a sine wave
    private void sine_Wave() {
        int i = 0;
        float A = m_amplitude / 2;
        Log.d("WAVEFORM", "generating sine wave with amplitude " + A);

        while (i < m_floats_per_cycle) {
            m_fundamental_waveform[i] += (float) ( (A) * Math.sin((m_freq * TWO_PI * i) / SAMPLE_RATE));
            i++;
        }

    }

    private void square_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            if ((float) Math.sin( (m_freq * TWO_PI * i) / SAMPLE_RATE) > 0) {
                m_waveform[i] = (float)1.0;
            } else {
                m_waveform[i] = (float)-1.0;
            }
            i++;
        }
    }

    private void saw_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            m_waveform[i] = (float)(i/m_floats_per_cycle);
            i++;
        }
    }

    private void rectified_Wave() {
        int i = 0;
        while (i < m_floats_per_cycle) {
            m_waveform[i] = (float)( Math.abs( Math.sin( (m_freq * TWO_PI * i) / SAMPLE_RATE) ) );
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
                //throw new IllegalArgumentException("Invalid fundamental frequency in new harmonic, must be greater than "
                //        + MINIMUM_AUDIBLE_FREQ + " and less than " + MAXIMUM_AUDIBLE_FREQ + ".");
            }

            //check for valid harmonics
            if (n < -1) {
                h_freq = fundamental_freq/Math.abs(n);
                if (h_freq < MINIMUM_AUDIBLE_FREQ) {
                    //throw new IllegalArgumentException("Invalid n, resulting harmonic inaudible.");
                }
            } else if (n > 1) {
                h_freq = fundamental_freq*n;
                if (h_freq < MAXIMUM_AUDIBLE_FREQ) {
                    //throw new IllegalArgumentException("Invalid n, resulting harmonic inaudible.");
                }
            } else {
                //throw new IllegalArgumentException("Invalid n in new harmonic.  Must be < -1 or > 1");
            }

            if (new_amplitude > 0.0) {
                h_amplitude = new_amplitude;
            } else {
                //throw new IllegalArgumentException("Invalid amplitude in new harmonic, must be > 0.0");
            }

            //everything checked out, let's get on it with
            h_floats_per_cycle = SAMPLE_RATE /(int) h_freq;

            h_waveform = new float[h_floats_per_cycle];
            h_shape = new_shape;
            sine_Wave(this);
        }
    }
}