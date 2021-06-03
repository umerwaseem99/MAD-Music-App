package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.music.Services.OnClearFromRecentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ActivityPlayer extends AppCompatActivity implements Playable {

    Button btnplay, btnnext, btnpre;
    TextView txtsname, txtstart, txtstop;
    SeekBar sekbar;
    ImageView playerimage;
    NotificationManager notificationManager;
    BroadcastReceiver broadcastReceiver;
    public static final String EXTRA_NAME = "song_name";
    MediaPlayer mediaPlayer;
    int position;
    Timer timer;
    Handler mHandler;

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CreateNotification.CHANNEL_ID, "TEST DEV", NotificationManager.IMPORTANCE_HIGH);
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
        playerimage = findViewById(R.id.playerimage);
        mHandler = new Handler();
        Objects.requireNonNull(getSupportActionBar()).hide();

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
        txtsname.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        int duration = mediaPlayer.getDuration();
        String time = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        txtstop.setText(time);
        updateTimer();
        /**
         * Working on notification
         */
        String[] item = songs.get(position).split(",_");
        Track track = new Track(item[2], item[1]);
        CreateNotification.createNotification(ActivityPlayer.this, track, R.drawable.pause,
                position, songs.size() - 1);

        startAnimation();
        btnplay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                onTrackPlay(track, uris, songs, item[2]);
                mediaPlayer.pause();
                startAnimation();
            } else {
                onTrackPause(track, uris, songs, item[2]);
                mediaPlayer.start();
                startAnimation();
            }
            updateTimer();
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");
                switch (action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        position--;
                        String[] item1 = songs.get(position).split(",_");
                        Track track1 = new Track(item1[2], item1[1]);
                        onTrackPrevious(track1, uris, songs, item1[2]);
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if (mediaPlayer.isPlaying()) {
                            onTrackPause(track, uris, songs, item[2]);
                            mediaPlayer.start();
                        } else {
                            onTrackPlay(track, uris, songs, item[2]);
                            mediaPlayer.stop();
                        }
                        updateTimer();
                        break;
                    case CreateNotification.ACTION_NEXT:
                        position++;
                        String[] item2 = songs.get(position).split(",_");
                        Track track2 = new Track(item2[2], item2[1]);
                        onTrackNext(track2, uris, songs, item2[2]);
                        break;
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
        /**
         * Events for media player
         */
        btnnext.setOnClickListener(v -> {
            if (position != songs.size() - 1) {
                position++;
                String[] item1 = songs.get(position).split(",_");
                Track track1 = new Track(item1[2], item1[1]);
                onTrackNext(track1, uris, songs, item1[1] + "\n" + item1[0]);
            }
        });

        btnpre.setOnClickListener(v -> {
            if (position != 0) {
                position--;
                String[] item1 = songs.get(position).split(",_");
                Track track1 = new Track(item1[2], item1[1]);
                onTrackPrevious(track1, uris, songs, item1[1] + "\n" + item1[0]);
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
        updateSeekbar();
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        Intent i = new Intent(this, MusicList.class);
        startActivity(i);
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(playerimage, "rotation", 0f, 360f);
        animator.setDuration(2500);
        animator.setRepeatCount(Animation.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    @Override
    public void onTrackPrevious(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName) {
        if (position > 0) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                CreateNotification.createNotification(ActivityPlayer.this, track,
                        position, songs.size() - 1);
                int duration = mediaPlayer.getDuration();
                String time = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                txtstop.setText(time);
                txtsname.setText(sName);
                updateSeekbar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTrackPlay(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName) {
        CreateNotification.createNotification(ActivityPlayer.this, track, R.drawable.play,
                position, songs.size() - 1);
        btnplay.setBackgroundResource(R.drawable.play);
        startAnimation();
    }

    @Override
    public void onTrackPause(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName) {
        CreateNotification.createNotification(ActivityPlayer.this, track, R.drawable.pause,
                position, songs.size() - 1);
        btnplay.setBackgroundResource(R.drawable.pause);
        startAnimation();
    }

    @Override
    public void onTrackNext(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName) {
        if (position <= songs.size() - 1) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uris.get(position).toString()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                CreateNotification.createNotification(ActivityPlayer.this, track,
                        position, songs.size() - 1);
                int duration = mediaPlayer.getDuration();
                String time = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                txtstop.setText(time);
                txtsname.setText(sName);
                updateSeekbar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);
    }

    public void updateTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int duration = mediaPlayer.getCurrentPosition();
                        String time = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                        txtstart.post(() -> txtstart.setText(time));
                    } else {
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 0, 1000);
    }

    public void updateSeekbar() {
        sekbar.setMax(mediaPlayer.getDuration());
        ActivityPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    sekbar.setProgress(currentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        sekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                updateTimer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}