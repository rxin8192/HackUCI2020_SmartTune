package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private final static String SHARED_PREFs = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        FloatingActionButton fab = findViewById(R.id.CloseButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SeekBar sensitivitySet = findViewById(R.id.sensitivityBar);
        sensitivitySet.setProgress(loadCalibration());

        sensitivitySet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Preference
                saveCalibration(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void saveCalibration(int progress){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFs,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("Calibration",  progress);
        editor.apply();

    }

    public int loadCalibration(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFs,MODE_PRIVATE);
        return sharedPreferences.getInt("Calibration", 50);
    }

}