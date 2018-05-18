package com.gnommostudios.alertru.alertru_android.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.util.StatesLog
import com.gnommostudios.alertru.alertru_android.util.Urls
import com.google.firebase.messaging.FirebaseMessaging

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 500

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // finally change the color
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

            window.navigationBarColor = resources.getColor(R.color.colorPrimary)
        }

        Handler().postDelayed({
            //Llamo a la asincrona para que compruebe el token
            val lat = LoginAsyncTask()
            lat.execute()
        }, SPLASH_DISPLAY_LENGTH.toLong())

    }

    internal inner class LoginAsyncTask : AsyncTask<String, Int, Int>() {

        override fun doInBackground(vararg strings: String): Int? {
            try {
                Thread.sleep(2000)
                val userId = prefs!!.getString("id", "")
                val access_token = prefs!!.getString("access_token", "")

                val urlSelect = URL(Urls.CHECK_TOKEN + userId + "/accessTokens/" + access_token + "?access_token=" + access_token)
                //Log.i("URL", urlSelect.toString());

                val conSelect = urlSelect.openConnection() as HttpURLConnection
                conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP")

                conSelect.connectTimeout = Urls.TIMEOUT
                conSelect.connect()

                val respuestaSelect = conSelect.responseCode

                //Log.i("CODE", respuestaSelect+"");

                val resultSelect = StringBuilder()

                if (respuestaSelect == HttpURLConnection.HTTP_OK) {
                    val inputStream = BufferedInputStream(conSelect.inputStream)

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    resultSelect.append(reader.readLine())

                    val respuestaJSONSelect = JSONObject(resultSelect.toString())

                    val id = respuestaJSONSelect.getString("id")

                    //Guardo las variables recogidas del JSON en el fichero de preferencias,
                    //a parte me guardo el estado a logueado
                    val editor = prefs!!.edit()

                    editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED)

                    editor.putString("userId", id)
                    editor.putString("access_token", access_token)

                    editor.commit()

                    return 1
                } else {
                    //Si llega a aqui significa que el token no es valido, por lo tanto, me guardo en preferencias que esta deslogueado
                    val editor = prefs!!.edit()

                    editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED)

                    editor.commit()
                    return 0
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return 2
        }

        override fun onPostExecute(integer: Int?) {
            val mainIntent = Intent(this@SplashScreen, MainActivity::class.java)

            when (integer) {
                0 ->
                    //Si esta deslogueado paso el indice de la pagina del formulario para desloguearse
                    mainIntent.putExtra("PAGE", 0)
                1 -> {
                    //Si esta logueado paso el indice de la pagina donde esta la lista de las alertas activas
                    FirebaseMessaging.getInstance().subscribeToTopic(prefs!!.getString("province", "")!!)
                    mainIntent.putExtra("PAGE", 2)
                }
            }

            //Si no ha entrado a ninguna excepcion (no ha habido ningun fallo de conexion), inicio la aplicacion
            if (integer != 2) {
                this@SplashScreen.startActivity(mainIntent)
                this@SplashScreen.finish()
            } else {
                //Si hay algun problema de conexion cierro la aplicacion, no tiene sentido que entre sin conexion
                Toast.makeText(this@SplashScreen, "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show()
                this@SplashScreen.finish()
            }
        }
    }

}
