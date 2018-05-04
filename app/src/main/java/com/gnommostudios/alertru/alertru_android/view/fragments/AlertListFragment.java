package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
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

public class AlertListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, FABProgressListener {

    private ArrayList<Alert> alertArrayList;

    private ConstraintLayout containerList;
    private LinearLayout layoutDisconnected;

    private SharedPreferences prefs;

    private ListView alertList;
    private SwipeRefreshLayout refresh;

    private AVLoadingIndicatorView loader;

    //Details
    private LinearLayout layoutDetail;

    private ConstraintLayout containerAssigned;
    private ConstraintLayout containerUnassigned;
    private ConstraintLayout containerAssignedOwner;

    private Alert alertDetail;

    //Assigned
    private TextView dateDetailAssigned;
    private TextView ownerDetail;
    private TextView provinceDetailAssigned;
    private TextView titleDetailAssigned;

    //Unassigned
    private TextView dateDetailUnassigned;
    private TextView provinceDetailUnassigned;
    private TextView titleDetailUnassigned;

    //Assigned owner
    private TextView dateDetailAssignedOwner;
    private TextView ownerDetailOwner;
    private TextView provinceDetailAssignedOwner;
    private TextView titleDetailAssignedOwner;
    private TextView partTextView;
    private EditText editTextPart;
    private CardView cardCloseAlert;
    private Button closeAlert;

    private FloatingActionButton assingFAB;
    private FABProgressCircle fabProgressCircle;

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
        containerList.setVisibility(View.VISIBLE);

        /*****************************Details*****************************/
        layoutDetail = (LinearLayout) view.findViewById(R.id.layout_detail);
        layoutDetail.setVisibility(View.GONE);

        containerAssigned = (ConstraintLayout) view.findViewById(R.id.container_assigned);
        containerAssigned.setVisibility(View.GONE);

        containerUnassigned = (ConstraintLayout) view.findViewById(R.id.container_unassigned);
        containerUnassigned.setVisibility(View.GONE);

        containerAssignedOwner = (ConstraintLayout) view.findViewById(R.id.container_assigned_owner);
        containerAssignedOwner.setVisibility(View.GONE);

        /******Assigned*****/
        dateDetailAssigned = (TextView) view.findViewById(R.id.date_detail_assigned);
        ownerDetail = (TextView) view.findViewById(R.id.owner_detail);
        provinceDetailAssigned = (TextView) view.findViewById(R.id.province_detail_assigned);
        titleDetailAssigned = (TextView) view.findViewById(R.id.title_detail_assigned);

        /*****Unassigned****/
        dateDetailUnassigned = (TextView) view.findViewById(R.id.date_detail_unassigned);
        provinceDetailUnassigned = (TextView) view.findViewById(R.id.province_detail_unassigned);
        titleDetailUnassigned = (TextView) view.findViewById(R.id.title_detail_unassigned);

        /******Assigned Owner*****/
        dateDetailAssignedOwner = (TextView) view.findViewById(R.id.date_detail_assigned_owner);
        ownerDetailOwner = (TextView) view.findViewById(R.id.owner_detail_owner);
        provinceDetailAssignedOwner = (TextView) view.findViewById(R.id.province_detail_assigned_owner);
        titleDetailAssignedOwner = (TextView) view.findViewById(R.id.title_detail_assigned_owner);

        /**Finalized**/
        partTextView = (TextView) view.findViewById(R.id.partTextView);
        editTextPart = (EditText) view.findViewById(R.id.editText_part);

        cardCloseAlert = (CardView) view.findViewById(R.id.card_close_alert);
        closeAlert = (Button) view.findViewById(R.id.close_alert);

        assingFAB = (FloatingActionButton) view.findViewById(R.id.assign_fab);
        assingFAB.setOnClickListener(this);

        fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle);
        fabProgressCircle.attachListener(this);

        /***************************************************************/

        layoutDisconnected = (LinearLayout) view.findViewById(R.id.layout_disconnected);
        layoutDisconnected.setVisibility(View.GONE);
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
        intent.putExtra("CHANGE_TITLE", false);
        //send broadcast
        getActivity().sendBroadcast(intent);

        layoutDetail.setVisibility(View.GONE);

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
        alertList.setOnItemLongClickListener(this);
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
            case R.id.assign_fab:
                AssignDetailsAlertAsyncTask adaat = new AssignDetailsAlertAsyncTask();
                adaat.execute(alertDetail);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        showAssingDetails(alertArrayList.get(pos));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
        showAssingDialog(alertArrayList.get(pos));
        return true;
    }

    private void showAssingDetails(Alert alert) {
        Intent intent = new Intent("MainActivity");
        intent.putExtra("CHANGE_TITLE", true);
        intent.putExtra("TITLE", "Detalles");
        //send broadcast
        getActivity().sendBroadcast(intent);

        alertDetail = alert;

        SpannableStringBuilder builder;

        String cabecera;
        SpannableString cabeceraSpannable;

        String contenido;
        SpannableString contenidoSpannable;

        if (alert.isAssigned()) {
            if (!alert.getIdTechnician().equals(prefs.getString("id", ""))) {
                dateDetailAssigned.setText(alert.getDate());

                /*****Technician*****/
                builder = new SpannableStringBuilder();

                cabecera = "Técnico: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getIdTechnician();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                ownerDetail.setText(builder, TextView.BufferType.SPANNABLE);

                /*****Province*****/
                builder = new SpannableStringBuilder();

                cabecera = "Provincia: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                provinceDetailAssigned.setText(builder, TextView.BufferType.SPANNABLE);

                /*****Title*****/
                builder = new SpannableStringBuilder();

                cabecera = "Asunto Alerta: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getAffair();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                titleDetailAssigned.setText(builder, TextView.BufferType.SPANNABLE);

                layoutDetail.setVisibility(View.VISIBLE);
                containerAssigned.setVisibility(View.VISIBLE);
                containerList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.GONE);
            } else {
                dateDetailAssignedOwner.setText(alert.getDate());

                /*****Technician*****/
                builder = new SpannableStringBuilder();

                cabecera = "Técnico: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getIdTechnician();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                ownerDetailOwner.setText(builder, TextView.BufferType.SPANNABLE);

                /*****Province*****/
                builder = new SpannableStringBuilder();

                cabecera = "Provincia: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                provinceDetailAssignedOwner.setText(builder, TextView.BufferType.SPANNABLE);

                /*****Title*****/
                builder = new SpannableStringBuilder();

                cabecera = "Asunto Alerta: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getAffair();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                titleDetailAssignedOwner.setText(builder, TextView.BufferType.SPANNABLE);

                layoutDetail.setVisibility(View.VISIBLE);
                containerAssigned.setVisibility(View.GONE);
                containerList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.VISIBLE);


                ///////////////if (finalized == true) tambien mostrar o no
                /*****Part*****/
                builder = new SpannableStringBuilder();

                cabecera = "Parte de la incidencia: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = "";
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                partTextView.setText(builder, TextView.BufferType.SPANNABLE);
            }
        } else {
            dateDetailUnassigned.setText(alert.getDate());

            /*****Province*****/
            builder = new SpannableStringBuilder();

            cabecera = "Provincia: ";
            cabeceraSpannable = new SpannableString(cabecera);
            cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
            builder.append(cabeceraSpannable);

            contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());
            contenidoSpannable = new SpannableString(contenido);
            contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
            builder.append(contenidoSpannable);

            provinceDetailUnassigned.setText(builder, TextView.BufferType.SPANNABLE);

            /*****Title*****/
            builder = new SpannableStringBuilder();

            cabecera = "Asunto Alerta: ";
            cabeceraSpannable = new SpannableString(cabecera);
            cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
            builder.append(cabeceraSpannable);

            contenido = alert.getAffair();
            contenidoSpannable = new SpannableString(contenido);
            contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
            builder.append(contenidoSpannable);

            titleDetailUnassigned.setText(builder, TextView.BufferType.SPANNABLE);

            layoutDetail.setVisibility(View.VISIBLE);
            containerAssigned.setVisibility(View.GONE);
            containerList.setVisibility(View.GONE);
            containerUnassigned.setVisibility(View.VISIBLE);
            containerAssignedOwner.setVisibility(View.GONE);
        }

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
            if (alert.getIdTechnician().equals(prefs.getString("id", ""))) {
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

    @Override
    public void onFABProgressAnimationEnd() {
        fabProgressCircle.hide();

        Snackbar.make(fabProgressCircle, "Alerta Asignada", Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .show();


        Intent intent = new Intent("MainActivity");
        intent.putExtra("CHANGE_TITLE", true);
        intent.putExtra("TITLE", "Incidencias");
        //send broadcast
        getActivity().sendBroadcast(intent);

        initList();
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
                URL url = new URL(Urls.GET_ALERT_LIST + prefs.getString("province", "").toLowerCase() +
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
                        String province = alert.getString("province");
                        Date d = new Date(Long.parseLong(alert.getString("date")));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String date = sdf.format(d);
                        boolean assigned = alert.getBoolean("assigned");

                        if (!assigned) {
                            alertArrayList.add(new Alert(id, affair, province, date, assigned));
                        } else {
                            String idTechnician = alert.getString("owner");
                            alertArrayList.add(new Alert(id, affair, province, date, idTechnician, assigned));
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

    class AssignDetailsAlertAsyncTask extends AsyncTask<Alert, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            fabProgressCircle.show();
        }

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
                fabProgressCircle.beginFinalAnimation();
                //initList();
            }
        }
    }
}

