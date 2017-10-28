
package com.avfplayer.splashScreen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.avfplayer.R;
import com.avfplayer.activities.HomeActivity;
import com.avfplayer.global.Global;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                            && ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.READ_PHONE_STATE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.WRITE_SETTINGS)) {

                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again
                        ActivityCompat.requestPermissions(SplashScreen.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_SETTINGS}, 1);

                    } else {
                        ActivityCompat.requestPermissions(SplashScreen.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_SETTINGS}, 1);
                    }
                } else {
                    SharedPreferences sp=getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt(Global.HOMETAG, 0);
                    editor.commit();
                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);

                    finish();

                    //Toast.makeText(context, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
                }



            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(SplashScreen.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Location
                case 1:
                    SharedPreferences sp=getSharedPreferences(Global.PRFENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt(Global.HOMETAG, 0);
                    editor.commit();

                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);

                    finish();
                    break;

            }
        }
    }

}
