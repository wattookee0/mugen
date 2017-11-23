package com.example.wattookee0.mugen;

import android.media.AudioTrack;

import java.util.Arrays;

/**
 * Created by wattookee0 on 7/4/2017.
 */

public class AudioSample {

    static final float two_pi = 2*(float)Math.PI;
    static final float sample_rate = 192000;

    float volume[];
    public float sample[];
    float pitch[];
    float frequency[];
    int attack;     //attack in ms
    int decay;      //decay in ms
    int sustain;    //sustain in ms

    public float[] compile_Sample(int sample_length) {
        sample = new float[sample_length];
        /*int i = 0;
        while (i < (int)sample_length) {
            sample[i] = (float)(volume[i]*Math.sin(((frequency[i]+pitch[i]) * two_pi * i)/sample_rate));
            i++;
        }*/
        return sample;
    }

}
