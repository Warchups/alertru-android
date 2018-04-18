package com.gnommostudios.alertru.alertru_android.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;

import java.util.ArrayList;

public class AdapterMenu extends ArrayAdapter<String> {

    Activity context;
    ArrayList<String> elements;


    public AdapterMenu(@NonNull Activity context, ArrayList<String> elements) {
        super(context, R.layout.element_list_menu, elements);
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.element_list_menu, null);

        TextView texto = (TextView) convertView.findViewById(R.id.textMenu);

        texto.setText(elements.get(position));

        return convertView;
    }

}
