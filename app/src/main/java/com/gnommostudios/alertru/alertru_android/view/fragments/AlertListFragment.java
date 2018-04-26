package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList;
import com.gnommostudios.alertru.alertru_android.model.Alert;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AlertListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Alert> alertArrayList;

    private SharedPreferences prefs;

    private ListView alertList;
    private SwipeRefreshLayout refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_list, container, false);

        prefs = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        alertList = (ListView) view.findViewById(R.id.alert_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refesh_layout);
        refresh.setOnRefreshListener(this);

        initList();

        return view;
    }

    private void initList() {
        alertArrayList = new ArrayList<>();

        if (prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED).equals(StatesLog.LOGGED)) {
            //alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));
            //alertArrayList.add(new Alert("¡Adios!", "00-00-0000", false));
            AlertListAsyncTask alat = new AlertListAsyncTask();
            alat.execute();
        }

        alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
    }

    @Override
    public void onRefresh() {
        initList();
        refresh.setRefreshing(false);
    }

    class AlertListAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(Urls.GET_ALERT_LIST + prefs.getString("province", "") +
                        "?access_token=" + prefs.getString("access_token", ""));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                connection.connect();

                int respuesta = connection.getResponseCode();

                Log.i("EEE", "Llego aqui");
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    String line;

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    connection.disconnect();

                    while ((line = br.readLine()) != null) {
                        Log.i("Line", line);
                        result.append(line);
                    }

                    JSONObject jsonObject = new JSONObject(result.toString());

                    Log.i("OBJECT", jsonObject.toString());

                    //JSONArray jsonArray = new JSONArray("[" + result.toString().substring(1,result.toString().length()-2) + "]");

                    //JSONArray jsonArray = new JSONArray(result.toString());

                    //Log.i("ARRAY", jsonArray.toString());

                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean correct) {
            super.onPostExecute(correct);

            if (correct) {
                Toast.makeText(getActivity(), "Correcto.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Incorrecto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
