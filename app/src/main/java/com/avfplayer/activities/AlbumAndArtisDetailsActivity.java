/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */

package com.avfplayer.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avfplayer.R;
import com.avfplayer.adapter.MultiChooseAdapter;
import com.avfplayer.dbhandler.FavoritePlayTableHelper;
import com.avfplayer.global.Global;
import com.avfplayer.global.MultiChooseInfo;
import com.avfplayer.manager.MediaController;
import com.avfplayer.manager.MusicPreferance;
import com.avfplayer.manager.NotificationManager;
import com.avfplayer.models.SongDetail;
import com.avfplayer.observablelib.ObservableScrollView;
import com.avfplayer.observablelib.ObservableScrollViewCallbacks;
import com.avfplayer.observablelib.ScrollState;
import com.avfplayer.observablelib.ScrollUtils;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.slidinguppanelhelper.SlidingUpPanelLayout;
import com.avfplayer.uicomponent.AddToPlaylistDialog;
import com.avfplayer.uicomponent.ExpandableHeightListView;
import com.avfplayer.uicomponent.PlayPauseView;
import com.avfplayer.uicomponent.Slider;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AlbumAndArtisDetailsActivity extends ActionBarActivity implements View.OnClickListener, ObservableScrollViewCallbacks, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    private SharedPreferences sharedPreferences;
    private int color = 0xFFFFFF;
    private Context context;

    private long id = -1;
    private long tagFor = -1;
    private String albumname = "";
    private String title_one = "";
    private String title_sec = "";
    private ImageView banner;
    private FloatingActionButton fab_button;
    private TextView tv_albumname, tv_title_fst, tv_title_sec;
    private ExpandableHeightListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set your theme first
        context = AlbumAndArtisDetailsActivity.this;
        theme();
        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumandartisdetails);

        initialize();
        getBundleValuse();

        initiSlidingUpPanel();
        loadAlreadyPaing();
        addObserver();
        fabanim();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bottombar_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
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
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().shuffleMusic = v.isSelected() ? true : false;
                MusicPreferance.setShuffel(context, (v.isSelected() ? true : false));
                MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                AVFPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.btn_toggle:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                MusicPreferance.setRepeat(context, (v.isSelected() ? 1 : 0));
                AVFPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                    v.setSelected(v.isSelected() ? false : true);
                    AVFPlayerUtility.animateHeartButton(v);
                    findViewById(R.id.ivLike).setSelected(v.isSelected() ? true : false);
                    AVFPlayerUtility.animatePhotoLike(findViewById(R.id.vBgLike), findViewById(R.id.ivLike));
                }
                break;

            case R.id.fab_button:
                if(songList.size()!=0) {
                    SongDetail mDetail = songList.get(0);
                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, (int) tagFor, (int) id);
                        }
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = color;
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(banner, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    //Catch  theme changed from settings
    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("THEME", 0);
        AVFPlayerUtility.settingTheme(context, theme);
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarView = findViewById(R.id.toolbar);

        // Setup RecyclerView inside drawer
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;

        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, color));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        banner = (ImageView) findViewById(R.id.banner);
        tv_albumname = (TextView) findViewById(R.id.tv_albumname);
        tv_title_fst = (TextView) findViewById(R.id.tv_title_frst);
        tv_title_sec = (TextView) findViewById(R.id.tv_title_sec);
        recycler_songslist = (ExpandableHeightListView) findViewById(R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(context);
        recycler_songslist.setAdapter(mAllSongsListAdapter);

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        try {
            fab_button = (FloatingActionButton) findViewById(R.id.fab_button);
            fab_button.setColorFilter(color);
            fab_button.setOnClickListener(this);
            if (Build.VERSION.SDK_INT > 15) {
                fab_button.setImageAlpha(255);
            } else {
                fab_button.setAlpha(255);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBundleValuse() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            id = mBundle.getLong("id");
            tagFor = mBundle.getLong("tagfor");
            albumname = mBundle.getString("albumname");
            title_one = mBundle.getString("title_one");
            title_sec = mBundle.getString("title_sec");
        }

        if (tagFor == PhoneMediaControl.SonLoadFor.Gener.ordinal()) {
            loadGenersSongs(id);
        } else if (tagFor == PhoneMediaControl.SonLoadFor.Album.ordinal()) {
            loadAlbumSongs(id);
        } else if (tagFor == PhoneMediaControl.SonLoadFor.Artis.ordinal()) {
            loadArtisSongs(id);
        } else if(tagFor== PhoneMediaControl.SonLoadFor.Playlist.ordinal()){
            loadPlaylistSongs(id);
        } else {

        }

        tv_albumname.setText(albumname);
        tv_title_fst.setText(title_one);
        tv_title_sec.setText(title_sec);
    }


    private void loadAlbumSongs(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    tv_title_sec.setText(songList.size() + " songs");
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Album, "");

        String contentURI = "content://media/external/audio/albumart/" + id;
        imageLoader.displayImage(contentURI, banner, options);
    }

    private void loadArtisSongs(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Artis, "");
    }

    private void loadGenersSongs(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                    tv_title_sec.setText(songList.size() + " songs");
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Gener, "");
    }

    private void loadPlaylistSongs(long id) {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
                if (songList != null && songList.size() >= 1) {
                    String contentURI = "content://media/external/audio/media/" + songList.get(0).getId() + "/albumart";
                    imageLoader.displayImage(contentURI, banner, options);
                    tv_title_sec.setText(songList.size() + " songs");
                }
            }
        });
        mPhoneMediaControl.loadMusicList(context, id, PhoneMediaControl.SonLoadFor.Playlist, "");
    }


    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
                mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
                mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = AVFPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SongDetail mDetail = songList.get(position);
                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, (int) tagFor, (int) id);
                        }
                    }

                    SharedPreferences sp=getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt(Global.HOMETAG, 0);
                    editor.commit();

                }
            });
            mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
            if (Build.VERSION.SDK_INT > 15) {
                mViewHolder.imagemore.setImageAlpha(255);
            } else {
                mViewHolder.imagemore.setAlpha(255);
            }

            mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                try {
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final SongDetail mDetail = songList.get(position);
                            switch (item.getItemId()) {
                                case R.id.share:

                                    String sharePath = mDetail.getPath();
                                    Uri uri = Uri.parse(sharePath);
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("audio/*");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(Intent.createChooser(share, "Share Sound File"));
                                    break;

                                case R.id.addtoplaylist:
                                    AddToPlaylistDialog playlistDialog=new AddToPlaylistDialog(context, mDetail);
                                    playlistDialog.show();
                                    break;

                                case R.id.addtofavorites:
                                    if(!FavoritePlayTableHelper.getInstance(context).getIsFavorite(mDetail))
                                        FavoritePlayTableHelper.getInstance(context).inserSong(mDetail, 1);
                                    else
                                        Toast.makeText(context,"Already added into favourite", Toast.LENGTH_SHORT).show();

                                    break;

                                case R.id.single:
                                    deleteSongConfirmationDialog(position, mDetail);
                                    break;

                                case R.id.multi:
                                    deleteMultiSelectionItemDialog();
                                    break;

                                case R.id.setasringtone:
                                    setSongasRingtone(mDetail);
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
            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView textViewSongName;
            ImageView imageSongThm, imagemore;
            TextView textViewSongArtisNameAndDuration;
            LinearLayout song_row;
        }

        private void deleteSongConfirmationDialog(final int postion, final SongDetail songDetail) {
            final boolean[] check = {false};
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setIcon(R.drawable.ic_app_icon);
            builderSingle.setMessage("Do you want to delete "+'"'+songDetail.display_name+'"');

            builderSingle.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteSong(postion,songDetail);

                }
            });

            builderSingle.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.show();
        }

        private void deleteSong(final int position, final SongDetail mDetail) {
            try {

                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mDetail.getId()
                );
                int a=context.getContentResolver().delete(uri, null, null);

                Log.e("delete", "aa"+a);
                if(a==1) {
                    songList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context,"Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

        private void setSongasRingtone(SongDetail songDetail) {
            File k = new File(songDetail.getPath());

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, songDetail.getDisplay_name());
            values.put(MediaStore.MediaColumns.SIZE, k.length());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.ARTIST, songDetail.getArtist());
            values.put(MediaStore.Audio.Media.DURATION, songDetail.getDuration());
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//Insert it into the database
            //Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
                    .getAbsolutePath());
            context.getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + k.getAbsolutePath() + "\"", null);
            Uri newUri = context.getContentResolver().insert(uri, values);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    RingtoneManager.setActualDefaultRingtoneUri(
                            context,
                            RingtoneManager.TYPE_RINGTONE,
                            newUri
                    );

                    Toast.makeText(context,"set as Rintone",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }


        }

        private void deleteMultiSelectionItemDialog() {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.multi_selection_delete_dialog);

            ListView list= (ListView) dialog.findViewById(R.id.dialog_listview);
            MultiChooseAdapter adapter=new MultiChooseAdapter(context, songList);
            list.setAdapter(adapter);
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            Button delete= (Button) dialog.findViewById(R.id.dialog_delete);
            Button cancel= (Button) dialog.findViewById(R.id.dialog_cancel);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MultiChooseInfo info=new MultiChooseInfo();

                    final ProgressDialog dialog1=new ProgressDialog(context);
                    dialog1.setMessage("Please wait...");
                    dialog1.setCancelable(false);
                    dialog1.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<info.getSelectedList().size();i++) {
                                if(info.getSelectedList().get(i)) {
                                    Log.e("select",""+i);
                                    deleteSong(i, songList.get(i));
                                    songList.remove(i);
                                    info.getSelectedList().remove(i);
                                    //notifyDataSetChanged();
                                }
                            }
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //notifyDataSetChanged();
                                    dialog1.dismiss();
                                    dialog.dismiss();
                                    info.getSelectedList().clear();
                                }
                            });
                        }
                    }).start();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        }
    }



    /*-----------------All Work Related to Slide Panel-----------------*/

    private static final String TAG = "ActivityDMPlayerBase";
    private SlidingUpPanelLayout mLayout;
    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    private boolean isExpand = false;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageView songAlbumbg;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private TextView txt_playesongname;
    private TextView txt_songartistname;

    private TextView txt_playesongname_slidetoptwo;
    private TextView txt_songartistname_slidetoptwo;

    //private TextView txt_timeprogress;
   // private TextView txt_timetotal;
    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;
    private ImageView imgbtn_toggle;
    private ImageView imgbtn_suffel;
    private ImageView img_Favorite;
    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;
    private Slider audio_progress;
    private boolean isDragingStart = false;
    private int TAG_Observer;

    private void initiSlidingUpPanel() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg);
        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);
       // txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        //txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        audio_progress = (Slider) findViewById(R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        audio_progress.setBackgroundColor(coloraccent);
        audio_progress.setValue(0);

        audio_progress.setOnValueChangedListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        imgbtn_toggle.setOnClickListener(this);
        imgbtn_suffel.setOnClickListener(this);
        img_Favorite.setOnClickListener(this);

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

    }

    private void loadAlreadyPaing() {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail != null) {
            loadSongsDetails(mSongDetail);
            updateTitle(false);
            MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
        }
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
        String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);

        txt_playesongname.setText(mDetail.getTitle());
        txt_songartistname.setText(mDetail.getArtist());
        txt_playesongname_slidetoptwo.setText(mDetail.getTitle());
        txt_songartistname_slidetoptwo.setText(mDetail.getArtist());

//        if (txt_timetotal != null) {
//            long duration = Long.valueOf(mDetail.getDuration());
//            txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
//        }
        updateProgress(mDetail);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioDidStarted || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset) {
            updateTitle(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        } else if (id == NotificationManager.audioProgressDidChanged) {
            SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        MediaController.getInstance().checkIsFavorite(context, (SongDetail) args[0], img_Favorite);
    }


    private void updateTitle(boolean shutdown) {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            updateProgress(mSongDetail);
            if (MediaController.getInstance().isAudioPaused()) {
                btn_playpausePanel.Pause();
                btn_playpause.Pause();
            } else {
                btn_playpausePanel.Play();
                btn_playpause.Play();
            }
            SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
            loadSongsDetails(audioInfo);

//            if (txt_timetotal != null) {
//                long duration = Long.valueOf(audioInfo.getDuration());
//                txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
//            }
        }
    }

    private void updateProgress(SongDetail mSongDetail) {
        if (audio_progress != null && mSongDetail != null) {
            // When SeekBar Draging Don't Show Progress
            if (!isDragingStart) {
                // Progress Value comming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
           // txt_timeprogress.setText(timeString);
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
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }


    private void fabanim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(fab_button, "scaleX", 0.0f, 1.0f);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fab_button, "scaleY", 0.0f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(fab_button, "alpha", 0.0f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, anim1, anim2);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

}
