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
import android.util.Log;

import com.gnommostudios.alertru.alertru_android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private SharedPreferences prefs;

    @Override
    public void handleIntent(Intent intent) {
        //Log.i("HANDLE-INTENT", intent.getAction());

        //Sobreescribo este metodo para controlar tambien cuando esta la pantalla bloqueada

        //Si el action del intent es el de recivir un mensaje llamo a mi funcion
        //para mostrar una notificacion pasandole el titulo, el cuerpo, si es una alerta y el collapsekey.
        //Si no estoy reciviendo nada llamo a super para que haga sus gestiones
        //en los casos que no me interesan para las notificaciones
        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            Bundle bundle = intent.getExtras();

            Log.i("BUNDLE", bundle.toString());

            showNotification(bundle.getString("title"), bundle.getString("body"), bundle.get("alert").equals("true"), bundle.getString("collapse_key"));
        } else {
            super.handleIntent(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("alert").equals("true"), remoteMessage.getCollapseKey());
    }

    private void showNotification(String title, String body, boolean alert, String collapseKeyString) {
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
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.alert_notification);

            Intent intent = new Intent("MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("CHANGE_TITLE", true);
            intent.putExtra("REFRESH", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);

            //Construyo la alerta
            //Le pongo el icono de alerta, tanto en pequeño como en grande
            //Le pongo el titulo y el cuerpo que le paso como parametros a la funcion
            //Le pongo la vibracion
            //Le pongo la visibilidad
            //Le pongo el intent
            //Le pongo el sonido de la notificacion
            //Le pongo las luces de la notificacion
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.drawable.alertru_azul500)
                            .setLargeIcon(image)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setVibrate(vibrate)
                            .setContentIntent(pendingIntent)
                            .setSound(sound)
                            .setLights(0xFFFF0000, 300, 100);

            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new Notification.BigTextStyle(mBuilder)
                    .bigText(body).build();

            //Le pongo la flag de insitent para que se repita hasta que el usuario la vea
            //y autocancel paara que se elimine cuando se pulse encima
            if (alert) {
                notification.flags = Notification.FLAG_INSISTENT|Notification.FLAG_AUTO_CANCEL;
            }

            int m = (int) ((Long.parseLong(collapseKeyString) / 1000L) % Integer.MAX_VALUE);

            //Muestro la notificacion con el collapsekey para que muestre notificaciones diferentes
            mNotificationManager.notify(m, notification);
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
