package com.avfplayer.global;

import com.avfplayer.models.SongDetail;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 1/24/2017.
 */

public class VideoListInfo {
    static ArrayList<SongDetail> playlist=new ArrayList<>();
    static int index;

    public ArrayList<SongDetail> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<SongDetail> playlist) {
        if(this.playlist.size() > 0){
            this.playlist.clear();
        }
        this.playlist = playlist;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
