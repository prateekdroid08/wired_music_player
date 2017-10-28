package com.avfplayer.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.avfplayer.R;
import com.avfplayer.fragments.FragmentEqualizer;

public class EqualizerDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_equalizer_dialog);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentEqualizer fragmentequalizer = new FragmentEqualizer();
        fragmentTransaction.replace(R.id.equalizer_dialog_frame_layout, fragmentequalizer);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
