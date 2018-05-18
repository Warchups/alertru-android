package com.gnommostudios.alertru.alertru_android.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.adapter.MyFragmentPagerAdapter
import com.gnommostudios.alertru.alertru_android.util.CustomViewPager
import com.gnommostudios.alertru.alertru_android.util.StatesLog

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var homeButton: ImageView? = null
    private var searchButton: ImageView? = null
    private var userButton: ImageView? = null
    private var settingsButton: ImageView? = null
    private var infoButton: ImageView? = null

    private var subSearch: LinearLayout? = null
    private var subUser: LinearLayout? = null
    private var subSettings: LinearLayout? = null
    private var subInfo: LinearLayout? = null

    private var titleToolbar: TextView? = null
    private var toolbar: Toolbar? = null
    private var viewPager: CustomViewPager? = null

    private var notification = false

    private var prefs: SharedPreferences? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            //Log.i("BROADCAST", "Hola");

            //Si el intent de broadcast que recibe no es para cambiar de titulo (es para mostrar notificacion),
            //compruebo el extra boolean de NOTIFICATION
            if (!intent.extras!!.getBoolean("CHANGE_TITLE")) {
                notification = intent.extras!!.getBoolean("NOTIFICATION")
                //Si es true cambio el icono de inicio a verde para que el usuario vea que hay una alerta nueva
                if (notification) {
                    homeButton!!.setImageResource(R.drawable.icon_tab_center_notification)
                    //Pongo un listener al icono para que mande un mensaje de broadcast para que se actualize la lista
                    homeButton!!.setOnClickListener {
                        val intent = Intent("AlertListFragment")
                        intent.putExtra("REFRESH", true)
                        //send broadcast
                        applicationContext.sendBroadcast(intent)
                        //Y pongo la pagina de la lista
                        viewPager!!.currentItem = 2

                        changeIcons(R.id.button_home)
                        changeTitle(R.id.button_home)
                    }
                } else {
                    //Si no es notificacion vuelvo a poner el listener en al que tiene en el main para poder navegar
                    //y quito el icono verde
                    if (viewPager!!.currentItem == 2) {
                        homeButton!!.setImageResource(R.drawable.icon_tab_center_black)
                        homeButton!!.setOnClickListener(this@MainActivity)
                    }
                }
            } else {
                if (intent.extras!!.getBoolean("REFRESH")) {
                    val intent2 = Intent("AlertListFragment")
                    intent2.putExtra("REFRESH", true)
                    //send broadcast
                    applicationContext.sendBroadcast(intent2)
                    //Y pongo la pagina de la lista
                    viewPager!!.currentItem = 2
                } else {
                    //Si el intent es desde main
                    if (intent.extras!!.getBoolean("IS_MAIN")) {
                        //Si la pagina es home
                        if (intent.extras!!.getString("PAGE") == "HOME") {
                            val containerList = findViewById<View>(R.id.container_list) as ConstraintLayout
                            val layoutDetails = findViewById<View>(R.id.layout_detail) as LinearLayout

                            if (layoutDetails.visibility == View.VISIBLE) {
                                containerList.visibility = View.VISIBLE
                                layoutDetails.visibility = View.GONE
                            }
                        }

                        //Si la pagina es search
                        if (intent.extras!!.getString("PAGE") == "SEARCH") {
                            val searchForm = findViewById<View>(R.id.search_form) as LinearLayout
                            val searchList = findViewById<View>(R.id.search_list) as LinearLayout
                            val searchDetails = findViewById<View>(R.id.details_search) as LinearLayout

                            if (searchList.visibility == View.VISIBLE || searchDetails.visibility == View.VISIBLE) {
                                searchList.visibility = View.GONE
                                searchForm.visibility = View.VISIBLE
                                searchDetails.visibility = View.GONE
                            }
                        }
                    } else {
                        //Si el intent es para cambiar el titulo lo recojo
                        val title = intent.extras!!.getString("TITLE")
                        if (title == "Iniciar Sesión" || title == "Datos de usuario" && viewPager!!.currentItem == 0) {
                            titleToolbar!!.text = title
                        } else if (title == "Detalles" || title == "Búsqueda" && viewPager!!.currentItem == 1) {
                            titleToolbar!!.text = title
                        } else if (title == "Detalles" || title == "Incidencias" && viewPager!!.currentItem == 2) {
                            titleToolbar!!.text = title
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)

        initCustomTab()

        initTitle()

        initFragments()

        //MyBroadcastReceiver mMessageReceiver = new MyBroadcastReceiver(homeButton);
        registerReceiver(mMessageReceiver, IntentFilter("MainActivity"))
    }

    private fun initFragments() {
        viewPager = findViewById<View>(R.id.view_pager_main) as CustomViewPager

        viewPager!!.adapter = MyFragmentPagerAdapter(supportFragmentManager)

        //Deshabilitamos el scroll de las paginas del ViewPager desde la clase CustomViewPager
        viewPager!!.disableScroll(true)

        //Ponemos el ViewPager en la pagina que recogemos desde els SplashScreen dependiendo de las comprobaciones del token
        viewPager!!.currentItem = intent.extras!!.getInt("PAGE")
    }

    private fun initTitle() {
        titleToolbar = findViewById<View>(R.id.titleToolbar) as TextView
        toolbar = findViewById<View>(R.id.toolbarMain) as Toolbar
        setSupportActionBar(toolbar)
        //Deshabilitamos el titulo del ToolBar
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (intent.extras!!.getInt("PAGE") == 2) {
            //Llamamos al método changeTitle(int) pasándole la id de R.id.button_home para que muestre el titulo de home
            changeTitle(R.id.button_home)
            //Llamamos el método changeIcons(int) pasándole la id de R.id.button_home para que muestre seleccionado el home
            changeIcons(R.id.button_home)
        }

        if (intent.extras!!.getInt("PAGE") == 0) {
            //Llamamos al método changeTitle(int) pasándole la ide de R.id.button_user para que muestre el titulo de user
            changeTitle(R.id.button_user)
            //Llamamos el método changeIcons(int) pasándole la id de R.id.button_user para que muestre seleccionado el user
            changeIcons(R.id.button_user)
        }
    }

    private fun initCustomTab() {
        homeButton = findViewById<View>(R.id.button_home) as ImageView
        searchButton = findViewById<View>(R.id.button_search) as ImageView
        userButton = findViewById<View>(R.id.button_user) as ImageView
        settingsButton = findViewById<View>(R.id.button_config) as ImageView
        infoButton = findViewById<View>(R.id.button_info) as ImageView

        subSearch = findViewById<View>(R.id.sub_search) as LinearLayout
        subInfo = findViewById<View>(R.id.sub_info) as LinearLayout
        subSettings = findViewById<View>(R.id.sub_config) as LinearLayout
        subUser = findViewById<View>(R.id.sub_user) as LinearLayout

        homeButton!!.setOnClickListener(this)
        searchButton!!.setOnClickListener(this)
        userButton!!.setOnClickListener(this)
        settingsButton!!.setOnClickListener(this)
        infoButton!!.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        //Switch para navegar por las paginas con los botones del TabLayout hecho a mano
        when (v.id) {
            R.id.button_search -> viewPager!!.currentItem = 1
            R.id.button_user -> viewPager!!.currentItem = 0
            R.id.button_home -> viewPager!!.currentItem = 2
            R.id.button_config -> viewPager!!.currentItem = 3
            R.id.button_info -> viewPager!!.currentItem = 4
        }

        //Llamamos a los metodos changeIcons(int) y changeTitle(int) pasándole la id de la imagen que hemos pulsado
        //para que colore y cambie el titulo dependiendo de cual hemos pulsado
        changeIcons(v.id)
        changeTitle(v.id)
    }

    //Función para cambiar los iconos y resaltarlos si los hemos pulsado
    private fun changeIcons(id: Int) {
        //Si ha sido Search
        if (id == R.id.button_search) {
            //searchButton.setImageResource(R.drawable.search_black);
            searchButton!!.background = resources.getDrawable(R.drawable.degraded_tab_item)
            subSearch!!.visibility = View.VISIBLE

            userButton!!.setImageResource(R.drawable.user_white)
            userButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subUser!!.visibility = View.INVISIBLE

            if (!notification)
                homeButton!!.setImageResource(R.drawable.icon_tab_center_white)

            settingsButton!!.setImageResource(R.drawable.config_white)
            settingsButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSettings!!.visibility = View.INVISIBLE

            infoButton!!.setImageResource(R.drawable.info_white)
            infoButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subInfo!!.visibility = View.INVISIBLE

            val intent = Intent("MainActivity")
            intent.putExtra("CHANGE_TITLE", true)
            intent.putExtra("IS_MAIN", true)
            intent.putExtra("PAGE", "SEARCH")
            //send broadcast
            sendBroadcast(intent)
        }

        //Si ha sido User
        if (id == R.id.button_user) {
            searchButton!!.setImageResource(R.drawable.search_white)
            searchButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSearch!!.visibility = View.INVISIBLE

            //userButton.setImageResource(R.drawable.user_black);
            userButton!!.background = resources.getDrawable(R.drawable.degraded_tab_item)
            subUser!!.visibility = View.VISIBLE

            if (!notification)
                homeButton!!.setImageResource(R.drawable.icon_tab_center_white)

            settingsButton!!.setImageResource(R.drawable.config_white)
            settingsButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSettings!!.visibility = View.INVISIBLE

            infoButton!!.setImageResource(R.drawable.info_white)
            infoButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subInfo!!.visibility = View.INVISIBLE
        }

        //Si ha sido Home
        if (id == R.id.button_home) {
            searchButton!!.setImageResource(R.drawable.search_white)
            searchButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSearch!!.visibility = View.INVISIBLE

            userButton!!.setImageResource(R.drawable.user_white)
            userButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subUser!!.visibility = View.INVISIBLE

            if (!notification)
                homeButton!!.setImageResource(R.drawable.icon_tab_center_black)

            settingsButton!!.setImageResource(R.drawable.config_white)
            settingsButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSettings!!.visibility = View.INVISIBLE

            infoButton!!.setImageResource(R.drawable.info_white)
            infoButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subInfo!!.visibility = View.INVISIBLE

            val intent = Intent("MainActivity")
            intent.putExtra("CHANGE_TITLE", true)
            intent.putExtra("IS_MAIN", true)
            intent.putExtra("PAGE", "HOME")
            //send broadcast
            sendBroadcast(intent)
        }

        //Si ha sido Config
        if (id == R.id.button_config) {
            searchButton!!.setImageResource(R.drawable.search_white)
            searchButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSearch!!.visibility = View.INVISIBLE

            userButton!!.setImageResource(R.drawable.user_white)
            userButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subUser!!.visibility = View.INVISIBLE

            if (!notification)
                homeButton!!.setImageResource(R.drawable.icon_tab_center_white)

            //settingsButton.setImageResource(R.drawable.config_black);
            settingsButton!!.background = resources.getDrawable(R.drawable.degraded_tab_item)
            subSettings!!.visibility = View.VISIBLE

            infoButton!!.setImageResource(R.drawable.info_white)
            infoButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subInfo!!.visibility = View.INVISIBLE
        }

        //Si ha sido Info
        if (id == R.id.button_info) {
            searchButton!!.setImageResource(R.drawable.search_white)
            searchButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSearch!!.visibility = View.INVISIBLE

            userButton!!.setImageResource(R.drawable.user_white)
            userButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subUser!!.visibility = View.INVISIBLE

            if (!notification)
                homeButton!!.setImageResource(R.drawable.icon_tab_center_white)

            settingsButton!!.setImageResource(R.drawable.config_white)
            settingsButton!!.setBackgroundColor(resources.getColor(R.color.colorTabLayout))
            subSettings!!.visibility = View.INVISIBLE

            //infoButton.setImageResource(R.drawable.info_black);
            infoButton!!.background = resources.getDrawable(R.drawable.degraded_tab_item)
            subInfo!!.visibility = View.VISIBLE
        }

    }

    //Función para cambiar el titulo dependiendo de la opción que hemos pulsado
    private fun changeTitle(id: Int) {
        when (id) {
            R.id.button_home -> titleToolbar!!.setText(R.string.incidents)
            R.id.button_search -> titleToolbar!!.setText(R.string.search)
            R.id.button_config -> titleToolbar!!.setText(R.string.settings)
            R.id.button_user -> if (prefs!!.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED) == StatesLog.LOGGED)
                titleToolbar!!.text = "Datos de usuario"
            else
                titleToolbar!!.text = "Iniciar Sesión"
            R.id.button_info -> titleToolbar!!.setText(R.string.info)
        }
    }

    override fun onBackPressed() {
        //Si estoy en la pagina del search o de la lista juego con las visibilidades de los detalles y lista
        if (viewPager!!.currentItem == 1) {
            val searchForm = findViewById<View>(R.id.search_form) as LinearLayout
            val searchList = findViewById<View>(R.id.search_list) as LinearLayout
            val searchDetails = findViewById<View>(R.id.details_search) as LinearLayout

            if (searchList.visibility == View.VISIBLE) {
                searchList.visibility = View.GONE
                searchForm.visibility = View.VISIBLE
            } else if (searchDetails.visibility == View.VISIBLE) {
                searchDetails.visibility = View.GONE
                searchList.visibility = View.VISIBLE
                titleToolbar!!.text = "Búsqueda"
            } else {
                super.onBackPressed()
            }

        } else if (viewPager!!.currentItem == 2) {
            val containerList = findViewById<View>(R.id.container_list) as ConstraintLayout
            val layoutDetails = findViewById<View>(R.id.layout_detail) as LinearLayout

            if (layoutDetails.visibility == View.VISIBLE) {
                containerList.visibility = View.VISIBLE
                layoutDetails.visibility = View.GONE
                titleToolbar!!.setText(R.string.incidents)
            } else {
                super.onBackPressed()
            }

        } else {
            super.onBackPressed()
        }

    }

}
