package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.model.Doctor;
import com.gnommostudios.alertru.alertru_android.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataUserFragment extends Fragment implements View.OnClickListener {

    private EditText txtName;
    private EditText txtPassword;

    private Button buttonLogin;

    private String email, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_user, container, false);

        txtName = (EditText) view.findViewById(R.id.txtName);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);

        buttonLogin = (Button) view.findViewById(R.id.btnRegister);
        buttonLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                login();
                break;
        }
    }

    private void login() {
        if (txtName.getText().length() == 0 || txtPassword.getText().length() == 0) {
            Toast.makeText(getActivity(), "Almenos hay que llenar el Nombre (con un correo) y la password", Toast.LENGTH_SHORT).show();
        } else {
            email = txtName.getText().toString();
            password = txtPassword.getText().toString();

            LoginAsyncTask lat = new LoginAsyncTask();
            lat.execute(email, password);
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
                    break;
                case 2:
                    Toast.makeText(getActivity(), "Error Desconocido", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
