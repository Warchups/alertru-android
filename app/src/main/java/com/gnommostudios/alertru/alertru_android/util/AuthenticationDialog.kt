package com.gnommostudios.alertru.alertru_android.util

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

import com.gnommostudios.alertru.alertru_android.R

class AuthenticationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val context = inflater.inflate(R.layout.authentication_dialog, null)

        builder.setView(context)

        return builder.create()
    }

}
