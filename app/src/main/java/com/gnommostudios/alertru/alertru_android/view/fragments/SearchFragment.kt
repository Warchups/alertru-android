package com.gnommostudios.alertru.alertru_android.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

import com.facebook.shimmer.ShimmerFrameLayout
import com.github.jorgecastilloprz.FABProgressCircle
import com.github.jorgecastilloprz.listeners.FABProgressListener
import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList
import com.gnommostudios.alertru.alertru_android.model.Alert
import com.gnommostudios.alertru.alertru_android.util.AuthenticationDialog
import com.gnommostudios.alertru.alertru_android.util.DatePicker
import com.gnommostudios.alertru.alertru_android.util.StatesLog
import com.gnommostudios.alertru.alertru_android.util.Urls

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class SearchFragment : Fragment(), View.OnClickListener, FABProgressListener, AdapterView.OnItemClickListener {

    private var searchForm: LinearLayout? = null
    private var searchList: LinearLayout? = null
    private var searchLoading: LinearLayout? = null

    private var titleSearch: TextView? = null
    private var withoutResults: TextView? = null

    private var dateEnter: TextView? = null
    private var dateExit: TextView? = null
    private var calendarStart: ImageView? = null
    private var calendarEnd: ImageView? = null

    private var searchOpen: RadioButton? = null
    private var searchClose: RadioButton? = null
    private var checkBoxSearch: CheckBox? = null
    private var btnSearch: Button? = null
    private var seeClosed: Button? = null
    private var dEnter: String? = null
    private var dExit: String? = null

    private var prefs: SharedPreferences? = null

    private var alertArrayList: ArrayList<Alert>? = null
    private var alertList: ListView? = null

    //Details
    private var layoutDetail: LinearLayout? = null

    private var containerAssigned: ConstraintLayout? = null
    private var containerUnassigned: ConstraintLayout? = null
    private var containerAssignedOwner: ConstraintLayout? = null

    private var alertDetail: Alert? = null

    //Assigned
    private var dateDetailAssigned: TextView? = null
    private var ownerDetail: TextView? = null
    private var provinceDetailAssigned: TextView? = null
    private var titleDetailAssigned: TextView? = null
    private var descriptionDetailAssigned: TextView? = null

    //Unassigned
    private var dateDetailUnassigned: TextView? = null
    private var provinceDetailUnassigned: TextView? = null
    private var titleDetailUnassigned: TextView? = null
    private var descriptionDetailUnassigned: TextView? = null

    //Assigned owner
    private var dateDetailAssignedOwner: TextView? = null
    private var ownerDetailOwner: TextView? = null
    private var provinceDetailAssignedOwner: TextView? = null
    private var titleDetailAssignedOwner: TextView? = null
    private var descriptionDetailAssignedOwner: TextView? = null

    private var writeDetails: ImageView? = null
    private var closedDetails: ImageView? = null
    private var ownerDetailsImg: ImageView? = null

    private var partTextView: TextView? = null
    private var editTextPart: EditText? = null
    private var cardCloseAlert: CardView? = null
    private var closeAlert: Button? = null

    private var shimmerCloseAlert: ShimmerFrameLayout? = null

    private var assingFAB: FloatingActionButton? = null
    private var fabProgressCircle: FABProgressCircle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_search, container, false)

        prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE)

        titleSearch = view.findViewById<View>(R.id.search_title) as TextView
        withoutResults = view.findViewById<View>(R.id.without_results) as TextView
        withoutResults!!.visibility = View.GONE

        alertList = view.findViewById<View>(R.id.search_list_view) as ListView
        alertList!!.visibility = View.GONE

        searchForm = view.findViewById<View>(R.id.search_form) as LinearLayout
        searchForm!!.visibility = View.VISIBLE

        searchList = view.findViewById<View>(R.id.search_list) as LinearLayout
        searchList!!.visibility = View.GONE

        searchLoading = view.findViewById<View>(R.id.search_loading) as LinearLayout
        searchLoading!!.visibility = View.GONE

        dateEnter = view.findViewById<View>(R.id.txtDateEnter) as TextView
        dateExit = view.findViewById<View>(R.id.txtDateExit) as TextView

        calendarStart = view.findViewById<View>(R.id.calendar_start) as ImageView
        calendarEnd = view.findViewById<View>(R.id.calendar_end) as ImageView

        val font = Typeface.createFromAsset(activity.assets, "fonts/walkway_ultrabold.ttf")

        searchOpen = view.findViewById<View>(R.id.radioOpenSearch) as RadioButton
        searchClose = view.findViewById<View>(R.id.radioCloseSearch) as RadioButton

        checkBoxSearch = view.findViewById<View>(R.id.checkSearchOpenAndExit) as CheckBox

        searchOpen!!.typeface = font
        searchClose!!.typeface = font
        checkBoxSearch!!.typeface = font

        btnSearch = view.findViewById<View>(R.id.searchButton) as Button
        btnSearch!!.setOnClickListener(this)

        seeClosed = view.findViewById<View>(R.id.searchFinalized) as Button
        seeClosed!!.setOnClickListener(this)

        dateEnter!!.setOnClickListener { showDatePickerDialogEnterDate() }

        dateExit!!.setOnClickListener { showDatePickerDialogEndDate() }

        calendarStart!!.setOnClickListener { showDatePickerDialogEnterDate() }

        calendarEnd!!.setOnClickListener { showDatePickerDialogEndDate() }

        checkBoxSearch!!.setOnClickListener {
            if (checkBoxSearch!!.isChecked) {
                searchOpen!!.isEnabled = false
                searchClose!!.isEnabled = false
            } else {
                searchOpen!!.isEnabled = true
                searchClose!!.isEnabled = true
            }
        }

        /*****************************Details */
        layoutDetail = view.findViewById<View>(R.id.details_search) as LinearLayout
        layoutDetail!!.visibility = View.GONE

        containerAssigned = view.findViewById<View>(R.id.search_assigned) as ConstraintLayout
        containerAssigned!!.visibility = View.GONE

        containerUnassigned = view.findViewById<View>(R.id.search_unassigned) as ConstraintLayout
        containerUnassigned!!.visibility = View.GONE

        containerAssignedOwner = view.findViewById<View>(R.id.search_assigned_owner) as ConstraintLayout
        containerAssignedOwner!!.visibility = View.GONE

        /******Assigned */
        dateDetailAssigned = view.findViewById<View>(R.id.date_detail_assigned_search) as TextView
        ownerDetail = view.findViewById<View>(R.id.owner_detail_search) as TextView
        provinceDetailAssigned = view.findViewById<View>(R.id.province_detail_assigned_search) as TextView
        titleDetailAssigned = view.findViewById<View>(R.id.title_detail_assigned_search) as TextView
        descriptionDetailAssigned = view.findViewById<View>(R.id.description_detail_assigned_search) as TextView

        /*****Unassigned */
        dateDetailUnassigned = view.findViewById<View>(R.id.date_detail_unassigned_search) as TextView
        provinceDetailUnassigned = view.findViewById<View>(R.id.province_detail_unassigned_search) as TextView
        titleDetailUnassigned = view.findViewById<View>(R.id.title_detail_unassigned_search) as TextView
        descriptionDetailUnassigned = view.findViewById<View>(R.id.description_detail_unassigned_search) as TextView

        /******Assigned Owner */
        dateDetailAssignedOwner = view.findViewById<View>(R.id.date_detail_assigned_owner_search) as TextView
        ownerDetailOwner = view.findViewById<View>(R.id.owner_detail_owner_search) as TextView
        provinceDetailAssignedOwner = view.findViewById<View>(R.id.province_detail_assigned_owner_search) as TextView
        titleDetailAssignedOwner = view.findViewById<View>(R.id.title_detail_assigned_owner_search) as TextView
        descriptionDetailAssignedOwner = view.findViewById<View>(R.id.description_detail_assigned_owner_search) as TextView

        /**Finalized */
        writeDetails = view.findViewById<View>(R.id.write_detail_assigned_owner_search) as ImageView
        closedDetails = view.findViewById<View>(R.id.closed_detail_assigned_owner_search) as ImageView
        ownerDetailsImg = view.findViewById<View>(R.id.image_detail_assigned_owner_search) as ImageView
        partTextView = view.findViewById<View>(R.id.partTextView_search) as TextView
        editTextPart = view.findViewById<View>(R.id.editText_part_search) as EditText

        cardCloseAlert = view.findViewById<View>(R.id.card_close_alert_search) as CardView
        shimmerCloseAlert = view.findViewById<View>(R.id.shimmer_close_alert_search) as ShimmerFrameLayout

        closeAlert = view.findViewById<View>(R.id.close_alert_search) as Button
        closeAlert!!.setOnClickListener(this)

        assingFAB = view.findViewById<View>(R.id.assign_fab_search) as FloatingActionButton
        assingFAB!!.setOnClickListener(this)

        fabProgressCircle = view.findViewById<View>(R.id.fabProgressCircle_search) as FABProgressCircle
        fabProgressCircle!!.attachListener(this)

        /** */

        return view
    }

    private fun showDatePickerDialogEnterDate() {
        val newFragment = DatePicker.newInstance(DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val selectedDate: String
            if (month + 1 >= 10) {
                if (day >= 10)
                    selectedDate = day.toString() + "-" + (month + 1) + "-" + year
                else
                    selectedDate = "0" + day + "-" + (month + 1) + "-" + year
            } else {
                if (day >= 10)
                    selectedDate = day.toString() + "-0" + (month + 1) + "-" + year
                else
                    selectedDate = "0" + day + "-0" + (month + 1) + "-" + year
            }

            dateEnter!!.text = selectedDate
            dEnter = selectedDate
        }, "Fecha Inicio")
        newFragment.show(activity.supportFragmentManager, "datePicker")
    }

    private fun showDatePickerDialogEndDate() {
        val newFragment = DatePicker.newInstance(DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val selectedDate: String
            if (month + 1 >= 10) {
                if (day >= 10)
                    selectedDate = day.toString() + "-" + (month + 1) + "-" + year
                else
                    selectedDate = "0" + day + "-" + (month + 1) + "-" + year
            } else {
                if (day >= 10)
                    selectedDate = day.toString() + "-0" + (month + 1) + "-" + year
                else
                    selectedDate = "0" + day + "-0" + (month + 1) + "-" + year
            }

            dateExit!!.text = selectedDate
            dExit = selectedDate
        }, "Fecha Fin")
        newFragment.show(activity.supportFragmentManager, "datePicker")
    }

    private fun writeDetails(cabecera: String, contenido: String?, textView: TextView?) {
        val builder = SpannableStringBuilder()

        val cabeceraSpannable = SpannableString(cabecera)
        cabeceraSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, cabecera.length, 0)
        builder.append(cabeceraSpannable)

        val contenidoSpannable = SpannableString(contenido)
        contenidoSpannable.setSpan(null, 0, contenido!!.length, 0)
        builder.append(contenidoSpannable)

        textView!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    //Muestro los detalles de la alerta que le paso
    private fun showAlertDetails(alert: Alert) {
        //Mando un mensaje de broadcast para que desde el MainActivity cambie el titulo
        val intent = Intent("MainActivity")
        intent.putExtra("CHANGE_TITLE", true)
        intent.putExtra("REFRESH", false)
        intent.putExtra("IS_MAIN", false)
        intent.putExtra("TITLE", "Detalles")
        //send broadcast
        activity.sendBroadcast(intent)

        //Me guardo la alerta en una variable global para despues si quero asignarmela o cerrarla, poder acceder
        alertDetail = alert

        var cabecera: String
        var contenido: String?

        if (alert.isAssigned) {
            if (alert.idTechnician != prefs!!.getString("id", "")) {
                dateDetailAssigned!!.text = alert.date

                /*****Technician */
                val stidat = SelectTechnicianIDAsyncTask()
                stidat.execute(alert.idTechnician)

                /*****Province */
                cabecera = "Provincia: "
                contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

                writeDetails(cabecera, contenido, provinceDetailAssigned)

                /*****Title */
                cabecera = "Asunto Alerta: "
                contenido = alert.affair

                writeDetails(cabecera, contenido, titleDetailAssigned)

                /*****Description */
                cabecera = "Descripción: "
                contenido = alert.description

                writeDetails(cabecera, contenido, descriptionDetailAssigned)

                layoutDetail!!.visibility = View.VISIBLE
                containerAssigned!!.visibility = View.VISIBLE
                searchList!!.visibility = View.GONE
                containerUnassigned!!.visibility = View.GONE
                containerAssignedOwner!!.visibility = View.GONE
            } else {
                dateDetailAssignedOwner!!.text = alert.date

                /*****Technician */
                cabecera = "Técnico: "
                contenido = prefs!!.getString("surname", "") + ", " + prefs!!.getString("name", "")

                writeDetails(cabecera, contenido, ownerDetailOwner)

                /*****Province */
                cabecera = "Provincia: "
                contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

                writeDetails(cabecera, contenido, provinceDetailAssignedOwner)

                /*****Title */
                cabecera = "Asunto Alerta: "
                contenido = alert.affair

                writeDetails(cabecera, contenido, titleDetailAssignedOwner)

                /*****Description */
                cabecera = "Descripción: "
                contenido = alert.description

                writeDetails(cabecera, contenido, descriptionDetailAssignedOwner)

                layoutDetail!!.visibility = View.VISIBLE
                containerAssigned!!.visibility = View.GONE
                searchList!!.visibility = View.GONE
                containerUnassigned!!.visibility = View.GONE
                containerAssignedOwner!!.visibility = View.VISIBLE
                ownerDetailsImg!!.visibility = View.VISIBLE
                closedDetails!!.visibility = View.GONE
                writeDetails!!.visibility = View.GONE
                partTextView!!.visibility = View.GONE
                editTextPart!!.setText("")
                editTextPart!!.visibility = View.GONE
                shimmerCloseAlert!!.visibility = View.GONE


                if (alert.state == "finished") {
                    /*****Part */
                    cabecera = "Parte de la incidencia: "
                    contenido = ""

                    writeDetails(cabecera, contenido, partTextView)

                    writeDetails!!.visibility = View.VISIBLE
                    partTextView!!.visibility = View.VISIBLE
                    editTextPart!!.visibility = View.VISIBLE
                    shimmerCloseAlert!!.visibility = View.VISIBLE
                } else if (alert.state == "closed") {
                    /*****Part */
                    cabecera = "Parte de la incidencia: "
                    contenido = alert.notes

                    writeDetails(cabecera, contenido, partTextView)

                    ownerDetailsImg!!.visibility = View.GONE
                    closedDetails!!.visibility = View.VISIBLE
                    partTextView!!.visibility = View.VISIBLE
                    editTextPart!!.visibility = View.GONE
                    shimmerCloseAlert!!.visibility = View.GONE
                }
            }
        } else {
            dateDetailUnassigned!!.text = alert.date

            /*****Province */
            cabecera = "Provincia: "
            contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

            writeDetails(cabecera, contenido, provinceDetailUnassigned)

            /*****Title */
            cabecera = "Asunto Alerta: "
            contenido = alert.affair

            writeDetails(cabecera, contenido, titleDetailUnassigned)

            /*****Description */
            cabecera = "Descripción: "
            contenido = alert.description

            writeDetails(cabecera, contenido, descriptionDetailUnassigned)

            layoutDetail!!.visibility = View.VISIBLE
            containerAssigned!!.visibility = View.GONE
            searchList!!.visibility = View.GONE
            containerUnassigned!!.visibility = View.VISIBLE
            containerAssignedOwner!!.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        checkBoxSearch!!.isChecked = false
        searchOpen!!.isChecked = false
        searchClose!!.isChecked = false
        dateEnter!!.text = dEnter
        dateExit!!.text = dExit
    }

    override fun onClick(v: View) {
        if (prefs!!.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED) == StatesLog.LOGGED) {
            when (v.id) {
                R.id.searchFinalized, R.id.searchButton -> searchAlerts(v.id)
                R.id.close_alert_search -> {
                    val alertToClose = alertDetail

                    if (editTextPart!!.text.toString().length > 0) {
                        alertToClose!!.notes = editTextPart!!.text.toString()
                        val caat = CloseAlertAsyncTask()
                        caat.execute(alertToClose)
                    } else {
                        Toast.makeText(context, R.string.writeNotes, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.assign_fab_search -> {
                    val adaat = AssignDetailsAlertAsyncTask()
                    adaat.execute(alertDetail)
                }
            }
        } else {
            Toast.makeText(activity, "No has iniciado sesión.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchAlerts(id: Int) {
        // Options //
        // 0 - Assigned and unassigned with a date
        // 1 - Unassigned with a date
        // 2 - Assigned with a date
        // 3 - Closed from technician

        if (id == R.id.searchButton) {
            val dEnter = dateEnter!!.text.toString()
            val dExit = dateExit!!.text.toString()

            val searchOpenAndClosed = checkBoxSearch!!.isChecked
            val searchOpen = this.searchOpen!!.isChecked
            val searchClosed = this.searchClose!!.isChecked

            if (dEnter.length == 0 || dExit.length == 0) {
                Toast.makeText(activity, "Selecciona una fecha de entrada y una de salida", Toast.LENGTH_SHORT).show()
            } else {
                alertArrayList = ArrayList()

                val alat = AlertListAsyncTask()

                if (!searchOpenAndClosed && !searchOpen && !searchClosed) {
                    titleSearch!!.text = "Asignadas y sin asignar\nde $dEnter a $dExit:"
                    alat.execute(0)
                } else if (searchOpenAndClosed) {
                    titleSearch!!.text = "Asignadas y sin asignar\nde $dEnter a $dExit:"
                    alat.execute(0)
                } else {
                    if (searchOpen) {
                        titleSearch!!.text = "Sin asignar\nde $dEnter a $dExit:"
                        alat.execute(1)
                    }
                    if (searchClosed) {
                        titleSearch!!.text = "Asignadas\nde $dEnter a $dExit:"
                        alat.execute(2)
                    }
                }

            }
        }

        if (id == R.id.searchFinalized) {
            alertArrayList = ArrayList()

            titleSearch!!.text = "Tus alertas cerradas:"
            val calat = ClosedAlertListAsyncTask()
            calat.execute()
        }

    }

    //Cualquier alerta
    private fun createAlertsArray(alert: Alert, d: String) {
        val enter = dEnter!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayEnter = Integer.parseInt(enter[0])
        val monthEnter = Integer.parseInt(enter[1])
        val yearEnter = Integer.parseInt(enter[2])

        val dateEnter = Date()
        dateEnter.date = dayEnter
        dateEnter.month = monthEnter
        dateEnter.year = yearEnter

        val exit = dExit!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayExit = Integer.parseInt(exit[0])
        val monthExit = Integer.parseInt(exit[1])
        val yearExit = Integer.parseInt(exit[2])

        val dateExit = Date()
        dateExit.date = dayExit
        dateExit.month = monthExit
        dateExit.year = yearExit

        val alertDate = d.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = Integer.parseInt(alertDate[0])
        val month = Integer.parseInt(alertDate[1])
        val year = Integer.parseInt(alertDate[2])

        val date = Date()
        date.date = day
        date.month = month
        date.year = year

        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.time >= dateEnter.time && date.time <= dateExit.time)
            alertArrayList!!.add(alert)

    }

    //Alertas sin asignar
    private fun createAlertsArrayOpen(alert: Alert, d: String) {
        val enter = dEnter!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayEnter = Integer.parseInt(enter[0])
        val monthEnter = Integer.parseInt(enter[1])
        val yearEnter = Integer.parseInt(enter[2])

        val dateEnter = Date()
        dateEnter.date = dayEnter
        dateEnter.month = monthEnter
        dateEnter.year = yearEnter

        val exit = dExit!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayExit = Integer.parseInt(exit[0])
        val monthExit = Integer.parseInt(exit[1])
        val yearExit = Integer.parseInt(exit[2])

        val dateExit = Date()
        dateExit.date = dayExit
        dateExit.month = monthExit
        dateExit.year = yearExit

        val alertDate = d.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = Integer.parseInt(alertDate[0])
        val month = Integer.parseInt(alertDate[1])
        val year = Integer.parseInt(alertDate[2])

        val date = Date()
        date.date = day
        date.month = month
        date.year = year

        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.time >= dateEnter.time && date.time <= dateExit.time && !alert.isAssigned)
            alertArrayList!!.add(alert)

    }

    //Alertas asignadas
    private fun createAlertsArrayClosed(alert: Alert, d: String) {
        val enter = dEnter!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayEnter = Integer.parseInt(enter[0])
        val monthEnter = Integer.parseInt(enter[1])
        val yearEnter = Integer.parseInt(enter[2])

        val dateEnter = Date()
        dateEnter.date = dayEnter
        dateEnter.month = monthEnter
        dateEnter.year = yearEnter

        val exit = dExit!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val dayExit = Integer.parseInt(exit[0])
        val monthExit = Integer.parseInt(exit[1])
        val yearExit = Integer.parseInt(exit[2])

        val dateExit = Date()
        dateExit.date = dayExit
        dateExit.month = monthExit
        dateExit.year = yearExit

        val alertDate = d.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = Integer.parseInt(alertDate[0])
        val month = Integer.parseInt(alertDate[1])
        val year = Integer.parseInt(alertDate[2])

        val date = Date()
        date.date = day
        date.month = month
        date.year = year

        //Log.i("DATE", date.getTime() + "");
        //Log.i("ENTER", dateEnter.getTime() + "");
        //Log.i("EXIT", dateExit.getTime() + "");

        if (date.time >= dateEnter.time && date.time <= dateExit.time && alert.isAssigned)
            alertArrayList!!.add(alert)

    }

    override fun onItemClick(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        showAlertDetails(alertArrayList!![pos])
    }

    //AsyncTask para cargal la lista de alertas
    internal inner class AlertListAsyncTask : AsyncTask<Int, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            searchForm!!.visibility = View.GONE
            searchLoading!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg op: Int?): Boolean? {
            try {
                val url = URL(Urls.GET_ALERT_LIST + prefs!!.getString("id", "") +
                        "/get-alerts-by-owner-province?access_token=" + prefs!!.getString("access_token", ""))
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()


                val respuesta = connection.responseCode

                //Log.i("EEE", "Llego aqui");
                val result = StringBuilder()

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    val br = BufferedReader(InputStreamReader(connection.inputStream))
                    result.append(br.readLine())

                    val alerts = JSONArray(result.toString())

                    for (i in 0 until alerts.length()) {
                        //Log.i("ALERT", alerts.get(i).toString());
                        val alert = alerts.get(i) as JSONObject

                        val id = alert.getString("id")
                        val affair = alert.getString("title")
                        val description = alert.getString("description")
                        val province = alert.getString("province")
                        val d = Date(java.lang.Long.parseLong(alert.getString("date")))
                        val sdf = SimpleDateFormat("dd-MM-yyyy")
                        val date = sdf.format(d)
                        val assigned = alert.getBoolean("assigned")
                        val state = alert.getString("state")

                        if (op[0] != 3) {
                            if (!assigned) {
                                if (op[0] == 0)
                                    createAlertsArray(Alert(id, affair, description, province, date, assigned, state), date)

                                if (op[0] == 1)
                                    createAlertsArrayOpen(Alert(id, affair, description, province, date, assigned, state), date)

                                if (op[0] == 2)
                                    createAlertsArrayClosed(Alert(id, affair, description, province, date, assigned, state), date)
                            } else {
                                if (state != "closed") {
                                    val idTechnician = alert.getString("owner")
                                    if (op[0] == 0)
                                        createAlertsArray(Alert(id, affair, description, province, date, idTechnician, assigned, state), date)

                                    if (op[0] == 1)
                                        createAlertsArrayOpen(Alert(id, affair, description, province, date, idTechnician, assigned, state), date)

                                    if (op[0] == 2)
                                        createAlertsArrayClosed(Alert(id, affair, description, province, date, idTechnician, assigned, state), date)
                                }
                            }
                        } else {
                            if (assigned) {
                                val idTechnician = alert.getString("owner")
                                if (state == "closed" && idTechnician == prefs!!.getString("id", "")) {
                                    val note = alert.getString("note")
                                    alertArrayList!!.add(Alert(id, affair, description, province, date, idTechnician, assigned, state, note))
                                }
                            }
                        }
                    }

                    return true
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(correct: Boolean?) {
            super.onPostExecute(correct)
            searchLoading!!.visibility = View.GONE

            if (correct!!) {
                setAdapter()
                searchList!!.visibility = View.VISIBLE
                if (alertArrayList!!.size == 0) {
                    withoutResults!!.visibility = View.VISIBLE
                    alertList!!.visibility = View.GONE
                } else {
                    withoutResults!!.visibility = View.GONE
                    alertList!!.visibility = View.VISIBLE
                }
            } else {
                val dialog = AuthenticationDialog()
                dialog.show(fragmentManager, "CONNECTION_ERROR")
                searchForm!!.visibility = View.VISIBLE
            }
        }
    }

    //AsyncTask para cargal la lista de alertas cerradas
    internal inner class ClosedAlertListAsyncTask : AsyncTask<Int, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            searchForm!!.visibility = View.GONE
            searchLoading!!.visibility = View.VISIBLE
        }

        protected override fun doInBackground(vararg op: Int?): Boolean? {
            try {
                val url = URL(Urls.GET_CLOSED_ALERT_LIST + prefs!!.getString("id", "") +
                        "/get-alerts-closed-by-owner?access_token=" + prefs!!.getString("access_token", ""))
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()


                val respuesta = connection.responseCode

                //Log.i("EEE", "Llego aqui");
                val result = StringBuilder()

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    val br = BufferedReader(InputStreamReader(connection.inputStream))
                    result.append(br.readLine())

                    val alerts = JSONArray(result.toString())

                    for (i in 0 until alerts.length()) {
                        //Log.i("ALERT", alerts.get(i).toString());
                        val alert = alerts.get(i) as JSONObject

                        val id = alert.getString("id")
                        val affair = alert.getString("title")
                        val description = alert.getString("description")
                        val province = alert.getString("province")
                        val d = Date(java.lang.Long.parseLong(alert.getString("date")))
                        val sdf = SimpleDateFormat("dd-MM-yyyy")
                        val date = sdf.format(d)
                        val assigned = alert.getBoolean("assigned")
                        val state = alert.getString("state")
                        val idTechnician = alert.getString("owner")
                        val note = alert.getString("note")

                        alertArrayList!!.add(Alert(id, affair, description, province, date, idTechnician, assigned, state, note))

                    }

                    return true
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(correct: Boolean?) {
            super.onPostExecute(correct)
            searchLoading!!.visibility = View.GONE

            if (correct!!) {
                setAdapter()
                searchList!!.visibility = View.VISIBLE
                if (alertArrayList!!.size == 0) {
                    withoutResults!!.visibility = View.VISIBLE
                    alertList!!.visibility = View.GONE
                } else {
                    withoutResults!!.visibility = View.GONE
                    alertList!!.visibility = View.VISIBLE
                }
            } else {
                val dialog = AuthenticationDialog()
                dialog.show(fragmentManager, "CONNECTION_ERROR")
                searchForm!!.visibility = View.VISIBLE
            }
        }
    }

    //AsyncTask para asignarse una alerta desde los detalles
    internal inner class AssignDetailsAlertAsyncTask : AsyncTask<Alert, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()

            fabProgressCircle!!.show()
        }

        override fun doInBackground(vararg alertsParams: Alert): Boolean? {
            try {
                val url = URL(Urls.ASSIGN_ALERT + prefs!!.getString("id", "") + "/assign-alert/" +
                        alertsParams[0].id + "?access_token=" + prefs!!.getString("access_token", ""))

                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()


                val respuesta = connection.responseCode

                return respuesta == HttpURLConnection.HTTP_OK

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(updated: Boolean?) {
            super.onPostExecute(updated)

            if (updated!!) {
                fabProgressCircle!!.beginFinalAnimation()
                //initList();
            }
        }
    }

    //AsyncTask para cerrar una alerta pasandole un parte
    internal inner class CloseAlertAsyncTask : AsyncTask<Alert, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()

            shimmerCloseAlert!!.startShimmerAnimation()
        }

        override fun doInBackground(vararg alertsParams: Alert): Boolean? {
            try {
                val url = URL(Urls.CLOSE_ALERTS + alertsParams[0].id + "/close-alert"
                        + "?access_token=" + prefs!!.getString("access_token", ""))

                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()

                val jsonParam = JSONObject()
                jsonParam.put("note", alertsParams[0].notes)

                //Log.i("JSON", jsonParam.toString());

                val os = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))

                writer.write(jsonParam.toString())

                writer.flush()
                writer.close()

                val respuesta = connection.responseCode

                return respuesta == HttpURLConnection.HTTP_OK

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return false
        }

        override fun onPostExecute(updated: Boolean?) {
            super.onPostExecute(updated)

            shimmerCloseAlert!!.stopShimmerAnimation()

            if (updated!!) {
                fromDetailsToList()
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //AsyncTask para comprobar el nombre y los apellidos pasandole la id
    internal inner class SelectTechnicianIDAsyncTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

            val cabecera = "Técnico: "
            val contenido = "cargando..."

            writeDetails(cabecera, contenido, ownerDetail)
        }

        override fun doInBackground(vararg ids: String): String {
            try {
                val url = URL(Urls.SELECT_ID + ids[0] + "?access_token=" + prefs!!.getString("access_token", ""))
                //Log.i("URL", url.toString());

                val connection = url.openConnection() as HttpURLConnection

                connection.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP")

                connection.connectTimeout = Urls.TIMEOUT
                connection.connect()

                val respuesta = connection.responseCode

                val resultSelect = StringBuilder()

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    val `in` = BufferedInputStream(connection.inputStream)

                    val reader = BufferedReader(InputStreamReader(`in`))

                    resultSelect.append(reader.readLine())

                    val respuestaJSONSelect = JSONObject(resultSelect.toString())

                    val name = respuestaJSONSelect.getString("name")
                    val surname = respuestaJSONSelect.getString("surname")

                    return "$surname, $name"
                }

                return ""

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(name: String) {
            super.onPostExecute(name)

            if (name != "") {
                val cabecera = "Técnico: "

                writeDetails(cabecera, name, ownerDetail)
            } else {
                Toast.makeText(context, "Error: No se puede ver el tecnico de la alerta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFABProgressAnimationEnd() {
        fabProgressCircle!!.hide()

        Snackbar.make(fabProgressCircle!!, "Alerta Asignada", Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .show()

        fromDetailsToList()
    }

    private fun fromDetailsToList() {
        val intent = Intent("MainActivity")
        intent.putExtra("CHANGE_TITLE", true)
        intent.putExtra("REFRESH", false)
        intent.putExtra("IS_MAIN", false)
        intent.putExtra("TITLE", "Búsqueda")
        //send broadcast
        activity.sendBroadcast(intent)
        searchAlerts(R.id.searchButton)
        layoutDetail!!.visibility = View.GONE
        searchList!!.visibility = View.VISIBLE
        searchForm!!.visibility = View.GONE
    }

    fun setAdapter() {
        alertList!!.adapter = AdapterAlertList(this, alertArrayList!!)
        alertList!!.onItemClickListener = this
    }

}
