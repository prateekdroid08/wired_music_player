package com.avfplayer.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.avfplayer.dbhandler.FavoritePlayTableHelper;
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.global.MultiChooseInfo;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.interfaces.OnPlayVideo;
import com.avfplayer.manager.MediaController;
import com.avfplayer.manager.MusicPlayerService;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;
import com.avfplayer.uicomponent.AddToPlaylistDialog;
import com.avfplayer.uicomponent.AddToVideolistDialog;
import com.avfplayer.uicomponent.PlayPauseView;
import com.avfplayer.video.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 12/8/2016.
 */

public class AllVideoAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    ArrayList<String> videoFile = new ArrayList<String>();
    ArrayList<String> video_url;
    ArrayList<String> video_size;
    ArrayList<String> video_duration;
    ArrayList<String> video_milli_duration;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    ArrayList<SongDetail> playlist;
    private long id = -1;
    private long tagFor = -1;
    OnPlayVideo onPlayVideo;
    VideoDetail videoDetail;

    public AllVideoAdapter(Context context,
                           ArrayList<String> video_url,
                           ArrayList<String> video_size,
                           ArrayList<String> video_duration,
                           ArrayList<String> video_milli_duration,
                           ArrayList<SongDetail> playlist, OnPlayVideo onPlayVideo) {
        this.context = context;
        this.video_url = video_url;
        this.video_size = video_size;
        this.video_duration = video_duration;
        this.video_milli_duration = video_milli_duration;
        this.layoutInflater = LayoutInflater.from(context);
        this.playlist = playlist;
        videoDetail = new VideoDetail();
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.onPlayVideo = onPlayVideo;
    }

    @Override
    public int getCount() {
        return video_url.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder mViewHolder;
        final MusicPlayerService musicPlayerService = new MusicPlayerService();
        if (view == null) {
            mViewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.inflate_allvideoitem, null);
            mViewHolder.imageVideo = (SmartImageView) view.findViewById(R.id.tumbnail_img);
            mViewHolder.textViewVideoName = (TextView) view.findViewById(R.id.video_item_name);
            mViewHolder.VideoSize = (TextView) view.findViewById(R.id.video_item_size);
            mViewHolder.textViewVideoDuration = (TextView) view.findViewById(R.id.video_item_time_log);
            mViewHolder.Play_view = (PlayPauseView) view.findViewById(R.id.play_pause_view);
            mViewHolder.imagemore = (ImageView) view.findViewById(R.id.img_moreicon);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        final String fileName = video_url.get(i).substring(video_url.get(i).lastIndexOf('/') + 1, video_url.get(i).length());
        String fileNameWithoutExtn = video_url.get(i).substring(0, video_url.get(i).lastIndexOf('.'));
        mViewHolder.textViewVideoName.setText(fileName);

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
                    popup.getMenuInflater().inflate(R.menu.list_item_video, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share:
                                    String sharePath = video_url.get(i);
                                    Uri uri = Uri.parse(sharePath);
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("video/*");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    context.startActivity(Intent.createChooser(share, "Share Sound File"));
                                    break;

                                case R.id.addtoplaylist:

                                    videoDetail.setVIDEO_DURATION(video_duration.get(i));
                                    videoDetail.setVIDEO_ID(getVideoId(new File(video_url.get(i))));
                                    videoDetail.setVIDEO_MILLI_SECONDS(Long.parseLong(video_milli_duration.get(i)));
                                    videoDetail.setVIDEO_PLAYED_TIME(0);
                                    videoDetail.setVIDEO_SIZE(video_size.get(i));
                                    videoDetail.setVIDEO_URL(video_url.get(i));
                                    videoDetail.setVIDEO_NAME(fileName);

                                    AddToVideolistDialog videolistDialog = new AddToVideolistDialog(context, videoDetail);
                                    videolistDialog.show();

                                    break;

                                /*case R.id.single:
                                    deleteVideoConfirmationDialog(i, video_url.get(i));
                                    break;

                                case R.id.multi:
                                    deleteMultiSelectionItemDialog();
                                    break;*/
                                case R.id.delete:
                                    deleteVideoConfirmationDialog(i, video_url.get(i));
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


        mViewHolder.VideoSize.setText(video_size.get(i));
        mViewHolder.textViewVideoDuration.setText(video_duration.get(i));
        String value[] = new String[video_url.size()];
        for (int k = 0; k < video_url.size(); k++) {
            File file = new File(video_url.get(k));
            String filename = file.getParent().substring(file.getParent().lastIndexOf("/"));
            String final_value = filename.substring(1);
            value = new String[]{final_value};

        }

        for (int j = 1; j < value.length; j++) {
            if (value[j].equals(value[j - 1])) {
                System.out.println(Arrays.toString(value));
            }
        }


        mViewHolder.Play_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.e("Playlist size", "" + playlist.size());

                onPlayVideo.onVideoClick(i, video_url.get(i));
            }
        });
        Glide.with(context).load(video_url.get(i))
                .thumbnail(0.5f)
                .crossFade()
                .into(mViewHolder.imageVideo);


        return view;
    }

    class ViewHolder {
        LinearLayout video_layout;
        TextView textViewVideoName;
        SmartImageView imageVideo;
        PlayPauseView Play_view;
        TextView textViewVideoDuration, VideoSize;
        ImageView imagemore;
    }

    private void PlayPauseEvent(View v) {

        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
        ((PlayPauseView) v).Pause();

    }

    private void deleteVideoConfirmationDialog(final int position, final String videoName) {
        final boolean[] check = {false};
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_app_icon);
        builderSingle.setMessage("Do you want to delete " + '"' + videoName + '"');

        builderSingle.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteVideo(position, videoName);

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

    private void deleteVideo(final int position, String videoPath) {
        try {

            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, getVideoId(new File(videoPath))
            );
            int a = context.getContentResolver().delete(uri, null, null);

            Log.e("delete", "aa" + a);
            if (a == 1) {
                Log.d("Delete Video", VideoPlayListDetail.getInstance(context)
                        .deleteVideo(getVideoId(new File(videoPath))) + "");
                video_url.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private int getVideoId(File file) {
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

    private void deleteMultiSelectionItemDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.multi_selection_delete_dialog);

        ListView list = (ListView) dialog.findViewById(R.id.dialog_listview);
        MultiChooseVideoAdapter adapter = new MultiChooseVideoAdapter(context, video_url, video_size,
                video_duration, video_milli_duration, playlist);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button delete = (Button) dialog.findViewById(R.id.dialog_delete);
        Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MultiChooseInfo info = new MultiChooseInfo();

                final ProgressDialog dialog1 = new ProgressDialog(context);
                dialog1.setMessage("Please wait...");
                dialog1.setCancelable(false);
                dialog1.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < info.getSelectedList().size(); i++) {
                            if (info.getSelectedList().get(i)) {
                                Log.e("select", "" + i);
                                deleteVideo(i, video_url.get(i));
                                video_url.remove(i);
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
                                notifyDataSetChanged();
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


