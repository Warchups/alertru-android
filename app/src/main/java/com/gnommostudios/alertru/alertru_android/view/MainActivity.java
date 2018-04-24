package com.gnommostudios.alertru.alertru_android.view;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter;
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private int positionMenu = -1;
    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        changeTitle();
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

    private void changeTitle() {
        switch (positionMenu) {
            case 0:
                getSupportActionBar().setTitle(getResources().getString(R.string.incidents));
                break;
            case 1:
                getSupportActionBar().setTitle(getResources().getString(R.string.search));
                break;
            case 2:
                getSupportActionBar().setTitle(getResources().getString(R.string.alertru));
                break;
            case 3:
                //toolbar.setVisibility(View.GONE);
                getSupportActionBar().setTitle(getResources().getString(R.string.settings));
                break;
            case 4:
                getSupportActionBar().setTitle(getResources().getString(R.string.user_data));
                break;
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.alertru));
                break;
        }
    }
}
