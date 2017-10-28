package com.avfplayer.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.global.AllAudioVideoInfo;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.models.SongDetail;
import com.avfplayer.phonemidea.AVFPlayerUtility;
import com.avfplayer.phonemidea.PhoneMediaControl;
import com.avfplayer.uicomponent.CircleImageView;
import com.avfplayer.video.MainActivity;
import com.bumptech.glide.Glide;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * Created by VlogicLabs on 2/23/2017.
 */

public class AdapterAudioVideo extends RecyclerView.Adapter<AdapterAudioVideo.ViewHolder> {

    ArrayList<SongDetail> allList=new ArrayList<>();
    Context context;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ProgressDialog dialog;
    AllAudioVideoInfo info;

    public AdapterAudioVideo(Context context) {
        this.context=context;
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        info=new AllAudioVideoInfo();

        if(info.getAllList().size()!=0) {
            allList=info.getAllList();
            notifyDataSetChanged();
        } else {
            getAllAudioVideoList();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_video_list_item_layout, parent, false);

        ViewHolder vh=new ViewHolder(v);
        vh.audio_ll= (LinearLayout) v.findViewById(R.id.audio_item_ll);
        vh.video_ll= (LinearLayout) v.findViewById(R.id.video_item_ll);
        vh.videoImage= (SmartImageView) v.findViewById(R.id.video_image);
        vh.videoName= (TextView) v.findViewById(R.id.video_item_name);
        vh.videoTime= (TextView) v.findViewById(R.id.video_item_time_log);
        vh.audioName= (TextView) v.findViewById(R.id.audio_textsongname);
        vh.audioArtist= (TextView) v.findViewById(R.id.audio_textsongArtisName_duration);
        vh.audioImage= (CircleImageView) v.findViewById(R.id.audio_imgSongThumb);
        vh.audioOption= (ImageView) v.findViewById(R.id.audio_moreicon);
        vh.view=v;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int type=allList.get(position).getType();

        SongDetail mDetail = allList.get(position);

        String audioDuration = "";
        try {
            audioDuration = AVFPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(type==0) {
            holder.video_ll.setVisibility(View.GONE);
            holder.audio_ll.setVisibility(View.VISIBLE);

            holder.audioArtist.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            holder.audioName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, holder.audioImage, options);

            holder.audioOption.setVisibility(View.GONE);

            holder.audioOption.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.play_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.share:
                                        SongDetail mDetail = allList.get(position);
                                        String sharePath = mDetail.getPath();
                                        Uri uri = Uri.parse(sharePath);
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("audio/*");
                                        share.putExtra(Intent.EXTRA_STREAM, uri);
                                        context.startActivity(Intent.createChooser(share, "Share Sound File"));
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
        } else {
            holder.video_ll.setVisibility(View.VISIBLE);
            holder.audio_ll.setVisibility(View.GONE);

            String fileName = allList.get(position).getDisplay_name();
            holder.videoName.setText(fileName);

            holder.videoTime.setText(audioDuration);

            Glide.with(context).load(allList.get(position).getPath())
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(holder.videoImage);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Playlist size",""+allList.size());

                final VideoListInfo info=new VideoListInfo();
                info.setPlaylist(allList);
                info.setIndex(position);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Intent intent=new Intent(context, MainActivity.class);
                        intent.putExtra("index", 0);
                    context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout audio_ll, video_ll;
        public SmartImageView videoImage;
        public TextView videoName, videoTime, audioName, audioArtist;
        public CircleImageView audioImage;
        public ImageView audioOption;
        public View view;
        public ViewHolder(View v) {
            super(v);
        }
    }

    private void getAllAudioVideoList() {
        dialog=new ProgressDialog(context);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadAllAudioSongs();

            }
        }).start();
    }

    private void loadAllAudioSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                allList = songsList_;


                //reloadAllAudioSongs();
                loadAllVideoSongs();
            }
        });
        mPhoneMediaControl.loadMusicList(context, -1, PhoneMediaControl.SonLoadFor.All, "");
    }

    private void reloadAllAudioSongs() {
        for(int i=0;i<allList.size();i++) {
            SongDetail songDetail=allList.get(i);
            songDetail.setType(1);
            allList.set(i,songDetail);

            loadAllVideoSongs();
        }
    }

    private void loadAllVideoSongs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String state = Environment.getExternalStorageState();
                if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {// we can read the External Storage...
                    getAllFilesOfDir(Environment.getExternalStorageDirectory());
                    //Log.e("folder list size", "bbb"+folderList.size());
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            notifyDataSetChanged();
                            Log.e("list size", allList.size()+"aaa");
                        }
                    });
                }
            }
        }).start();

    }

    private void getAllFilesOfDir(File directory) {

        String pattern[] = {".mp4", ".mkv", ".avi", ".3gp", ".mpeg", ".mpg", ".webm",
                ".flv", ".vob", ".ogg", ".wmv", ".ts"};
        int count=1;

        String folder_name="", folder_path="";
        final File[] files = directory.listFiles();

        if ( files != null ) {
            for ( File file : files ) {

                if ( file != null ) {
                    if ( file.isDirectory() ) {  // it is a folder...
                        if(!file.getName().equalsIgnoreCase("android") && !file.getName().startsWith(".") && !file.getName().startsWith("time4popcorn") && !file.getName().startsWith("Sent")) {

                            getAllFilesOfDir(file);

                        }

                    }
                    else {  // it is a file...
                        for(int i=0; i<pattern.length; i++) {
                            try {
                                if (file.getName().endsWith(pattern[i])) {

                                    Log.e("Video Name", file.getPath().toString());
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(file.getPath());
                                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    long timeInmillisec = Long.parseLong(time);

                                    //getDurationBreakdown(timeInmillisec);

                                    long length = file.length();

                                    //formatFileSize(length);

                                    createVideoPlaylist(file.getPath().toString(), file.getName().toString(), "" + timeInmillisec);
                                }

                                Log.d("folderPath", file.getPath());

                                Log.d("Video Folder Log", "Folder Name: " + folder_name + "\n");

                                Comparator<SongDetail> comparator = new Comparator<SongDetail>() {
                                    @Override
                                    public int compare(SongDetail lhs, SongDetail rhs) {

                                        String left = lhs.getDisplay_name();
                                        String right = rhs.getDisplay_name();

                                        return left.compareTo(right);

                                    }
                                };

                                Collections.sort(allList, comparator);

                                info.setAllList(allList);


                                //notifyDataSetChanged();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }


        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append(":");
        sb.append(minutes);
        sb.append(":");
        sb.append(seconds);

        return sb.toString();
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    private void createVideoPlaylist(String videoPath, String video_name, String duration) {
        Log.e("test","1");
        SongDetail mSongDetail = new SongDetail(getVideoId(context, new File(videoPath)), 0, "", video_name, videoPath, video_name, duration, 1, 1);
        allList.add(mSongDetail);
    }

    private int getVideoId(Context context, File file) {
        int id =0;
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID,
                        MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));


            Log.e("Video_id", "" + id);
        }

        return id;
    }
}
