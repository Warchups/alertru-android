package com.gnommostudios.alertru.alertru_android.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.AdapterMenu;
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter;
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private ListView listMenu;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private int positionMenu = -1;
    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMenu();

        initTitle();

        initFragments();

        initTabLayout();
    }

    private void initFragments() {
        viewPager = (CustomViewPager) findViewById(R.id.view_pager_main);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        viewPager.disableScroll(true);
    }

    private void initTitle() {
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.alertru));
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.appbartabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                positionMenu = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.home_black);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.search_black);
                        break;
                    case 2:

                        break;
                    case 3:
                        tab.setIcon(R.drawable.config_black);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.user_black);
                        break;

                }
                changeTitle();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.home_white);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.search_white);
                        break;
                    case 2:

                        break;
                    case 3:
                        tab.setIcon(R.drawable.config_white);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.user_white);
                        break;

                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.home_black);
        tabLayout.getTabAt(1).setIcon(R.drawable.search_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.alertru_azul500);
        tabLayout.getTabAt(3).setIcon(R.drawable.config_white);
        tabLayout.getTabAt(4).setIcon(R.drawable.user_white);


    }

    private void initMenu() {
        /*listMenu = (ListView) findViewById(R.id.menu_nav_list);

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

                viewPager.setCurrentItem(position);
                changeTitle();
                onBackPressed();
            }
        });*/
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
}
