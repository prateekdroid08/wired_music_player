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
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.global.MultiChooseInfo;
import com.avfplayer.interfaces.OnPlayVideo;
import com.avfplayer.interfaces.OnPlayVideoPlayList;
import com.avfplayer.manager.MediaController;
import com.avfplayer.manager.MusicPlayerService;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;
import com.avfplayer.uicomponent.AddToVideolistDialog;
import com.avfplayer.uicomponent.PlayPauseView;
import com.bumptech.glide.Glide;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by softradix on 21/10/17.
 */

public class AdapterVideoPlayList extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    ArrayList<VideoDetail> videoDetails;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    ArrayList<SongDetail> playlist;
    VideoDetail videoDetail;
    OnPlayVideoPlayList onPlayVideoPlayList;

    public AdapterVideoPlayList(Context context, ArrayList<VideoDetail> videoDetails, ArrayList<SongDetail> playlist, OnPlayVideoPlayList onPlayVideoPlayList) {
        this.context = context;
        this.videoDetails = videoDetails;
        this.layoutInflater = LayoutInflater.from(context);
        this.onPlayVideoPlayList = onPlayVideoPlayList;
        videoDetail = new VideoDetail();
        this.playlist = playlist;
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        return videoDetails.size();
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
        final AdapterVideoPlayList.ViewHolder mViewHolder;
        if (view == null) {
            mViewHolder = new AdapterVideoPlayList.ViewHolder();
            view = layoutInflater.inflate(R.layout.inflate_allvideoitem, null);
            mViewHolder.imageVideo = (SmartImageView) view.findViewById(R.id.tumbnail_img);
            mViewHolder.textViewVideoName = (TextView) view.findViewById(R.id.video_item_name);
            mViewHolder.VideoSize = (TextView) view.findViewById(R.id.video_item_size);
            mViewHolder.textViewVideoDuration = (TextView) view.findViewById(R.id.video_item_time_log);
            mViewHolder.Play_view = (PlayPauseView) view.findViewById(R.id.play_pause_view);
            mViewHolder.imagemore = (ImageView) view.findViewById(R.id.img_moreicon);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (AdapterVideoPlayList.ViewHolder) view.getTag();
        }
        final String fileName = videoDetails.get(i).getVIDEO_URL().substring(videoDetails.get(i).getVIDEO_URL().lastIndexOf('/') + 1, videoDetails.get(i).getVIDEO_URL().length());
        mViewHolder.textViewVideoName.setText(fileName);

        mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
        if (Build.VERSION.SDK_INT > 15) {
            mViewHolder.imagemore.setImageAlpha(255);
        } else {
            mViewHolder.imagemore.setAlpha(255);
        }

        mViewHolder.VideoSize.setText(videoDetails.get(i).getVIDEO_SIZE());
        mViewHolder.textViewVideoDuration.setText(videoDetails.get(i).getVIDEO_DURATION());
        String value[] = new String[videoDetails.size()];
        for (int k = 0; k < videoDetails.size(); k++) {
            File file = new File(videoDetails.get(k).getVIDEO_URL());
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
                // play video from here
                onPlayVideoPlayList.onPlayVideoPlayList(i);
            }
        });
        Glide.with(context).load(videoDetails.get(i).getVIDEO_URL())
                .thumbnail(0.5f)
                .crossFade()
                .into(mViewHolder.imageVideo);

        mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.list_item_video_playlist, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share:
                                    String sharePath = videoDetails.get(i).getVIDEO_URL();
                                    Uri uri = Uri.parse(sharePath);
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("video/*");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    context.startActivity(Intent.createChooser(share, "Share Sound File"));
                                    break;
                                case R.id.delete:
                                    deleteVideoConfirmationDialog(i, videoDetails.get(i).getVIDEO_URL());
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


        return view;
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
                        .deleteVideo(videoDetails.get(position).getVIDEO_ID()) + "");

                videoDetails.remove(position);
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

    class ViewHolder {
        LinearLayout video_layout;
        TextView textViewVideoName;
        SmartImageView imageVideo;
        PlayPauseView Play_view;
        TextView textViewVideoDuration, VideoSize;
        ImageView imagemore;
    }
}