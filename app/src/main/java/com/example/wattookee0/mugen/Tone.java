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



    //user defined variables
    int track_duration_in_seconds = 1;
    private int pitch = 0;
    private float volume = 1.0f;

    //dev defined variables and calculated
    private int sample_rate = 192000;
    int frequency_in_Hz = 440;
    private int samples_per_cycle;

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

    public void play_Tone() {
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sample_rate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_FLOAT,
                track.getMinBufferSize(sample_rate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT),
                AudioManager.MODE_NORMAL);
        AudioSample sample = new AudioSample();
        track.write(sample.compile_Sample(sample_rate/frequency_in_Hz), 0, (int)samples_per_cycle, AudioTrack.WRITE_NON_BLOCKING);
        track.setVolume(volume);
        track.setLoopPoints(0, samples_per_cycle-1, -1);
        track.play();
        track_timer.schedule(track_length, track_duration_in_seconds*1000+1);
    }

    private void destroy_Tone() {
        Log.d("DESTROY", "Tone Destroyed! Freq: " + frequency_in_Hz);
        track.stop();
        track.release();
    }
}
