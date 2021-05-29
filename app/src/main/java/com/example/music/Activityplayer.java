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
import java.io.IOException;
import java.util.ArrayList;

public class Activityplayer extends AppCompatActivity {

    Button btnplay, btnnext, btnpre;
    TextView txtsname, txtstart, txtstop;
    SeekBar sekbar;
    BarVisualizer visualizer;
    ImageView playerimage;
    String sname;
    NotificationManager notificationManager;

    public static final String EXTRA_NAME = "song_name";
    MediaPlayer mediaPlayer;
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

        btnnext = findViewById(R.id.btnnext);
        btnplay = findViewById(R.id.playbtn);
        btnpre = findViewById(R.id.btnpre);
        txtsname = findViewById(R.id.txtxn);
        txtstart = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        sekbar = findViewById(R.id.sekbar);
        visualizer = findViewById(R.id.blast);
        playerimage = findViewById(R.id.playerimage);

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
        ArrayList<String> songs = (ArrayList) bundle.getParcelableArrayList("songs");
        Uri uri = Uri.parse(uris.get(position).toString());
        sname = songName;
        txtsname.setText(sname);

        createChannel();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        /**
         * Working on notification
         * CreateNotification.createNotification(Activityplayer.this, Track.gets());
         */
        startAnimation();
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

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position <= songs.size() - 1) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        String tem = songs.get(position).split(",_")[2].toString();
                        txtsname.setText(tem);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position >= 0) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        String tem = songs.get(position).split(",_")[2].toString();
                        txtsname.setText(tem);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnnext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int p = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(p + 5000);
                return true;
            }
        });
        btnpre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int p = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(p - 5000);
                return true;
            }
        });
        sekbar.setMax(mediaPlayer.getDuration());
        sekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        bekbarTimeUpdate(mediaPlayer,);
    }


//    public void bekbarTimeUpdate  (MediaPlayer mediaplayer,){
//        Runnable UpdateSongTime = new Runnable() {
//            @Override
//            public void run() {
//                int starttime = mediaplayer.getCurrentPosition();
//
//            }
//        };
//
//    }

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(playerimage, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
}