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
import com.gnommostudios.alertru.alertru_android.model.Doctor;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;

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

    private TextView registryOption;
    private TextView logginOption;

    private TextView emailLogged;
    private TextView provinceLogged;

    private LinearLayout layoutLogin;
    private LinearLayout layoutLogout;
    private LinearLayout layoutRegisty;

    private String email, password;

    private SharedPreferences prefs;
    private String stateLog;

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

        layoutLogin = (LinearLayout) view.findViewById(R.id.layout_login);
        layoutLogout = (LinearLayout) view.findViewById(R.id.layout_logout);
        layoutRegisty = (LinearLayout) view.findViewById(R.id.layout_registry);

        emailLogged = (TextView) view.findViewById(R.id.emailLogout);
        provinceLogged = (TextView) view.findViewById(R.id.provinceLogout);

        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);

        buttonLogin = (Button) view.findViewById(R.id.btnLogin);
        buttonLogout = (Button) view.findViewById(R.id.btnLogout);
        buttonLogin.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        registryOption = (TextView) view.findViewById(R.id.registry_option);
        logginOption = (TextView) view.findViewById(R.id.loggin_option);
        registryOption.setOnClickListener(this);
        logginOption.setOnClickListener(this);

        changeStateVisibility();

        return view;
    }

    private void changeStateVisibility() {
        stateLog = prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

        switch (stateLog) {
            case StatesLog.LOGGED:
                layoutLogout.setVisibility(View.VISIBLE);
                layoutLogin.setVisibility(View.GONE);
                layoutRegisty.setVisibility(View.GONE);
                emailLogged.setText(prefs.getString("email", ""));
                provinceLogged.setText(prefs.getString("province", ""));
                break;
            case StatesLog.DISCONNECTED:
                layoutLogout.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
                layoutRegisty.setVisibility(View.GONE);
                break;
            case StatesLog.CHECKING_IN:
                layoutLogout.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.GONE);
                layoutRegisty.setVisibility(View.VISIBLE);
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
            case R.id.registry_option:
                changeState(StatesLog.CHECKING_IN);
                break;
            case R.id.loggin_option:
                changeState(StatesLog.DISCONNECTED);
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
            Toast.makeText(getActivity(), "Almenos hay que llenar el Nombre (con un correo) y la password", Toast.LENGTH_SHORT).show();
        } else {
            email = txtEmail.getText().toString();
            password = txtPassword.getText().toString();

            LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
            loginAsyncTask.execute(email, password);
        }
    }

    class LoginAsyncTask extends AsyncTask<String, Integer, Integer> {

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
                connection.connect();


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", strings[0]);
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

                    JSONObject jsonSelect = new JSONObject();
                    jsonParam.put("id", userIdJSON);

                    URL urlSelect = new URL(Urls.SELECT_ID + jsonSelect.toString());

                    HttpURLConnection conSelect = (HttpURLConnection) urlSelect.openConnection();
                    conSelect.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                    conSelect.connect();

                    int respuestaSelect = conSelect.getResponseCode();
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
                        boolean emailVerified = respuestaJSONSelect.getBoolean("emailVerified");
                        String id = respuestaJSONSelect.getString("id");
                        String province = respuestaJSONSelect.getString("province");

                        Doctor doctor = new Doctor(name, surname, username, email, emailVerified, id, province);

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(StatesLog.STATE_LOG, StatesLog.LOGGED);
                        editor.putString("name", doctor.getName());
                        editor.putString("surname", doctor.getSurname());
                        editor.putString("email", doctor.getEmail());
                        editor.putBoolean("emailVerified", doctor.isEmailVerified());
                        editor.putString("id", doctor.getId());
                        editor.putString("province", doctor.getProvince());
                        editor.putString("access_token", access_token);

                        editor.commit();

                        Log.i("DOCTOR", doctor.toString());

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
            switch (integer) {
                case 0:
                    Toast.makeText(getActivity(), "Login Fallido", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "Correcto", Toast.LENGTH_SHORT).show();
                    changeStateVisibility();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "Error Desconocido", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    class LogoutAsyncTask extends AsyncTask<String, Void, Boolean> {

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

            if (correct) {
                Toast.makeText(getActivity(), "Hasta luego.", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED);

                editor.commit();
                changeState(StatesLog.DISCONNECTED);
            }else {
                Toast.makeText(getActivity(), "Hay algun problema, no te puedes desloguear.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
