package com.example.audrec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    ImageButton play,del,pause;
    FloatingActionButton startRecording;
    String AudioSavePathInDevice;
    MediaRecorder mediaRecorder ;
    private SeekBar mSeekBar;

    Random random =new Random() ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    int i = 0;
    int length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRecording = findViewById(R.id.btnRecord);
        play=findViewById(R.id.play);
        del=findViewById(R.id.del);
        pause=findViewById(R.id.pause);

        play.setEnabled(false);
        del.setEnabled(false);


      //  mediaPlayer = new MediaPlayer();

       startRecording.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               switch (event.getAction()) {
                   case MotionEvent.ACTION_DOWN:

                       startRecording();

               return true;
                   case MotionEvent.ACTION_UP:
                       stoprec();
                        break;
               }
               return false;
           }
       });


    play.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        mediaPlayer = new MediaPlayer();
        i=0;

        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (i==0){
        mediaPlayer.start();
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);

        Toast.makeText(MainActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
       i++;
            System.out.println("ii"+i);
    }

    if(i==1)
        {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.INVISIBLE);


            Toast.makeText(MainActivity.this, "Recording Resumed", Toast.LENGTH_LONG).show();
            i = 0;

            System.out.println("ii"+i);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
                mediaPlayer.reset();
                i=0;
                System.out.println("ii"+i);
            }
        });
    }



});

    pause.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // if (i == 0) {
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);

                Toast.makeText(MainActivity.this, "Recording paused", Toast.LENGTH_LONG).show();
               // i++;
                System.out.println("ii"+i);
                i=0;
            }


    });





            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mediaPlayer != null){

                        mediaPlayer.stop();
                        mediaPlayer.release();

                        play.setEnabled(false);
                        pause.setEnabled(false);
                        del.setEnabled(false);

                        startRecording.setVisibility(View.VISIBLE);
                        mediaRecorder.reset();
                        MediaRecorderReady();
                        i=0;
                    }
                }
            });

}

    private void stoprec() {
        mediaRecorder.stop();
        Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
        play.setEnabled(true);
        pause.setEnabled(true);
        del.setEnabled(true);
        startRecording.setVisibility(View.INVISIBLE);
    }

    private void startRecording() {

        if(checkPermission()){
            AudioSavePathInDevice = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.mp3";
            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException | IOException e) {

                e.printStackTrace();
            }

            Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_LONG).show();
        }
        else {

            requestPermission();

        }

    }




    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    private void MediaRecorderReady() {

        mediaRecorder=new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private String CreateRandomAudioFileName(int j) {

        StringBuilder stringBuilder = new StringBuilder( j );

        int k = 0 ;
        while(k < j ) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            k++ ;
        }
        return stringBuilder.toString();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

}