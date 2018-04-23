package com.gnommostudios.alertru.alertru_android.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class SearchDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View context = inflater.inflate(R.layout.search_dialog, null);

        builder.setView(context);

        AlertDialog dialog = builder.create();

        return dialog;
    }
}