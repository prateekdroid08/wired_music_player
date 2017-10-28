package com.avfplayer.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.activities.AllVideoSongActivity;
import com.avfplayer.global.VideoFolderInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 12/23/2016.
 */

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {

    Context c;
    SwipeRefreshLayout swipe;
    ArrayList<HashMap<String, String>> folderList = new ArrayList<>();
    public FragmentTransaction fragmentTransaction;
    ProgressDialog dialog;
    VideoFolderInfo info;

    public VideoFolderAdapter(final Context c) {
        this.c = c;
        final String state = Environment.getExternalStorageState();


        info = new VideoFolderInfo();

        if (info.getFolderList().size() == 0) {
            dialog = new ProgressDialog(c);
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {// we can read the External Storage...
                        getAllFilesOfDir(Environment.getExternalStorageDirectory());
                        Log.e("folder list size", "bbb" + folderList.size());
                        ((Activity) c).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }).start();
        } else {
            folderList = info.getFolderList();
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_folder_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.view = v;
        vh.folderName = (TextView) v.findViewById(R.id.folder_name);
        vh.video_count = (TextView) v.findViewById(R.id.video_count);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.folderName.setText(folderList.get(position).get("folderName"));
        holder.video_count.setText(folderList.get(position).get("videoCount") + " videos");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, AllVideoSongActivity.class);
                intent.putExtra("folder_path", folderList.get(position).get("folderPath"));
                c.startActivity(intent);
                ((Activity) c).finish();

            }
        });


    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView folderName;
        public TextView video_count;
        public View view;

        public ViewHolder(View v) {
            super(v);
        }
    }

    private void getAllFilesOfDir(File directory) {

        String pattern[] = {".mp4", ".mkv", ".avi", ".3gp", ".mpeg", ".mpg", ".webm", ".flv", ".vob", ".ogg", ".wmv"};
        int count = 1;

        String folder_name = "", folder_path = "";
        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file != null) {
                    if (file.isDirectory()) {  // it is a folder...
                        if (!file.getName().equalsIgnoreCase("android") && !file.getName().startsWith(".") && !file.getName().startsWith("time4popcorn") && !file.getName().startsWith("Sent")) {

                            getAllFilesOfDir(file);

                        }

                    } else {  // it is a file...
                        for (int i = 0; i < pattern.length; i++) {
                            if (file.getName().endsWith(pattern[i])) {
                                folder_path = file.getParentFile().getName().toString();

                                if (!folder_path.equals(folder_name)) {
                                    HashMap<String, String> list = new HashMap<>();
                                    folder_name = file.getParentFile().getName();
                                    int video_count = getVideoCount(file.getParentFile().getPath());

                                    list.put("folderName", folder_name);
                                    list.put("folderPath", file.getParentFile().getPath());
                                    list.put("videoCount", "" + video_count);

                                    folderList.add(list);
                                    info.setFolderList(folderList);

                                    Log.e("folderPath", file.getPath());

                                    Log.e("Video Folder Log", "Folder Name: " + folder_name + "\n");

                                    Log.e("list", "cc" + folderList.size());

                                    //notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private int getVideoCount(String path) {
        int count = 0;
        String pattern[] = {".mp4", ".mkv", ".avi", ".3gp", ".mpeg", ".mpg", ".webm", ".flv", ".vob", ".ogg", ".wmv"};
        File directory = new File(path);

        String folder_name = "", folder_path = "";
        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file != null) {
                    // it is a file...
                    for (int i = 0; i < pattern.length; i++) {
                        if (file.getName().endsWith(pattern[i])) {

                            folder_path = file.getParentFile().getName().toString();

                            if (!folder_path.equals(folder_name)) {
                                count++;
                            }
                            break;
                        }
                    }

                }
            }
        }
        return count;
    }
}
