package com.avfplayer.manager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.avfplayer.R;
import com.avfplayer.models.SongDetail;

import java.util.Random;

/**
 * Created by VlogicLabs on 3/8/2017.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String NOTIFY_PREVIOUS = "musicplayer.previous";
    public static final String NOTIFY_CLOSE = "musicplayer.close";
    public static final String NOTIFY_PAUSE = "musicplayer.pause";
    public static final String NOTIFY_PLAY = "musicplayer.play";
    public static final String NOTIFY_NEXT = "musicplayer.next";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            Log.w("WidgetExample", String.valueOf(number));

            if(MediaController.getInstance().getPlayingSongDetail()!=null) {
                SongDetail mDetail=MediaController.getInstance().getPlayingSongDetail();
                String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";

                remoteViews.setTextViewText(R.id.widget_song_name, MediaController.getInstance().getPlayingSongDetail().getDisplay_name());
                remoteViews.setImageViewUri(R.id.song_image, Uri.parse(contentURI));

                setListeners(context, remoteViews);
            }

            // Set the text



            //remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            // Register an onClickListener
            Intent intent = new Intent(context.getApplicationContext(),
                    MusicPlayerService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            // Update the widgets via the service


            //Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            context.startService(intent);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void setListeners(Context context, RemoteViews view) {
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_PREVIOUS),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_play, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
