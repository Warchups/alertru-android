package com.gnommostudios.alertru.alertru_android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.gnommostudios.alertru.alertru_android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private SharedPreferences prefs;

    @Override
    public void handleIntent(Intent intent) {
        //Log.i("HANDLE-INTENT", intent.getAction());

        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            Bundle bundle = intent.getExtras();

            showNotification(bundle.getString("gcm.notification.title"), bundle.getString("gcm.notification.body"));
        } else {
            super.handleIntent(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title, String body) {
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        boolean activateAlert = prefs.getBoolean("activateAlert", true);

        if (activateAlert) {
            String ringote = prefs.getString("ringote", "");
            Uri sound;

            if (ringote.equals("a1")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm1);
            } else if (ringote.equals("a2")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm2);
            } else if (ringote.equals("be")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep);
            } else if (ringote.equals("nu")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.nuclear);
            } else {
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            long [] vibrate = {0, 1000, 2000, 3000};

            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_alarma);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon_alarma)
                            .setLargeIcon(image)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setVibrate(vibrate)
                            .setVisibility(VISIBILITY_PUBLIC)
                            .setSound(sound)
                            .setLights(0xFFFF0000, 300, 100);

            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_INSISTENT;

            mNotificationManager.notify(0, notification);
        }

        Intent intent = new Intent("MainActivity");
        intent.putExtra("NOTIFICATION", true);
        intent.putExtra("CHANGE_TITLE", false);
        //send broadcast
        getApplicationContext().sendBroadcast(intent);
    }

}
