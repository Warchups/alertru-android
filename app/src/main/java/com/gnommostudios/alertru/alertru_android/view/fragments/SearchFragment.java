package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;


import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.util.DatePicker;

public class SearchFragment extends Fragment {

    private TextView dateEnter, dateExit;
    private RadioButton searchOpen, searchClose;
    private CheckBox checkBoxSearch;
    private Button btnSearchAll, btnSearchByDate;
    private String dEnter, dExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dateEnter = view.findViewById(R.id.txtDateEnter);
        dateExit = view.findViewById(R.id.txtDateExit);

        searchOpen = view.findViewById(R.id.radioOpenSearch);
        searchClose = view.findViewById(R.id.radioCloseSearch);

        checkBoxSearch = view.findViewById(R.id.checkSearchOpenAndExit);

        btnSearchAll = view.findViewById(R.id.searchButtonAll);
        btnSearchByDate = view.findViewById(R.id.searchButtonByDate);

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
                if(checkBoxSearch.isChecked()){
                    searchOpen.setEnabled(false);
                    searchClose.setEnabled(false);
                }else{
                    searchOpen.setEnabled(true);
                    searchClose.setEnabled(true);
                }
            }
        });

        return view;
    }

    private void showDatePickerDialogEnterDate() {
        DatePicker newFragment = DatePicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
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
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
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
}
