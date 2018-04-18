package com.gnommostudios.alertru.alertru_android.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listMenu;
    private TextView poweredText;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private int positionMenu = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMenu();

        initPowered();

        initTitle();
    }

    private void initTitle() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.alertru));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(getResources().getString(R.string.menu));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                changeTitle();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    private void initPowered() {
        poweredText = (TextView) findViewById(R.id.powered_text);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String poweredBy = "Powered by ";
        SpannableString poweredBySpannable = new SpannableString(poweredBy);
        poweredBySpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, poweredBy.length(), 0);
        builder.append(poweredBySpannable);

        String gnommostudios = "Gnommostudios";
        SpannableString gnommostudiosSpannable = new SpannableString(gnommostudios);
        gnommostudiosSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#cccccc")), 0, gnommostudios.length(), 0);
        builder.append(gnommostudiosSpannable);

        poweredText.setText(builder, TextView.BufferType.SPANNABLE);

    }

    private void initMenu() {
        listMenu = (ListView) findViewById(R.id.menu_nav_list);

        ArrayList<String> elementsListMenu = new ArrayList<>();
        elementsListMenu.add(getResources().getString(R.string.search));
        elementsListMenu.add(getResources().getString(R.string.user_data));
        elementsListMenu.add(getResources().getString(R.string.settings));
        elementsListMenu.add(getResources().getString(R.string.info));

        listMenu.setAdapter(new AdapterMenu(this, elementsListMenu));

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        positionMenu = position;
                        //Search
                        break;
                    case 1:
                        positionMenu = position;
                        //User dates
                        break;
                    case 2:
                        positionMenu = position;
                        //Settings
                        break;
                    case 3:
                        positionMenu = position;
                        //Info
                        break;
                }
                changeTitle();
                onBackPressed();
            }
        });
    }

    private void changeTitle() {
        switch (positionMenu) {
            case 0:
                getSupportActionBar().setTitle(getResources().getString(R.string.incidents));
                break;
            case 1:
                getSupportActionBar().setTitle(getResources().getString(R.string.user_data));
                break;
            case 2:
                getSupportActionBar().setTitle(getResources().getString(R.string.settings));
                break;
            case 3:
                getSupportActionBar().setTitle(getResources().getString(R.string.info));
                break;
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.alertru));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
