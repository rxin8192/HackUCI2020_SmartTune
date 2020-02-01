package com.example.myapplication;

import android.media.MediaRecorder;

import java.io.IOException;

public class SoundMeter {

    private MediaRecorder recorder;

    public void start() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/dev/null");

            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            recorder.start();
        }
    }

    public void stop() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public double getAmplitude() {
        if (recorder == null) {
            return 0;
        } else {
            int amp = recorder.getMaxAmplitude();
            System.out.println(amp);
            return amp/2700.0;
        }
    }

}
