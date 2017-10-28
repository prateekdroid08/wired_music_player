package com.avfplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.avfplayer.ApplicationAVFPlayer;
import com.avfplayer.models.SongDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 3/23/2017.
 */

public class PlaylistPlayTableHelper {

    public static final String TABLENAME = "PlaylistSongs";
    public static final String ID = "_id";
    public static final String ALBUM_ID = "album_id";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String AUDIOPROGRESS = "audioProgress";
    public static final String AUDIOPROGRESSSEC = "audioProgressSec";
    public static final String LastPlayTime = "lastplaytime";
    public static final String TYPE = "type";
    public static final String VIDEOCHECK = "videoCheck";
    public static final String PLAYLIST_ID = "playlistId";

    private static AVFLayerDBHelper dbHelper = null;
    private static PlaylistPlayTableHelper mInstance;
    private SQLiteDatabase sampleDB;

    public static synchronized PlaylistPlayTableHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlaylistPlayTableHelper(context);
        }
        return mInstance;
    }

    public Context context;

    public PlaylistPlayTableHelper(Context context_) {
        this.context = context_;
        if (dbHelper == null) {
            dbHelper = ((ApplicationAVFPlayer) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void inserSong(SongDetail songDetail, int playlist_id) {
        try {

            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            if (!getIsExist(songDetail, "" + playlist_id)) {
                String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?,?,?,?,?,?,?,?);";
                SQLiteStatement insert = sampleDB.compileStatement(sql);

                try {
                    if (songDetail != null) {
                        insert.clearBindings();
                        insert.bindLong(1, songDetail.getId());
                        insert.bindLong(2, songDetail.getAlbum_id());
                        insert.bindString(3, songDetail.getArtist());
                        insert.bindString(4, songDetail.getTitle());
                        insert.bindString(5, songDetail.getDisplay_name());
                        insert.bindString(6, songDetail.getDuration());
                        insert.bindString(7, songDetail.getPath());
                        insert.bindString(8, songDetail.audioProgress + "");
                        insert.bindString(9, songDetail.audioProgressSec + "");
                        insert.bindString(10, System.currentTimeMillis() + "");
                        insert.bindLong(11, playlist_id);
                        insert.bindLong(12, songDetail.getType());
                        insert.bindLong(13, songDetail.getVideoCheck());

                        insert.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sampleDB.setTransactionSuccessful();

                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Already Exist!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            sampleDB.endTransaction();
        }
    }

    private void closeCurcor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public Cursor getFavoriteSongList(String playlist_id) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME + " where " + PLAYLIST_ID + "=" + playlist_id;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return mCursor;
    }

    public boolean getIsExist(SongDetail mDetail, String playlist_id) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME + " where " + ID + "=" + mDetail.getId() + " and " + PLAYLIST_ID + "=" + playlist_id;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
            if (mCursor != null && mCursor.getCount() >= 1) {
                closeCurcor(mCursor);
                return true;
            }
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> getPlayListFromSQLDBCursor(String id) {
        ArrayList<HashMap<String, String>> generassongsList = new ArrayList<>();
        Cursor cursor = getFavoriteSongList(id);
        //Log.e("db","Cursor count"+cursor.getCount());
        try {
            if (cursor != null && cursor.getCount() >= 1) {

                while (cursor.moveToNext()) {
                    Log.e("db", "ID : " + cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID)));
                    Log.e("db", "Name : " + cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));

                    long ID = cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID));
                    String NAME = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                    HashMap<String, String> map = new HashMap<>();
                    map.put("playlistId", "" + ID);
                    map.put("playlistName", NAME);

                    generassongsList.add(map);
                }
            }
            closeCrs(cursor);
        } catch (Exception e) {
            closeCrs(cursor);
            e.printStackTrace();
            Log.e("db", "no list");
        }
        return generassongsList;
    }

    private void closeCrs(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }
}
