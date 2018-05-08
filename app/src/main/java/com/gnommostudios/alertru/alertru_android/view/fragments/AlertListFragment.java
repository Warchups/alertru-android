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
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
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
    private TextView withoutAlerts;

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
    private TextView descriptionDetailAssigned;

    //Unassigned
    private TextView dateDetailUnassigned;
    private TextView provinceDetailUnassigned;
    private TextView titleDetailUnassigned;
    private TextView descriptionDetailUnassigned;

    //Assigned owner
    private TextView dateDetailAssignedOwner;
    private TextView ownerDetailOwner;
    private TextView provinceDetailAssignedOwner;
    private TextView titleDetailAssignedOwner;
    private TextView descriptionDetailAssignedOwner;
    private TextView partTextView;
    private EditText editTextPart;
    private CardView cardCloseAlert;
    private Button closeAlert;

    private ShimmerFrameLayout shimmerCloseAlert;

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
        descriptionDetailAssigned = (TextView) view.findViewById(R.id.description_detail_assigned);

        /*****Unassigned****/
        dateDetailUnassigned = (TextView) view.findViewById(R.id.date_detail_unassigned);
        provinceDetailUnassigned = (TextView) view.findViewById(R.id.province_detail_unassigned);
        titleDetailUnassigned = (TextView) view.findViewById(R.id.title_detail_unassigned);
        descriptionDetailUnassigned = (TextView) view.findViewById(R.id.description_detail_unassigned);

        /******Assigned Owner*****/
        dateDetailAssignedOwner = (TextView) view.findViewById(R.id.date_detail_assigned_owner);
        ownerDetailOwner = (TextView) view.findViewById(R.id.owner_detail_owner);
        provinceDetailAssignedOwner = (TextView) view.findViewById(R.id.province_detail_assigned_owner);
        titleDetailAssignedOwner = (TextView) view.findViewById(R.id.title_detail_assigned_owner);
        descriptionDetailAssignedOwner = (TextView) view.findViewById(R.id.description_detail_assigned_owner);

        /**Finalized**/
        partTextView = (TextView) view.findViewById(R.id.partTextView);
        editTextPart = (EditText) view.findViewById(R.id.editText_part);

        cardCloseAlert = (CardView) view.findViewById(R.id.card_close_alert);
        shimmerCloseAlert = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_close_alert);

        closeAlert = (Button) view.findViewById(R.id.close_alert);
        closeAlert.setOnClickListener(this);

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
        withoutAlerts = (TextView) view.findViewById(R.id.without_alerts);
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
        if (alertArrayList.size() > 0) {
            withoutAlerts.setVisibility(View.GONE);
            alertList.setVisibility(View.VISIBLE);
            alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
        } else {
            withoutAlerts.setVisibility(View.VISIBLE);
            alertList.setVisibility(View.GONE);
        }

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
            case R.id.close_alert:
                Alert alertToClose = alertDetail;

                if (editTextPart.getText().toString().length() > 0) {
                    alertToClose.setNotes(editTextPart.getText().toString());
                    CloseAlertAsyncTask caat = new CloseAlertAsyncTask();
                    caat.execute(alertToClose);
                } else {
                    Toast.makeText(getContext(), R.string.writeNotes, Toast.LENGTH_SHORT).show();
                }
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
                SelectTechnicianIDAsyncTask stidat = new SelectTechnicianIDAsyncTask();
                stidat.execute(alert.getIdTechnician());

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

                /*****Description*****/
                builder = new SpannableStringBuilder();

                cabecera = "Descripción: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getDescription();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                descriptionDetailAssigned.setText(builder, TextView.BufferType.SPANNABLE);

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

                contenido = prefs.getString("surname", "") + ", " + prefs.getString("name", "");
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

                /*****Description*****/
                builder = new SpannableStringBuilder();

                cabecera = "Descripción: ";
                cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                contenido = alert.getDescription();
                contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                descriptionDetailAssignedOwner.setText(builder, TextView.BufferType.SPANNABLE);

                layoutDetail.setVisibility(View.VISIBLE);
                containerAssigned.setVisibility(View.GONE);
                containerList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.VISIBLE);
                partTextView.setVisibility(View.GONE);
                editTextPart.setText("");
                editTextPart.setVisibility(View.GONE);
                shimmerCloseAlert.setVisibility(View.GONE);


                if (alert.getState().equals("finished")) {
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

                    partTextView.setVisibility(View.VISIBLE);
                    editTextPart.setVisibility(View.VISIBLE);
                    shimmerCloseAlert.setVisibility(View.VISIBLE);
                }
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

            /*****Description*****/
            builder = new SpannableStringBuilder();

            cabecera = "Descripción: ";
            cabeceraSpannable = new SpannableString(cabecera);
            cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
            builder.append(cabeceraSpannable);

            contenido = alert.getDescription();
            contenidoSpannable = new SpannableString(contenido);
            contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
            builder.append(contenidoSpannable);

            descriptionDetailUnassigned.setText(builder, TextView.BufferType.SPANNABLE);

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

        fromDetailsToList();
    }

    private void fromDetailsToList() {
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
                URL url = new URL(Urls.GET_ALERT_LIST + prefs.getString("id", "") +
                        "/get-alerts-by-owner-province?access_token=" + prefs.getString("access_token", ""));

                Log.i("CONSULTA_ALERTAS", url.toString());
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

                    //connection.disconnect();

                    /*while ((line = br.readLine()) != null) {
                        //Log.i("Line", line);
                        result.append(line);
                    }*/
                    result.append(br.readLine());

                    //JSONObject jsonObject = new JSONObject(result.toString());
                    JSONArray alerts = new JSONArray(result.toString());

                    for (int i = 0; i < alerts.length(); i++) {
                        //Log.i("ALERT", alerts.get(i).toString());
                        JSONObject alert = (JSONObject) alerts.get(i);

                        String id = alert.getString("id");
                        String affair = alert.getString("title");
                        String description = alert.getString("description");
                        String province = alert.getString("province");
                        Date d = new Date(Long.parseLong(alert.getString("date")));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String date = sdf.format(d);
                        boolean assigned = alert.getBoolean("assigned");
                        String state = alert.getString("state");

                        if (!state.equals("closed")) {
                            if (!assigned) {
                                alertArrayList.add(new Alert(id, affair, description, province, date, assigned, state));
                            } else {
                                String idDoctor = alert.getString("owner");
                                alertArrayList.add(new Alert(id, affair, description, province, date, idDoctor, assigned, state));
                            }
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

    class CloseAlertAsyncTask extends AsyncTask<Alert, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            shimmerCloseAlert.startShimmerAnimation();
        }

        @Override
        protected Boolean doInBackground(Alert... alertsParams) {
            try {
                URL url = new URL(Urls.CLOSE_ALERTS + alertsParams[0].getId() + "/close-alert"
                        + "?access_token=" + prefs.getString("access_token", ""));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                connection.setConnectTimeout(Urls.TIMEOUT);
                connection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("note", alertsParams[0].getNotes());

                Log.i("JSON", jsonParam.toString());

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonParam.toString());

                writer.flush();
                writer.close();

                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    return true;
                }

                return false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean updated) {
            super.onPostExecute(updated);

            shimmerCloseAlert.stopShimmerAnimation();

            if (updated) {
                fromDetailsToList();
            } else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class SelectTechnicianIDAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... ids) {
            try {
                URL url = new URL(Urls.SELECT_ID + ids[0] + "?access_token=" + prefs.getString("access_token", ""));
                Log.i("URL", url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                connection.setConnectTimeout(Urls.TIMEOUT);
                connection.connect();

                int respuesta = connection.getResponseCode();

                StringBuilder resultSelect = new StringBuilder();

                Log.i("EE", "ee");
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    Log.i("OO", "oo");
                    InputStream in = new BufferedInputStream(connection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String lineSelect;

                    while ((lineSelect = reader.readLine()) != null) {
                        resultSelect.append(lineSelect);
                    }

                    JSONObject respuestaJSONSelect = new JSONObject(resultSelect.toString());

                    String name = respuestaJSONSelect.getString("name");
                    String surname = respuestaJSONSelect.getString("surname");


                    return surname + ", " + name;
                }

                return "";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);

            if (!name.equals("")) {
                Toast.makeText(getContext(), "EEE", Toast.LENGTH_SHORT).show();
                SpannableStringBuilder builder = new SpannableStringBuilder();

                String cabecera = "Técnico: ";
                SpannableString cabeceraSpannable = new SpannableString(cabecera);
                cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                builder.append(cabeceraSpannable);

                String contenido = name;
                SpannableString contenidoSpannable = new SpannableString(contenido);
                contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                builder.append(contenidoSpannable);

                ownerDetail.setText(builder, TextView.BufferType.SPANNABLE);
            }else {
                Toast.makeText(getContext(), "OOO", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

