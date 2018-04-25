package com.gnommostudios.alertru.alertru_android.service;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gnommostudios.alertru.alertru_android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FIREBASE", remoteMessage.getNotification().getBody());

        long [] vibrate = {0,100,200,300};
        //Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turtle);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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


        Log.i("PRUEBA", remoteMessage.getData().get("priority"));
        Log.i("TIME", remoteMessage.getTtl()+"");

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

        long delayInMilliseconds = remoteMessage.getTtl();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mNotificationManager.cancelAll();
            }
        }, delayInMilliseconds);
    }
}
