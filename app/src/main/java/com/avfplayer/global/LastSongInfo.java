package com.avfplayer.global;

import android.content.Context;
import android.content.SharedPreferences;

import com.avfplayer.manager.MediaController;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 2/15/2017.
 */

public class LastSongInfo {
    Context context;
    static SharedPreferences sharedPreferences;
    public static ArrayList<SongDetail> playlist = new ArrayList<SongDetail>();
    static SongDetail playingSongDetail;

    public LastSongInfo(Context context) {
        this.context=context;
    }



    public SharedPreferences getPreferanse() {
        sharedPreferences=context.getSharedPreferences("AVF", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public void saveLastSong(SongDetail mDetail) {
        if(mDetail.getVideoCheck()==0) {
            SharedPreferences.Editor editor = getPreferanse().edit();
        /*editor.clear();
        editor.commit();*/
            Gson mGson = new Gson();
            String lastplaySong = mGson.toJson(mDetail);
            editor.putString("lastplaysong2", lastplaySong);
            editor.commit();
        }

    }

    public SongDetail getLastSong() {

            //SharedPreferences mSharedPreferences = getPreferanse();
            String lastplaySong = getPreferanse().getString("lastplaysong2", "");
            Gson mGson = new Gson();

        return mGson.fromJson(lastplaySong, SongDetail.class);
    }

    public void saveLastSongListType(int type) {

        SharedPreferences.Editor editor = getPreferanse().edit();
        /*editor.clear();
        editor.commit();*/

        editor.putInt("songlisttype", type);
        editor.commit();
    }

    public int getLastSongListType() {
        SharedPreferences mSharedPreferences = getPreferanse();
        return mSharedPreferences.getInt("songlisttype", 0);
    }

    public void saveLastAlbID(int id) {
        SharedPreferences.Editor editor = getPreferanse().edit();
        /*editor.clear();
        editor.commit();*/

        editor.putInt("lastalbid", id);
        editor.commit();
    }

    public int getLastAlbID() {
        SharedPreferences mSharedPreferences = getPreferanse();
        return mSharedPreferences.getInt("lastalbid", 0);
    }

    public void saveLastPosition(int positon) {
        SharedPreferences.Editor editor = getPreferanse().edit();
        /*editor.clear();
        editor.commit();*/

        editor.putInt("lastposition", positon);
        editor.commit();
    }

    public int getLastPosition() {
        SharedPreferences mSharedPreferences = getPreferanse();
        return mSharedPreferences.getInt("lastposition", 0);
    }

    public void saveLastPath(String path) {
        SharedPreferences.Editor editor = getPreferanse().edit();
        /*editor.clear();
        editor.commit();*/

        editor.putString("path", path);
        editor.commit();
    }

    public String getLastPath() {
        SharedPreferences mSharedPreferences = getPreferanse();
        return mSharedPreferences.getString("path", "");
    }

    public void setPlaylist(ArrayList<SongDetail> playlist1) {
        playlist=playlist1;
    }

    public ArrayList<SongDetail> getPlaylist() {
        return playlist;
    }


    /*public ArrayList<SongDetail> getPlaylist() {
        if (playlist == null || playlist.isEmpty()) {
            int type = getLastSongListType(context);
            int id = getLastAlbID(context);
            String path = getLastPath(context);
            MediaController.getInstance().type = type;
            MediaController.getInstance().id = id;
            MediaController.getInstance().currentPlaylistNum = getLastPosition(context);
            MediaController.getInstance().path = path;
            playlist = PhoneMediaControl.getInstance().getList(context, id, PhoneMediaControl.SonLoadFor.values()[type], path);
        }
        return playlist;
    }*/

    public ArrayList<SongDetail> getPlaylist(String path) {
        MediaController.getInstance().type = PhoneMediaControl.SonLoadFor.Musicintent.ordinal();
        MediaController.getInstance().id = -1;
        MediaController.getInstance().currentPlaylistNum = 0;
        MediaController.getInstance().path = path;
        playlist = PhoneMediaControl.getInstance().getList(context, -1, PhoneMediaControl.SonLoadFor.Musicintent, path);
        if (playlist != null && !playlist.isEmpty())
            playingSongDetail = playlist.get(0);
        return playlist;
    }
}
