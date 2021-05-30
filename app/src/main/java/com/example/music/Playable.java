package com.example.music;

import android.content.BroadcastReceiver;
import android.net.Uri;

import java.util.ArrayList;

public interface Playable {
    void onTrackPrevious(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName);

    void onTrackPlay(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName);
    void onTrackPause(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName);
    void onTrackNext(Track track, ArrayList<Uri> uris, ArrayList<String> songs, String sName);

}
