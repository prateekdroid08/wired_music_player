package com.avfplayer.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.avfplayer.R;
import com.avfplayer.adapter.AdapterVideoSuggestions;
import com.avfplayer.global.Global;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.interfaces.OnVideoClick;
import com.avfplayer.manager.MediaController;
import com.avfplayer.models.MessageEvent;
import com.avfplayer.models.SongDetail;
import com.avfplayer.utility.Constants;
import com.avfplayer.videoMediaController.ResizeSurfaceView;
import com.avfplayer.videoMediaController.VideoControllerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class VideoPlayerFragment extends Fragment implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControlListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, View.OnClickListener, OnVideoClick {

    View view;
    private final static String TAG = "MainActivity";
    ResizeSurfaceView mVideoSurface;
    MediaPlayer mMediaPlayer;
    VideoControllerView controller;
    private int mVideoWidth;
    private int mVideoHeight;
    private View mContentView;
    private View mLoadingView;
    private boolean mIsComplete;
    String video_name, video_image, video_duration;
    public static String video_data;
    Global global;
    ArrayList<SongDetail> playlist = new ArrayList<>();
    int currentSeek = 0;
    float j;
    SurfaceHolder videoHolder;
    static MediaController mediaController;
    private ImageView setting, pervious, next, back, minimize, videoSuggestions, lock;
    RecyclerView videoSuggestionsList;
    AdapterVideoSuggestions adapterVideoSuggestions;
    static int index;
    VideoNextPerviousController videoNextPerviousController;
    FragmentManager fm;
    int position;
    VideoListInfo info;
    private boolean isPause = false;
    int lastIndex;
    int back_check = 0;
    ImageView thumb;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    int surfaceView_Width, surfaceView_Height;
    float videoWidth, videoHeight;

    public static VideoPlayerFragment newInstance(String resume) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("resume", resume);
        videoPlayerFragment.setArguments(bundle);
        return videoPlayerFragment;
    }

    String resumeVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_player_2, null);

        global = new Global();

        resumeVideo = getArguments().getString("resume");

        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();


        mVideoSurface = (ResizeSurfaceView) view.findViewById(R.id.videoSurface2);
        thumb = (ImageView) view.findViewById(R.id.thumb_icon);
        mContentView = getActivity().findViewById(R.id.videoContainer);
        mLoadingView = view.findViewById(R.id.loading);


        videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);
        videoHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return view;
    }

    private void setData() {
        controller = new VideoControllerView.Builder(getActivity(), this)
                .withVideoTitle(video_name)
                .withVideoSurfaceView(mVideoSurface)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.ic_media_pause)
                .playIcon(R.drawable.ic_media_play)
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build((FrameLayout) view.findViewById(R.id.videoSurfaceContainer));

        surfaceView_Width = mVideoSurface.getWidth();
        surfaceView_Height = mVideoSurface.getHeight();

        setting = (ImageView) controller.findViewById(R.id.bottom_setting);
        lock = (ImageView) controller.findViewById(R.id.bottom_lock);
        videoSuggestions = (ImageView) controller.findViewById(R.id.iv_overflow_icon);
        pervious = (ImageView) controller.findViewById(R.id.bottom_pervious);
        next = (ImageView) controller.findViewById(R.id.bottom_next);
        back = (ImageView) controller.findViewById(R.id.top_back);
        minimize = (ImageView) controller.findViewById(R.id.video_minimize);
        videoSuggestionsList = (RecyclerView) controller.findViewById(R.id.video_suggestions);

        // set category tables
        adapterVideoSuggestions = new AdapterVideoSuggestions(getActivity(), playlist, this);
        videoSuggestionsList.setItemAnimator(new DefaultItemAnimator());
        videoSuggestionsList.setAdapter(adapterVideoSuggestions);
        videoSuggestionsList.setHasFixedSize(true);
        //Layout manager for Recycler view
        videoSuggestionsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        setting.setOnClickListener(this);
        lock.setOnClickListener(this);
        pervious.setOnClickListener(this);
        videoSuggestions.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        minimize.setOnClickListener(this);

        try {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getActivity(), Uri.parse(video_data));


            if (!isPause())
                mMediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mLoadingView.setVisibility(View.VISIBLE);

        mVideoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                controller.toggleControllerView();
                return false;
            }
        });

        checkLockScreenStatus();

    }

    @Override
    public void onResume() {
        super.onResume();

        info = new VideoListInfo();

        playlist = info.getPlaylist();
        Log.e("Playlist size1", "" + playlist.size());

        index = info.getIndex();

        Log.e("index", "" + index);

        try {
            if (playlist.get(index).getType() == 0) {
                thumb.setVisibility(View.VISIBLE);
                String contentURI = "content://media/external/audio/media/" + playlist.get(index).getId() + "/albumart";
                imageLoader.displayImage(contentURI, thumb, options);

            } else {
                thumb.setVisibility(View.GONE);
            }


            video_data = info.getPlaylist().get(index).getPath();
            video_name = info.getPlaylist().get(index).getDisplay_name();
            //video_image=intent.getExtras().getString("video_image");
            video_duration = info.getPlaylist().get(index).getDuration();


            if (MediaController.getInstance().id != 0) {
                //mediaController.getInstance().cleanupPlayer(true, true);
                Log.e("Media Video", info.getPlaylist().get(index).getDisplay_name());
                if (video_name.equals(info.getPlaylist().get(index).getDisplay_name())) {
                    int mControllerSeek = 0;
                    if (MediaController.getInstance() != null) {
                        try {
                            if (video_name.equals(MediaController.getInstance().getPlayingSongDetail().getDisplay_name())) {
                                mControllerSeek = MediaController.getInstance().getSeekTO();
                                MediaController.getInstance().stopAudio();
                                MediaController.getInstance().cleanupPlayer(getActivity(), true, true);
                            }
                        } catch (Exception e) {
                            mControllerSeek = 0;
                        }

                    }

                    Log.e("get seek", "aa" + mControllerSeek);
                    currentSeek = mControllerSeek;
                    //currentSeek= (int) (mControllerSeek*Float.parseFloat(video_duration));
                    Log.e("current seek", "aa" + currentSeek);
                    //myVideoView.seekTo(currentSeek);
                    if (!MediaController.getInstance().isAudioPaused()) {
                        MediaController.getInstance().stopAudio();
                        MediaController.getInstance().cleanupPlayer(getActivity(), true, true);
                    }
                    if (!isPause())
                        isPause = false;
                    start();
                    seekTo(currentSeek);
                    //setData();
                } else {
                    //setData();
                    start();
                    seekTo(currentSeek);
                }

            } else {
                //setData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //mMediaPlayer.pause();

        try {
            int current = getCurrentPosition();
            int total = getDuration();
            j = (Float.parseFloat("" + current) / Float.parseFloat("" + total));

            Log.e("onPause", current + "///" + total + "///" + j);

            Log.e("Pause index", "" + index);

            SongDetail mDetail = playlist.get(index);

            if (j != 0) {

                MediaController.getInstance().setPlaylist(info.getPlaylist(), mDetail, -1, -1);
                MediaController.getInstance().currentPlaylistNum = index;
                MediaController.getInstance().seekToProgress(mDetail, j);
                MediaController.getInstance().pauseAudio(mDetail);

                if (!isPlaying()) {
                    Log.e("Media Pause", "true");
                    isPause = true;
                    MediaController.getInstance().pauseAudio(mDetail);
                } else {
                    if (back_check == 1) {
                        pause();
                        MediaController.getInstance().playAudio(mDetail);
                    } else {
                        pause();
                        MediaController.getInstance().stopAudio();
                        //MediaController.getInstance().pauseAudio(mDetail);
                        MediaController.getInstance().cleanupPlayer(getActivity(), true, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            mVideoSurface.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            mVideoSurface.adjustSize(getDeviceWidth(getActivity()), getDeviceHeight(getActivity()), mVideoSurface.getWidth(), mVideoSurface.getHeight());
    }

    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            videoHolder.getSurface().release();

        }
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (info.getIndex() == index) {
                setData();
                mMediaPlayer.setDisplay(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        //holder.getSurface().release();
        resetPlayer();
        mVideoSurface.destroyDrawingCache();
    }
// End SurfaceHolder.Callback


    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //setup video controller view
        if (!isPause()) {
            mLoadingView.setVisibility(View.GONE);
            mVideoSurface.setVisibility(View.VISIBLE);
            mMediaPlayer.start();
            if (resumeVideo != null && resumeVideo.length() > 0) {
                mMediaPlayer.seekTo(Integer.parseInt(resumeVideo));
            } else
                mMediaPlayer.seekTo(currentSeek);
            mIsComplete = false;

            videoWidth = mMediaPlayer.getVideoWidth();
            videoHeight = mMediaPlayer.getVideoHeight();
            Log.e("Size", videoWidth + "//" + videoHeight);
        }

    }


// End MediaPlayer.OnPreparedListener

    /**
     * Implement VideoMediaController.MediaPlayerControl
     */

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (null != mMediaPlayer)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if (null != mMediaPlayer)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        if (null != mMediaPlayer)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    public void pause() {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
            Log.e("Media Pause", "true");
        }

    }

    public boolean isPause() {
        return isPause;
    }

    @Override
    public void seekTo(int i) {
        if (null != mMediaPlayer) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public void start() {
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
            mIsComplete = false;
            Log.e("Media Play", "true");
            videoSuggestionsList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean isFullScreen() {
        return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
    }

    @Override
    public void toggleFullScreen() {
        if (isFullScreen()) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void exit() {
        resetPlayer();
        getActivity().finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsComplete = true;

        ((MainActivity) getActivity()).updateStopTimeOfVideo
                (0, getVideoId(getActivity(), new File(video_data)));

        if (index < playlist.size() - 1) {
            info.setIndex(index + 1);

            currentSeek = 0;
            index = index + 1;
            Log.e("Playlist size2", "" + playlist.size());
            info.setIndex(index);

            /*MediaController.getInstance().playNextSong();
            MediaController.getInstance().stopAudio();*/
            pause();

            seekTo(0);


            mVideoSurface.destroyDrawingCache();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFragment();
                }
            });


        } else {
            getActivity().finish();
        }

    }

    // End VideoMediaController.MediaPlayerControl


    private ArrayList<SongDetail> createVideoPlaylist(String videoPath) {
        Log.e("test", "1");

        ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();

        SongDetail mSongDetail = new SongDetail(getVideoId(getActivity(), new File(video_data)), 0, "", video_name, videoPath, video_name, "", 1, 1);
        generassongsList.add(mSongDetail);

        return generassongsList;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

        Log.e("MediaPlayer Error", i + "///" + i1);
        return false;
    }

    boolean isLocked;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_setting:
                //setVideoSpeedPopup();
                setVideoSetting(view);
                break;

            case R.id.iv_overflow_icon:
                // Show Video Suggestions
                videoSuggestionsList.setVisibility(View.VISIBLE);

                break;

            case R.id.bottom_lock:
                lockUnlockScreen();
                break;
            case R.id.bottom_pervious:
                if (index > 0) {

                    currentSeek = 0;
                    index = index - 1;
                    Log.e("change index", "" + index);
                    Log.e("Playlist size2", "" + playlist.size());
                    info.setIndex(index);
                    pause();

                    seekTo(0);


                    mVideoSurface.destroyDrawingCache();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFragment();
                        }
                    });
                }


                break;

            case R.id.bottom_next:
                if (index < playlist.size() - 1) {


                    currentSeek = 0;
                    index = index + 1;
                    Log.e("change index", "" + index);
                    info.setIndex(index);
                    Log.e("Playlist size3", "" + playlist.size());
                    pause();


                    seekTo(0);

                    mVideoSurface.destroyDrawingCache();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFragment();
                        }
                    });
                }
                break;

            case R.id.top_back:
                back_check = 0;
                mLoadingView.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
                break;

            case R.id.video_minimize:
                back_check = 1;
                mLoadingView.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
                break;
        }
    }


    void checkLockScreenStatus() {
        if (!isLocked) {
            setting.setVisibility(View.VISIBLE);
            pervious.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            minimize.setVisibility(View.VISIBLE);
            videoSuggestions.setVisibility(View.VISIBLE);

            controller.setEnabled(true);
            controller.mFullscreenButton.setVisibility(View.VISIBLE);
            controller.mPauseButton.setVisibility(View.VISIBLE);
        } else {
            setting.setVisibility(View.INVISIBLE);
            pervious.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            minimize.setVisibility(View.INVISIBLE);
            videoSuggestions.setVisibility(View.INVISIBLE);

            controller.setEnabled(false);
            controller.mFullscreenButton.setVisibility(View.INVISIBLE);
            controller.mPauseButton.setVisibility(View.INVISIBLE);
        }
    }

    void lockUnlockScreen() {
        if (!isLocked) {
            isLocked = true;
            setting.setVisibility(View.INVISIBLE);
            pervious.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            minimize.setVisibility(View.INVISIBLE);
            videoSuggestions.setVisibility(View.INVISIBLE);

            controller.setEnabled(false);
            controller.mFullscreenButton.setVisibility(View.INVISIBLE);
            controller.mPauseButton.setVisibility(View.INVISIBLE);
        } else {
            isLocked = false;
            setting.setVisibility(View.VISIBLE);
            pervious.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            minimize.setVisibility(View.VISIBLE);
            videoSuggestions.setVisibility(View.VISIBLE);

            controller.setEnabled(true);
            controller.mFullscreenButton.setVisibility(View.VISIBLE);
            controller.mPauseButton.setVisibility(View.VISIBLE);
        }
    }

    private void setVideoZoomFit(float i) {
        ViewGroup.LayoutParams layoutParams = mVideoSurface.getLayoutParams();
        layoutParams.width = (int) (videoWidth * i);
        layoutParams.height = (int) (videoHeight * i);
        mVideoSurface.setLayoutParams(layoutParams);
    }

    private void setVideoZoom() {

        ViewGroup.LayoutParams layoutParams = mVideoSurface.getLayoutParams();
        layoutParams.width = (int) (surfaceView_Height);
        layoutParams.height = (int) (surfaceView_Width);

        mVideoSurface.setLayoutParams(layoutParams);
    }

    private void setVideoSetting(View view) {
        try {
            PopupMenu popup = new PopupMenu(getActivity(), view);
            popup.getMenuInflater().inflate(R.menu.video_menu, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.video_slow1:
                            setVideoSpeed(0.50f);
                            break;

                        case R.id.video_normal:
                            setVideoSpeed(1.0f);
                            break;

                        case R.id.video_fast1:
                            setVideoSpeed(1.5f);
                            break;

                        case R.id.video_fit:
                            onVideoSizeChanged(mMediaPlayer, surfaceView_Width, surfaceView_Height);
                            break;

                        case R.id.video_stretch:
                            setVideoZoom();
                            break;

                        case R.id.video_100:
                            setVideoZoomFit(1f);
                            break;

                        case R.id.video_150:
                            setVideoZoomFit(1.5f);
                            break;

                        case R.id.video_200:
                            setVideoZoomFit(2f);
                            break;
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVideoSpeed(float rate) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            PlaybackParams myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed(rate);
            mMediaPlayer.setPlaybackParams(myPlayBackParams);
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


    public static String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA};

    public String getThumbnailPathForLocalFile(Activity context, String fileUri) {

        long fileId = getVideoId(context, new File(fileUri));

        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        try {

            thumbCursor = context.managedQuery(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                return thumbPath;
            }

        } finally {
        }

        return null;
    }

    private void setFragment() {
        fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.video_frame_layout, new VideoPlayerFragment().newInstance("")).commit();
    }

    private void clearFragment() {
        try {
            fm = getActivity().getSupportFragmentManager();
            fm.beginTransaction().remove(new VideoPlayerFragment()).commit();
        } catch (Exception e) {
        }
    }

    @Override
    public void onVideoClick(int index) {
        currentSeek = 0;
        this.index = index;
        info.setIndex(this.index);
                    /*MediaController.getInstance().playPreviousSong();
                    MediaController.getInstance().stopAudio();*/
        pause();

        seekTo(0);


        mVideoSurface.destroyDrawingCache();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFragment();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(MessageEvent messageEvent) {
        Log.d("notification received:", "notification received:");
        if (messageEvent.message.contains(getString(R.string.headphones_plugged)) ||
                messageEvent.message.contains(getString(R.string.pause_media))) {
            if (!Prefs.getBoolean(Constants.IS_HEADPHONE_BLUETOOTH, false)) {
                EventBus.getDefault().removeStickyEvent(messageEvent);
                pause();
                if (controller != null)
                    controller.mPauseButton.setImageResource(R.drawable.ic_media_play);
            }
        }
    }
}
