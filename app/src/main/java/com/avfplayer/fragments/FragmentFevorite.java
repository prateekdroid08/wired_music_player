/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.avfplayer.fragments;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.avfplayer.R;
import com.avfplayer.activities.AVFlayerBaseActivity;
import com.avfplayer.activities.HomeActivity;
import com.avfplayer.adapter.MultiChooseAdapter;
import com.avfplayer.dbhandler.FavoritePlayTableHelper;
import com.avfplayer.global.Global;
import com.avfplayer.global.MultiChooseInfo;
import com.avfplayer.manager.MediaController;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.uicomponent.AddToPlaylistDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class FragmentFevorite extends Fragment {

    private static final String TAG = "FragmentFevorite";
    private static Context context;
    private ListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
    Global global;
    public static FragmentFevorite newInstance(int position, Context mContext) {
        FragmentFevorite f = new FragmentFevorite();
        context = mContext;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragmentchild_mostplay, null);
        setupInitialViews(rootview);
        global=new Global();
        loadAllSongs();
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View inflatreView) {
        recycler_songslist = (ListView) inflatreView.findViewById(R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
        recycler_songslist.setAdapter(mAllSongsListAdapter);
    }

    private void loadAllSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.Favorite, "");
    }

    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
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
                    if (global.selection_category == "home")
                    {
                        ((HomeActivity)getActivity()).loadSongsDetails(mDetail);
                    }
                    else
                    {
                        ((AVFlayerBaseActivity) getActivity()).loadSongsDetails(mDetail);
                    }

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.Favorite.ordinal(), -1);
                        }
                    }

                    SharedPreferences sp=getActivity().getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
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
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
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
                                    loadAllSongs();
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
}
