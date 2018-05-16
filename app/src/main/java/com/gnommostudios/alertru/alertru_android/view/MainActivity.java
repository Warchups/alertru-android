package com.gnommostudios.alertru.alertru_android.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gnommostudios.alertru.alertru_android.R;
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter;
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager;
import com.gnommostudios.alertru.alertru_android.util.StatesLog;

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
    private CustomViewPager viewPager;

    private boolean notification = false;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);

        initCustomTab();

        initTitle();

        initFragments();

        //MyBroadcastReceiver mMessageReceiver = new MyBroadcastReceiver(homeButton);
        registerReceiver(mMessageReceiver, new IntentFilter("MainActivity"));
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
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        //Deshabilitamos el titulo del ToolBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getExtras().getInt("PAGE") == 2) {
            //Llamamos al método changeTitle(int) pasándole la id de R.id.button_home para que muestre el titulo de home
            changeTitle(R.id.button_home);
            //Llamamos el método changeIcons(int) pasándole la id de R.id.button_home para que muestre seleccionado el home
            changeIcons(R.id.button_home);
        }

        if (getIntent().getExtras().getInt("PAGE") == 0) {
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
                viewPager.setCurrentItem(1);
                break;
            case R.id.button_user:
                viewPager.setCurrentItem(0);
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

            if (!notification)
                homeButton.setImageResource(R.drawable.icon_tab_center_white);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);

            Intent intent = new Intent("MainActivity");
            intent.putExtra("CHANGE_TITLE", true);
            intent.putExtra("IS_MAIN", true);
            intent.putExtra("PAGE", "SEARCH");
            //send broadcast
            sendBroadcast(intent);
        }

        //Si ha sido User
        if (id == R.id.button_user) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            //userButton.setImageResource(R.drawable.user_black);
            userButton.setBackground(getResources().getDrawable(R.drawable.degraded_tab_item));
            subUser.setVisibility(View.VISIBLE);

            if (!notification)
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

            if (!notification)
                homeButton.setImageResource(R.drawable.icon_tab_center_black);

            settingsButton.setImageResource(R.drawable.config_white);
            settingsButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSettings.setVisibility(View.INVISIBLE);

            infoButton.setImageResource(R.drawable.info_white);
            infoButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subInfo.setVisibility(View.INVISIBLE);

            Intent intent = new Intent("MainActivity");
            intent.putExtra("CHANGE_TITLE", true);
            intent.putExtra("IS_MAIN", true);
            intent.putExtra("PAGE", "HOME");
            //send broadcast
            sendBroadcast(intent);
        }

        //Si ha sido Config
        if (id == R.id.button_config) {
            searchButton.setImageResource(R.drawable.search_white);
            searchButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subSearch.setVisibility(View.INVISIBLE);

            userButton.setImageResource(R.drawable.user_white);
            userButton.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
            subUser.setVisibility(View.INVISIBLE);

            if (!notification)
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

            if (!notification)
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
                if (prefs.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED).equals(StatesLog.LOGGED))
                    titleToolbar.setText("Datos de usuario");
                else
                    titleToolbar.setText("Iniciar Sesión");
                break;
            case R.id.button_info:
                titleToolbar.setText(R.string.info);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //Si estoy en la pagina del search o de la lista juego con las visibilidades de los detalles y lista
        if (viewPager.getCurrentItem() == 1) {
            LinearLayout searchForm = (LinearLayout) findViewById(R.id.search_form);
            LinearLayout searchList = (LinearLayout) findViewById(R.id.search_list);
            LinearLayout searchDetails = (LinearLayout) findViewById(R.id.details_search);

            if (searchList.getVisibility() == View.VISIBLE) {
                searchList.setVisibility(View.GONE);
                searchForm.setVisibility(View.VISIBLE);
            } else if (searchDetails.getVisibility() == View.VISIBLE) {
                searchDetails.setVisibility(View.GONE);
                searchList.setVisibility(View.VISIBLE);
                titleToolbar.setText("Búsqueda");
            } else {
                super.onBackPressed();
            }

        } else if (viewPager.getCurrentItem() == 2) {
            ConstraintLayout containerList = (ConstraintLayout) findViewById(R.id.container_list);
            LinearLayout layoutDetails = (LinearLayout) findViewById(R.id.layout_detail);

            if (layoutDetails.getVisibility() == View.VISIBLE) {
                containerList.setVisibility(View.VISIBLE);
                layoutDetails.setVisibility(View.GONE);
                titleToolbar.setText(R.string.incidents);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //Log.i("BROADCAST", "Hola");

            //Si el intent de broadcast que recibe no es para cambiar de titulo (es para mostrar notificacion),
            //compruebo el extra boolean de NOTIFICATION
            if (!intent.getExtras().getBoolean("CHANGE_TITLE")) {
                notification = intent.getExtras().getBoolean("NOTIFICATION");
                //Si es true cambio el icono de inicio a verde para que el usuario vea que hay una alerta nueva
                if (notification) {
                    homeButton.setImageResource(R.drawable.icon_tab_center_notification);
                    //Pongo un listener al icono para que mande un mensaje de broadcast para que se actualize la lista
                    homeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("AlertListFragment");
                            intent.putExtra("REFRESH", true);
                            //send broadcast
                            getApplicationContext().sendBroadcast(intent);
                            //Y pongo la pagina de la lista
                            viewPager.setCurrentItem(2);

                            changeIcons(R.id.button_home);
                            changeTitle(R.id.button_home);
                        }
                    });
                } else {
                    //Si no es notificacion vuelvo a poner el listener en al que tiene en el main para poder navegar
                    //y quito el icono verde
                    if (viewPager.getCurrentItem() == 2) {
                        homeButton.setImageResource(R.drawable.icon_tab_center_black);
                        homeButton.setOnClickListener(MainActivity.this);
                    }
                }
            } else {
                if (intent.getExtras().getBoolean("REFRESH")) {
                    Intent intent2 = new Intent("AlertListFragment");
                    intent2.putExtra("REFRESH", true);
                    //send broadcast
                    getApplicationContext().sendBroadcast(intent2);
                    //Y pongo la pagina de la lista
                    viewPager.setCurrentItem(2);
                } else {
                    //Si el intent es desde main
                    if (intent.getExtras().getBoolean("IS_MAIN")) {
                        //Si la pagina es home
                        if (intent.getExtras().getString("PAGE").equals("HOME")) {
                            ConstraintLayout containerList = (ConstraintLayout) findViewById(R.id.container_list);
                            LinearLayout layoutDetails = (LinearLayout) findViewById(R.id.layout_detail);

                            if (layoutDetails.getVisibility() == View.VISIBLE) {
                                containerList.setVisibility(View.VISIBLE);
                                layoutDetails.setVisibility(View.GONE);
                            }
                        }

                        //Si la pagina es search
                        if (intent.getExtras().getString("PAGE").equals("SEARCH")) {
                            LinearLayout searchForm = (LinearLayout) findViewById(R.id.search_form);
                            LinearLayout searchList = (LinearLayout) findViewById(R.id.search_list);
                            LinearLayout searchDetails = (LinearLayout) findViewById(R.id.details_search);

                            if (searchList.getVisibility() == View.VISIBLE || searchDetails.getVisibility() == View.VISIBLE) {
                                searchList.setVisibility(View.GONE);
                                searchForm.setVisibility(View.VISIBLE);
                                searchDetails.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        //Si el intent es para cambiar el titulo lo recojo
                        String title = intent.getExtras().getString("TITLE");
                        if (title.equals("Iniciar Sesión") || title.equals("Datos de usuario") && viewPager.getCurrentItem() == 0) {
                            titleToolbar.setText(title);
                        } else if (title.equals("Detalles") || title.equals("Búsqueda") && viewPager.getCurrentItem() == 1) {
                            titleToolbar.setText(title);
                        } else if (title.equals("Detalles") || title.equals("Incidencias") && viewPager.getCurrentItem() == 2) {
                            titleToolbar.setText(title);
                        }
                    }
                }
            }
        }
    };

}
