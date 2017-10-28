/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.avfplayer.dbhandler;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.avfplayer.R;


public class AVFLayerDBHelper extends SQLiteOpenHelper {
    private Context context_;
    private static String DATABASE_NAME = "AvfDb";
    private static int DATABASE_VERSION = 1;
    private String DB_PATH = "";
    private SQLiteDatabase db;

    public AVFLayerDBHelper(Context context) {
        super(context, context.getResources().getString(R.string.DataBaseName), null, Integer.parseInt(context.getResources().getString(R.string.DataBaseName_Version)));
        this.context_ = context;
        DATABASE_NAME = context.getResources().getString(R.string.DataBaseName);
        DATABASE_VERSION = Integer.parseInt(context.getResources().getString(R.string.DataBaseName_Version));
        DB_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
        context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(sqlForCreateMostPlay());
            db.execSQL(sqlForCreateFavoritePlay());
            db.execSQL(sqlForCreatePlaylist());
            db.execSQL(sqlForCreatePlaylistSong());
            db.execSQL(sqlForCreateVideoData());
            db.execSQL(sqlForCreateVideoPlayList());
            Log.e("db", "create");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("db", "no create");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MostAndRecentPlayTableHelper.TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + FavoritePlayTableHelper.TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + PlaylistPlayTableHelper.TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + PlaylistTableHelper.TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + VideoPlayListDetail.TABLENAME);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        if (getDB() != null)
            getDB().close();

        super.close();
    }

    public void openDataBase() throws SQLException {
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String sqlForCreateMostPlay() {
        String sql = "CREATE TABLE " + MostAndRecentPlayTableHelper.TABLENAME + " (" + MostAndRecentPlayTableHelper.ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MostAndRecentPlayTableHelper.ALBUM_ID + " INTEGER NOT NULL,"

                + MostAndRecentPlayTableHelper.ARTIST + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.TITLE + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.DURATION + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.PATH + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.AUDIOPROGRESS + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.AUDIOPROGRESSSEC + " INTEGER NOT NULL,"
                + MostAndRecentPlayTableHelper.LastPlayTime + " TEXT NOT NULL,"
                + MostAndRecentPlayTableHelper.PLAYCOUNT + " INTEGER NOT NULL,"
                + MostAndRecentPlayTableHelper.TYPE + " INTEGER NOT NULL,"
                + MostAndRecentPlayTableHelper.VIDEOCHECK + " INTEGER NOT NULL);";
        return sql;
    }

    public static String sqlForCreateFavoritePlay() {
        String sql = "CREATE TABLE " + FavoritePlayTableHelper.TABLENAME + " (" + FavoritePlayTableHelper.ID + " INTEGER NOT NULL PRIMARY KEY,"
                + FavoritePlayTableHelper.ALBUM_ID + " INTEGER NOT NULL,"

                + FavoritePlayTableHelper.ARTIST + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.TITLE + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.DURATION + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.PATH + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.AUDIOPROGRESS + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.AUDIOPROGRESSSEC + " INTEGER NOT NULL,"
                + FavoritePlayTableHelper.LastPlayTime + " TEXT NOT NULL,"
                + FavoritePlayTableHelper.ISFAVORITE + " INTEGER NOT NULL,"
                + FavoritePlayTableHelper.TYPE + " INTEGER NOT NULL,"
                + FavoritePlayTableHelper.VIDEOCHECK + " INTEGER NOT NULL);";
        return sql;
    }

    public static String sqlForCreatePlaylist() {
        String sql = "CREATE TABLE " + PlaylistTableHelper.TABLENAME + " ("
                + PlaylistTableHelper.PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlaylistTableHelper.PLAYLIST_NAME + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.TYPE + " TEXT NOT NULL);";
        return sql;
    }

    public static String sqlForCreatePlaylistSong() {
        String sql = "CREATE TABLE " + PlaylistPlayTableHelper.TABLENAME + " (" + PlaylistPlayTableHelper.ID + " INTEGER NOT NULL,"
                + PlaylistPlayTableHelper.ALBUM_ID + " INTEGER NOT NULL,"
                + PlaylistPlayTableHelper.ARTIST + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.TITLE + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.DISPLAY_NAME + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.DURATION + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.PATH + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.AUDIOPROGRESS + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.AUDIOPROGRESSSEC + " INTEGER NOT NULL,"
                + PlaylistPlayTableHelper.LastPlayTime + " TEXT NOT NULL,"
                + PlaylistPlayTableHelper.PLAYLIST_ID + " INTEGER NOT NULL,"
                + PlaylistPlayTableHelper.TYPE + " INTEGER NOT NULL,"
                + PlaylistPlayTableHelper.VIDEOCHECK + " INTEGER NOT NULL);";
        return sql;
    }

    public static String sqlForCreateVideoData() {
        String sql = "CREATE TABLE " + VideoPlayListDetail.TABLENAME
                + " (" + VideoPlayListDetail.VIDEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VideoPlayListDetail.VIDEO_URL + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_SIZE + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_DURATION + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_MILLI_SECONDS + " INTEGER NOT NULL,"
                + VideoPlayListDetail.VIDEO_PLAYED_TIME + " INTEGER NOT NULL);";
        return sql;
    }

    public static String sqlForCreateVideoPlayList() {
        String sql = "CREATE TABLE " + VideoPlayListDetail.TABLE_VIDEO_PLAYLIST
                + " (" + VideoPlayListDetail.VIDEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VideoPlayListDetail.VIDEO_URL + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_SIZE + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_DURATION + " TEXT NOT NULL,"
                + VideoPlayListDetail.VIDEO_MILLI_SECONDS + " INTEGER NOT NULL,"
                + VideoPlayListDetail.VIDEO_PLAYED_TIME + " INTEGER NOT NULL,"
                + VideoPlayListDetail.VIDEO_PLAYLIST_ID + " INTEGER NOT NULL,"
                + VideoPlayListDetail.VIDEO_NAME + " TEXT NOT NULL);";
        return sql;
    }
}
