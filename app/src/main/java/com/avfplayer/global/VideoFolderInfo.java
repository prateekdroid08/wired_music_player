package com.avfplayer.global;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 1/21/2017.
 */

public class VideoFolderInfo {

    static ArrayList<HashMap<String, String>> folderList=new ArrayList<>();

    public ArrayList<HashMap<String, String>> getFolderList() {
        return folderList;
    }

    public void setFolderList(ArrayList<HashMap<String, String>> folderList) {
        this.folderList = folderList;
    }
}
