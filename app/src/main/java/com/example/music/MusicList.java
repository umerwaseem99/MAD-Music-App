 package com.example.music;

import java.util.ArrayList;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MusicList extends AppCompatActivity {

    ListView listView;
    public ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        // Get firebase storage instance and set root reference for music files
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Task<ListResult> listRef = storage.getReference().listAll();
        listRef.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // All the items under listRef.
                    Log.d("FirebaseItem", item.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Uh-oh, an error occurred!
            }
        });

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
            items.add(cursor.getString(1) + ",_" + cursor.getString(2) + ",_" + cursor.getString(4) + ",_" + time );
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
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(myAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Uri> finalUris = uris;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), ActivityPlayer.class).putExtra("uris", finalUris).putExtra("songs", items).putExtra("songname", songName).putExtra("pos", position));
            }
        });
    }
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
