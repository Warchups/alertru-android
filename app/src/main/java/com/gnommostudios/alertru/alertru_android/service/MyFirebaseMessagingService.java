package com.gnommostudios.alertru.alertru_android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

        //Sobreescribo este metodo para controlar tambien cuando esta la pantalla bloqueada

        //Si el action del intent es el de recivir un mensaje llamo a mi funcion
        //para mostrar una notificacion pasandole el titulo y el cuerpo.
        //Si no estoy reciviendo nada llamo a super para que haga sus gestiones
        //en los casos que no me interesan
        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            Bundle bundle = intent.getExtras();

            //Log.i("BUNDLE", bundle.toString());

            showNotification(bundle.getString("title"), bundle.getString("body"), bundle.get("alert").equals("true"));
        } else {
            super.handleIntent(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("alert").equals("true"));
    }

    private void showNotification(String title, String body, boolean alert) {
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        boolean activateAlert = prefs.getBoolean("activateAlert", true);

        //Compruebo si estan activadas las alertas en preferencias
        if (activateAlert) {
            //Si estan activadas recojo el valor de ringote que me dice que alerta quiere que suene el usuario
            String ringote = prefs.getString("ringote", "");
            Uri sound;

            //Dependiendo de el valor de ringote creo una Uri de la alerta o otra
            if (ringote.equals("a1")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm1);
            } else if (ringote.equals("a2")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm2);
            } else if (ringote.equals("be")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep);
            } else if (ringote.equals("nu")) {
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.nuclear);
            } else {
                //Si no hay ninguna guardada o esta la default coge la que tiene el telefono predeterminada
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            //Preparo una vibracion ascendiente
            long [] vibrate = {0, 1000, 2000, 3000};

            //Preparo un bitmap con la imagen de icono de alarma
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_alarma);

            Intent intent = new Intent("AlertListFragment");
            intent.putExtra("REFRESH", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Construyo la alerta
            //Le pongo el icono de alerta, tanto en pequeño como en grande
            //Le pongo el titulo y el cuerpo que le paso como parametros a la funcion
            //Le pongo la vibracion
            //Le pongo la visibilidad
            //Le pongo el sonido de la notificacion
            //Le pongo las luces de la notificacion
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.imgcircle)
                            .setLargeIcon(image)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setVibrate(vibrate)
                            .addAction(0, "", pendingIntent)
                            .setVisibility(VISIBILITY_PUBLIC)
                            .setSound(sound)
                            .setLights(0xFFFF0000, 300, 100);

            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = mBuilder.build();
            //Le pongo la flag de inssitent para que se repita hasta que el usuario la vea
            if (alert)
                notification.flags = Notification.FLAG_INSISTENT;

            //Muestro la notificacion
            mNotificationManager.notify(0, notification);
        }

        if (alert) {
            //Mando un mensaje de broadcast para que, desde el main, avise que hay una alerta nueva
            Intent intent = new Intent("MainActivity");
            intent.putExtra("NOTIFICATION", true);
            intent.putExtra("CHANGE_TITLE", false);
            //send broadcast
            getApplicationContext().sendBroadcast(intent);
        }
    }

}
