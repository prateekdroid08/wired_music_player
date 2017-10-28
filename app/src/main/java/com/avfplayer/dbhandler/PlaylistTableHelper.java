package com.avfplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.avfplayer.ApplicationAVFPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 3/23/2017.
 */

public class PlaylistTableHelper {

    public static final String TABLENAME = "PlaylistName";

    public static final String PLAYLIST_ID = "_id";
    public static final String PLAYLIST_NAME = "pName";

    private static AVFLayerDBHelper dbHelper = null;
    private static PlaylistTableHelper mInstance;
    private SQLiteDatabase sampleDB;

    public static synchronized PlaylistTableHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlaylistTableHelper(context);
        }
        return mInstance;
    }

    public Context context;

    public PlaylistTableHelper(Context context_) {
        this.context = context_;
        if (dbHelper == null) {
            dbHelper = ((ApplicationAVFPlayer) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertInPlayList(String playlist_name, String type) {
        Log.e("playlist name", playlist_name);
        try {

            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            /*ContentValues values = new ContentValues();
            values.put(PLAYLIST_NAME, playlist_name);
            sampleDB.insert(TABLENAME, null, values);*/

            String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?);";
            SQLiteStatement insert = sampleDB.compileStatement(sql);

            try {
                if (!playlist_name.equals("")) {
                    insert.clearBindings();
                    //insert.bindLong(1,);
                    insert.bindString(2, playlist_name);
                    insert.bindString(3, type);

                    insert.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("db", "no insert");
            }
            sampleDB.setTransactionSuccessful();

            Log.e("db", "insert");

        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            sampleDB.endTransaction();
            Log.e("db", "no insert2");
        }
    }

    private void closeCurcor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public Cursor getPlaylistList() {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
            Log.e("db", "no select");
        }
        return mCursor;
    }

    public boolean getIsExist(String playlist_name) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME + " where " + PLAYLIST_NAME + "= ?";
            Log.e("db", "SqlQuery : " + sqlQuery);
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, new String[]{playlist_name});
            Log.e("db", "aa" + mCursor.getCount());
            if (mCursor != null && mCursor.getCount() >= 1) {
                closeCurcor(mCursor);
                return true;
            }
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
            Log.e("db", "no exist");
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> getPlayListFromSQLDBCursor() {
        ArrayList<HashMap<String, String>> generassongsList = new ArrayList<>();
        Cursor cursor = getPlaylistList();
        try {
            if (cursor != null && cursor.getCount() >= 1) {

                while (cursor.moveToNext()) {
                    Log.e("db", "ID : " + cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID)));
                    Log.e("db", "Name : " + cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME)));

                    long ID = cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID));
                    String NAME = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME));
                    String TYPE = cursor.getString(cursor.getColumnIndex(PlaylistPlayTableHelper.TYPE));

                    HashMap<String, String> map = new HashMap<>();
                    map.put("playlistId", "" + ID);
                    map.put("playlistName", NAME);
                    map.put("type", TYPE);

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
