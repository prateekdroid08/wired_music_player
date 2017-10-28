package com.avfplayer.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.avfplayer.ApplicationAVFPlayer;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;

/**
 * Created by softradix on 16/10/17.
 */

public class VideoPlayListDetail {


    public static final String TABLENAME = "VideoData";
    public static final String TABLE_VIDEO_PLAYLIST = "video_playlist";
    public static final String VIDEO_ID = "video_id";
    public static final String VIDEO_URL = "video_url";
    public static final String VIDEO_SIZE = "video_size";
    public static final String VIDEO_DURATION = "video_duration";
    public static final String VIDEO_MILLI_SECONDS = "video_milli_duration";
    public static final String VIDEO_PLAYED_TIME = "video_played_time";
    public static final String VIDEO_PLAYLIST_ID = "video_playlist_id";
    public static final String VIDEO_NAME = "video_name";

    private static AVFLayerDBHelper dbHelper = null;

    private static VideoPlayListDetail mInstance;
    private SQLiteDatabase sampleDB;

    public static synchronized VideoPlayListDetail getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VideoPlayListDetail(context);
        }
        return mInstance;
    }

    public Context context;

    public VideoPlayListDetail(Context context_) {
        this.context = context_;
        if (dbHelper == null) {
            dbHelper = ((ApplicationAVFPlayer) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void insertVideo(int VIDEO_ID, String VIDEO_URL, String VIDEO_SIZE, String VIDEO_DURATION,
                            long VIDEO_MILLI_SECONDS, int VIDEO_PLAYED_TIME) {
        try {

            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?);";
            SQLiteStatement insert = sampleDB.compileStatement(sql);

            try {
                insert.clearBindings();
                insert.bindLong(1, VIDEO_ID);
                insert.bindString(2, VIDEO_URL);
                insert.bindString(3, VIDEO_SIZE);
                insert.bindString(4, VIDEO_DURATION);
                insert.bindLong(5, VIDEO_MILLI_SECONDS);
                insert.bindLong(6, VIDEO_PLAYED_TIME);

                insert.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sampleDB.setTransactionSuccessful();

            Toast.makeText(context, "Videos Added", Toast.LENGTH_SHORT).show();

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

    public Cursor getVideoDetail(int videoId) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME + " where " + VIDEO_ID + "=" + videoId;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return mCursor;
    }

    public boolean deleteVideo(int videoId) {
        return sampleDB.delete(TABLENAME, VIDEO_ID + "=" + videoId, null) > 0;
    }

    public Cursor getVideosFromPlayList(int playListID) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLE_VIDEO_PLAYLIST + " where " + VIDEO_PLAYLIST_ID + "=" + playListID;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return mCursor;
    }

    public void updatePlayedTime(long playedTime, int videoId) {
        try {
            ContentValues values = new ContentValues();
            values.put(VIDEO_PLAYED_TIME, playedTime);
            long success = sampleDB.update(TABLENAME, values, VIDEO_ID + "=?", new String[]{String.valueOf(videoId)});
            Log.d("success", success + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertVideoInPlaylist(VideoDetail videoDetail) {
        try {

            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            if (!checkIsExist(videoDetail.getVIDEO_ID())) {
                String sql = "Insert or Replace into " + TABLE_VIDEO_PLAYLIST + " values(?,?,?,?,?,?,?,?);";
                SQLiteStatement insert = sampleDB.compileStatement(sql);

                try {
                    insert.clearBindings();
                    insert.bindLong(1, videoDetail.getVIDEO_ID());
                    insert.bindString(2, videoDetail.getVIDEO_URL());
                    insert.bindString(3, videoDetail.getVIDEO_SIZE());
                    insert.bindString(4, videoDetail.getVIDEO_DURATION());
                    insert.bindLong(5, videoDetail.getVIDEO_MILLI_SECONDS());
                    insert.bindLong(6, videoDetail.getVIDEO_PLAYED_TIME());
                    insert.bindLong(7, videoDetail.getPLAY_LIST_ID());
                    insert.bindString(8, videoDetail.getVIDEO_NAME());

                    insert.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sampleDB.setTransactionSuccessful();

                Toast.makeText(context, "Video Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Already Exist!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            sampleDB.endTransaction();
        }

    }

    public boolean checkIsExist(int videoId) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLE_VIDEO_PLAYLIST + " where " + VIDEO_ID + "=" + videoId;
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

}
