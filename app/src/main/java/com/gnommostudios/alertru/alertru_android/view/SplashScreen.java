package com.gnommostudios.alertru.alertru_android.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    private SharedPreferences prefs;
    private String stateLog;

    private AVLoadingIndicatorView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        stateLog = prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

        loader = (AVLoadingIndicatorView) findViewById(R.id.loading_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Si esta logeado en las preferencias compruebo el token
                if (stateLog.equals(StatesLog.LOGGED)) {
                    LoginAsyncTask lat = new LoginAsyncTask();
                    lat.execute();
                } else {
                    //Si esta deslogeado inicio la aplicacion pasandole la pagina 0,
                    //para que vaya al fragment donde esta el formulario para logearse,
                    //ya que si no esta loqueado no tiene ninguna funcionalidad ni puede ver las alertas
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    mainIntent.putExtra("PAGE", 0);

                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    class LoginAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                String userId = prefs.getString("id", "");
                String access_token = prefs.getString("access_token", "");

                URL urlSelect = new URL(Urls.CHECK_TOKEN + userId + "/accessTokens/" + access_token + "?access_token=" + access_token);
                //Log.i("URL", urlSelect.toString());

                HttpURLConnection conSelect = (HttpURLConnection) urlSelect.openConnection();
                conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                conSelect.setConnectTimeout(Urls.TIMEOUT);
                conSelect.connect();

                int respuestaSelect = conSelect.getResponseCode();

                //Log.i("CODE", respuestaSelect+"");

                StringBuilder resultSelect = new StringBuilder();

                if (respuestaSelect == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conSelect.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String lineSelect;

                    while ((lineSelect = reader.readLine()) != null) {
                        resultSelect.append(lineSelect);
                    }

                    JSONObject respuestaJSONSelect = new JSONObject(resultSelect.toString());

                    String id = respuestaJSONSelect.getString("id");

                    //Guardo las variables recogidas del JSON en el fichero de preferencias,
                    //a parte me guardo el estado a logueado
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED);

                    editor.putString("userId", id);
                    editor.putString("access_token", access_token);

                    editor.commit();

                    return 1;
                } else {
                    //Si llega a aqui significa que el token no es valido, por lo tanto, me guardo en preferencias que esta deslogueado
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

                    editor.commit();
                    return 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);

            switch (integer) {
                case 0:
                    //Si esta deslogeado paso el indice de la pagina del formulario para desloguearse
                    mainIntent.putExtra("PAGE", 0);
                    break;
                case 1:
                    //Si esta logueado paso el indice de la pagina donde esta la lista de las alertas activas
                    mainIntent.putExtra("PAGE", 2);
                    break;
            }

            //Si no ha entrado a ninguna excepcion (no ha habido ningun fallo de conexion), inicio la aplicacion
            if (integer != 2) {
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            } else {
                //Si hay algun problema de conexion cierro la aplicacion, no tiene sentido que entre sin conexion
                Toast.makeText(SplashScreen.this, "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show();
                SplashScreen.this.finish();
            }
        }
    }

}
