package com.gnommostudios.alertru.alertru_android.util;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.gnommostudios.alertru.alertru_android.R;

public class AuthenticationDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View context = inflater.inflate(R.layout.authentication_dialog, null);

        builder.setView(context);


        return builder.create();
    }

}
