package com.gnommostudios.alertru.alertru_android.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

import java.util.Calendar

class DatePicker : DialogFragment() {

    private var listener: DatePickerDialog.OnDateSetListener? = null
    private var title: String? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener, title: String) {
        this.title = title
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        // Create a new instance of DatePickerDialog and return it
        val dpd = DatePickerDialog(activity, listener, year, month, day)
        dpd.setTitle(title)
        return dpd
    }

    companion object {

        fun newInstance(listener: DatePickerDialog.OnDateSetListener, title: String): DatePicker {
            val fragment = DatePicker()
            fragment.setListener(listener, title)
            return fragment
        }
    }

}



























