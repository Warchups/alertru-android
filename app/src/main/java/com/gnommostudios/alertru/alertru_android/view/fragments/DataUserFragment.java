package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.model.Technician;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;
import com.google.firebase.messaging.FirebaseMessaging;
import com.victor.loading.rotate.RotateLoading;

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
import java.net.URL;

public class DataUserFragment extends Fragment implements View.OnClickListener {

    private EditText txtEmail;
    private EditText txtPassword;

    private Button buttonLogin;
    private Button buttonLogout;

    private TextView nameLogged;
    private TextView emailLogged;
    private TextView userNameLogged;
    private TextView provinceLogged;

    private LinearLayout layoutLogin;
    private LinearLayout layoutLogout;

    private String email, password;

    private SharedPreferences prefs;
    private String stateLog;

    private RotateLoading loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_user, container, false);

        prefs = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        loader = (RotateLoading) view.findViewById(R.id.newton_cradle_loading);

        layoutLogin = (LinearLayout) view.findViewById(R.id.layout_login);
        layoutLogout = (LinearLayout) view.findViewById(R.id.layout_logout);

        nameLogged = (TextView) view.findViewById(R.id.nameLogout);
        emailLogged = (TextView) view.findViewById(R.id.emailLogout);
        userNameLogged = (TextView) view.findViewById(R.id.userNameLogout);
        provinceLogged = (TextView) view.findViewById(R.id.provinceLogout);

        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);

        buttonLogin = (Button) view.findViewById(R.id.btnLogin);
        buttonLogout = (Button) view.findViewById(R.id.btnLogout);
        buttonLogin.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        changeStateVisibility();

        return view;
    }

    private void changeStateVisibility() {
        stateLog = prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

        switch (stateLog) {
            case StatesLog.LOGGED:
                layoutLogout.setVisibility(View.VISIBLE);
                layoutLogin.setVisibility(View.GONE);

                nameLogged.setText(prefs.getString("surname", "") + ", " + prefs.getString("name", ""));
                emailLogged.setText(prefs.getString("email", ""));
                userNameLogged.setText(prefs.getString("username", ""));
                provinceLogged.setText(prefs.getString("province", ""));
                break;
            case StatesLog.DISCONNECTED:
                txtEmail.setText("");
                txtPassword.setText("");
                layoutLogout.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnLogout:
                logout();
                break;
        }
    }

    private void changeState(String state) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(StatesLog.STATE_LOG, state);

        editor.commit();
        changeStateVisibility();
    }

    private void logout() {
        LogoutAsyncTask logoutAsyncTask = new LogoutAsyncTask();
        logoutAsyncTask.execute();
    }

    private void login() {
        if (txtEmail.getText().length() == 0 || txtPassword.getText().length() == 0) {
            Toast.makeText(getActivity(), "Indique el usuario y la contraseña.", Toast.LENGTH_SHORT).show();
        } else {
            email = txtEmail.getText().toString();
            password = txtPassword.getText().toString();

            LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
            if (email.contains("@"))
                loginAsyncTask.execute(email, password, "email");
            else
                loginAsyncTask.execute(email, password, "username");
        }
    }

    class LoginAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutLogin.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            loader.start();

        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                URL url = new URL(Urls.LOGIN);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                Log.i("URL", connection.getURL().toString());
                Log.i("REQUEST", connection.getRequestMethod() + " " + connection.getRequestProperties().toString());
                connection.setConnectTimeout(Urls.TIMEOUT);
                connection.connect();


                JSONObject jsonParam = new JSONObject();
                jsonParam.put(strings[2], strings[0]);
                jsonParam.put("password", strings[1]);

                Log.i("JSON", jsonParam.toString());

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonParam.toString());

                writer.flush();
                writer.close();

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    Log.i("RESPONSE", HttpURLConnection.HTTP_OK + "");
                    String line;

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    connection.disconnect();

                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject respuestaJSON = new JSONObject(result.toString());

                    String userIdJSON = respuestaJSON.getString("userId");
                    String access_token = respuestaJSON.getString("id");

                    URL urlSelect = new URL(Urls.SELECT_ID + userIdJSON + "?access_token=" + access_token);

                    HttpURLConnection conSelect = (HttpURLConnection) urlSelect.openConnection();
                    conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                    conSelect.setConnectTimeout(Urls.TIMEOUT);
                    conSelect.connect();

                    int respuestaSelect = conSelect.getResponseCode();
                    Log.i("EE", conSelect.getResponseCode() + "");
                    StringBuilder resultSelect = new StringBuilder();

                    if (respuestaSelect == HttpURLConnection.HTTP_OK) {
                        Log.i("EE", "Llego aqui");
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

                        Technician technician = new Technician(name, surname, username, email, id, province);

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED);
                        editor.putString("name", technician.getName());
                        editor.putString("surname", technician.getSurname());
                        editor.putString("username", technician.getUsername());
                        editor.putString("email", technician.getEmail());
                        editor.putString("id", technician.getId());
                        editor.putString("province", technician.getProvince());
                        editor.putString("access_token", access_token);

                        editor.commit();

                        Log.i("TECHNICIAN", technician.toString());
                        Log.i("TOKEN", access_token);

                        return 1;
                    }
                } else {
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
            loader.stop();
            loader.setVisibility(View.GONE);
            switch (integer) {
                case 0:
                    Toast.makeText(getActivity(), "Usuario y/o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                    layoutLogin.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //Toast.makeText(getActivity(), "Correcto", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic(prefs.getString("province", ""));
                    changeStateVisibility();
                    break;
                case 2:
                    //Toast.makeText(getActivity(), "Error Desconocido", Toast.LENGTH_SHORT).show();
                    layoutLogin.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class LogoutAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutLogout.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            loader.start();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(Urls.LOGOUT + prefs.getString("access_token", ""));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                connection.setConnectTimeout(Urls.TIMEOUT);

                connection.connect();

                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_NO_CONTENT) {
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean correct) {
            super.onPostExecute(correct);
            loader.stop();
            loader.setVisibility(View.GONE);

            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

            editor.commit();

            FirebaseMessaging.getInstance().unsubscribeFromTopic(prefs.getString("province", ""));
            changeState(StatesLog.DISCONNECTED);
        }
    }

}
