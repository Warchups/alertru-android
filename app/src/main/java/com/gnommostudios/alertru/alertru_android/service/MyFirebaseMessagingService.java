package com.gnommostudios.alertru.alertru_android.service;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gnommostudios.alertru.alertru_android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap imagen;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FIREBASE", remoteMessage.getNotification().getBody());

        long [] vibrate = {0,100,200,300};
        //Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turtle);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String url = "https://firebasestorage.googleapis.com/v0/b/christianllopis-271d8.appspot.com/o/op.png?alt=media&token=379531e3-99a5-4aa9-beaf-d8d2bc1f419d";

        imagen = descargarImagen(url);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.aviso)
                        .setLargeIcon(imagen)
                        .setContentTitle(getApplication().getPackageName())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setVibrate(vibrate)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setLights(0xFFFF0000, 300, 100);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

        /*AlertDialog.Builder alerta =
                new AlertDialog.Builder(getApplicationContext());
        alerta.setMessage(remoteMessage.getNotification().getBody())
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alerta.show();*/
    }

    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return imagen;
    }
}
