package com.gnommostudios.alertru.alertru_android.view;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter;
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView homeButton;
    private ImageView searchButton;
    private ImageView userButton;
    private ImageView settingsButton;
    private ImageView infoButton;

    private Toolbar toolbar;
    private AppBarLayout appBar;
    private int positionMenu = 0;
    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCustomTab();

        initTitle();

        initFragments();
    }

    private void initFragments() {
        viewPager = (CustomViewPager) findViewById(R.id.view_pager_main);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        viewPager.disableScroll(true);

        viewPager.setCurrentItem(2);
    }

    private void initTitle() {
        appBar = (AppBarLayout) findViewById(R.id.appbarMain);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        changeTitle(-1);
        changeIcons(R.id.button_home);
    }

    private void initCustomTab() {
        homeButton = (ImageView) findViewById(R.id.button_home);
        searchButton = (ImageView) findViewById(R.id.button_search);
        userButton = (ImageView) findViewById(R.id.button_user);
        settingsButton = (ImageView) findViewById(R.id.button_config);
        infoButton = (ImageView) findViewById(R.id.button_info);

        homeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        userButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                viewPager.setCurrentItem(0);
                break;
            case R.id.button_user:
                viewPager.setCurrentItem(1);
                break;
            case R.id.button_home:
                viewPager.setCurrentItem(2);
                break;
            case R.id.button_config:
                viewPager.setCurrentItem(3);
                break;
            case R.id.button_info:
                viewPager.setCurrentItem(4);
                break;
        }

        changeIcons(v.getId());
        changeTitle(v.getId());
    }

    private void changeIcons(int id) {
        if (id == R.id.button_search) {
            searchButton.setImageResource(R.drawable.search_black);
            searchButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        }

        if (id == R.id.button_user) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            userButton.setImageResource(R.drawable.user_black);
            userButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        }

        if (id == R.id.button_home) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            homeButton.setImageResource(R.drawable.icon_tab_center_black);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        }

        if (id == R.id.button_config) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_black);
            settingsButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        }

        if (id == R.id.button_info) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));

            infoButton.setImageResource(R.drawable.info_black);
            infoButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
        }

    }

    private void changeTitle(int id) {
        switch (id) {
            case R.id.button_home:
                getSupportActionBar().setTitle(getResources().getString(R.string.incidents));
                break;
            case R.id.button_search:
                getSupportActionBar().setTitle(getResources().getString(R.string.search));
                break;
            case R.id.button_config:
                getSupportActionBar().setTitle(getResources().getString(R.string.settings));
                break;
            case R.id.button_user:
                getSupportActionBar().setTitle(getResources().getString(R.string.user_data));
                break;
            case R.id.button_info:
                getSupportActionBar().setTitle(getResources().getString(R.string.info));
                break;
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.alertru));
                break;
        }
    }
}
