package com.gnommostudios.alertru.alertru_android.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri

import com.gnommostudios.alertru.alertru_android.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var prefs: SharedPreferences? = null

    override fun handleIntent(intent: Intent) {
        //Log.i("HANDLE-INTENT", intent.getAction());

        //Sobreescribo este metodo para controlar tambien cuando esta la pantalla bloqueada

        //Si el action del intent es el de recibir un mensaje llamo a mi funcion
        //para mostrar una notificacion pasandole el titulo, el cuerpo, si es una alerta y el collapsekey.
        //Si no estoy recibiendo nada llamo a super para que haga sus gestiones
        //en los casos que no me interesan para las notificaciones
        if (intent.action == "com.google.android.c2dm.intent.RECEIVE") {
            val bundle = intent.extras

            //Log.i("BUNDLE", bundle.toString());

            showNotification(bundle!!.getString("title"), bundle.getString("body"), bundle.get("alert") == "true", bundle.getString("collapse_key"))
        } else {
            super.handleIntent(intent)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        showNotification(remoteMessage!!.data["title"], remoteMessage.data["body"], remoteMessage.data["alert"] == "true", remoteMessage.collapseKey)
    }

    private fun showNotification(title: String?, body: String?, alert: Boolean, collapseKeyString: String?) {
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        val activateAlert = prefs!!.getBoolean("activateAlert", true)

        //Compruebo si estan activadas las alertas en preferencias
        if (activateAlert) {
            //Si estan activadas recojo el valor de ringote que me dice que alerta quiere que suene el usuario
            val ringote = prefs!!.getString("ringote", "")
            val sound: Uri

            //Dependiendo de el valor de ringote creo una Uri de la alerta o otra
            if (ringote == "a1") {
                sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm1)
            } else if (ringote == "a2") {
                sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm2)
            } else if (ringote == "be") {
                sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.beep)
            } else if (ringote == "nu") {
                sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.nuclear)
            } else {
                //Si no hay ninguna guardada o esta la default coge la que tiene el telefono predeterminada
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            //Preparo una vibracion ascendiente
            val vibrate = longArrayOf(0, 1000, 2000, 3000)

            //Preparo un bitmap con la imagen de icono de alarma
            val image = BitmapFactory.decodeResource(resources, R.drawable.alert_notification)

            //Preparo el intent que ara al pulsar la notificacion
            val intent = Intent("MainActivity")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("CHANGE_TITLE", true)
            intent.putExtra("REFRESH", true)

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

            //Construyo la alerta
            //Le pongo el icono de alerta, tanto en pequeño como en grande
            //Le pongo el titulo y el cuerpo que le paso como parametros a la funcion
            //Le pongo la vibracion
            //Le pongo la visibilidad
            //Le pongo el intent
            //Le pongo el sonido de la notificacion
            //Le pongo las luces de la notificacion
            val mBuilder = Notification.Builder(this)
                    .setSmallIcon(R.drawable.alertru_azul500)
                    .setLargeIcon(image)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(vibrate)
                    .setContentIntent(pendingIntent)
                    .setSound(sound)
                    .setLights(-0x10000, 300, 100)

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = Notification.BigTextStyle(mBuilder)
                    .bigText(body).build()

            //Le pongo la flag de insitent para que se repita hasta que el usuario la vea
            //y autocancel paara que se elimine cuando se pulse encima
            if (alert) {
                notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL
            }

            val m = (java.lang.Long.parseLong(collapseKeyString) / 1000L % Integer.MAX_VALUE).toInt()

            //Muestro la notificacion con el collapsekey para que muestre notificaciones diferentes
            mNotificationManager.notify(m, notification)
        }

        if (alert) {
            //Mando un mensaje de broadcast para que, desde el main, avise que hay una alerta nueva
            val intent = Intent("MainActivity")
            intent.putExtra("NOTIFICATION", true)
            intent.putExtra("CHANGE_TITLE", false)
            //send broadcast
            applicationContext.sendBroadcast(intent)
        }
    }

}
