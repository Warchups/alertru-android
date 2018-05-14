package com.gnommostudios.alertru.alertru_android.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePicker extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private String title;

    public static DatePicker newInstance(DatePickerDialog.OnDateSetListener listener, String title) {
        DatePicker fragment = new DatePicker();
        fragment.setListener(listener, title);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener, String title) {
        this.title = title;
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), listener, year, month, day);
        dpd.setTitle(title);
        return dpd;
    }

}



























