package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.os.AsyncTask;
import android.media.MediaRecorder;
import android.os.Handler;

import android.content.SharedPreferences;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public final static String SHARED_PREFs = "sharedPrefs";

    // Constants
    private static final int SAMPLE_SIZE = 10;
    private int AUDIO_PERMISSION_CODE = 1;

    // Fields
    private SoundMeter sMeter;
    private boolean recording;  // True if the the microphone is recording
    private int default_vol;
    private int curr_increment = 0;
    private double ambient_noise = 0;
    private AudioManager audio;
    private double sensitivity = 1.0;
    private double initial_noise;
    private boolean volume_adjusted = false;


    @Override
    protected void onDestroy() {
        recording = false;
        sMeter.stop();
        System.out.println("MainThreadDestroyed");
        super.onDestroy();
    }

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
            double last_volume = 1000;

            // Initialize Queue
            ArrayQueue volumes = new ArrayQueue(SAMPLE_SIZE);
            for(int i = 0; i < SAMPLE_SIZE; i++) {
                last_volume = sMeter.getAmplitude();
                try {
                    Thread.sleep(300);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

                volumes.enqueue(last_volume);
            }
            initial_noise = volumes.getMedian();

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFs,MODE_PRIVATE);

            // Event Loop
            while (recording) {
                sensitivity = sharedPreferences.getInt("Calibration", 50);


                long startTime = System.currentTimeMillis();
                double recordedVolume = sMeter.getAmplitude(); //GetAmplitude returns a range from ~ 20~80
                //This is sensitivity - eliminates outlierss. Inertia. Low Sens = high Inertia, High = low.

                if(sharedPreferences.getBoolean("Inverse", false)){
                    sensitivity = -Math.abs(sensitivity);
                }
                else{
                    sensitivity = Math.abs(sensitivity);
                    if(recordedVolume > (((sensitivity*1.3)/100)+1)*last_volume) //How much bigger the next data point is from the last one. 1.3 - 2.3
                        recordedVolume = (((sensitivity*1.3)/100)+1)*last_volume; //Outlier Avoider.
                }

                if(recordedVolume < 5)
                    recordedVolume = 5;
                last_volume = recordedVolume;
                volumes.poppush(last_volume);
                if(!volume_adjusted)
                    setVolume(volumes.getMedian());
                System.out.println("Recorded Volume: " + recordedVolume);
//                System.out.println("Average last " + SAMPLE_SIZE + ": " + volumes.getMedian());
//                System.out.println("Current Volume: " + (default_vol+curr_increment)  );

                ambient_noise = volumes.getMedian();


                try {
                    Thread.sleep(500-(int)(startTime-System.currentTimeMillis()));
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

        //Retrieves the calibration value from the settings.

        //ambient noise and current volume
        av = (TextView)findViewById(R.id.currentNoise);
        cv = (TextView)findViewById(R.id.currentVolume);
        final Handler handler = new Handler();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        changeStrings mAsync = new changeStrings();
                        mAsync.execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000); //Every 1 second


        ImageButton buttonrequest = findViewById(R.id.MicButton);
        recording = false;
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        default_vol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);


        //timer
        Chronometer timer = findViewById(R.id.timer);
        timer.setVisibility(View.GONE);

        //Mic Button Code
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

        //Help Menu code.
        ImageButton help = findViewById(R.id.HelpButton);
        help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                default_vol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (!volume_adjusted)
                {
                    Thread adjust = new Thread(lockVolume);
                    adjust.start();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                default_vol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (!volume_adjusted)
                {
                    Thread adjust = new Thread(lockVolume);
                    adjust.start();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void setVolume(double median){
        //sensitivity 0-100. 0.25 - 1.75. sense*1.5/100

        //Amount each of increase per each change. And Range.
        int diff = (int)((median - initial_noise));
        curr_increment = (diff/20);

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, default_vol+curr_increment > 0 ? default_vol+curr_increment : 1, 0);
    }

    private void startMicrophone() {
        recording = true;
        (new Thread(micListener)).start();
        default_vol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void startRecording() {
        startService(new Intent(this, MyService.class));
        sMeter = new SoundMeter();
        sMeter.start();

        // Handle polling
        (new Thread(pollThread)).start();


    }


    private void stopRecording() {
        stopService(new Intent(this, MyService.class));
        recording = false;
        sMeter.stop();

        Log.d("myTag", "Stop");
//        threadStop = true;
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
        System.out.println( audio.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    public void increaseVolume(View view){
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

//    private void updateAmbient(double average)
//    {
//
//    }

    private TextView cv,av;
    private String ambientString,volumeString;
    changeStrings mAsync = null;
    Timer timer = null;
    TimerTask task = null;
    private class changeStrings extends AsyncTask<String, Void, String> {

        public changeStrings(){

        }

        @Override
        protected String doInBackground(String... params) {
            //Background operation in a separate thread
            //Write here your code to run in the background thread
            //calculate here whatever you like
            DecimalFormat df = new DecimalFormat("0.00");
            if(recording) {
                ambientString = "Ambient Noise: " + df.format(ambient_noise);
                volumeString = "Current Volume: " + Integer.toString(default_vol + curr_increment);
            }
            else
            {
                ambientString = "";
                volumeString = "";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Called on Main UI Thread. Executed after the Background operation, allows you to have access to the UI
            av.setText(ambientString);
            cv.setText(volumeString);


        }

        @Override
        protected void onPreExecute() {
            //Called on Main UI Thread. Executed before the Background operation, allows you to have access to the UI
        }
    }


    private Runnable lockVolume = new Runnable(){
        public void run(){
            volume_adjusted = true;
            try {
                Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                System.out.println("interrupted");
            }
            volume_adjusted = false;
        }
    };
}

