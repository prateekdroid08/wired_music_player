/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.avfplayer.fragments;

import com.avfplayer.R;
import com.avfplayer.activities.HomeActivity;
import com.avfplayer.receiver.AlarmReceiver;
import com.avfplayer.utility.Constants;
import com.pixplicity.easyprefs.library.Prefs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class FragmentFeedBack extends Fragment {

    public FragmentFeedBack() {

    }

    AlarmManager alarmMgr;
    TimePicker timePicker;
    TextView selectTimer;
    CheckBox cbHeadphone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_feedback, null);

        timePicker = (TimePicker) rootview.findViewById(R.id.time_picker);
        selectTimer = (TextView) rootview.findViewById(R.id.select_timer);
        cbHeadphone = (CheckBox) rootview.findViewById(R.id.cb_headphone);

        if (!Prefs.getBoolean(Constants.IS_HEADPHONE_BLUETOOTH, false))
            cbHeadphone.setChecked(true);
        else
            cbHeadphone.setChecked(false);

        cbHeadphone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Prefs.putBoolean(Constants.IS_HEADPHONE_BLUETOOTH, !b);
            }
        });

        selectTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    cal.set(Calendar.MINUTE, timePicker.getMinute());
                } else {
                    cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                }

                long startTime = cal.getTimeInMillis();

                setupInitialViews(startTime);
            }
        });

        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(long milliseconds) {
        alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, milliseconds, pendingIntent);

        Toast.makeText(getActivity(), "Timer Set Successfully", Toast.LENGTH_SHORT).show();
    }
}
