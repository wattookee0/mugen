package com.example.wattookee0.mugen;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Tone current_tone = new Tone();
    Waveform waveform = new Waveform(440, Waveform.shape_e.SINE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final WaveformView waveform_View_graph = (WaveformView) findViewById((R.id.waveform_graph));

        /*final SeekBar amp_bar = (SeekBar) findViewById(R.id.amplitude);
            amp_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d("MAIN:", "setting amplitude to " + seekBar.getProgress());
                    waveform.amplitude = (float)(10.0*(seekBar.getProgress() + 1.0));
                }
            });*/

        /*final SeekBar freq_bar = (SeekBar) findViewById(R.id.freq_bar);
            freq_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    waveform.set_waveform((float)seekBar.getProgress(), Waveform.shape_e.SINE);
                    waveform_View_graph.set_Waveform(waveform);
                    waveform_View_graph.invalidate();
                }
            });*/
        final SeekBar harmonicsBar = (SeekBar) findViewById(R.id.harmonicsBar);
            harmonicsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    waveform.set_harmonic_shift(seekBar.getProgress() - seekBar.getMax()/2);
                }
            });
        final SeekBar pitch_bar = (SeekBar) findViewById(R.id.pitch);
            pitch_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    waveform.set_waveform(seekBar.getProgress()-57, Waveform.shape_e.SINE);
                    //current_tone.set_Pitch(seekBar.getProgress()-seekBar.getMax()/2);
                    //current_tone.generate_Sample();
                    waveform_View_graph.set_Waveform(waveform);
                    waveform_View_graph.invalidate();
                }
            });
        final SeekBar volume_bar = (SeekBar) findViewById(R.id.volume_bar);
            volume_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //current_tone.set_Volume(seekBar.getProgress()/100);
                    //current_tone.generate_Sample();
                    //waveform_View_graph.set_Waveform(current_tone.get_Sample(), current_tone.get_Floats_Per_Cycle());
                    //waveform_View_graph.invalidate();
                }
            });

       FloatingActionButton play_button = (FloatingActionButton) findViewById(R.id.play_button);
            play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tone.set_Duration(5);
                current_tone.play_Tone(waveform);
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
