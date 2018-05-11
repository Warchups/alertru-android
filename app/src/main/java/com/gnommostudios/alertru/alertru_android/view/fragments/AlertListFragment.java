package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AlertListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, FABProgressListener, AbsListView.OnScrollListener {

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

    private ImageView writeDetails;
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
        writeDetails = (ImageView) view.findViewById(R.id.write_detail_assigned_owner);

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

        alertList = (ListView) view.findViewById(R.id.alert_list);
        withoutAlerts = (TextView) view.findViewById(R.id.without_alerts);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refesh_layout);
        refresh.setOnRefreshListener(this);

        initList();

        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("AlertListFragment"));

        return view;
    }

    public void initList() {
        //Inicio la lista y mando un mensaje de broadcast para que en el MainActivity cambie el icono,
        //para que ya no salga el aviso de alerta nueva, ya que ya se han cargado las alertas
        Intent intent = new Intent("MainActivity");
        intent.putExtra("NOTIFICATION", false);
        intent.putExtra("CHANGE_TITLE", false);
        //send broadcast
        getActivity().sendBroadcast(intent);

        //Pongo los detalles en GONE, por si estaban mostrandose
        layoutDetail.setVisibility(View.GONE);

        //Si esta logueado llamo muestro el contenedor de la lista y
        //llamo a la funcion asincrona que hace la consulta de las alertas
        if (prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED).equals(StatesLog.LOGGED)) {
            containerList.setVisibility(View.VISIBLE);
            layoutDisconnected.setVisibility(View.GONE);
            AlertListAsyncTask alat = new AlertListAsyncTask();
            alat.execute();
        } else {
            //Si no esta logueado oculto el contenedor de la lista y muestro el layout que te dice que no estas logueado
            containerList.setVisibility(View.GONE);
            layoutDisconnected.setVisibility(View.VISIBLE);
        }
    }

    public void setAdapter() {
        if (alertArrayList.size() > 0) {
            //Si hay alertas en el array, muestro la lista y pongo el adapter
            withoutAlerts.setVisibility(View.GONE);
            alertList.setVisibility(View.VISIBLE);
            alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
        } else {
            //Si no hay alertas oculto la lista y muestro un textview que me avisa de que no hay alertas
            withoutAlerts.setVisibility(View.VISIBLE);
            alertList.setVisibility(View.GONE);
        }

        //Pongo los listeners a la lista
        alertList.setOnItemClickListener(this);
        alertList.setOnItemLongClickListener(this);
        alertList.setOnScrollListener(this);
    }

    @Override
    public void onRefresh() {
        initList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_disconnected:
                //Si pulso encima del layout que me sale cuando estoy deslogeuado intenta volver a cargar la lista,
                //si no estoy logueado volvera a aparecer este layout, y si ya me he logueado cargara la lista
                initList();
                break;
            case R.id.assign_fab:
                //Si, desde los detalles de una alerta, pulso en el FAB para assignarme la alerta,
                //llamo a la funcion asincrona que me asigna la alerta
                AssignDetailsAlertAsyncTask adaat = new AssignDetailsAlertAsyncTask();
                adaat.execute(alertDetail);
                break;
            case R.id.close_alert:
                Alert alertToClose = alertDetail;

                //Si, desde los detalles de una alerta, pulso en el boton para cerrar la alerta,
                //compruebo que el usuario haya introducido algo en el EditText del parte,
                //si ha escrito un parte, se lo añado a un POJO de alerta y llamo a la asincrona
                if (editTextPart.getText().toString().length() > 0) {
                    alertToClose.setNotes(editTextPart.getText().toString());
                    CloseAlertAsyncTask caat = new CloseAlertAsyncTask();
                    caat.execute(alertToClose);
                } else {
                    //Si no ha introducido nada, le aviso para que lo escriba
                    Toast.makeText(getContext(), R.string.writeNotes, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        showAlertDetails(alertArrayList.get(pos));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
        showAssingDialog(alertArrayList.get(pos));
        return true;
    }

    private void writeDetails(String cabecera, String contenido, TextView textView) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString cabeceraSpannable = new SpannableString(cabecera);
        cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
        builder.append(cabeceraSpannable);

        SpannableString contenidoSpannable = new SpannableString(contenido);
        contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
        builder.append(contenidoSpannable);

        textView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    //Muestro los detalles de la alerta que le paso
    private void showAlertDetails(Alert alert) {
        //Mando un mensaje de broadcast para que desde el MainActivity cambie el titulo
        Intent intent = new Intent("MainActivity");
        intent.putExtra("CHANGE_TITLE", true);
        intent.putExtra("TITLE", "Detalles");
        //send broadcast
        getActivity().sendBroadcast(intent);

        //Me guardo la alerta en una variable global para despues si quero asignarmela o cerrarla, poder acceder
        alertDetail = alert;

        String cabecera;
        String contenido;

        if (alert.isAssigned()) {
            if (!alert.getIdTechnician().equals(prefs.getString("id", ""))) {
                dateDetailAssigned.setText(alert.getDate());

                /*****Technician*****/
                SelectTechnicianIDAsyncTask stidat = new SelectTechnicianIDAsyncTask();
                stidat.execute(alert.getIdTechnician());

                /*****Province*****/
                cabecera = "Provincia: ";
                contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());

                writeDetails(cabecera, contenido, provinceDetailAssigned);

                /*****Title*****/
                cabecera = "Asunto Alerta: ";
                contenido = alert.getAffair();

                writeDetails(cabecera, contenido, titleDetailAssigned);

                /*****Description*****/
                cabecera = "Descripción: ";
                contenido = alert.getDescription();

                writeDetails(cabecera, contenido, descriptionDetailAssigned);

                layoutDetail.setVisibility(View.VISIBLE);
                containerAssigned.setVisibility(View.VISIBLE);
                containerList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.GONE);
            } else {
                dateDetailAssignedOwner.setText(alert.getDate());

                /*****Technician*****/
                cabecera = "Técnico: ";
                contenido = prefs.getString("surname", "") + ", " + prefs.getString("name", "");

                writeDetails(cabecera, contenido, ownerDetailOwner);

                /*****Province*****/
                cabecera = "Provincia: ";
                contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());

                writeDetails(cabecera, contenido, provinceDetailAssignedOwner);

                /*****Title*****/
                cabecera = "Asunto Alerta: ";
                contenido = alert.getAffair();

                writeDetails(cabecera, contenido, titleDetailAssignedOwner);

                /*****Description*****/
                cabecera = "Descripción: ";
                contenido = alert.getDescription();

                writeDetails(cabecera, contenido, descriptionDetailAssignedOwner);

                layoutDetail.setVisibility(View.VISIBLE);
                containerAssigned.setVisibility(View.GONE);
                containerList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.VISIBLE);
                writeDetails.setVisibility(View.GONE);
                partTextView.setVisibility(View.GONE);
                editTextPart.setText("");
                editTextPart.setVisibility(View.GONE);
                shimmerCloseAlert.setVisibility(View.GONE);


                if (alert.getState().equals("finished")) {
                    /*****Part*****/
                    cabecera = "Parte de la incidencia: ";
                    contenido = "";

                    writeDetails(cabecera, contenido, partTextView);

                    writeDetails.setVisibility(View.VISIBLE);
                    partTextView.setVisibility(View.VISIBLE);
                    editTextPart.setVisibility(View.VISIBLE);
                    shimmerCloseAlert.setVisibility(View.VISIBLE);
                }
            }
        } else {
            dateDetailUnassigned.setText(alert.getDate());

            /*****Province*****/
            cabecera = "Provincia: ";
            contenido = Character.toUpperCase(alert.getProvince().charAt(0)) + "" + alert.getProvince().subSequence(1, alert.getProvince().length());

            writeDetails(cabecera, contenido, provinceDetailUnassigned);

            /*****Title*****/
            cabecera = "Asunto Alerta: ";
            contenido = alert.getAffair();

            writeDetails(cabecera, contenido, titleDetailUnassigned);

            /*****Description*****/
            cabecera = "Descripción: ";
            contenido = alert.getDescription();

            writeDetails(cabecera, contenido, descriptionDetailUnassigned);

            layoutDetail.setVisibility(View.VISIBLE);
            containerAssigned.setVisibility(View.GONE);
            containerList.setVisibility(View.GONE);
            containerUnassigned.setVisibility(View.VISIBLE);
            containerAssignedOwner.setVisibility(View.GONE);
        }

    }

    //Muestro un dialogo para asignarte la alerta que has mantenido
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem > 0) {
            refresh.setEnabled(false);
        } else {
            refresh.setEnabled(true);
        }
    }

    //AsyncTask para cargar la lista de las alertas
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

                //Log.i("CONSULTA_ALERTAS", url.toString());
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

                //Log.i("EEE", "Llego aqui");
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    result.append(br.readLine());

                    //Recojo el array de alertas que me devuelve la consulta
                    JSONArray alerts = new JSONArray(result.toString());

                    //Inicio el array para que se machaque cada vez
                    alertArrayList = new ArrayList<>();

                    //Recorro el JSONArray
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

                        //Si el estado no es closed
                        if (!state.equals("closed")) {
                            //Si no esta asignada la añado a la array sin mas
                            if (!assigned) {
                                alertArrayList.add(new Alert(id, affair, description, province, date, assigned, state));
                            } else {
                                //Si esta asignada la añado con el propietario
                                String idTechnician = alert.getString("owner");
                                alertArrayList.add(new Alert(id, affair, description, province, date, idTechnician, assigned, state));
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
                //setAdapter();
            }
        }
    }

    //AsyncTask para assignar una alerta pasandole la id de la alerta y la id del tecnico
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

    //AsyncTask para assignar una alerta pasandole la id de la alerta y la id del tecnico (desde los detalles)
    // (Diferencia --> El FAB)
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
            }
        }
    }

    //AsyncTask para cerrar una alerta pasandole el parte de esta
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

                //Log.i("JSON", jsonParam.toString());

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

    //AsyncTask para comprobar el nombre y los apellidos pasandole la id
    class SelectTechnicianIDAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            String cabecera = "Técnico: ";
            String contenido = "cargando...";

            writeDetails(cabecera, contenido, ownerDetail);
        }

        @Override
        protected String doInBackground(String... ids) {
            try {
                URL url = new URL(Urls.SELECT_ID + ids[0] + "?access_token=" + prefs.getString("access_token", ""));
                //Log.i("URL", url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

                connection.setConnectTimeout(Urls.TIMEOUT);
                connection.connect();

                int respuesta = connection.getResponseCode();

                StringBuilder resultSelect = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
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
                String cabecera = "Técnico: ";
                String contenido = name;

                writeDetails(cabecera, contenido, ownerDetail);
            } else {
                Toast.makeText(getContext(), "Error: No se puede ver el tecnico de la alerta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //Si el intent es para refescar inicio la lista
            if (intent.getExtras().getBoolean("REFRESH")) {
                initList();
            }
        }
    };

}
