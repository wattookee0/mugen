package com.example.wattookee0.mugen;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    Waveform waveform = new Waveform(0, Waveform.shape_e.SINE, null);
    HashSet<Integer> harmonics = new HashSet<Integer>(6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final WaveformView waveform_View_graph = (WaveformView) findViewById((R.id.waveform_graph));

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
                    waveform = new Waveform(seekBar.getProgress()-57, Waveform.shape_e.SINE, harmonics.toArray(new Integer[harmonics.size()]));
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
                Tone current_tone = new Tone();
                current_tone.set_Duration(5);
                current_tone.play_Tone(waveform);
            }
        });

    // Example of a call to a native method
    //TextView tv = (TextView) findViewById(R.id.sample_text);
    //tv.setText(stringFromJNI());
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        int value = 0;

        switch (view.getId()) {
            case R.id.harmonic_minus1:
                value = -2;
                break;
            case R.id.harmonic_minus2:
                value = -3;
                break;
            case R.id.harmonic_minus3:
                value = -4;
                break;
            case R.id.harmonic_plus1:
                value = 2;
                break;
            case R.id.harmonic_plus2:
                value = 3;
                break;
            case R.id.harmonic_plus3:
                value = 4;
                break;
        }

        Log.d("MAIN", "id:" + view.getId() + " value:" + value + " checked:" + checked);
        boolean fail = false;
        if (checked) {
            Log.d("MAIN", "setting harmonic");
            harmonics.add(value);
        } else {
            Log.d("MAIN", "removing harmonic");
            harmonics.remove(value);
        }

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
