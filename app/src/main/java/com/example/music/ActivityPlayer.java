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

import java.io.IOException;
import java.util.ArrayList;

public class ActivityPlayer extends AppCompatActivity {

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
         */
        String[] item = songs.get(position).split(",_");
        Track track = new Track(item[2], item[1]);
        CreateNotification.createNotification(ActivityPlayer.this, track, R.drawable.pause,
                position, songs.size() - 1);

        startAnimation(mediaPlayer.isPlaying());
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnplay.setBackgroundResource(R.drawable.play);
                    startAnimation(mediaPlayer.isPlaying());
                    mediaPlayer.pause();
                } else {
                    btnplay.setBackgroundResource(R.drawable.pause);
                    startAnimation(mediaPlayer.isPlaying());
                    mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(v -> {
            position++;
            if (position <= songs.size() - 1) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    String[] item1 = songs.get(position).split(",_");
                    Track track1 = new Track(item1[2], item1[1]);
                    CreateNotification.createNotification(ActivityPlayer.this, track1, R.drawable.pause,
                            position, songs.size() - 1);
                    txtsname.setText(item1[2]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnpre.setOnClickListener(v -> {
            position--;
            if (position >= 0) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    String[] item12 = songs.get(position).split(",_");
                    Track track12 = new Track(item12[2], item12[1]);
                    CreateNotification.createNotification(ActivityPlayer.this, track12, R.drawable.pause,
                            position, songs.size() - 1);
                    txtsname.setText(item12[2]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnnext.setOnLongClickListener(v -> {
            int p = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(p + 5000);
            return true;
        });
        btnpre.setOnLongClickListener(v -> {
            int p = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(p - 5000);
            return true;
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

    public void startAnimation(Boolean isPlaying) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(playerimage, "rotation", 0f, 360f);
//        animator.setDuration();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        if (isPlaying) {
            animatorSet.start();
        } else {
            animatorSet.cancel();
        }
    }
}