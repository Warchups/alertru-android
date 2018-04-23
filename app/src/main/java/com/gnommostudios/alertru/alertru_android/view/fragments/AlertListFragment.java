package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList;
import com.gnommostudios.alertru.alertru_android.model.Alert;

import java.util.ArrayList;

public class AlertListFragment extends Fragment {

    private ListView alertList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_list, container, false);

        alertList = (ListView) view.findViewById(R.id.alert_list);

        initList();

        // Inflate the layout for this fragment
        return view;
    }

    private void initList() {
        ArrayList<Alert> alertArrayList = new ArrayList<>();
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", false));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", false));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", false));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", false));
        alertArrayList.add(new Alert("¡Hola!", "00-00-0000", true));

        alertList.setAdapter(new AdapterAlertList(this, alertArrayList));
    }

}
