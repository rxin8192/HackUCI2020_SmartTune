package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.media.MediaRecorder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private int AUDIO_PERMISSION_CODE = 1;

    private SoundMeter sMeter;
    private boolean recording;  // True if the the microphone is recording

    // Thread loops
    private Runnable micListener = new Runnable() {
        @Override
        public void run() {
            Log.d("tag", "Message");
            startRecording();
        }
    };

    private Runnable pollThread = new Runnable() {
        @Override
        public void run() {
            while (recording) {
                double recordedVolume = sMeter.getAmplitude();

                System.out.println("Recorded Volume: " + recordedVolume);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private MediaRecorder recorder = null;
    private volatile boolean threadStop = false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton buttonrequest = findViewById(R.id.MicButton);
        recording = false;


        Chronometer timer = findViewById(R.id.timer);
        timer.setVisibility(View.GONE);
        buttonrequest.setOnClickListener(new View.OnClickListener() {
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
                    }
                    else {
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.setVisibility(View.VISIBLE);
                        timer.start();
                        startMicrophone();
                    }

                }
            }
        });
    }

    class test implements Runnable{
        @Override
        public void run(){
            while(true) {
                // listen here
                try {
                    Thread.sleep(1500);
                } catch(InterruptedException e) {
                    System.out.println("got interrupted!");
                }
                System.out.println("Hello");
                if(threadStop){
                    return;
                }
            }
        }
    };

    private void startMicrophone() {
        recording = true;
        (new Thread(micListener)).start();
    }

    private void startRecording() {
        sMeter = new SoundMeter();
        sMeter.start();

        // Handle polling
        (new Thread(pollThread)).start();


        threadStop = false;
        test t = new test();
        new Thread(t).start();
    }


    private void stopRecording() {
        recording = false;
        sMeter.stop();
        Log.d("myTag", "Stop");
        threadStop = true;
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

