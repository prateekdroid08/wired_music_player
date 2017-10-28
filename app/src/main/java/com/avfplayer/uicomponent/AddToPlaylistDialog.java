package com.avfplayer.uicomponent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.avfplayer.R;
import com.avfplayer.dbhandler.PlaylistPlayTableHelper;
import com.avfplayer.dbhandler.PlaylistTableHelper;
import com.avfplayer.models.SongDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VlogicLabs on 3/23/2017.
 */

public class AddToPlaylistDialog {

    Context context;
    SongDetail songDetail;
    ArrayList<HashMap<String, String>> playlist = new ArrayList<>();

    public AddToPlaylistDialog(Context context, SongDetail songDetail) {
        this.context = context;
        this.songDetail = songDetail;
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_app_icon);
        builderSingle.setTitle("Add to playlist");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);


        playlist = PlaylistTableHelper.getInstance(context).getPlayListFromSQLDBCursor();
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).get("type").equalsIgnoreCase("song"))
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
                AddPlaylistNameDialog playlistNameDialog = new AddPlaylistNameDialog(context, songDetail);
                playlistNameDialog.show();
                dialog.dismiss();
            }
        });

        final ArrayList<HashMap<String, String>> finalPlaylist = playlist;
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0; i<playlist.size(); i++){
                    if(playlist.get(i).get("playlistName").equalsIgnoreCase(arrayAdapter.getItem(which))){
                        int id = Integer.parseInt(playlist.get(i).get("playlistId"));
                        PlaylistPlayTableHelper.getInstance(context).inserSong(songDetail, id);
                        return;
                    }
                }


                //PlaylistPlayTableHelper.getInstance(context).getPlayListFromSQLDBCursor(""+id);


            }
        });
        builderSingle.show();
    }


}
