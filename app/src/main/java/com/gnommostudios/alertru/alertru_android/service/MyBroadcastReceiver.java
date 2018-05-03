package com.gnommostudios.alertru.alertru_android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private ImageView homeButton;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BROACAST", "OYYYY");
        //homeButton.setImageResource(R.drawable.icon_tab_center_notification);
    }
}
