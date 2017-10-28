package com.avfplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avfplayer.R;
import com.avfplayer.fragments.FragmentAllVideo;

/**
 * Created by VlogicLabs on 2/15/2017.
 */

public class AllVideoSongActivity extends AppCompatActivity {

    String songPath;
    ImageView back;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_songs);

        Intent intent=getIntent();
        songPath=intent.getExtras().getString("folder_path");

        title= (TextView) findViewById(R.id.title_text);
        title.setText("Video Songs List");

        back= (ImageView) findViewById(R.id.menu_icon_image);
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.drawable.back_icon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle=new Bundle();
        bundle.putString("folder_path", songPath);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragmentAllVideos = new FragmentAllVideo();
        fragmentAllVideos.setArguments(bundle);
        fragmentTransaction.replace(R.id.video_song_frame, fragmentAllVideos);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AllVideoSongActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
