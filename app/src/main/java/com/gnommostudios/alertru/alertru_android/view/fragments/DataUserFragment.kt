package com.gnommostudios.alertru.alertru_android.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.model.Technician
import com.gnommostudios.alertru.alertru_android.util.StatesLog
import com.gnommostudios.alertru.alertru_android.util.Urls
import com.google.firebase.messaging.FirebaseMessaging
import com.victor.loading.rotate.RotateLoading

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DataUserFragment : Fragment(), View.OnClickListener {

    private var txtEmail: EditText? = null
    private var txtPassword: EditText? = null

    private var buttonLogin: Button? = null
    private var buttonLogout: Button? = null

    private var nameLogged: TextView? = null
    private var emailLogged: TextView? = null
    private var userNameLogged: TextView? = null
    private var provinceLogged: TextView? = null

    private var layoutLogin: CardView? = null
    private var layoutLogout: CardView? = null

    private var email: String? = null
    private var password: String? = null

    private var prefs: SharedPreferences? = null
    private var stateLog: String? = null

    private var loader: RotateLoading? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_data_user, container, false)

        prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE)

        loader = view.findViewById<View>(R.id.newton_cradle_loading) as RotateLoading

        layoutLogin = view.findViewById<View>(R.id.layout_login) as CardView
        layoutLogout = view.findViewById<View>(R.id.layout_logout) as CardView

        nameLogged = view.findViewById<View>(R.id.nameLogout) as TextView
        emailLogged = view.findViewById<View>(R.id.emailLogout) as TextView
        userNameLogged = view.findViewById<View>(R.id.userNameLogout) as TextView
        provinceLogged = view.findViewById<View>(R.id.provinceLogout) as TextView

        txtEmail = view.findViewById<View>(R.id.txtEmail) as EditText
        txtPassword = view.findViewById<View>(R.id.txtPassword) as EditText

        buttonLogin = view.findViewById<View>(R.id.btnLogin) as Button
        buttonLogout = view.findViewById<View>(R.id.btnLogout) as Button
        buttonLogin!!.setOnClickListener(this)
        buttonLogout!!.setOnClickListener(this)

        changeStateVisibility()

        return view
    }

    //Dependiendo del estado de conexion, cambia la visibilidad de los componentes
    private fun changeStateVisibility() {
        stateLog = prefs!!.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED)
        val intent = Intent("MainActivity")

        when (stateLog) {
            StatesLog.LOGGED -> {
                layoutLogout!!.visibility = View.VISIBLE
                layoutLogin!!.visibility = View.GONE

                nameLogged!!.text = prefs!!.getString("surname", "") + ", " + prefs!!.getString("name", "")
                emailLogged!!.text = prefs!!.getString("email", "")
                userNameLogged!!.text = prefs!!.getString("username", "")
                provinceLogged!!.text = prefs!!.getString("province", "")
                //Mando un mensaje de broadcast para que desde el MainActivity cambie el titulo
                intent.putExtra("CHANGE_TITLE", true)
                intent.putExtra("REFRESH", false)
                intent.putExtra("IS_MAIN", false)
                intent.putExtra("TITLE", "Datos de usuario")
                //send broadcast
                activity.sendBroadcast(intent)
            }
            StatesLog.DISCONNECTED -> {
                txtEmail!!.setText("")
                txtPassword!!.setText("")
                layoutLogout!!.visibility = View.GONE
                layoutLogin!!.visibility = View.VISIBLE
                //Mando un mensaje de broadcast para que desde el MainActivity cambie el titulo
                intent.putExtra("CHANGE_TITLE", true)
                intent.putExtra("REFRESH", false)
                intent.putExtra("IS_MAIN", false)
                intent.putExtra("TITLE", "Iniciar Sesión")
                //send broadcast
                activity.sendBroadcast(intent)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogin -> login()
            R.id.btnLogout -> logout()
        }
    }

    //Cambia el estado de conexion en las preferencias
    private fun changeState(state: String) {
        val editor = prefs!!.edit()

        editor.putString(StatesLog.STATE_LOG, state)

        editor.commit()
        //Llamo a la funcion para cambiar la visibilidad de los componentes
        changeStateVisibility()
    }

    //Llamo a la asincrona que me desloguea
    private fun logout() {
        val logoutAsyncTask = LogoutAsyncTask()
        logoutAsyncTask.execute()
    }

    private fun login() {
        //Controlo que se haya introducido un correo/username y una password
        if (txtEmail!!.text.isEmpty() || txtPassword!!.text.isEmpty()) {
            Toast.makeText(activity, "Indique el usuario y la contraseña.", Toast.LENGTH_SHORT).show()
        } else {
            email = txtEmail!!.text.toString()
            password = txtPassword!!.text.toString()

            val loginAsyncTask = LoginAsyncTask()
            //Si el primer campo es un correo (contiene una @, ya que en el servicio los username no pueden llevar @),
            //llamo a la asincrona pasandole el indicativo de que se esta iniciando con email,
            //si no contiene @, le indico que es username
            if (email!!.contains("@"))
                loginAsyncTask.execute(email, password, "email")
            else
                loginAsyncTask.execute(email, password, "username")
        }
    }

    //AsyncTask para logearse
    internal inner class LoginAsyncTask : AsyncTask<String, Int, Int>() {

        override fun onPreExecute() {
            super.onPreExecute()
            layoutLogin!!.visibility = View.GONE
            loader!!.visibility = View.VISIBLE
            loader!!.start()

        }

        override fun doInBackground(vararg strings: String): Int? {
            try {
                val url = URL(Urls.LOGIN)

                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                //Log.i("URL", connection.getURL().toString());
                //Log.i("REQUEST", connection.getRequestMethod() + " " + connection.getRequestProperties().toString());
                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()


                val jsonParam = JSONObject()
                //Le paso los parametros para decirle si es email o username
                jsonParam.put(strings[2], strings[0])
                jsonParam.put("password", strings[1])

                //Log.i("JSON", jsonParam.toString());

                val os = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))

                writer.write(jsonParam.toString())

                writer.flush()
                writer.close()

                val respuesta = connection.responseCode
                val result = StringBuilder()

                //Si el login me responde ok, paso a recojer la id y el access_token y busco los datos del usuario por la id
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    //Log.i("RESPONSE", HttpURLConnection.HTTP_OK + "");

                    val br = BufferedReader(InputStreamReader(connection.inputStream))

                    connection.disconnect()

                    result.append(br.readLine())

                    val respuestaJSON = JSONObject(result.toString())

                    val userIdJSON = respuestaJSON.getString("userId")
                    val access_token = respuestaJSON.getString("id")

                    val urlSelect = URL(Urls.SELECT_ID + userIdJSON + "?access_token=" + access_token)

                    val conSelect = urlSelect.openConnection() as HttpURLConnection
                    conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP")

                    conSelect.connectTimeout = Urls.TIMEOUT
                    conSelect.connect()

                    val respuestaSelect = conSelect.responseCode
                    //Log.i("EE", conSelect.getResponseCode() + "");
                    val resultSelect = StringBuilder()

                    if (respuestaSelect == HttpURLConnection.HTTP_OK) {
                        //Log.i("EE", "Llego aqui");
                        val inputStream = BufferedInputStream(conSelect.inputStream)

                        val reader = BufferedReader(InputStreamReader(inputStream))

                        resultSelect.append(reader.readLine())

                        val respuestaJSONSelect = JSONObject(resultSelect.toString())

                        val name = respuestaJSONSelect.getString("name")
                        val surname = respuestaJSONSelect.getString("surname")
                        val username = respuestaJSONSelect.getString("username")
                        val email = respuestaJSONSelect.getString("email")
                        val id = respuestaJSONSelect.getString("id")
                        val province = respuestaJSONSelect.getString("province")

                        val technician = Technician(name, surname, username, email, id, province)

                        //Me guardo los atributos del tecnico recogido por la consulta por id, me guardo el estado de logeado
                        //y el access_token en las preferencias
                        val editor = prefs!!.edit()

                        editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED)
                        editor.putString("name", technician.name)
                        editor.putString("surname", technician.surname)
                        editor.putString("username", technician.username)
                        editor.putString("email", technician.email)
                        editor.putString("id", technician.id)
                        editor.putString("province", technician.province)
                        editor.putString("access_token", access_token)

                        editor.commit()

                        //Log.i("TECHNICIAN", technician.toString());
                        //Log.i("TOKEN", access_token);

                        return 1
                    }
                } else {
                    return 0
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return 2
        }

        override fun onPostExecute(integer: Int?) {
            loader!!.stop()
            loader!!.visibility = View.GONE

            when (integer) {
            //Si me devuelve 0 es que alguno de los dos campos no es correcto
                0 -> {
                    Toast.makeText(activity, "Usuario y/o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                    layoutLogin!!.visibility = View.VISIBLE
                }
            //Si me devuelve 1 es que el login es correcto
                1 -> {
                    //Toast.makeText(getActivity(), "Correcto", Toast.LENGTH_SHORT).show();
                    //Me suscribo al tema de la provincia del usuario para que me lleguen notificaciones de Firebase
                    FirebaseMessaging.getInstance().subscribeToTopic(prefs!!.getString("province", "")!!)
                    //Cambio la visibilidad de los elementos para que se muestre los datos del usuario y la opcion de desloguearse
                    changeStateVisibility()
                }
            //Hay algun error de conexion desconocido
                2 ->
                    //Toast.makeText(getActivity(), "Error Desconocido", Toast.LENGTH_SHORT).show();
                    layoutLogin!!.visibility = View.VISIBLE
            }
        }
    }

    //AsyncTask para desloguearse
    internal inner class LogoutAsyncTask : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            layoutLogout!!.visibility = View.GONE
            loader!!.visibility = View.VISIBLE
            loader!!.start()

        }

        override fun doInBackground(vararg strings: String): Boolean? {
            try {
                val url = URL(Urls.LOGOUT + prefs!!.getString("access_token", "")!!)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT

                connection.connect()

                val respuesta = connection.responseCode

                if (respuesta == HttpURLConnection.HTTP_NO_CONTENT) {
                    return true
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(correct: Boolean?) {
            super.onPostExecute(correct)
            loader!!.stop()
            loader!!.visibility = View.GONE

            //Me desuscribo del tema de la provincia para que no me lleguen notificaciones de Firebase
            FirebaseMessaging.getInstance().unsubscribeFromTopic(prefs!!.getString("province", "")!!)

            //Cambio el estado a desconectado
            changeState(StatesLog.DISCONNECTED)
        }
    }

}
