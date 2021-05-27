 package com.example.music;

import java.io.File;
import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MusicList extends AppCompatActivity {

    ListView listView;
    public ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);


        getSupportActionBar().hide();
        listView = findViewById(R.id.songlist);
        runtimePermission();

    }

    public void runtimePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<String> findSong() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        ArrayList<String> songs = new ArrayList<String>();
        while (cursor.moveToNext()) {
            int time =Integer.parseInt(cursor.getString(5));
            time = time/(1000*60);
            songs.add(cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(4) + " " + time);
        }
        return songs;
    }


    public void displaySongs() {
        try {
//            Environment.getExternalStorageDirectory()
//            final ArrayList<String> mySongs = findSong();
            items = findSong();
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(myAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

      //Ye video se dekha hai hai ispr errors aray hain

          /*  listView.setOnClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String songName = (String)listView.getItemAtPosition(position);
                        startActivity(new Intent(getApplicationContext(),Activityplayer.class).putExtra("songs", mySong).putExtra("songname",songName).putExtra("pos", position));

                }*/
    }//);

    }
//aapka code update kia uskay baad se yahan error aray


   /*     class customAdapter extends BaseAdapter{


            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int i, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.list_item, null);
                TextView textSong = (TextView)findViewById(R.id.txtsongname);
                textSong.setSelected(true);
                textSong.setText(items[i]);
                return view;
            }
        }
    }*/
