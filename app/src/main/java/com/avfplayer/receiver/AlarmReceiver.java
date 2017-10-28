package com.avfplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.avfplayer.R;
import com.avfplayer.models.MessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by softradix on 18/10/17.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        EventBus.getDefault().postSticky(new MessageEvent(context.getString(R.string.pause_media)));
        Log.d("Alarm Received:", "Alarm Received:");
    }
}