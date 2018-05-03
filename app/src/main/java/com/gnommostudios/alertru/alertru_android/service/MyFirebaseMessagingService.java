package com.gnommostudios.alertru.alertru_android.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.view.MainActivity;
import com.gnommostudios.alertru.alertru_android.view.fragments.AlertListFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private SharedPreferences prefs;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FIREBASE", remoteMessage.getNotification().getBody());

        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        boolean activateAlert = prefs.getBoolean("activateAlert", true);

        if (activateAlert) {
            String ringote = prefs.getString("ringote", "");
            Uri sound;

            if (ringote.equals("a1")) {
                sound = RingtoneManager.getDefaultUri(R.raw.alarm1);
            } else if (ringote.equals("a2")) {
                sound = RingtoneManager.getDefaultUri(R.raw.alarm2);
            } else if (ringote.equals("be")) {
                sound = RingtoneManager.getDefaultUri(R.raw.beep);
            } else if (ringote.equals("nu")) {
                sound = RingtoneManager.getDefaultUri(R.raw.nuclear);
            } else {
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            long [] vibrate = {0, 100, 200, 300};

            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_alarma);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon_alarma)
                            .setLargeIcon(image)
                            .setPriority(Integer.parseInt(remoteMessage.getData().get("priority")))
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setVibrate(vibrate)
                            .setSound(sound)
                            .setLights(0xFFFF0000, 300, 100);

            //Log.i("PRUEBA", remoteMessage.getData().get("priority"));
            //Log.i("TIME", remoteMessage.getTtl() + "");

            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, mBuilder.build());

            /*long delayInMilliseconds = remoteMessage.getTtl();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mNotificationManager.cancelAll();
                }
            }, delayInMilliseconds);*/
        }

        //Intent intent = new Intent("com.gnommostudios.alertru.mybroadcastreceiver");
        Intent intent = new Intent("MainActivity");
        intent.putExtra("NOTIFICATION", true);
        //send broadcast
        getApplicationContext().sendBroadcast(intent);
    }
}
