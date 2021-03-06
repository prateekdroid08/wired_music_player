package com.avfplayer.uicomponent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avfplayer.R;
import com.avfplayer.dbhandler.PlaylistTableHelper;
import com.avfplayer.models.SongDetail;
import com.avfplayer.models.VideoDetail;

/**
 * Created by VlogicLabs on 3/23/2017.
 */

public class AddVideolistNameDialog {

    Context context;
    EditText name;
    VideoDetail videoDetail;

    public AddVideolistNameDialog(Context context, VideoDetail videoDetail) {
        this.context=context;
        this.videoDetail=videoDetail;
    }

    public void show() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("New Playlist");

        // Setting Dialog Message
        //alertDialog.setMessage("Enter Playlist Name");
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.create_playlist_name_layout,null);

        alertDialog.setView(row);

        name= (EditText) row.findViewById(R.id.playlist_name_edt);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("CREATE PLAYLIST",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                        if(name.getText().toString().equals("")) {
                            Toast.makeText(context, "Please, Enter the playlist name", Toast.LENGTH_SHORT).show();

                            AddVideolistNameDialog playlistNameDialog=new AddVideolistNameDialog(context, videoDetail);
                            playlistNameDialog.show();
                        } else {

                            if(!PlaylistTableHelper.getInstance(context).getIsExist(name.getText().toString())) {
                                PlaylistTableHelper.getInstance(context).insertInPlayList(name.getText().toString(), "video");
                                Toast.makeText(context,"created", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                AddToVideolistDialog videolistDialog=new AddToVideolistDialog(context, videoDetail);
                                videolistDialog.show();
                            } else {
                                Toast.makeText(context,"Playlist already exist !", Toast.LENGTH_SHORT).show();

                                AddToVideolistDialog videolistDialog=new AddToVideolistDialog(context, videoDetail);
                                videolistDialog.show();
                            }
                        }

                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }
}
