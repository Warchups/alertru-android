package com.gnommostudios.alertru.alertru_android.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;

public class SearchDialog extends DialogFragment {

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View context = inflater.inflate(R.layout.search_dialog, null);

        ImageView closeButton = (ImageView) context.findViewById(R.id.searchCloseWindow);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(context);

        AlertDialog dialog = builder.create();

        //dialog.getWindow().setLayout(20, 20);

        return dialog;
    }
}