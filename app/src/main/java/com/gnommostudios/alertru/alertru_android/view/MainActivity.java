package com.gnommostudios.alertru.alertru_android.view;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter;
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView homeButton;
    private ImageView searchButton;
    private ImageView userButton;
    private ImageView settingsButton;
    private ImageView infoButton;

    private LinearLayout subSearch;
    private LinearLayout subUser;
    private LinearLayout subSettings;
    private LinearLayout subInfo;

    private TextView titleToolbar;
    private Toolbar toolbar;
    private AppBarLayout appBar;
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

        //Deshabilitamos el scroll de las paginas del ViewPager desde la clase CustomViewPager
        viewPager.disableScroll(true);

        //Ponemos el ViewPager en la pagina que recogemos desde els SplashScreen dependiendo de las comprobaciones del token
        viewPager.setCurrentItem(getIntent().getExtras().getInt("PAGE"));
    }

    private void initTitle() {
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbarMain);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        //Deshabilitamos el titulo del ToolBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getExtras().getInt("PAGE") == 2) {
            //Llamamos al método changeTitle(int) pasándole un -1 para que vaya al default
            changeTitle(-1);
            //Llamamos el método changeIcons(int) pasándole la id de R.id.button_home para que muestre seleccionado el home
            changeIcons(R.id.button_home);
        }

        if (getIntent().getExtras().getInt("PAGE") == 1) {
            //Llamamos al método changeTitle(int) pasándole la ide de R.id.button_user para que muestre el titulo de user
            changeTitle(R.id.button_user);
            //Llamamos el método changeIcons(int) pasándole la id de R.id.button_user para que muestre seleccionado el user
            changeIcons(R.id.button_user);
        }
    }

    private void initCustomTab() {
        homeButton = (ImageView) findViewById(R.id.button_home);
        searchButton = (ImageView) findViewById(R.id.button_search);
        userButton = (ImageView) findViewById(R.id.button_user);
        settingsButton = (ImageView) findViewById(R.id.button_config);
        infoButton = (ImageView) findViewById(R.id.button_info);

        subSearch = (LinearLayout) findViewById(R.id.sub_search);
        subInfo = (LinearLayout) findViewById(R.id.sub_info);
        subSettings = (LinearLayout) findViewById(R.id.sub_config);
        subUser = (LinearLayout) findViewById(R.id.sub_user);

        homeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        userButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Switch para navegar por las paginas con los botones del TabLayout hecho a mano
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

        //Llamamos a los metodos changeIcons(int) y changeTitle(int) pasándole la id de la imagen que hemos pulsado
        //para que colore y cambie el titulo dependiendo de cual hemos pulsado
        changeIcons(v.getId());
        changeTitle(v.getId());
    }

    //Función para cambiar los iconos y resaltarlos si los hemos pulsado
    private void changeIcons(int id) {
        //Si ha sido Search
        if (id == R.id.button_search) {
            //searchButton.setImageResource(R.drawable.search_black);
            searchButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
            subSearch.setVisibility(View.VISIBLE);

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subUser.setVisibility(View.INVISIBLE);

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);
        }

        //Si ha sido User
        if (id == R.id.button_user) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            //userButton.setImageResource(R.drawable.user_black);
            userButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
            subUser.setVisibility(View.VISIBLE);

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);
        }

        //Si ha sido Home
        if (id == R.id.button_home) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subUser.setVisibility(View.INVISIBLE);

            homeButton.setImageResource(R.drawable.icon_tab_center_black);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);
        }

        //Si ha sido Config
        if (id == R.id.button_config) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subUser.setVisibility(View.INVISIBLE);

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            //settingsButton.setImageResource(R.drawable.config_black);
            settingsButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
            subSettings.setVisibility(View.VISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);
        }

        //Si ha sido Info
        if (id == R.id.button_info) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subUser.setVisibility(View.INVISIBLE);

            homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            //infoButton.setImageResource(R.drawable.info_black);
            infoButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
            subInfo.setVisibility(View.VISIBLE);
        }

    }

    //Función para cambiar el titulo dependiendo de la opción que hemos pulsado
    private void changeTitle(int id) {
        switch (id) {
            case R.id.button_home:
                titleToolbar.setText(R.string.incidents);
                break;
            case R.id.button_search:
                titleToolbar.setText(R.string.search);
                break;
            case R.id.button_config:
                titleToolbar.setText(R.string.settings);
                break;
            case R.id.button_user:
                titleToolbar.setText(R.string.user_data);
                break;
            case R.id.button_info:
                titleToolbar.setText(R.string.info);
                break;
            default:
                titleToolbar.setText(R.string.alertru);
                break;
        }
    }
}
