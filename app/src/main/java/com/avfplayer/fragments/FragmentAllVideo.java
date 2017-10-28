package com.avfplayer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.avfplayer.R;
import com.avfplayer.adapter.AllVideoAdapter;
import com.avfplayer.dbhandler.FavoritePlayTableHelper;
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.global.Global;
import com.avfplayer.global.VideoListInfo;
import com.avfplayer.interfaces.OnPlayVideo;
import com.avfplayer.models.SongDetail;
import com.avfplayer.slidinguppanelhelper.SlidingUpPanelLayout;
import com.avfplayer.video.MainActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class FragmentAllVideo extends Fragment implements OnPlayVideo {
    private ListView recycler_videoslist;
    //private RecyclerView recycler_videoslist;
    private AllVideoAdapter videoAdapter;
    ArrayList<String> video_url;
    ArrayList<String> video_size;
    ArrayList<String> video_duration;
    ArrayList<String> video_milli_duration;
    ArrayList<HashMap<String, String>> folderList = new ArrayList<>();
    ImageView back;
    public FragmentTransaction fragmentTransaction;
    String path;
    private SlidingUpPanelLayout mLayout;
    ProgressDialog dialog;
    ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();
    private int TAG_Observer;
    VideoListInfo info;

    private Cursor cursor = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_all_video, container, false);

        Bundle bundle = getArguments();
        path = bundle.getString("folder_path");

        info = new VideoListInfo();

        setupInitialViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View view) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(path);
                getAllFilesOfDir(file);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        info.setPlaylist(generassongsList);

                        videoAdapter = new AllVideoAdapter(getActivity(), video_url, video_size,
                                video_duration, video_milli_duration, generassongsList, FragmentAllVideo.this);
                        recycler_videoslist.setAdapter(videoAdapter);
                        dialog.dismiss();

                        if (generassongsList.size() != 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //SongDetail mDetail=generassongsList.get(MediaController.getInstance().currentPlaylistNum);
                                    //MediaController.getInstance().setPlaylist(generassongsList, mDetail, -1, -1);

                                }
                            }).start();

                            //MediaController.getInstance().stopAudio();
                            SharedPreferences sp = getActivity().getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(Global.HOMETAG, 1);
                            editor.commit();
                        }
                    }
                });

            }
        }).start();

        recycler_videoslist = (ListView) view.findViewById(R.id.recycler_allVideos);

        /*mLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);*/
        /*recycler_videoslist = (RecyclerView) view.findViewById(R.id.recycler_allVideos);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_videoslist.setLayoutManager(linearLayoutManager);
*/
        /*VideoFolderAdapter videoFolderAdapter=new VideoFolderAdapter(getActivity());
        recycler_videoslist.setAdapter(videoFolderAdapter);*/
    }

    private long getAllVideoStopTime(Cursor cursor) {
        long VIDEO_PLAYED_TIME = 0;
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                while (cursor.moveToNext()) {
                    VIDEO_PLAYED_TIME = cursor.getLong(cursor.getColumnIndex(VideoPlayListDetail.VIDEO_PLAYED_TIME));
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return VIDEO_PLAYED_TIME;
    }

    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }

    /* Handler mHandler = new Handler();
     Runnable mTicker = new Runnable() {
         @Override
         public void run() {
             Bitmap thumb = ThumbnailUtils.createVideoThumbnail(String.valueOf(printNamesToLogCat(getActivity())), MediaStore.Video.Thumbnails.MICRO_KIND);
             System.out.println(thumb);
           //  mHandler.postDelayed(mTicker, 1000);

         }
     };*/
    public void gen_bitmap() {

    }

    @Override
    public void onVideoClick(final int position, String videoPath) {

        cursor = VideoPlayListDetail.getInstance(getActivity()).getVideoDetail(getVideoId(getActivity(), new File(videoPath)));

        final long videoTime = getAllVideoStopTime(cursor);
        if (videoTime != 0) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Start Video from?")
                    .setCancelable(false)
                    .setPositiveButton("Beginning", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VideoListInfo info = new VideoListInfo();
                            info.setIndex(position);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("index", 1);
                                    getActivity().startActivity(intent);
                                }
                            });
                        }
                    })
                    .setNegativeButton("Resume", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            VideoListInfo info = new VideoListInfo();
                            info.setIndex(position);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("index", 1);
                                    intent.putExtra("resume", videoTime + "");
                                    getActivity().startActivity(intent);
                                }
                            });
                        }
                    }).show();
        } else {
            VideoListInfo info = new VideoListInfo();
            info.setIndex(position);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("index", 1);
                    getActivity().startActivity(intent);
                }
            });
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public ArrayList<String> printNamesToLogCat(Context context) {
        ArrayList<String> data = new ArrayList<String>();
//        String selection=MediaStore.Video.Media.DATA +" like?";
//        String[] selectionArgs=new String[]{"%FolderName%"};
//        String[] parameters = { MediaStore.Video.Media._ID};
//        Cursor c = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            while (c.moveToNext()) {
                // Log.e("VIDEO", c.getString(0));
                // String fileNameWithoutExtn = c.getString(0).substring(0, c.getString(0).lastIndexOf('.'));
                // String filename=c.getString(0).substring(c.getString(0).lastIndexOf("/")+1);
                //Log.e("sile_name",fileNameWithoutExtn);
                data.add(c.getString(0));
            }
            c.close();
        }
        return data;
    }

    private void getAllFilesOfDir(File directory) {

        video_url = new ArrayList<String>();
        video_size = new ArrayList<String>();
        video_duration = new ArrayList<String>();
        video_milli_duration = new ArrayList<>();

        String pattern[] = {".mp4", ".mkv", ".avi", ".3gp", ".mpeg", ".mpg", ".webm",
                ".flv", ".vob", ".ogg", ".wmv", ".ts"};

        String folder_name = "", folder_path = "";
        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file != null) {
                    // it is a file...
                    for (int i = 0; i < pattern.length; i++) {
                        try {
                            if (file.getName().endsWith(pattern[i])) {
                                folder_path = file.getParentFile().getName().toString();

                                if (!folder_path.equals(folder_name)) {

                                    video_url.add(file.getPath().toString());

                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(file.getPath());
                                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    long timeInmillisec = Long.parseLong(time);
                                    video_milli_duration.add("" + timeInmillisec);
                                    video_duration.add(getDurationBreakdown(timeInmillisec));


                                    long length = file.length();


                                    video_size.add(formatFileSize(length));

                                    createVideoPlaylist(file.getPath().toString(), file.getName().toString(), "" + timeInmillisec);

                                    VideoPlayListDetail.getInstance(getActivity()).insertVideo
                                            (getVideoId(getActivity(), new File(file.getPath().toString())),
                                                    file.getPath().toString(),
                                                    formatFileSize(length),
                                                    getDurationBreakdown(timeInmillisec),
                                                    timeInmillisec,
                                                    0);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            video_milli_duration.add("");
                            video_duration.add("");
                            video_size.add("");
                        }
                    }
                }
            }
        }
    }

    public void printNames(Context context, String path) {
        video_url = new ArrayList<String>();
        video_size = new ArrayList<String>();
        video_duration = new ArrayList<String>();

//        String selection=MediaStore.Video.Media.DATA +" like?";
//        String[] selectionArgs=new String[]{"%FolderName%"};
//        String[] parameters = { MediaStore.Video.Media._ID};
//        Cursor c = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        //Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uri = Uri.parse(path);
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            String dummy;
            while (c.moveToNext()) {
                video_url.add(c.getString(0));
            }
            c.close();
        }

        String[] size = {MediaStore.Video.VideoColumns.SIZE};
        Cursor cursor_size = context.getContentResolver().query(uri, size, null, null, null);
        int vids_size = 0;
        if (cursor_size != null) {
            vids_size = cursor_size.getCount();
            while (cursor_size.moveToNext()) {
                video_size.add(formatFileSize(Long.parseLong(cursor_size.getString(0))));
            }
            cursor_size.close();
        }

        String[] duration = {MediaStore.Video.VideoColumns.DURATION};
        Cursor cursor_duration = context.getContentResolver().query(uri, duration, null, null, null);
        int vids_duration = 0;
        if (cursor_duration != null) {
            vids_duration = cursor_duration.getCount();
            while (cursor_duration.moveToNext()) {
                video_duration.add(getDurationBreakdown(Long.parseLong(cursor_duration.getString(0))));
            }
            cursor_duration.close();
        }


    }


//    List<File> files = getListFiles(new File(String.valueOf(Environment.getExternalStorageDirectory())));
//    private List<File> getListFiles(File parentDir) {
//        ArrayList<File> inFiles = new ArrayList<File>();
//        File[] files = parentDir.listFiles();
//        for (File file : files) {
//            if (file.isDirectory()) {
//                inFiles.addAll(getListFiles(file));
//            } else {
//                if(file.getName().endsWith(".mp4")){
//                    inFiles.add(file);
//                }
//            }
//            System.out.println(inFiles);
//        }
//        return inFiles;
//    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
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

    private void createVideoPlaylist(String videoPath, String video_name, String duration) {
        Log.e("test", "1");
        SongDetail mSongDetail = new SongDetail(getVideoId(getActivity(), new File(videoPath)), 0, "", video_name, videoPath, video_name, duration, 1, 1);
        generassongsList.add(mSongDetail);
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


}
