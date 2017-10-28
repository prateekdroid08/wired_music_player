package com.avfplayer.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import com.avfplayer.R;
import com.avfplayer.global.Global;
import com.avfplayer.manager.MediaController;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.PhoneMediaControl;

import java.util.ArrayList;

/**
 * Created by admin on 11/25/2016.
 */

public class PlayVideoStorage extends Activity {
    VideoView myVideoView;
    float j;
    String video_data, video_name, video_image, video_duration;
    Global global;
    ArrayList<SongDetail> playlist=new ArrayList<>();
    int currentSeek=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.play_video);

        global=new Global();



        Intent intent = getIntent();
        video_data = intent.getStringExtra("video_data");
        video_name=intent.getExtras().getString("video_name");
        video_image=intent.getExtras().getString("video_image");
        video_duration=intent.getExtras().getString("video_duration");
        playlist.clear();
        playlist = createVideoPlaylist(video_data);
        //setData();
        Log.e("onCreate", "true");




    }

    public void getScreenOrientation(MediaPlayer player)
    {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
       /* int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = myVideoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        myVideoView.setLayoutParams(lp);*/
       /* if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                Log.e("Print_height_width",""+getOrient.getHeight()+getOrient.getWidth());
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                Log.e("Print_height_width",""+getOrient.getHeight()+getOrient.getWidth());
                if(getOrient.getHeight()<=480){
                    ViewGroup.LayoutParams params = myVideoView.getLayoutParams();
                    params.height = 300;
                    myVideoView.setLayoutParams(params);
                }
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }*/

    }


    private void setData(){

        Log.e("set Data", "true 1");
        myVideoView = (VideoView)findViewById(R.id.videoView2);

        myVideoView.setVideoPath(video_data);
        myVideoView.setMediaController(new android.widget.MediaController(this));
        myVideoView.requestFocus();
        myVideoView.start();
        myVideoView.seekTo(currentSeek);
        /*controller = new VideoControllerView.Builder(this, this)
                .withVideoTitle("Buck Bunny")
                .withVideoSurfaceView(mVideoSurface)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.ic_media_pause)
                .playIcon(R.drawable.ic_media_play)
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build((FrameLayout) findViewById(R.id.videoSurfaceContainer));*/
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();

            }
        });

        //-----------------------Exit-------------------------------------------------------
    }

    private void showToast(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(PlayVideoStorage.this, string, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    public void onPause() {

        super.onPause();

        myVideoView.pause();

        int current=myVideoView.getCurrentPosition();
        int total=myVideoView.getDuration();
        j=(Float.parseFloat(""+current)/Float.parseFloat(""+total));

        Log.e("onPause", current+"///"+total+"///"+j);

        SongDetail mDetail = playlist.get(playlist.size()-1);

        MediaController.getInstance().cleanupPlayer(true, true);
        MediaController.getInstance().setPlaylist(playlist, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
        MediaController.getInstance().playAudio(mDetail);
        MediaController.getInstance().seekToProgress(mDetail, j);
    }
    @Override
    public void onResume() {
        super.onResume();

        Log.e("video_name", video_name);
        //

        MediaController.getInstance().stopAudio();

        if(MediaController.getInstance().id!=0) {
            Log.e("Media Video", MediaController.getInstance().getPlayingSongDetail().display_name);
            if(video_name.equals(MediaController.getInstance().getPlayingSongDetail().display_name)) {
                Float mControllerSeek=MediaController.getInstance().getPlayingSongDetail().audioProgress;
                Log.e("get seek", "aa"+mControllerSeek);
                currentSeek= (int) (mControllerSeek*Float.parseFloat(video_duration));
                Log.e("current seek","aa"+currentSeek);
                //myVideoView.seekTo(currentSeek);
                setData();
            } else {
                setData();
            }
        } else {
            setData();
        }



    }

    @Override
    protected void onDestroy() {



        super.onDestroy();

    }

    private ArrayList<SongDetail> createVideoPlaylist(String videoPath) {
        Log.e("test","1");

        ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();

        SongDetail mSongDetail = new SongDetail(0, 0, "", video_name, videoPath, video_name, "", 1, 1);
        generassongsList.add(mSongDetail);

        return generassongsList;
    }


}
