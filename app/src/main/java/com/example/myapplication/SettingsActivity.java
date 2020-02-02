package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private final static String SHARED_PREFs = "sharedPrefs";
    public final static int DEFAULT_SENSITIVITY = 50, DEFAULT_BASE=0, DEFAULT_MAX=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Sensitivity Settings
        FloatingActionButton fab = findViewById(R.id.CloseButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Sensitivity Settings
        RangeSeekBar<Integer> sensitivitySet = findViewById(R.id.seekBar);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFs, MODE_PRIVATE);
        sensitivitySet.setSelectedMaxValue(sharedPreferences.getInt("Calibration", DEFAULT_SENSITIVITY));
        sensitivitySet.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFs, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                int newSensitivity = 100 - maxValue;
                editor.putInt("Calibration",  newSensitivity);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Sensitivity set to " + newSensitivity, Toast.LENGTH_SHORT).show();
            }
        });


        // Min Max settings
        RangeSeekBar<Integer> seekBar = findViewById(R.id.VolumeRange);
        seekBar.setSelectedMinValue(sharedPreferences.getInt("Base", DEFAULT_BASE));
        seekBar.setSelectedMaxValue(sharedPreferences.getInt("Max", DEFAULT_MAX));
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFs, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("Base", minValue);
                editor.putInt("Max", maxValue);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Volume Range: " + minValue + "-" + maxValue, Toast.LENGTH_SHORT).show();
            }
        });

        Switch inverse = findViewById(R.id.Inverse);
        SharedPreferences sp = getSharedPreferences(SHARED_PREFs, MODE_PRIVATE);
        inverse.setChecked( sp.getBoolean("Inverse", false));

        inverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFs, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putBoolean("Inverse", true);
                    editor.apply();
                } else {
                    editor.putBoolean("Inverse", false);
                    editor.apply();
                }

            }
        });
    }

}