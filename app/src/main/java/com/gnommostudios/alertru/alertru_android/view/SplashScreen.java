package com.gnommostudios.alertru.alertru_android.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.model.Doctor;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    private ImageView imageSplash;
    private SharedPreferences prefs;
    private String stateLog;

    private AVLoadingIndicatorView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        stateLog = prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

        imageSplash = (ImageView) findViewById(R.id.splashscreen_image);
        loader = (AVLoadingIndicatorView) findViewById(R.id.loading_splash);

        /*ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(SPLASH_DISPLAY_LENGTH);
        imageSplash.startAnimation(scaleAnimation);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stateLog.equals(StatesLog.LOGGED)) {
                    LoginAsyncTask lat = new LoginAsyncTask();
                    lat.execute();
                }else {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    mainIntent.putExtra("PAGE", 1);
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

                URL urlSelect = new URL(Urls.SELECT_ID + userId + "?access_token=" + access_token);

                HttpURLConnection conSelect = (HttpURLConnection) urlSelect.openConnection();
                conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                conSelect.setConnectTimeout(Urls.TIMEOUT);
                conSelect.connect();

                int respuestaSelect = conSelect.getResponseCode();
                Log.i("EE", conSelect.getResponseCode() + "");
                StringBuilder resultSelect = new StringBuilder();

                if (respuestaSelect == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conSelect.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String lineSelect;

                    while ((lineSelect = reader.readLine()) != null) {
                        resultSelect.append(lineSelect);
                    }

                    JSONObject respuestaJSONSelect = new JSONObject(resultSelect.toString());

                    String name = respuestaJSONSelect.getString("name");
                    String surname = respuestaJSONSelect.getString("surname");
                    String username = respuestaJSONSelect.getString("username");
                    String email = respuestaJSONSelect.getString("email");
                    String id = respuestaJSONSelect.getString("id");
                    String province = respuestaJSONSelect.getString("province");

                    Doctor doctor = new Doctor(name, surname, username, email, id, province);

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED);
                    editor.putString("name", doctor.getName());
                    editor.putString("surname", doctor.getSurname());
                    editor.putString("email", doctor.getEmail());
                    editor.putString("id", doctor.getId());
                    editor.putString("province", doctor.getProvince());
                    editor.putString("access_token", access_token);

                    editor.commit();

                    return 1;
                } else {
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

                    editor.commit();
                    return 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                return 3;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //SharedPreferences.Editor editor = prefs.edit();
            //editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);
            //editor.commit();

            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);

            switch (integer) {
                case 0:
                    //Toast.makeText(getApplicationContext(), "Login Fallido", Toast.LENGTH_SHORT).show();
                    mainIntent.putExtra("PAGE", 1);
                    break;
                case 1:
                    mainIntent.putExtra("PAGE", 2);
                    break;
                case 2:
                    //Toast.makeText(getApplicationContext(), "Error Desconocido", Toast.LENGTH_SHORT).show();
                    mainIntent.putExtra("PAGE", 1);
                    break;
                case 3:
                    mainIntent.putExtra("PAGE", 2);
                    break;
            }

            SplashScreen.this.startActivity(mainIntent);
            SplashScreen.this.finish();
        }
    }
}
