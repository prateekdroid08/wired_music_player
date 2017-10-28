package com.avfplayer.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.avfplayer.R;
import com.avfplayer.adapter.AdapterVideoPlayList;
import com.avfplayer.adapter.AllVideoAdapter;
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.fragments.FragmentAllVideo;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.interfaces.OnPlayVideoPlayList;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by softradix on 21/10/17.
 */

public class ActivityVideoPlayList extends AppCompatActivity implements OnPlayVideoPlayList {

    private ListView recycler_videoslist;
    private ImageView backBtn;

    int playlistId;
    private Cursor cursor = null;

    ArrayList<VideoDetail> videoDetails;
    private AdapterVideoPlayList adapterVideoPlayList;

    ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();
    VideoListInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_list);

        recycler_videoslist = (ListView) findViewById(R.id.recycler_allVideos);
        backBtn = (ImageView) findViewById(R.id.iv_back);

        playlistId = getIntent().getIntExtra("playlistId", 0);

        info = new VideoListInfo();

        getVideosFromPlayList();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void getVideosFromPlayList() {
        cursor = VideoPlayListDetail.getInstance(this).getVideosFromPlayList(playlistId);
        getAllVideosFromPlayList(cursor);
    }

    private void getAllVideosFromPlayList(Cursor cursor) {
        videoDetails = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                while (cursor.moveToNext()) {
                    VideoDetail videoDetail = new VideoDetail();
                    videoDetail.setPLAY_LIST_ID((int) cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_PLAYLIST_ID)));
                    videoDetail.setVIDEO_SIZE(cursor.getString(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_SIZE)));
                    videoDetail.setVIDEO_MILLI_SECONDS(cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_MILLI_SECONDS)));
                    videoDetail.setVIDEO_URL(cursor.getString(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_URL)));
                    videoDetail.setVIDEO_ID((int) cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_ID)));
                    videoDetail.setVIDEO_DURATION(cursor.getString(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_DURATION)));
                    videoDetail.setVIDEO_PLAYED_TIME((int) cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_PLAYED_TIME)));
                    videoDetail.setVIDEO_NAME(cursor.getString(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_NAME)));

                    try {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(videoDetail.getVIDEO_URL());
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInmillisec = Long.parseLong(time);

                        videoDetails.add(videoDetail);

                        SongDetail mSongDetail = new SongDetail
                                ((int) cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_ID)), 0, "",
                                        videoDetail.getVIDEO_NAME(), videoDetail.getVIDEO_URL(), videoDetail.getVIDEO_NAME(),
                                        timeInmillisec + "", 1, 1);

                        generassongsList.add(mSongDetail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            info.setPlaylist(generassongsList);

            adapterVideoPlayList = new AdapterVideoPlayList(this, videoDetails, generassongsList, this);
            recycler_videoslist.setAdapter(adapterVideoPlayList);

            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
    }

    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }

    @Override
    public void onPlayVideoPlayList(final int position) {

        cursor = VideoPlayListDetail.getInstance(this).getVideoDetail
                (getVideoId(this, new File(videoDetails.get(position).getVIDEO_URL())));

        final long videoTime = getAllVideoStopTime(cursor);
        if (videoTime != 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Start Video from?")
                    .setCancelable(false)
                    .setPositiveButton("Beginning", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VideoListInfo info = new VideoListInfo();
                            info.setIndex(position);

                            ActivityVideoPlayList.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ActivityVideoPlayList.this, MainActivity.class);
                                    intent.putExtra("index", 1);
                                    ActivityVideoPlayList.this.startActivity(intent);
                                }
                            });
                        }
                    })
                    .setNegativeButton("Resume", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            VideoListInfo info = new VideoListInfo();
                            info.setIndex(position);

                            ActivityVideoPlayList.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ActivityVideoPlayList.this, MainActivity.class);
                                    intent.putExtra("index", 1);
                                    intent.putExtra("resume", videoTime + "");
                                    ActivityVideoPlayList.this.startActivity(intent);
                                }
                            });
                        }
                    }).show();
        } else {
            VideoListInfo info = new VideoListInfo();
            info.setIndex(position);

            ActivityVideoPlayList.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ActivityVideoPlayList.this, MainActivity.class);
                    intent.putExtra("index", 1);
                    ActivityVideoPlayList.this.startActivity(intent);
                }
            });
        }
    }

    private int getVideoId(Context context, File file) {
        int id = 0;
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID,
                        MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));


            Log.e("Video_id", "" + id);
        }

        return id;
    }

    private long getAllVideoStopTime(Cursor cursor) {
        long VIDEO_PLAYED_TIME = 0;
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                while (cursor.moveToNext()) {
                    VIDEO_PLAYED_TIME = cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_PLAYED_TIME));
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return VIDEO_PLAYED_TIME;
    }
}
