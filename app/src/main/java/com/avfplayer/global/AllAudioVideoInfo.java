package com.avfplayer.global;

import com.avfplayer.models.SongDetail;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 2/24/2017.
 */

public class AllAudioVideoInfo {

    static ArrayList<SongDetail> allList=new ArrayList<>();
    static int index;

    public ArrayList<SongDetail> getAllList() {
        return allList;
    }

    public void setAllList(ArrayList<SongDetail> allList) {
        this.allList = allList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
