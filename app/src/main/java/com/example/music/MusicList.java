package com.example.music;

import java.util.ArrayList;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MusicList extends AppCompatActivity {

    ListView listView;
    public ArrayList<String> items = new ArrayList<>();
    public ArrayList<String> dis_items = new ArrayList<>();

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

    public ArrayList<Uri> findSong() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        ArrayList<Uri> uris = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            int time = Integer.parseInt(cursor.getString(5));
            time = time / (1000 * 60);
            items.add(cursor.getString(1) + ",_" + cursor.getString(2) + ",_" + cursor.getString(4) + ",_" + time);
            dis_items.add(cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(4) + " " + time);
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            uris.add(contentUri);
        }
        cursor.close();
        return uris;
    }


    public void displaySongs() {
        ArrayList<Uri> uris = new ArrayList<>();
        try {
            uris = findSong();
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dis_items) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.BLACK);
                    return view;
                }
            };

            listView.setAdapter(myAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Uri> finalUris = uris;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] song = items.get(position).split(",_");
                String songName = song[1] + "\n" + song[0];
                startActivity(new Intent(getApplicationContext(), ActivityPlayer.class).putExtra("uris", finalUris).putExtra("songs", items).putExtra("songname", songName).putExtra("pos", position));

            }
        });
    }
}