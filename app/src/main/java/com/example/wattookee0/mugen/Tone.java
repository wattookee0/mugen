package com.example.wattookee0.mugen;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wattookee0 on 6/24/2017.
 */

public class Tone {

    static final float two_pi = 2*(float)Math.PI;

    //user defined variables
    int track_duration_in_seconds = 2;
    int test = 0;

    int sample_length = track_duration_in_seconds;
    private int pitch = 0;
    private float volume = 1.0f;

    //dev defined variables and calculated
    public float sample[];
    private int sample_rate = 192000;

    int frequency_in_Hz = 440;
    private int floats_per_cycle;

    //objects
    AudioTrack track;
    private Timer track_timer = new Timer();
    TimerTask track_length = new TimerTask() {
        @Override
        public void run() {
            Log.d("TRACK_TIMER", "timer triggered at " + track.getPlaybackHeadPosition());
            if (track.getPlaybackHeadPosition() >= track_duration_in_seconds*1000) {
                Log.d("HEAD POSITION:", "position: " + track.getPlaybackHeadPosition());
                destroy_Tone();
            }
        }
    };
    double random_noise = 1.0;
    float attack;
    float decay;
    public Tone() {
    }

    public Tone(int duration_in_seconds, float volume, AudioSample sample) {

    }

    public void set_Volume(float new_volume) {
        volume = new_volume;
    }

    public void set_Pitch(int new_pitch) {
        frequency_in_Hz += new_pitch;
    }

    public void set_Freq(int new_freq) {
        frequency_in_Hz = new_freq;
    }

    public void set_Duration(int new_track_duration_in_seconds) {
        track_duration_in_seconds = new_track_duration_in_seconds;
    }

    public float[] get_Sample() { return sample; }
    public int get_Sample_Length() { return sample_length; }
    public int get_Floats_Per_Cycle() {return floats_per_cycle;}

    public void generate_Sample() {
        floats_per_cycle = sample_rate/frequency_in_Hz;
        sample = new float[floats_per_cycle];
        int i = 0;
        while (i < (int)floats_per_cycle) {
            if (i < floats_per_cycle/4) {
                sample[i] = (float) (Math.sin(((frequency_in_Hz + pitch) * two_pi * i) / sample_rate));
            }
            if ((i >= floats_per_cycle/4) && (i < floats_per_cycle/2)) {
                //sample[i] = (float)Math.abs(Math.sin( ((frequency_in_Hz+pitch) * two_pi * i)/sample_rate ) );
                if (Math.sin( ( (frequency_in_Hz + pitch) * two_pi * i) / sample_rate) > 0.0) {
                    sample[i] = (float) .99;
                } else {
                    sample[i] = (float) -.99;
                }
            }
            if ((i>=floats_per_cycle/2) && (i < 3*floats_per_cycle/4)) {
                if (Math.sin( ( (frequency_in_Hz + pitch) * two_pi * i) / sample_rate) > 0.0) {
                    sample[i] = (float) .99;
                } else {
                    sample[i] = (float) -.99;
                }
            }
            if (i >= 3*floats_per_cycle/4) {
                sample[i] = (float) (Math.sin(((frequency_in_Hz + pitch) * two_pi * i) / sample_rate));
            }
            i++;
        }
    }

    public void play_Tone() {
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sample_rate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_FLOAT,
                sample_rate,
                AudioManager.MODE_NORMAL);
        //AudioSample sample = new AudioSample();
        track.setBufferSizeInFrames(floats_per_cycle);
        Log.d("BUFFER:", "capacity:" + track.getBufferCapacityInFrames());
        test = track.write(sample, 0, (int)floats_per_cycle, AudioTrack.WRITE_NON_BLOCKING);
        Log.d("WRITTEN: ", ": "+test);
        track.setVolume(volume);
        track.setLoopPoints(0, floats_per_cycle-1, -1);
        track.play();
        track_timer.schedule(track_length, track_duration_in_seconds*1000+1);
    }

    private void destroy_Tone() {
        Log.d("DESTROY", "Tone Destroyed! Freq: " + frequency_in_Hz);
        track.stop();
        track.release();
    }
}
