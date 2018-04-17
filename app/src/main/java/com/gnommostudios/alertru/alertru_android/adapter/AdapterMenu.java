package com.gnommostudios.alertru.alertru_android.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.model.ElementoMenu;

import java.util.ArrayList;

public class AdapterMenu extends ArrayAdapter<ElementoMenu> {

    Activity context;
    ArrayList<ElementoMenu> elementos;


    public AdapterMenu(@NonNull Activity context, ArrayList<ElementoMenu> elementos) {
        super(context, R.layout.elemento_lista_menu, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        /*convertView = inflater.inflate(R.layout.elemento_lista_menu, null);

        ImageView imagen = (ImageView) convertView.findViewById(R.id.imagenMenu);
        TextView texto = (TextView) convertView.findViewById(R.id.textoMenu);

        imagen.setImageResource(elementos.get(position).getImagen());
        texto.setText(elementos.get(position).getTexto());*/

        return convertView;
    }

}
