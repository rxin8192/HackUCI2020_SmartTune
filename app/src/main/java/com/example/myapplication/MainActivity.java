package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.media.MediaRecorder;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private int AUDIO_PERMISSION_CODE = 1;
    private MediaRecorder recorder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton buttonrequest = findViewById(R.id.MicButton);
        Chronometer timer = findViewById(R.id.timer);
        timer.setVisibility(View.GONE);
        buttonrequest.setOnClickListener(new View.OnClickListener() {
            boolean recording = false;
            @Override
            public void onClick(View v) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
//                }
                Chronometer timer = findViewById(R.id.timer);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                }
                else {
                    if(recording){
                        timer.stop();
                        timer.setVisibility(View.GONE);
                        stopRecording();
                        recording = !recording;
                    }
                    else {
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.setVisibility(View.VISIBLE);
                        timer.start();
                        startMicrophone();
                        recording = !recording;
                    }

                }
            }
        });

    }

    private void startMicrophone() {
        Log.d("tag", "Start");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    private void stopRecording(){
        Log.d("myTag", "Stop");
        recorder.stop();
        recorder.release();
        recorder = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    public void printStreamVolume(View view){
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        System.out.println( audio.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    public void increaseVolume(View view){
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

}

