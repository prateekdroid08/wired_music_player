package com.avfplayer.global;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 3/25/2017.
 */

public class MultiChooseInfo {

    static ArrayList<Boolean> selectedList=new ArrayList<>();

    public ArrayList<Boolean> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(ArrayList<Boolean> selectedList) {
        this.selectedList = selectedList;
    }
}
