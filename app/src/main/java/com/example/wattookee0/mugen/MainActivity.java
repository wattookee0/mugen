package com.example.wattookee0.mugen;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Tone current_tone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SeekBar freq_bar = (SeekBar) findViewById(R.id.freq_bar);
        final SeekBar pitch_bar = (SeekBar) findViewById(R.id.pitch_bar);
        final SeekBar volume_bar = (SeekBar) findViewById(R.id.volume_bar);

       FloatingActionButton play_button = (FloatingActionButton) findViewById(R.id.play_button);
            play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FREQ_BAR", "progress: " + freq_bar.getProgress());
                Log.d("PITCH_BAR", "progress: " + pitch_bar.getProgress());
                Log.d("VOL_BAR", "progress: " + volume_bar.getProgress());
                Log.d("VOL_BAR", "progress: " + ((float)volume_bar.getProgress()/100));
                current_tone = new Tone();
                Log.d("POINTER", "current_tone " + current_tone);
                current_tone.set_Duration(5);
                current_tone.set_Freq(freq_bar.getProgress());
                current_tone.set_Pitch(pitch_bar.getProgress() - 10);
                current_tone.set_Volume(((float)volume_bar.getProgress()/100));
                Log.d("POINTER", "current_tone " + current_tone);
                current_tone.play_Tone();
            }
        });

    // Example of a call to a native method
    //TextView tv = (TextView) findViewById(R.id.sample_text);
    //tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
