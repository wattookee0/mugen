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

    //constants
    static final int sample_rate = 192000;

    //user defined variables
    int track_duration_in_seconds = 2;
    private float volume = 1.0f;

    //objects
    AudioTrack track;
    public Tone() {
    }

    public void set_Volume(float new_volume) {
        volume = new_volume;
    }

    public void set_Duration(int new_track_duration_in_seconds) {
        track_duration_in_seconds = new_track_duration_in_seconds;
    }

    public void play_Tone(Waveform waveform) {
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sample_rate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_FLOAT,
                sample_rate,
                AudioManager.MODE_NORMAL);
        track.setBufferSizeInFrames(waveform.floats_per_cycle);
        track.write(waveform.waveform, 0, (int)waveform.floats_per_cycle, AudioTrack.WRITE_NON_BLOCKING);
        track.setVolume(volume);
        track.setLoopPoints(0, waveform.floats_per_cycle-1, -1);
        track.play();
        Timer track_timer = new Timer();
        TimerTask track_length = new TimerTask() {
            @Override
            public void run() {
                //Log.d("TRACK_TIMER", "timer triggered at " + track.getPlaybackHeadPosition());
                if (track.getPlaybackHeadPosition() >= track_duration_in_seconds*1000) {
                    //Log.d("HEAD POSITION:", "position: " + track.getPlaybackHeadPosition());
                    destroy_Tone();
                }
            }
        };
        track_timer.schedule(track_length, track_duration_in_seconds*1000+1);
    }

    private void destroy_Tone() {
        //Log.d("DESTROY", "Tone Destroyed! Freq: " + frequency_in_Hz);
        track.stop();
        track.release();
    }
}
