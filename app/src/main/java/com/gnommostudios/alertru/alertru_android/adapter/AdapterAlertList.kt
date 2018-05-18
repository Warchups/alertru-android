package com.gnommostudios.alertru.alertru_android.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.model.Alert

import java.util.ArrayList

class AdapterAlertList(context: Fragment, internal var elements: ArrayList<Alert>) : ArrayAdapter<Alert>(context.activity, R.layout.element_list, elements) {

    internal var context: Activity
    internal var prefs: SharedPreferences

    init {
        this.context = context.activity
        prefs = getContext().getSharedPreferences("user", Context.MODE_PRIVATE)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        var convertView = inflater.inflate(R.layout.element_list, null)

        val background = convertView!!.findViewById<View>(R.id.background_element) as LinearLayout
        val imagePadlock = convertView.findViewById<View>(R.id.imagePadlock) as ImageView
        val affairTxt = convertView.findViewById<View>(R.id.affairTxt) as TextView
        val descriptionTxt = convertView.findViewById<View>(R.id.descriptionTxt) as TextView
        val dateTxt = convertView.findViewById<View>(R.id.dateTxt) as TextView
        val ownerImage = convertView.findViewById<View>(R.id.ownerImage) as ImageView
        val finishedImage = convertView.findViewById<View>(R.id.finishedImage) as ImageView

        affairTxt.text = elements[position].affair
        dateTxt.text = elements[position].date
        descriptionTxt.text = elements[position].description

        //Diferencio las alertas dependiendo de si estan assignadas o no, así les doy un estilo diferente
        if (elements[position].isAssigned) {
            //Les pongo el icono del candado cerado (el nombre de los ficheros esta del reves)
            imagePadlock.setImageResource(R.drawable.icono_candado4)
            //Les pongo el fondo en rojo
            background.background = getContext().resources.getDrawable(R.drawable.degraded_elements_assigned)

            if (elements[position].idTechnician == prefs.getString("id", "")) {
                //Si el tecnico que tiene asignada la alerta es el que tiene la sesion iniciada pongo un indicativo en la alerta
                ownerImage.visibility = View.VISIBLE
                if (elements[position].state == "finished") {
                    //Si la alerta esta lista para pasarle un parte y cerrarla, le pongo otro indicativo
                    finishedImage.visibility = View.VISIBLE
                }
            } else {
                //Si no es el propietario oculto el indicativo de propiedad
                ownerImage.visibility = View.GONE
            }
        } else {
            //Si no esta asingnada le pongo el candado abierto (el nombre de los ficheros esta del reves)
            imagePadlock.setImageResource(R.drawable.candado_cerrado4)
            //Pongo el fondo en verde
            background.background = getContext().resources.getDrawable(R.drawable.degraded_elements_unassigned)
        }

        return convertView
    }

}
