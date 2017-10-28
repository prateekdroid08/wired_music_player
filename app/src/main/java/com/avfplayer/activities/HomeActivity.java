package com.avfplayer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.adapter.SearchAdapter;
import com.avfplayer.childfragment.ChildFragmentPlaylist;
import com.avfplayer.fragments.FragmentAllAudioVideoSong;
import com.avfplayer.fragments.FragmentAllVideoFolder;
import com.avfplayer.fragments.FragmentEqualizer;
import com.avfplayer.fragments.FragmentFeedBack;
import com.avfplayer.fragments.FragmentLibrary;
import com.avfplayer.fragments.FragmentThemes;
import com.avfplayer.global.AllAudioVideoInfo;
import com.avfplayer.global.Global;
import com.avfplayer.global.HomeIndex;
import com.avfplayer.global.LastSongInfo;
import com.avfplayer.manager.MediaController;
import com.avfplayer.manager.MusicPreferance;
import com.avfplayer.manager.NotificationManager;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.slidinguppanelhelper.SlidingUpPanelLayout;
import com.avfplayer.uicomponent.PlayPauseView;
import com.avfplayer.uicomponent.Slider;
import com.avfplayer.utility.LogWriter;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements Slider.OnValueChangedListener, View.OnClickListener, NotificationManager.NotificationCenterDelegate, NavigationView.OnNavigationItemSelectedListener, TextWatcher {
    Button music_Button, video_Button, all_song_Button;
    Context context;
    Global global;
    FrameLayout frameLayout;
    private boolean isExpand = false;
    private SharedPreferences sharedPreferences;
    private DrawerLayout mDrawerLayout;

    ImageView imagecover;
    private ActionBarDrawerToggle mDrawerToggle;

    private RecyclerView recyclerViewDrawer;

    private Toolbar toolbar;

    private FrameLayout statusBar;

    private FrameLayout home_layout;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageView songAlbumbg;
    private int theme;
    private RecyclerView.Adapter adapterDrawer;
    private SlidingUpPanelLayout mLayout;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private TextView txt_playesongname;
    private TextView txt_songartistname;
    private TextView txt_playesongname_slidetoptwo;
    private TextView txt_songartistname_slidetoptwo;
    private TextView txt_timetotal;
    private TextView title_text;

    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;
    private ImageView imgbtn_toggle;
    private ImageView imgbtn_suffel;
    private ImageView imgbtn_moreOption;
    private ImageView imgbtn_equalizer;
    private ImageView img_Favorite;
    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;

    private SeekBar volume;

    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;

    private static final String TAG = "ActivityAVFPlayer";

    private LinearLayout main_list, editext_layout, playlist;
    private Slider audio_progress;
    private boolean isDragingStart = false;


    public FragmentTransaction fragmentTransaction;
    private int TAG_Observer;
    private AutoCompleteTextView search;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = HomeActivity.this;
        theme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intializer();
        main_list.setVisibility(View.VISIBLE);
        toolbarStatusBar();
        navigationDrawer();

        global = new Global();
        global.selection_category = "home";

    }


    @Override
    protected void onResume() {
        super.onResume();

        //songInfo=new LastSongInfo(context);


        addObserver();
        loadAlreadyPlayng();

        /*SharedPreferences sp=getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
        int tag=sp.getInt(Global.HOMETAG,0);

        if(tag==0) {
            addObserver();
            loadAlreadyPlayng();
        }*/


    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObserver();
    }

    @Override
    protected void onDestroy() {
        removeObserver();
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().cleanupPlayer(context, true, true);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            if (!mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.HIDDEN))
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            else {
                super.onBackPressed();
                overridePendingTransition(0, 0);
            }
        } else {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                overridePendingTransition(0, 0);
            }


            //finish();
        }
    }

    @Override
    public void onClick(View v) {
        //String pattern1 = ".mp4", pattern2=".mkv", pattern3=".avi", pattern4=".3gp";
        //String songName="";
        int type;

        LastSongInfo songInfo = new LastSongInfo(HomeActivity.this);

        if (MediaController.getInstance().getPlayingSongDetail() == null) {
            type = songInfo.getLastSong().getVideoCheck();
            //songName=songInfo.getLastSong().getDisplay_name();
        } else {
            type = MediaController.getInstance().getPlayingSongDetail().getVideoCheck();
            //songName=MediaController.getInstance().getPlayingSongDetail().getDisplay_name();
        }

        Log.e("list size", songInfo.getPlaylist().size() + "aaa");

        switch (v.getId()) {
            case R.id.bottombar_play:
                if (type == 0) {
                    if (MediaController.getInstance().getPlayingSongDetail() != null)
                        PlayPauseEvent(v);
                    else {
                        //MediaController.getInstance().currentPlaylistNum=songInfo.getLastPosition();
                        MediaController.getInstance().setPlaylist(songInfo.getPlaylist(), songInfo.getLastSong(), PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                        Log.e("position", songInfo.getLastPosition() + "aaa");
                        MediaController.getInstance().playAudio(songInfo.getLastSong());
                        //PlayPauseEvent(v);
                    }
                } else {
                    MediaController.getInstance().setPlaylist(songInfo.getPlaylist(), songInfo.getLastSong(), PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                    MediaController.getInstance().currentPlaylistNum = songInfo.getLastPosition();
                    MediaController.getInstance().playAudio(songInfo.getLastSong());
                    PlayPauseEvent(v);
                    Log.e("HomeLastSong2", songInfo.getLastSong().getDisplay_name());
                }
                break;

            case R.id.btn_play:
                if (type == 0) {
                    if (MediaController.getInstance().getPlayingSongDetail() != null)
                        PlayPauseEvent(v);
                    else {
                        MediaController.getInstance().setPlaylist(songInfo.getPlaylist(), songInfo.getLastSong(), PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                        MediaController.getInstance().currentPlaylistNum = songInfo.getLastPosition();
                        MediaController.getInstance().playAudio(songInfo.getLastSong());
                        //PlayPauseEvent(v);
                    }
                } else {
                    MediaController.getInstance().setPlaylist(songInfo.getPlaylist(), songInfo.getLastSong(), PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                    MediaController.getInstance().currentPlaylistNum = songInfo.getLastPosition();
                    MediaController.getInstance().playAudio(songInfo.getLastSong());
                    PlayPauseEvent(v);
                    Log.e("HomeLastSong2", songInfo.getLastSong().getDisplay_name());
                }

                break;

            case R.id.btn_forward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playNextSong();


                break;

            case R.id.btn_backward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playPreviousSong();

                break;

            case R.id.btn_suffel:
                if (type == 0) {
                    v.setSelected(v.isSelected() ? false : true);
                    MediaController.getInstance().shuffleMusic = v.isSelected() ? true : false;
                    MusicPreferance.setShuffel(context, (v.isSelected() ? true : false));
                    MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                    AVFPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                }

                break;

            case R.id.btn_toggle:
                if (type == 0) {
                    v.setSelected(v.isSelected() ? false : true);
                    MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                    MusicPreferance.setRepeat(context, (v.isSelected() ? 1 : 0));
                    AVFPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                }

                break;

            case R.id.bottombar_img_Favorite:
                if (type == 0) {
                    if (MediaController.getInstance().getPlayingSongDetail() != null) {
                        MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                        v.setSelected(v.isSelected() ? false : true);
                        AVFPlayerUtility.animateHeartButton(v);
                        findViewById(R.id.ivLike).setSelected(v.isSelected() ? true : false);
                        AVFPlayerUtility.animatePhotoLike(findViewById(R.id.vBgLike), findViewById(R.id.ivLike));
                    }
                }

                break;

            case R.id.btn_equalizer:
                if (type == 0) {

                }
                Intent intent = new Intent(HomeActivity.this, EqualizerDialog.class);
                startActivity(intent);

                break;

            case R.id.playlist:
//                Intent intent=new Intent(getApplicationContext(),FragmentAllSongs.class);
//                startActivity(intent);
                /*onBackPressed();*/
                break;
            default:
                break;
        }
    }

    private void getIntentData() {
        try {
            Uri data = getIntent().getData();
            if (data != null) {
                if (data.getScheme().equalsIgnoreCase("file")) {
                    String path = data.getPath().toString();
                    if (!TextUtils.isEmpty(path)) {
                        MediaController.getInstance().cleanupPlayer(context, true, true);
                        MusicPreferance.getPlaylist(context, path);
                        updateTitle(false);
                        MediaController.getInstance().playAudio(MusicPreferance.playingSongDetail);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
                if (data.getScheme().equalsIgnoreCase("http"))
                    LogWriter.info(TAG, data.getPath().toString());
                if (data.getScheme().equalsIgnoreCase("content"))
                    LogWriter.info(TAG, data.getPath().toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toolbarStatusBar() {
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void navigationDrawer() {
        // Cast drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void Intializer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_1);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);
        music_Button = (Button) findViewById(R.id.music_button);

        video_Button = (Button) findViewById(R.id.video_button);

        all_song_Button = (Button) findViewById(R.id.all_song_button);

        volume = (SeekBar) findViewById(R.id.volume_seekbar);


        home_layout = (FrameLayout) findViewById(R.id.fragment_layout);

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        audio_progress = (Slider) findViewById(R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);
        imgbtn_equalizer = (ImageView) findViewById(R.id.btn_equalizer);

        playlist = (LinearLayout) findViewById(R.id.playlist);


        imgbtn_moreOption = (ImageView) findViewById(R.id.bottombar_moreicon);

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;

        audio_progress.setBackgroundColor(coloraccent);
        //audio_progress.setValue(0);


        search = (AutoCompleteTextView) findViewById(R.id.search);
        search.addTextChangedListener(this);


        audio_progress.setOnValueChangedListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        imgbtn_toggle.setOnClickListener(this);
        imgbtn_suffel.setOnClickListener(this);
        img_Favorite.setOnClickListener(this);
        imgbtn_equalizer.setOnClickListener(this);
        playlist.setOnClickListener(this);


        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        ((PlayPauseView) findViewById(R.id.bottombar_play)).setOnClickListener(this);
        ((PlayPauseView) findViewById(R.id.btn_play)).setOnClickListener(this);

        imgbtn_toggle.setSelected((MusicPreferance.getRepeat(context) == 1) ? true : false);
        MediaController.getInstance().shuffleMusic = imgbtn_toggle.isSelected() ? true : false;
        AVFPlayerUtility.changeColorSet(context, (ImageView) imgbtn_toggle, imgbtn_toggle.isSelected());

        imgbtn_suffel.setSelected(MusicPreferance.getShuffel(context) ? true : false);
        MediaController.getInstance().repeatMode = imgbtn_suffel.isSelected() ? 1 : 0;
        AVFPlayerUtility.changeColorSet(context, (ImageView) imgbtn_suffel, imgbtn_suffel.isSelected());

        MediaController.getInstance().shuffleList(MusicPreferance.playlist);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    // if (isExpand) {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
                    // slideOffset);
                    // } else {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f -
                    // slideOffset);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                    // }

                } else {
                    isExpand = true;
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        title_text = (TextView) findViewById(R.id.title_text);

        main_list = (LinearLayout) findViewById(R.id.botton_list_main);

        imagecover = (ImageView) findViewById(R.id.imageViewCover);

        editext_layout = (LinearLayout) findViewById(R.id.edittext_main_layout);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_layout);


        final HomeIndex index = new HomeIndex();
        if (index.getHomeIndex() == 1) {
            FragmentAllVideoFolder fragmentAllVideos = new FragmentAllVideoFolder();
            frameLayout.setVisibility(View.VISIBLE);
            changeFragment(fragmentAllVideos);
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else if (index.getHomeIndex() == 2) {
            FragmentAllAudioVideoSong fragmentAllVideos = new FragmentAllAudioVideoSong();
            frameLayout.setVisibility(View.VISIBLE);
            changeFragment(fragmentAllVideos);
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            FragmentLibrary fragmentAllSongs = new FragmentLibrary();
            frameLayout.setVisibility(View.VISIBLE);
            changeFragment(fragmentAllSongs);
        }
        final AllAudioVideoInfo info = new AllAudioVideoInfo();

        music_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setIndex(0);
                index.setHomeIndex(0);
                //loadAlreadyPlayng();
                LastSongInfo songInfo = new LastSongInfo(HomeActivity.this);
                loadSongsDetails(songInfo.getLastSong());

                FragmentLibrary fragmentAllSongs = new FragmentLibrary();
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentAllSongs);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        title_text.setText("Home");


        video_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index.setHomeIndex(1);
                info.setIndex(1);
                LastSongInfo songInfo2 = new LastSongInfo(HomeActivity.this);


                SongDetail mDetail = MediaController.getInstance().getPlayingSongDetail();
                //Log.e("LastSong", mDetail.getDisplay_name());

                if (mDetail != null) {
                    if (mDetail.getVideoCheck() == 0) {
                        songInfo2.saveLastAlbID(mDetail.getAlbum_id());
                        songInfo2.saveLastPath(mDetail.getPath());
                        songInfo2.saveLastPosition(MediaController.getInstance().currentPlaylistNum);
                        songInfo2.saveLastSong(mDetail);
                        songInfo2.saveLastSongListType(0);
                    }
                }


                FragmentAllVideoFolder fragmentAllVideos = new FragmentAllVideoFolder();
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentAllVideos);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        all_song_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setIndex(1);
                index.setHomeIndex(2);
                LastSongInfo songInfo2 = new LastSongInfo(HomeActivity.this);


                SongDetail mDetail = MediaController.getInstance().getPlayingSongDetail();
                //Log.e("LastSong", mDetail.getDisplay_name());

                if (mDetail != null) {
                    if (mDetail.getVideoCheck() == 0) {
                        songInfo2.saveLastAlbID(mDetail.getAlbum_id());
                        songInfo2.saveLastPath(mDetail.getPath());
                        songInfo2.saveLastPosition(MediaController.getInstance().currentPlaylistNum);
                        songInfo2.saveLastSong(mDetail);
                        songInfo2.saveLastSongListType(0);
                    }

                }

                FragmentAllAudioVideoSong fragmentAllSongs = new FragmentAllAudioVideoSong();
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentAllSongs);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });


        imgbtn_moreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.getMenuInflater().inflate(R.menu.play_option, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.share:
                                    SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
                                    String sharePath = mSongDetail.getPath();
                                    Uri uri = Uri.parse(sharePath);
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("audio/*");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(Intent.createChooser(share, "Share Sound File"));
                                    break;
                            }

                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float result = Float.parseFloat("" + volume_level) / Float.parseFloat("" + maxVolume);
        int level = (int) (result * 100);
        volume.setProgress(level);

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int result = i * maxVolume;

                int level = result / 100;

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setFragment(int position) {

        switch (position) {

            case 0:
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                title_text.setText("Home");
                main_list.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
                FragmentLibrary fragmentAllSongs = new FragmentLibrary();
                changeFragment(fragmentAllSongs);
                toolbar.setTitle("Home");

                break;


            case 1:
                Log.e("Themes Click", "True");
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                title_text.setText("Theme");
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentThemes fragmentsettings = new FragmentThemes();
                main_list.setVisibility(View.GONE);
                editext_layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentsettings);
                toolbar.setTitle("Settings");
                Log.e("Themes Click 2", "True");
                break;


            case 2:
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                title_text.setText("Equalizer");
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentEqualizer fragmentequalizer = new FragmentEqualizer();
                main_list.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentequalizer);
                toolbar.setTitle("Equilizer");
                break;


            case 3:
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                title_text.setText("My Library");
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentLibrary fragmentlibrary = new FragmentLibrary();
                frameLayout.setVisibility(View.VISIBLE);
                main_list.setVisibility(View.GONE);
                editext_layout.setVisibility(View.GONE);
                changeFragment(fragmentlibrary);
                toolbar.setTitle("My Library");
                break;

            case 4:
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                title_text.setText("Settings");
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentFeedBack fragmentfeedback = new FragmentFeedBack();
                main_list.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentfeedback);
                toolbar.setTitle("Send feedback");
                break;


            case 5:
                Log.e("Themes Click", "True");
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                title_text.setText("Playlist");
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                ChildFragmentPlaylist fragmentallsongs = new ChildFragmentPlaylist();
                main_list.setVisibility(View.GONE);
                editext_layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                changeFragment(fragmentallsongs);
                toolbar.setTitle("Playlist");
                Log.e("Themes Click 2", "True");
                break;

        }
    }

    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 0);
        AVFPlayerUtility.settingTheme(context, theme);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                setFragment(0);
                break;

            case R.id.nav_themes:
                setFragment(1);
                break;

            case R.id.nav_playlist:
                setFragment(5);
                break;

         /*   case R.id.nav_equalizer:
                setFragment(2);
                break;*/

            /*case R.id.nav_library:
                setFragment(3);
                break;*/

            case R.id.nav_help:
                setFragment(4);
                break;
        }


        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ArrayList<SongDetail> songlist = MusicPreferance.getPlaylist(context);
        SearchAdapter adapter = new SearchAdapter(HomeActivity.this, R.layout.inflate_allsongsitem, R.id.inflate_allsong_textsongname);
        search.setAdapter(adapter);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

    }

    private void loadAlreadyPlayng() {
        LastSongInfo songInfo = new LastSongInfo(HomeActivity.this);
        SongDetail mSongDetail = songInfo.getLastSong();
        //ArrayList<SongDetail> playlist = MusicPreferance.getPlaylist(context);
        //Log.e("Resume", songInfo.getLastSong().getDisplay_name()+"a");
        if (mSongDetail != null) {
            //Log.e("Resume", songInfo.getLastSong().getDisplay_name()+"a");
            updateTitle(false);
        }
        MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
    }

    public void addObserver() {
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    public void removeObserver() {
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    public void loadSongsDetails(SongDetail mDetail) {
        //String pattern1 = ".mp4", pattern2=".mkv", pattern3=".avi", pattern4=".3gp";
        //String songName=mDetail.getDisplay_name();

        if (mDetail != null) {
            int type = mDetail.getVideoCheck();
            Log.e("type", "vv" + type);


            if (type == 0) {
                String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
                imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
                imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
                imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);

                txt_playesongname.setText(mDetail.getTitle());
                txt_songartistname.setText(mDetail.getArtist());
                txt_playesongname_slidetoptwo.setText(mDetail.getTitle());
                txt_songartistname_slidetoptwo.setText(mDetail.getArtist());

                if (txt_timetotal != null) {
                    long duration = Long.valueOf(mDetail.getDuration());
                    txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
                }
                updateProgress(mDetail);
            } else {
                LastSongInfo songInfo = new LastSongInfo(HomeActivity.this);
                Log.e("HomeLastSong2222", songInfo.getLastSong().getDisplay_name());

                Glide.with(context)
                        .load(mDetail.getPath())
                        .into(songAlbumbg);

                Glide.with(context)
                        .load(mDetail.getPath())
                        .into(img_bottom_slideone);

                Glide.with(context)
                        .load(mDetail.getPath())
                        .into(img_bottom_slidetwo);

            /*String contentURI = "content://media/external/audio/media/" + songInfo.getLastSong().getId() + "/albumart";

            imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
            imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
            imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);*/

                txt_playesongname.setText(songInfo.getLastSong().getDisplay_name());
                txt_songartistname.setText(songInfo.getLastSong().getArtist());
                txt_playesongname_slidetoptwo.setText(songInfo.getLastSong().getDisplay_name());
                txt_songartistname_slidetoptwo.setText(songInfo.getLastSong().getArtist());

                if (txt_timetotal != null) {
                    long duration = Long.valueOf(songInfo.getLastSong().getDuration());
                    txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
                }
                //updateProgress(mDetail);
            }

        } else {
            startActivity(new Intent(this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioDidStarted || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset) {
            updateTitle(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        } else if (id == NotificationManager.audioProgressDidChanged) {
            SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            if (mSongDetail.getVideoCheck() == 0)
                updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        MediaController.getInstance().checkIsFavorite(context, (SongDetail) args[0], img_Favorite);
    }

    private void updateTitle(boolean shutdown) {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail(); //

        LastSongInfo songInfo = new LastSongInfo(HomeActivity.this);


        //mSongDetail=songInfo.getLastSong();
        if (mSongDetail == null) {

            mSongDetail = songInfo.getLastSong();
            Log.e("updateTitle1", mSongDetail.getDisplay_name() + "aaa");
        } else {
            //String pattern1 = ".mp4", pattern2=".mkv", pattern3=".avi", pattern4=".3gp";
            //String songName=mSongDetail.getDisplay_name();
            //Log.e("updateTitle3", songName);
            int type = mSongDetail.getVideoCheck();
            Log.e("uodate1", "cc" + type);

            if (type == 1) {
                mSongDetail = songInfo.getLastSong();
            } else {
                mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            }



            /*if (type==1) {
                mSongDetail=songInfo.getLastSong();
            } else {
               mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            }*/
        }
        Log.e("updateTitle2", mSongDetail.getDisplay_name() + "aaa");
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            if (mSongDetail.getVideoCheck() == 0) {
                updateProgress(mSongDetail);
            } else {

            }
            if (MediaController.getInstance().isAudioPaused()) {
                btn_playpausePanel.Pause();
                btn_playpause.Pause();
            } else {
                if (MediaController.getInstance().getPlayingSongDetail().getVideoCheck() == 1) {
                    btn_playpausePanel.Pause();
                    btn_playpause.Pause();
                } else {
                    btn_playpausePanel.Play();
                    btn_playpause.Play();
                }

            }
            //SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
            loadSongsDetails(mSongDetail);
//            if (txt_timetotal != null) {
//                long duration = Long.valueOf(audioInfo.getDuration());
//                txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
//            }
        }
    }


    private void updateProgress(SongDetail mSongDetail) {
        if (audio_progress != null) {
            // When SeekBar Draging Don't Show Progress
            if (!isDragingStart) {
                // Progress Value comming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
//            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
//            txt_timeprogress.setText(timeString);
        }
    }

    private void PlayPauseEvent(View v) {
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Play();
        } else {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Pause();
        }
    }

    @Override
    public void onValueChanged(int value) {
        if (MediaController.getInstance().getPlayingSongDetail().getVideoCheck() == 0)
            MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }

    private void changeFragment(Fragment fragment) {
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_layout, fragment).commit();
    }
}
