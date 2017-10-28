package com.avfplayer.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.avfplayer.R;
import com.avfplayer.global.Global;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.manager.MediaController;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.videoMediaController.ResizeVideoView;
import com.avfplayer.videoMediaController.VideoViewController;

import java.util.ArrayList;

/**
 * Created by VlogicLabs on 2/4/2017.
 */

public class VideoPlayer extends Activity implements VideoViewController.MediaPlayerControlListener, MediaPlayer.OnCompletionListener, View.OnClickListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    //VideoView myVideoView;
    ResizeVideoView myVideoView;
    float j;
    String video_data, video_name, video_image, video_duration;
    Global global;
    ArrayList<SongDetail> playlist=new ArrayList<>();
    int currentSeek=0;
    VideoViewController controller;
    VideoListInfo info;
    int index;
    private boolean isPause=false;
    private boolean mIsComplete;
    private ImageView option, pervious, next, back;
    MediaPlayer mMediaPlayer;
    private int mVideoWidth;
    private int mVideoHeight;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_player);

        global=new Global();

        info=new VideoListInfo();

        playlist.clear();
        playlist = info.getPlaylist();

        index= info.getIndex();

        Log.e("onCreate", "true");

        myVideoView = (ResizeVideoView) findViewById(R.id.videoSurface);
        mContentView = findViewById(R.id.video_container);




    }


    private void setData(){

        Log.e("set Data", "true 1");


        controller = new VideoViewController.Builder(this, this)
                .withVideoTitle(video_name)
                .withVideoSurfaceView(myVideoView)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.ic_media_pause)
                .playIcon(R.drawable.ic_media_play)
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build((FrameLayout) findViewById(R.id.videoSurfaceContainer2));


        option= (ImageView) controller.findViewById(R.id.bottom_setting);
        pervious= (ImageView) controller.findViewById(R.id.bottom_pervious);
        next= (ImageView) controller.findViewById(R.id.bottom_next);
        back= (ImageView) controller.findViewById(R.id.top_back);

        option.setOnClickListener(this);
        pervious.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);

        myVideoView.setVideoPath(video_data);

        myVideoView.start();
        myVideoView.seekTo(currentSeek);


        //myVideoView.setOnPreparedListener(this);
        //myVideoView.requestFocus();

        myVideoView.setOnCompletionListener(this);

        myVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                controller.toggleControllerView();
                return false;
            }
        });

        //-----------------------Exit-------------------------------------------------------
    }

    private void showToast(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(VideoPlayer.this, string, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    public void onPause() {

        super.onPause();

        pause();

        int current=getCurrentPosition();
        int total=getDuration();
        j=(Float.parseFloat(""+current)/Float.parseFloat(""+total));

        Log.e("onPause", current+"///"+total+"///"+j);

        SongDetail mDetail = playlist.get(index);

        MediaController.getInstance().cleanupPlayer(true, true);
        MediaController.getInstance().setPlaylist(playlist, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
        MediaController.getInstance().playAudio(mDetail);
        MediaController.getInstance().seekToProgress(mDetail, j);
    }
    @Override
    public void onResume() {
        super.onResume();

        if(info.getIndex()!=index) {

            System.gc();
            Intent intent=new Intent(VideoPlayer.this, VideoPlayer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {
            index= info.getIndex();


            Log.e("index",""+index);


            video_data = info.getPlaylist().get(index).getPath();
            video_name=info.getPlaylist().get(index).getDisplay_name();
            video_duration=info.getPlaylist().get(index).getDuration();



            if(MediaController.getInstance().id!=0) {
                Log.e("Media Video", info.getPlaylist().get(index).getDisplay_name());
                if(video_name.equals(info.getPlaylist().get(index).getDisplay_name())) {
                    int mControllerSeek=0;
                    if(MediaController.getInstance()!=null) {
                        try {
                            if(video_name.equals(MediaController.getInstance().getPlayingSongDetail().getDisplay_name())) {
                                mControllerSeek=MediaController.getInstance().getSeekTO();
                                MediaController.getInstance().stopAudio();
                            }
                        } catch (Exception e) {
                            mControllerSeek=0;
                        }

                    }

                    Log.e("get seek", "aa"+mControllerSeek);
                    currentSeek=mControllerSeek;
                    Log.e("current seek","aa"+currentSeek);
                    if(!MediaController.getInstance().isAudioPaused()) {
                        MediaController.getInstance().stopAudio();
                    }
                    if(!isPause())
                        isPause=false;
                    setData();
                } else {
                    setData();
                }

            } else {
                setData();
            }
        }



    }

    @Override
    protected void onDestroy() {



        super.onDestroy();

    }

    public boolean isPause() {
        return isPause;
    }


    @Override
    public void start() {
        if(null != myVideoView) {
            myVideoView.start();
            mIsComplete = false;
            Log.e("Media Play", "true");
        }
    }

    @Override
    public void pause() {
        if(null != myVideoView) {
            myVideoView.pause();
            Log.e("Media Pause", "true");
        }
    }

    @Override
    public int getDuration() {
        if(null != myVideoView)
            return myVideoView.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(null != myVideoView)
            return myVideoView.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int position) {
        if(null != myVideoView) {
            myVideoView.seekTo(position);
        }
    }

    @Override
    public boolean isPlaying() {
        if(null != myVideoView)
            return myVideoView.isPlaying();
        else
            return false;
    }

    @Override
    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
    }

    @Override
    public void toggleFullScreen() {
        if(isFullScreen()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsComplete = true;
        if(index<playlist.size()-1) {
            index=index+1;
            info.setIndex(index);
            currentSeek=0;
            controller=null;



            video_data = info.getPlaylist().get(index).getPath();
            video_name=info.getPlaylist().get(index).getDisplay_name();
            video_duration=info.getPlaylist().get(index).getDuration();

            setData();


        }

    }

    private void setVideoSpeed(float rate) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            PlaybackParams myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed(rate);
            mMediaPlayer.setPlaybackParams(myPlayBackParams);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_setting:
                try {
                    PopupMenu popup = new PopupMenu(VideoPlayer.this, v);
                    popup.getMenuInflater().inflate(R.menu.video_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                /*case R.id.speed:

                                    break;*/

                                case R.id.video_slow1:
                                    setVideoSpeed(0.50f);
                                    break;

                                case R.id.video_normal:
                                    setVideoSpeed(1.0f);
                                    break;

                                case R.id.video_fast1:
                                    setVideoSpeed(1.5f);
                                    break;
                            }

                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.bottom_pervious:
                if(index!=0) {
                    index=index-1;
                    info.setIndex(index);
                    currentSeek=0;
                    controller=null;


                    video_data = info.getPlaylist().get(index).getPath();
                    video_name=info.getPlaylist().get(index).getDisplay_name();
                    //video_image=intent.getExtras().getString("video_image");
                    video_duration=info.getPlaylist().get(index).getDuration();

                    setData();

                }



                break;

            case R.id.bottom_next:
                if(index<playlist.size()-1) {
                    index=index+1;
                    info.setIndex(index);
                    currentSeek=0;
                    controller=null;


                    video_data = info.getPlaylist().get(index).getPath();
                    video_name=info.getPlaylist().get(index).getDisplay_name();
                    //video_image=intent.getExtras().getString("video_image");
                    video_duration=info.getPlaylist().get(index).getDuration();

                    setData();


                }

                break;

            case R.id.top_back:
                onBackPressed();
                break;
        }
    }

    /*@Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            myVideoView.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mp.getVideoWidth(), mp.getVideoHeight());

    }*/

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MediaPlayer Error", what+"///"+extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("Media player", "true");
        if(!isPause()) {
            mMediaPlayer=mp;
            myVideoView.setVideoPath(video_data);
            myVideoView.start();
            myVideoView.seekTo(currentSeek);
            mIsComplete = false;
        }

    }
}
