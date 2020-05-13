package com.dybcatering.lajauja.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dybcatering.lajauja.R;

public class NotificationHelper extends ContextWrapper {

    public static final String LAJAUJA_CHANNEL_ID = "com.dybcatering.lajauja";
    public static final String LAJAUJA_CHANNEL_NAME = "La Jauja";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            createChannel();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(LAJAUJA_CHANNEL_ID,
                LAJAUJA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);

    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getLaJaujaChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(),LAJAUJA_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getLaJaujaChannelNotification(String title, String body, Uri soundUri){
        return new Notification.Builder(getApplicationContext(),LAJAUJA_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
