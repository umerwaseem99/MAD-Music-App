package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class Activityplayer extends AppCompatActivity {

    Button btnplay, btnnext, btnpre, btnff, btnfr;
    TextView txtsname, txtsstart, txtsstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    ImageView imageview;
    String sname;
    NotificationManager notificationManager;

    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
//    ArrayList<File> mySongs;

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CreateNotification.CHANNNEL_ID, "TEST DEV", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        btnff = findViewById(R.id.btbff);
        btnfr = findViewById(R.id.btnfr);
        btnnext = findViewById(R.id.btnnext);
        btnplay = findViewById(R.id.playbtn);
        btnpre = findViewById(R.id.btnpre);
        txtsname = findViewById(R.id.txtxn);
        txtsstart = findViewById(R.id.txtstart);
        txtsstop = findViewById(R.id.txtstop);
        seekmusic = findViewById(R.id.sekbar);
        visualizer = findViewById(R.id.blast);
        imageview = findViewById(R.id.imageView);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        ArrayList<Uri> uris = (ArrayList) bundle.getParcelableArrayList("uris");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(uris.get(position).toString());
//        Uri uri = Uri.parse(bundle.get("uri").toString());
        sname = songName;
        txtsname.setText(sname);

        createChannel();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        /**
         * Working on notification
         * CreateNotification.createNotification(Activityplayer.this, Track.gets());
         */
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnplay.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    btnplay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });


        //Ye complete krna hai apun ko abhi
/*
        View.OnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri u = Uri.parse()
            }
        })*/


    }

    public void startAnimation(View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(imageview, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();


    }
}