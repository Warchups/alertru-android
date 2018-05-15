package com.gnommostudios.alertru.alertru_android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.model.Alert;

import java.util.ArrayList;

public class AdapterAlertList extends ArrayAdapter<Alert> {

    Activity context;
    ArrayList<Alert> elements;
    SharedPreferences prefs;

    public AdapterAlertList(@NonNull Fragment context, ArrayList<Alert> elements) {
        super(context.getActivity(), R.layout.element_list, elements);
        this.context = context.getActivity();
        this.elements = elements;
        prefs = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.element_list, null);

        LinearLayout background = (LinearLayout) convertView.findViewById(R.id.background_element);
        ImageView imagePadlock = (ImageView) convertView.findViewById(R.id.imagePadlock);
        TextView affairTxt = (TextView) convertView.findViewById(R.id.affairTxt);
        TextView descriptionTxt = (TextView) convertView.findViewById(R.id.descriptionTxt);
        TextView dateTxt = (TextView) convertView.findViewById(R.id.dateTxt);
        ImageView ownerImage = (ImageView) convertView.findViewById(R.id.ownerImage);
        ImageView finishedImage = (ImageView) convertView.findViewById(R.id.finishedImage);

        affairTxt.setText(elements.get(position).getAffair());
        dateTxt.setText(elements.get(position).getDate());
        descriptionTxt.setText(elements.get(position).getDescription());

        //Diferencio las alertas dependiendo de si estan assignadas o no, así les doy un estilo diferente
        if (elements.get(position).isAssigned()) {
            //Les pongo el icono del candado cerado (el nombre de los ficheros esta del reves)
            imagePadlock.setImageResource(R.drawable.icono_candado4);
            //Les pongo el fondo en rojo
            background.setBackground(getContext().getResources().getDrawable(R.drawable.degraded_elements_assigned));

            if (elements.get(position).getIdTechnician().equals(prefs.getString("id", ""))) {
                //Si el tecnico que tiene asignada la alerta es el que tiene la sesion iniciada pongo un indicativo en la alerta
                ownerImage.setVisibility(View.VISIBLE);
                if (elements.get(position).getState().equals("finished")) {
                    //Si la alerta esta lista para pasarle un parte y cerrarla, le pongo otro indicativo
                    finishedImage.setVisibility(View.VISIBLE);
                }
            } else {
                //Si no es el propietario oculto el indicativo de propiedad
                ownerImage.setVisibility(View.GONE);
            }
        } else {
            //Si no esta asingnada le pongo el candado abierto (el nombre de los ficheros esta del reves)
            imagePadlock.setImageResource(R.drawable.candado_cerrado4);
            //Pongo el fondo en verde
            background.setBackground(getContext().getResources().getDrawable(R.drawable.degraded_elements_unassigned));
        }

        return convertView;
    }

}
