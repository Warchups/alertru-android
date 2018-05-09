package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList;
import com.gnommostudios.alertru.alertru_android.model.Alert;
import com.gnommostudios.alertru.alertru_android.util.AuthenticationDialog;
import com.gnommostudios.alertru.alertru_android.util.DatePicker;
import com.gnommostudios.alertru.alertru_android.util.Urls;

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

public class SearchFragment extends Fragment implements View.OnClickListener, FABProgressListener, AdapterView.OnItemClickListener {

    private LinearLayout searchForm, searchList, searchLoading;

    private TextView titleSearch, withoutResults;

    private TextView dateEnter, dateExit;
    private RadioButton searchOpen, searchClose;
    private CheckBox checkBoxSearch;
    private Button btnSearch, seeClosed;
    private String dEnter, dExit;

    private SharedPreferences prefs;

    private ArrayList<Alert> alertArrayList;
    private ListView alertList;

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
    private ImageView closedDetails;
    private ImageView ownerDetailsImg;

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        prefs = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        titleSearch = (TextView) view.findViewById(R.id.search_title);
        withoutResults = (TextView) view.findViewById(R.id.without_results);
        withoutResults.setVisibility(View.GONE);

        alertList = (ListView) view.findViewById(R.id.search_list_view);
        alertList.setVisibility(View.GONE);

        searchForm = (LinearLayout) view.findViewById(R.id.search_form);
        searchForm.setVisibility(View.VISIBLE);

        searchList = (LinearLayout) view.findViewById(R.id.search_list);
        searchList.setVisibility(View.GONE);

        searchLoading = (LinearLayout) view.findViewById(R.id.search_loading);
        searchLoading.setVisibility(View.GONE);

        dateEnter = (TextView) view.findViewById(R.id.txtDateEnter);
        dateExit = (TextView) view.findViewById(R.id.txtDateExit);

        searchOpen = (RadioButton) view.findViewById(R.id.radioOpenSearch);
        searchClose = (RadioButton) view.findViewById(R.id.radioCloseSearch);

        checkBoxSearch = (CheckBox) view.findViewById(R.id.checkSearchOpenAndExit);

        btnSearch = (Button) view.findViewById(R.id.searchButton);
        btnSearch.setOnClickListener(this);

        seeClosed = (Button) view.findViewById(R.id.searchFinalized);
        seeClosed.setOnClickListener(this);

        dateEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialogEnterDate();
            }

        });

        dateExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialogEnterExit();
            }

        });

        checkBoxSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkBoxSearch.isChecked()) {
                    searchOpen.setEnabled(false);
                    searchClose.setEnabled(false);
                } else {
                    searchOpen.setEnabled(true);
                    searchClose.setEnabled(true);
                }
            }
        });

        /*****************************Details*****************************/
        layoutDetail = (LinearLayout) view.findViewById(R.id.details_search);
        layoutDetail.setVisibility(View.GONE);

        containerAssigned = (ConstraintLayout) view.findViewById(R.id.search_assigned);
        containerAssigned.setVisibility(View.GONE);

        containerUnassigned = (ConstraintLayout) view.findViewById(R.id.search_unassigned);
        containerUnassigned.setVisibility(View.GONE);

        containerAssignedOwner = (ConstraintLayout) view.findViewById(R.id.search_assigned_owner);
        containerAssignedOwner.setVisibility(View.GONE);

        /******Assigned*****/
        dateDetailAssigned = (TextView) view.findViewById(R.id.date_detail_assigned_search);
        ownerDetail = (TextView) view.findViewById(R.id.owner_detail_search);
        provinceDetailAssigned = (TextView) view.findViewById(R.id.province_detail_assigned_search);
        titleDetailAssigned = (TextView) view.findViewById(R.id.title_detail_assigned_search);
        descriptionDetailAssigned = (TextView) view.findViewById(R.id.description_detail_assigned_search);

        /*****Unassigned****/
        dateDetailUnassigned = (TextView) view.findViewById(R.id.date_detail_unassigned_search);
        provinceDetailUnassigned = (TextView) view.findViewById(R.id.province_detail_unassigned_search);
        titleDetailUnassigned = (TextView) view.findViewById(R.id.title_detail_unassigned_search);
        descriptionDetailUnassigned = (TextView) view.findViewById(R.id.description_detail_unassigned_search);

        /******Assigned Owner*****/
        dateDetailAssignedOwner = (TextView) view.findViewById(R.id.date_detail_assigned_owner_search);
        ownerDetailOwner = (TextView) view.findViewById(R.id.owner_detail_owner_search);
        provinceDetailAssignedOwner = (TextView) view.findViewById(R.id.province_detail_assigned_owner_search);
        titleDetailAssignedOwner = (TextView) view.findViewById(R.id.title_detail_assigned_owner_search);
        descriptionDetailAssignedOwner = (TextView) view.findViewById(R.id.description_detail_assigned_owner_search);

        /**Finalized**/
        writeDetails = (ImageView) view.findViewById(R.id.write_detail_assigned_owner_search);
        closedDetails = (ImageView) view.findViewById(R.id.closed_detail_assigned_owner_search);
        ownerDetailsImg = (ImageView) view.findViewById(R.id.image_detail_assigned_owner_search);
        partTextView = (TextView) view.findViewById(R.id.partTextView_search);
        editTextPart = (EditText) view.findViewById(R.id.editText_part_search);

        cardCloseAlert = (CardView) view.findViewById(R.id.card_close_alert_search);
        shimmerCloseAlert = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_close_alert_search);

        closeAlert = (Button) view.findViewById(R.id.close_alert_search);
        closeAlert.setOnClickListener(this);

        assingFAB = (FloatingActionButton) view.findViewById(R.id.assign_fab_search);
        assingFAB.setOnClickListener(this);

        fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle_search);
        fabProgressCircle.attachListener(this);

        /***************************************************************/

        return view;
    }

    private void showDatePickerDialogEnterDate() {
        DatePicker newFragment = DatePicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                final String selectedDate;
                if (month + 1 >= 10) {
                    if (day >= 10)
                        selectedDate = day + "-" + (month + 1) + "-" + year;
                    else
                        selectedDate = "0" + day + "-" + (month + 1) + "-" + year;
                } else {
                    if (day >= 10)
                        selectedDate = day + "-0" + (month + 1) + "-" + year;
                    else
                        selectedDate = "0" + day + "-0" + (month + 1) + "-" + year;
                }

                dateEnter.setText(selectedDate);
                dEnter = selectedDate;
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showDatePickerDialogEnterExit() {
        DatePicker newFragment = DatePicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                final String selectedDate;
                if (month + 1 >= 10) {
                    if (day >= 10)
                        selectedDate = day + "-" + (month + 1) + "-" + year;
                    else
                        selectedDate = "0" + day + "-" + (month + 1) + "-" + year;
                } else {
                    if (day >= 10)
                        selectedDate = day + "-0" + (month + 1) + "-" + year;
                    else
                        selectedDate = "0" + day + "-0" + (month + 1) + "-" + year;
                }

                dateExit.setText(selectedDate);
                dExit = selectedDate;
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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
                searchList.setVisibility(View.GONE);
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
                searchList.setVisibility(View.GONE);
                containerUnassigned.setVisibility(View.GONE);
                containerAssignedOwner.setVisibility(View.VISIBLE);
                ownerDetailsImg.setVisibility(View.VISIBLE);
                closedDetails.setVisibility(View.GONE);
                writeDetails.setVisibility(View.GONE);
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

                    writeDetails.setVisibility(View.VISIBLE);
                    partTextView.setVisibility(View.VISIBLE);
                    editTextPart.setVisibility(View.VISIBLE);
                    shimmerCloseAlert.setVisibility(View.VISIBLE);
                } else if (alert.getState().equals("closed")) {
                    /*****Part*****/
                    builder = new SpannableStringBuilder();

                    cabecera = "Parte de la incidencia: ";
                    cabeceraSpannable = new SpannableString(cabecera);
                    cabeceraSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, cabecera.length(), 0);
                    builder.append(cabeceraSpannable);

                    contenido = alert.getNotes();
                    contenidoSpannable = new SpannableString(contenido);
                    contenidoSpannable.setSpan(null, 0, contenido.length(), 0);
                    builder.append(contenidoSpannable);

                    partTextView.setText(builder, TextView.BufferType.SPANNABLE);

                    ownerDetailsImg.setVisibility(View.GONE);
                    closedDetails.setVisibility(View.VISIBLE);
                    partTextView.setVisibility(View.VISIBLE);
                    editTextPart.setVisibility(View.GONE);
                    shimmerCloseAlert.setVisibility(View.GONE);
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
            searchList.setVisibility(View.GONE);
            containerUnassigned.setVisibility(View.VISIBLE);
            containerAssignedOwner.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        checkBoxSearch.setChecked(false);
        searchOpen.setChecked(false);
        searchClose.setChecked(false);
        dateEnter.setText(dEnter);
        dateExit.setText(dExit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchFinalized:
            case R.id.searchButton:
                searchAlerts(v.getId());
                break;
            case R.id.close_alert_search:
                Alert alertToClose = alertDetail;

                if (editTextPart.getText().toString().length() > 0) {
                    alertToClose.setNotes(editTextPart.getText().toString());
                    CloseAlertAsyncTask caat = new CloseAlertAsyncTask();
                    caat.execute(alertToClose);
                } else {
                    Toast.makeText(getContext(), R.string.writeNotes, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.assign_fab_search:
                AssignDetailsAlertAsyncTask adaat = new AssignDetailsAlertAsyncTask();
                adaat.execute(alertDetail);
                break;
        }
    }

    private void searchAlerts(int id) {
        // Options //
        // 0 - Assigned and unassigned with a date
        // 1 - Unassigned with a date
        // 2 - Assigned with a date
        // 3 - Closed from technician

        if (id == R.id.searchButton) {
            String dEnter = dateEnter.getText().toString();
            String dExit = dateExit.getText().toString();

            boolean searchOpenAndClosed = checkBoxSearch.isChecked();
            boolean searchOpen = this.searchOpen.isChecked();
            boolean searchClosed = this.searchClose.isChecked();

            if (dEnter.toString().length() == 0 || dExit.length() == 0) {
                Toast.makeText(getActivity(), "Selecciona una fecha de entrada y una de salida", Toast.LENGTH_SHORT).show();
            } else {
                alertArrayList = new ArrayList<>();

                AlertListAsyncTask alat = new AlertListAsyncTask();

                if (!searchOpenAndClosed && !searchOpen && !searchClosed) {
                    titleSearch.setText("Asignadas y sin asignar\nde " + dEnter + " a " + dExit + ":");
                    alat.execute(0);
                } else if (searchOpenAndClosed) {
                    titleSearch.setText("Asignadas y sin asignar\nde " + dEnter + " a " + dExit + ":");
                    alat.execute(0);
                } else {
                    if (searchOpen) {
                        titleSearch.setText("Sin asignar\nde " + dEnter + " a " + dExit + ":");
                        alat.execute(1);
                    }
                    if (searchClosed) {
                        titleSearch.setText("Asignadas\nde " + dEnter + " a " + dExit + ":");
                        alat.execute(2);
                    }
                }

            }
        }

        if (id == R.id.searchFinalized) {
            alertArrayList = new ArrayList<>();

            titleSearch.setText("Tus alertas cerradas:");
            AlertListAsyncTask alat = new AlertListAsyncTask();
            alat.execute(3);
        }

    }

    private void createAlertsArray(Alert alert, String d) {
        String[] enter = dEnter.split("-");
        int dayEnter = Integer.parseInt(enter[0]);
        int monthEnter = Integer.parseInt(enter[1]);
        int yearEnter = Integer.parseInt(enter[2]);

        Date dateEnter = new Date();
        dateEnter.setDate(dayEnter);
        dateEnter.setMonth(monthEnter);
        dateEnter.setYear(yearEnter);

        String[] exit = dExit.split("-");
        int dayExit = Integer.parseInt(exit[0]);
        int monthExit = Integer.parseInt(exit[1]);
        int yearExit = Integer.parseInt(exit[2]);

        Date dateExit = new Date();
        dateExit.setDate(dayExit);
        dateExit.setMonth(monthExit);
        dateExit.setYear(yearExit);

        String[] alertDate = d.split("-");
        int day = Integer.parseInt(alertDate[0]);
        int month = Integer.parseInt(alertDate[1]);
        int year = Integer.parseInt(alertDate[2]);

        Date date = new Date();
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);


        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.getTime() >= dateEnter.getTime() && date.getTime() <= dateExit.getTime())
            alertArrayList.add(alert);

    }

    private void createAlertsArrayOpen(Alert alert, String d) {
        String[] enter = dEnter.split("-");
        int dayEnter = Integer.parseInt(enter[0]);
        int monthEnter = Integer.parseInt(enter[1]);
        int yearEnter = Integer.parseInt(enter[2]);

        Date dateEnter = new Date();
        dateEnter.setDate(dayEnter);
        dateEnter.setMonth(monthEnter);
        dateEnter.setYear(yearEnter);

        String[] exit = dExit.split("-");
        int dayExit = Integer.parseInt(exit[0]);
        int monthExit = Integer.parseInt(exit[1]);
        int yearExit = Integer.parseInt(exit[2]);

        Date dateExit = new Date();
        dateExit.setDate(dayExit);
        dateExit.setMonth(monthExit);
        dateExit.setYear(yearExit);

        String[] alertDate = d.split("-");
        int day = Integer.parseInt(alertDate[0]);
        int month = Integer.parseInt(alertDate[1]);
        int year = Integer.parseInt(alertDate[2]);

        Date date = new Date();
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);


        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.getTime() >= dateEnter.getTime() && date.getTime() <= dateExit.getTime() && !alert.isAssigned())
            alertArrayList.add(alert);

    }

    private void createAlertsArrayClosed(Alert alert, String d) {
        String[] enter = dEnter.split("-");
        int dayEnter = Integer.parseInt(enter[0]);
        int monthEnter = Integer.parseInt(enter[1]);
        int yearEnter = Integer.parseInt(enter[2]);

        Date dateEnter = new Date();
        dateEnter.setDate(dayEnter);
        dateEnter.setMonth(monthEnter);
        dateEnter.setYear(yearEnter);

        String[] exit = dExit.split("-");
        int dayExit = Integer.parseInt(exit[0]);
        int monthExit = Integer.parseInt(exit[1]);
        int yearExit = Integer.parseInt(exit[2]);

        Date dateExit = new Date();
        dateExit.setDate(dayExit);
        dateExit.setMonth(monthExit);
        dateExit.setYear(yearExit);

        String[] alertDate = d.split("-");
        int day = Integer.parseInt(alertDate[0]);
        int month = Integer.parseInt(alertDate[1]);
        int year = Integer.parseInt(alertDate[2]);

        Date date = new Date();
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);


        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.getTime() >= dateEnter.getTime() && date.getTime() <= dateExit.getTime() && alert.isAssigned())
            alertArrayList.add(alert);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        showAssingDetails(alertArrayList.get(pos));
    }

    class AlertListAsyncTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchForm.setVisibility(View.GONE);
            searchLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Integer... op) {
            try {
                URL url = new URL(Urls.GET_ALERT_LIST + prefs.getString("id", "") +
                        "/get-alerts-by-owner-province?access_token=" + prefs.getString("access_token", ""));
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

                //Log.i("EEE", "Llego aqui");
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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

                        if (op[0] != 3) {
                            if (!assigned) {
                                if (op[0] == 0)
                                    createAlertsArray(new Alert(id, affair, description, province, date, assigned, state), date);

                                if (op[0] == 1)
                                    createAlertsArrayOpen(new Alert(id, affair, description, province, date, assigned, state), date);

                                if (op[0] == 2)
                                    createAlertsArrayClosed(new Alert(id, affair, description, province, date, assigned, state), date);
                            } else {
                                if (!state.equals("closed")) {
                                    String idTechnician = alert.getString("owner");
                                    if (op[0] == 0)
                                        createAlertsArray(new Alert(id, affair, description, province, date, idTechnician, assigned, state), date);

                                    if (op[0] == 1)
                                        createAlertsArrayOpen(new Alert(id, affair, description, province, date, idTechnician, assigned, state), date);

                                    if (op[0] == 2)
                                        createAlertsArrayClosed(new Alert(id, affair, description, province, date, idTechnician, assigned, state), date);
                                }
                            }
                        } else {
                            if (assigned) {
                                String idTechnician = alert.getString("owner");
                                if (state.equals("closed") && idTechnician.equals(prefs.getString("id", ""))) {
                                    String note = alert.getString("note");
                                    alertArrayList.add(new Alert(id, affair, description, province, date, idTechnician, assigned, state, note));
                                }
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
            searchLoading.setVisibility(View.GONE);

            if (correct) {
                setAdapter();
                searchList.setVisibility(View.VISIBLE);
                if (alertArrayList.size() == 0) {
                    withoutResults.setVisibility(View.VISIBLE);
                    alertList.setVisibility(View.GONE);
                } else {
                    withoutResults.setVisibility(View.GONE);
                    alertList.setVisibility(View.VISIBLE);
                }
            } else {
                AuthenticationDialog dialog = new AuthenticationDialog();
                dialog.show(getFragmentManager(), "CONNECTION_ERROR");
                searchForm.setVisibility(View.VISIBLE);
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

    class SelectTechnicianIDAsyncTask extends AsyncTask<String, Void, String> {

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
            } else {
                Toast.makeText(getContext(), "Error: No se puede ver el tecnico de la alerta", Toast.LENGTH_SHORT).show();
            }
        }
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
        intent.putExtra("TITLE", "Búsqueda");
        //send broadcast
        getActivity().sendBroadcast(intent);
        searchAlerts(R.id.searchButton);
        layoutDetail.setVisibility(View.GONE);
        searchList.setVisibility(View.VISIBLE);
        searchForm.setVisibility(View.GONE);
    }

    public void setAdapter() {
        alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
        alertList.setOnItemClickListener(this);
    }

}
