package com.avfplayer.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.avfplayer.R;
import com.avfplayer.activities.HomeActivity;
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.global.AllAudioVideoInfo;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.models.MessageEvent;
import com.avfplayer.videoMediaController.VideoControllerView;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class MainActivity extends AppCompatActivity {

    FragmentManager fm;
    int position;
    VideoListInfo info;
    AllAudioVideoInfo info2;
    int index;

    String resumeVideo;

    private MusicIntentReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player_2);

        myReceiver = new MusicIntentReceiver();

        if (getIntent().hasExtra("resume")) {
            resumeVideo = getIntent().getStringExtra("resume");
        }
        info2 = new AllAudioVideoInfo();
        info2.setIndex(1);

        Intent intent = getIntent();
        index = intent.getExtras().getInt("index");

        //clearFragment();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setFragment();
            }
        }).start();
    }

    private void setFragment() {
        fm = getSupportFragmentManager();
        if (resumeVideo != null)
            fm.beginTransaction().replace(R.id.video_frame_layout, new VideoPlayerFragment().newInstance(resumeVideo)).commit();
        else
            fm.beginTransaction().replace(R.id.video_frame_layout, new VideoPlayerFragment().newInstance("")).commit();
    }

    private void clearFragment() {
        try {
            fm = getSupportFragmentManager();
            fm.beginTransaction().remove(new VideoPlayerFragment()).commit();
        } catch (Exception e) {
        }
    }

    IntentFilter filter;

    @Override
    protected void onResume() {
        super.onResume();

        if (filter == null) {
            filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(myReceiver, filter);
        }
    }

    @Override
    public void onBackPressed() {
        if (index == 0) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        updateStopTimeOfVideo(VideoControllerView.position,
                getVideoId(this, new File(VideoPlayerFragment.video_data)));

        finish();
        return;
    }

    public int getVideoId(Context context, File file) {
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

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(myReceiver);
    }

    public void updateStopTimeOfVideo(long stopedTime, int videoId) {
        VideoPlayListDetail.getInstance(this).updatePlayedTime(stopedTime, videoId);
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        EventBus.getDefault().postSticky(new MessageEvent(getString(R.string.headphones_plugged)));
                        Log.d("", "Headset is unplugged");
                        break;
                    case 1:
                        /*pause();
                        if (controller != null)
                            controller.mPauseButton.setImageResource(R.drawable.ic_media_play);*/
                        EventBus.getDefault().postSticky(new MessageEvent(getString(R.string.headphones_plugged)));
                        Log.d("", "Headset is plugged");
                        break;
                    default:
                        Log.d("", "I have no idea what the headset state is");
                }
            }
        }
    }
}
