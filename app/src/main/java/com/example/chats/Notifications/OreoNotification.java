package com.example.chats.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.chats.R;

public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.chats";
    private static final String CHANNEL_NAME = "chats";
    private NotificationManager notificationManager;


    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    public Notification.Builder getOreoNotification(String title,
                                                    String body,
                                                    PendingIntent pendingIntent,
                                                    Uri soundUri,
                                                    String icon){

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID).setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setColor(ContextCompat.getColor(this, R.color.online))
                .setAutoCancel(true);
    }
}
