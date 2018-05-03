package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList;
import com.gnommostudios.alertru.alertru_android.model.Alert;
import com.gnommostudios.alertru.alertru_android.util.AuthenticationDialog;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;
import com.gnommostudios.alertru.alertru_android.util.Urls;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlertListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private ArrayList<Alert> alertArrayList;

    private ConstraintLayout containerList;
    private LinearLayout layoutDisconnected;

    private SharedPreferences prefs;

    private ListView alertList;
    private SwipeRefreshLayout refresh;

    private AVLoadingIndicatorView loader;

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

        containerList = (ConstraintLayout) view.findViewById(R.id.container_list);
        layoutDisconnected = (LinearLayout) view.findViewById(R.id.layout_disconnected);
        layoutDisconnected.setOnClickListener(this);

        loader = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        //loader.hide();

        alertList = (ListView) view.findViewById(R.id.alert_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refesh_layout);
        refresh.setOnRefreshListener(this);

        initList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "Hola", Toast.LENGTH_SHORT).show();
    }

    public void initList() {
        alertArrayList = new ArrayList<>();

        Intent intent = new Intent("MainActivity");
        intent.putExtra("NOTIFICATION", false);
        //send broadcast
        getActivity().sendBroadcast(intent);

        if (prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED).equals(StatesLog.LOGGED)) {
            containerList.setVisibility(View.VISIBLE);
            layoutDisconnected.setVisibility(View.GONE);
            AlertListAsyncTask alat = new AlertListAsyncTask();
            alat.execute();
        } else {
            containerList.setVisibility(View.GONE);
            layoutDisconnected.setVisibility(View.VISIBLE);
        }
    }

    public void setAdapter() {
        alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
        alertList.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        initList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_disconnected:
                initList();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        showAssingDialog(alertArrayList.get(pos));
    }

    private void showAssingDialog(final Alert alert) {
        AlertDialog.Builder alerta =
                new AlertDialog.Builder(getContext());
        if (!alert.isAssigned()) {
            alerta.setMessage("Asunto: " + alert.getAffair() + "\nFecha: " + alert.getDate())
                    .setTitle(R.string.titleAssign)
                    .setPositiveButton(R.string.assign, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AssignAlertAsyncTask aaat = new AssignAlertAsyncTask();
                            aaat.execute(alert);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        } else {
            if (alert.getIdDoctor().equals(prefs.getString("id", ""))) {
                alerta.setMessage("Asunto: " + alert.getAffair() + "\nFecha: " + alert.getDate())
                        .setTitle(R.string.titleAssignedYou)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );
            } else {
                alerta.setMessage("Asunto: " + alert.getAffair() + "\nFecha: " + alert.getDate())
                        .setTitle(R.string.titleAssigned)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );
            }
        }
        alerta.show();
    }

    class AlertListAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!refresh.isRefreshing())
                loader.setVisibility(View.VISIBLE);
        }

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

                connection.setConnectTimeout(Urls.TIMEOUT_LONG);
                connection.connect();


                int respuesta = connection.getResponseCode();

                Log.i("EEE", "Llego aqui");
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    String line;

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    connection.disconnect();

                    while ((line = br.readLine()) != null) {
                        //Log.i("Line", line);
                        result.append(line);
                    }

                    //JSONObject jsonObject = new JSONObject(result.toString());
                    JSONArray alerts = new JSONArray(result.toString());

                    for (int i = 0; i < alerts.length(); i++) {
                        //Log.i("ALERT", alerts.get(i).toString());
                        JSONObject alert = (JSONObject) alerts.get(i);

                        String id = alert.getString("id");
                        String affair = alert.getString("title");
                        Date d = new Date(Long.parseLong(alert.getString("date")));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String date = sdf.format(d);
                        boolean assigned = alert.getBoolean("assigned");

                        if (!assigned) {
                            alertArrayList.add(new Alert(id, affair, date, assigned));
                        } else {
                            String idDoctor = alert.getString("owner");
                            alertArrayList.add(new Alert(id, affair, date, idDoctor, assigned));
                        }

                    }

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

            loader.setVisibility(View.GONE);
            refresh.setRefreshing(false);

            if (correct) {
                setAdapter();
            } else {
                AuthenticationDialog dialog = new AuthenticationDialog();
                dialog.show(getFragmentManager(), "CONNECTION_ERROR");
                setAdapter();
            }
        }
    }

    class AssignAlertAsyncTask extends AsyncTask<Alert, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Alert... alertsParams) {
            try {
                URL url = new URL(Urls.ASSIGN_ALERT + prefs.getString("id", "") + "/assign-alert/" +
                        alertsParams[0].getId() + "?access_token=" + prefs.getString("access_token", ""));

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

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    return true;
                }

                return false;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean updated) {
            super.onPostExecute(updated);

            if (updated) {
                initList();
            }
        }
    }
}

