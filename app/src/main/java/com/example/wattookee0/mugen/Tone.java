package com.example.wattookee0.mugen;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wattookee0 on 6/24/2017.
 */

public class Tone {

    //constants
    static final int SAMPLE_RATE = 48000;
    static final int BUFFER_LENGTH_IN_BYTES = 4096;
    static final int BUFFER_LENGTH_IN_FRAMES = BUFFER_LENGTH_IN_BYTES /Float.BYTES;
    //user defined variables
    int track_duration_in_seconds = 2;
    private float volume = 0.01f;

    //objects
    AudioTrack[] track;
    int track_num = 0;
    int tracks;
    public Tone() {
    }

    public void set_Volume(float new_volume) {
        volume = new_volume;
    }

    public void set_Duration(int new_track_duration_in_seconds) {
        track_duration_in_seconds = new_track_duration_in_seconds;
    }

    public void play_Tone(Waveform waveform) {
        int j = 0;
        tracks = waveform.m_floats_per_cycle/ BUFFER_LENGTH_IN_FRAMES;
        if (tracks < 1) {
            tracks = 1;
        }
        float[] waveform_slice;
        Log.d("TONE", "Expect " + tracks + " tracks.");
        track = new AudioTrack[tracks];
        while (track_num < tracks) {
            track[track_num] = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_FLOAT,
                    BUFFER_LENGTH_IN_BYTES,
                    AudioManager.MODE_NORMAL);
            waveform_slice = Arrays.copyOfRange(waveform.m_waveform, j, j+BUFFER_LENGTH_IN_FRAMES);
            track[track_num].write(
                    waveform_slice,
                    0,
                    waveform_slice.length,
                    AudioTrack.WRITE_NON_BLOCKING);
            track[track_num].setVolume(volume);
            Log.d("TONE", "num:"+track_num+"/"+track.length+" track:"+track[track_num]+" floats in slice " + waveform_slice.length + " size:" + track[track_num].getBufferSizeInFrames());
            track[track_num].setLoopPoints(0, track[track_num].getBufferSizeInFrames(),1); //no looping
            track[track_num].setPlaybackHeadPosition(0);
            j+= track[track_num].getBufferSizeInFrames();
            track_num++;
        }
        track[0].play();
        final Timer track_timer = new Timer();
        TimerTask track_length = new TimerTask() {
            @Override
            public void run() {
                int k = 0;
                while (k < track_num) {
                    if (track[k].getPlaybackHeadPosition() >= track[k].getBufferSizeInFrames()) {
                        Log.d("HEAD POSITION:", "["+ k + "][" +tracks+"] position:" + track[k].getPlaybackHeadPosition()+ " size:" + track[k].getBufferSizeInFrames());
                        destroy_Tone(k);
                        k++;
                        if (k < track_num) {
                            track[k].play();
                        }
                    }
                }
            }
        };
        track_timer.schedule(track_length, track_duration_in_seconds*1000);
    }

    private void destroy_Tone(int index) {
        Log.d("DESTROY", "Tone Destroyed! index:" + index);
        track[index].stop();
        track[index].release();
    }
}
