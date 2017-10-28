package com.avfplayer.uicomponent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.avfplayer.R;
import com.avfplayer.dbhandler.PlaylistPlayTableHelper;
import com.avfplayer.dbhandler.PlaylistTableHelper;
import com.avfplayer.dbhandler.VideoPlayListDetail;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 3/23/2017.
 */

public class AddToVideolistDialog {

    Context context;
    VideoDetail videoDetail;
    ArrayList<HashMap<String, String>> playlist = new ArrayList<>();

    public AddToVideolistDialog(Context context, VideoDetail videoDetail) {
        this.context = context;
        this.videoDetail = videoDetail;
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_app_icon);
        builderSingle.setTitle("Add to playlist");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);


        playlist = PlaylistTableHelper.getInstance(context).getPlayListFromSQLDBCursor();
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).get("type").equalsIgnoreCase("video"))
                arrayAdapter.add(playlist.get(i).get("playlistName"));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Create Playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddVideolistNameDialog addToVideolistDialog = new AddVideolistNameDialog(context, videoDetail);
                addToVideolistDialog.show();
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for(int i=0; i<playlist.size(); i++){
                    if(playlist.get(i).get("playlistName").equalsIgnoreCase(arrayAdapter.getItem(which))){
                        videoDetail.setPLAY_LIST_ID(Integer.parseInt(playlist.get(i).get("playlistId")));
                        VideoPlayListDetail.getInstance(context).insertVideoInPlaylist(videoDetail);
                        return;
                    }
                }
            }
        });
        builderSingle.show();
    }


}
