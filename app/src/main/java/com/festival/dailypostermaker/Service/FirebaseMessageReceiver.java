package com.festival.dailypostermaker.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;


public class FirebaseMessageReceiver extends FirebaseMessagingService {

    private String TAG = FirebaseMessageReceiver.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG, "onNewToken: " + token);
        MyPreference.set_Token(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: ");
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "onMessageReceived: custom_message  :: " + remoteMessage.getData().get("custom_message"));

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            if (remoteMessage.getNotification().getImageUrl() == null) {
                displayImageNotification(title, body, null);
            } else {
                getBitmapAsyncAndDoWork(title, body, remoteMessage.getNotification().getImageUrl());
            }
        }

    }

    private void getBitmapAsyncAndDoWork(String title, String message, Uri imageUrl) {

        final Bitmap[] bitmap = {null};

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        bitmap[0] = resource;
                        displayImageNotification(title, message, bitmap[0]);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private PendingIntent getCancelNotificationIntent() {
        Intent cancelIntent = new Intent(this, CancelNotification.class);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    public void displayImageNotification(String title, String message, Bitmap bitmap) {
        String channel_id = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(getCancelNotificationIntent())
                .setChannelId(channel_id)
                .setPriority(Notification.PRIORITY_HIGH);

        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channel_id, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}

