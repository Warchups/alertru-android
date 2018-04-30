package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList;
import com.gnommostudios.alertru.alertru_android.model.Alert;
import com.gnommostudios.alertru.alertru_android.util.AuthenticationDialog;
import com.gnommostudios.alertru.alertru_android.util.DatePicker;
import com.gnommostudios.alertru.alertru_android.util.Urls;

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

public class SearchFragment extends Fragment implements View.OnClickListener {

    private LinearLayout searchForm, searchList;

    private TextView titleSearch;

    private TextView dateEnter, dateExit;
    private RadioButton searchOpen, searchClose;
    private CheckBox checkBoxSearch;
    private Button btnSearch;
    private String dEnter, dExit;

    private SharedPreferences prefs;

    private ArrayList<Alert> alertArrayList;
    private ListView alertList;

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

        alertList = (ListView) view.findViewById(R.id.search_list_view);

        searchForm = (LinearLayout) view.findViewById(R.id.search_form);
        searchForm.setVisibility(View.VISIBLE);

        searchList = (LinearLayout) view.findViewById(R.id.search_list);
        searchList.setVisibility(View.GONE);

        dateEnter = view.findViewById(R.id.txtDateEnter);
        dateExit = view.findViewById(R.id.txtDateExit);

        searchOpen = view.findViewById(R.id.radioOpenSearch);
        searchClose = view.findViewById(R.id.radioCloseSearch);

        checkBoxSearch = view.findViewById(R.id.checkSearchOpenAndExit);

        btnSearch = view.findViewById(R.id.searchButton);
        btnSearch.setOnClickListener(this);

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

        //view.setOnKeyListener(this);

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
            case R.id.searchButton:
                searchAlerts();
                break;
        }
    }

    private void searchAlerts() {
        if (dateEnter.getText().toString().length() == 0 || dateExit.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Selecciona una fecha de entrada y una de salida", Toast.LENGTH_SHORT).show();
        } else {
            alertArrayList = new ArrayList<>();

            titleSearch.setText("Busqueda de " + dateEnter.getText().toString() + " a " + dateExit.getText().toString() + ":");

            AlertListAsyncTask alat = new AlertListAsyncTask();
            alat.execute();
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

        String [] alertDate = d.split("-");
        int day = Integer.parseInt(alertDate[0]);
        int month = Integer.parseInt(alertDate[1]);
        int year = Integer.parseInt(alertDate[2]);

        Date date = new Date();
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);


        Log.i("DATE", date.getTime() + "");
        Log.i("ENTER", dateEnter.getTime() + "");
        Log.i("EXIT", dateExit.getTime() + "");

        if (date.getTime() >= dateEnter.getTime() && date.getTime() <= dateExit.getTime())
            alertArrayList.add(alert);
        else
            Toast.makeText(getActivity(), "EE", Toast.LENGTH_SHORT).show();

    }

    class AlertListAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchForm.setVisibility(View.GONE);
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

                connection.setConnectTimeout(Urls.TIMEOUT);
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

                    //JSONObject jsonObject = new JSONObject(result.toString());
                    JSONArray alerts = new JSONArray(result.toString());

                    for (int i = 0; i < alerts.length(); i++) {
                        Log.i("ALERT", alerts.get(i).toString());
                        JSONObject alert = (JSONObject) alerts.get(i);

                        String id = alert.getString("id");
                        String affair = alert.getString("subject");
                        Date d = new Date(Long.parseLong(alert.getString("date")));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String date = sdf.format(d);
                        boolean assigned = alert.getBoolean("assigned");

                        createAlertsArray(new Alert(id, affair, date, assigned), date);
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


            if (correct) {
                setAdapter();
                searchList.setVisibility(View.VISIBLE);
            } else {
                AuthenticationDialog dialog = new AuthenticationDialog();
                dialog.show(getFragmentManager(), "CONNECTION_ERROR");
                searchForm.setVisibility(View.VISIBLE);
                //setAdapter();
            }
        }
    }

    public void setAdapter() {
        alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
    }
}
