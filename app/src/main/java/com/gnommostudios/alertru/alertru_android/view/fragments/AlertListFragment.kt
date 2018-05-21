package com.gnommostudios.alertru.alertru_android.view.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.facebook.shimmer.ShimmerFrameLayout
import com.github.jorgecastilloprz.FABProgressCircle
import com.github.jorgecastilloprz.listeners.FABProgressListener
import com.gnommostudios.alertru.alertru_android.R
import com.gnommostudios.alertru.alertru_android.adapter.AdapterAlertList
import com.gnommostudios.alertru.alertru_android.model.Alert
import com.gnommostudios.alertru.alertru_android.util.AuthenticationDialog
import com.gnommostudios.alertru.alertru_android.util.StatesLog
import com.gnommostudios.alertru.alertru_android.util.Urls
import com.wang.avi.AVLoadingIndicatorView

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

class AlertListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, FABProgressListener, AbsListView.OnScrollListener {

    private var alertArrayList: ArrayList<Alert>? = null

    private var containerList: ConstraintLayout? = null
    private var layoutDisconnected: LinearLayout? = null

    private var prefs: SharedPreferences? = null

    private var alertList: ListView? = null
    private var refresh: SwipeRefreshLayout? = null

    private var withoutAlerts: TextView? = null

    private var loader: AVLoadingIndicatorView? = null

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
    private var partTextView: TextView? = null
    private var editTextPart: EditText? = null
    private var cardCloseAlert: CardView? = null
    private var closeAlert: Button? = null

    private var shimmerCloseAlert: ShimmerFrameLayout? = null

    private var assingFAB: FloatingActionButton? = null
    private var fabProgressCircle: FABProgressCircle? = null

    private var upFAB: FloatingActionButton? = null

    private var isFirst = true
    private var lastCharge = -1

    //BroadcastReveiver
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            //Si el intent es para refescar inicio la lista
            if (intent.extras!!.getBoolean("REFRESH")) {
                initList("0")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_alert_list, container, false)

        prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE)

        containerList = view.findViewById<View>(R.id.container_list) as ConstraintLayout
        containerList!!.visibility = View.VISIBLE

        /*****************************Details*****************************/
        layoutDetail = view.findViewById<View>(R.id.layout_detail) as LinearLayout
        layoutDetail!!.visibility = View.GONE

        containerAssigned = view.findViewById<View>(R.id.container_assigned) as ConstraintLayout
        containerAssigned!!.visibility = View.GONE

        containerUnassigned = view.findViewById<View>(R.id.container_unassigned) as ConstraintLayout
        containerUnassigned!!.visibility = View.GONE

        containerAssignedOwner = view.findViewById<View>(R.id.container_assigned_owner) as ConstraintLayout
        containerAssignedOwner!!.visibility = View.GONE

        /******Assigned*****/
        dateDetailAssigned = view.findViewById<View>(R.id.date_detail_assigned) as TextView
        ownerDetail = view.findViewById<View>(R.id.owner_detail) as TextView
        provinceDetailAssigned = view.findViewById<View>(R.id.province_detail_assigned) as TextView
        titleDetailAssigned = view.findViewById<View>(R.id.title_detail_assigned) as TextView
        descriptionDetailAssigned = view.findViewById<View>(R.id.description_detail_assigned) as TextView

        /*****Unassigned*****/
        dateDetailUnassigned = view.findViewById<View>(R.id.date_detail_unassigned) as TextView
        provinceDetailUnassigned = view.findViewById<View>(R.id.province_detail_unassigned) as TextView
        titleDetailUnassigned = view.findViewById<View>(R.id.title_detail_unassigned) as TextView
        descriptionDetailUnassigned = view.findViewById<View>(R.id.description_detail_unassigned) as TextView

        /******Assigned Owner*****/
        dateDetailAssignedOwner = view.findViewById<View>(R.id.date_detail_assigned_owner) as TextView
        ownerDetailOwner = view.findViewById<View>(R.id.owner_detail_owner) as TextView
        provinceDetailAssignedOwner = view.findViewById<View>(R.id.province_detail_assigned_owner) as TextView
        titleDetailAssignedOwner = view.findViewById<View>(R.id.title_detail_assigned_owner) as TextView
        descriptionDetailAssignedOwner = view.findViewById<View>(R.id.description_detail_assigned_owner) as TextView

        /*****Finalized*****/
        partTextView = view.findViewById<View>(R.id.partTextView) as TextView
        editTextPart = view.findViewById<View>(R.id.editText_part) as EditText
        writeDetails = view.findViewById<View>(R.id.write_detail_assigned_owner) as ImageView

        cardCloseAlert = view.findViewById<View>(R.id.card_close_alert) as CardView
        shimmerCloseAlert = view.findViewById<View>(R.id.shimmer_close_alert) as ShimmerFrameLayout

        closeAlert = view.findViewById<View>(R.id.close_alert) as Button
        closeAlert!!.setOnClickListener(this)

        assingFAB = view.findViewById<View>(R.id.assign_fab) as FloatingActionButton
        assingFAB!!.setOnClickListener(this)

        fabProgressCircle = view.findViewById<View>(R.id.fabProgressCircle) as FABProgressCircle
        fabProgressCircle!!.attachListener(this)

        /**********************************************************/

        layoutDisconnected = view.findViewById<View>(R.id.layout_disconnected) as LinearLayout
        layoutDisconnected!!.visibility = View.GONE
        layoutDisconnected!!.setOnClickListener(this)

        loader = view.findViewById<View>(R.id.avi) as AVLoadingIndicatorView

        alertList = view.findViewById<View>(R.id.alert_list) as ListView
        withoutAlerts = view.findViewById<View>(R.id.without_alerts) as TextView
        refresh = view.findViewById<View>(R.id.refesh_layout) as SwipeRefreshLayout
        refresh!!.setOnRefreshListener(this)

        upFAB = view.findViewById<View>(R.id.up_fab) as FloatingActionButton
        upFAB!!.hide()
        upFAB!!.setOnClickListener(this)

        //Inicio la lista pasandole 0 para indicarle que es el primero
        initList("0")

        activity.registerReceiver(mMessageReceiver, IntentFilter("AlertListFragment"))

        return view
    }

    private fun initList(skip: String) {
        //Pongo los detalles en GONE, por si estaban mostrandose
        layoutDetail!!.visibility = View.GONE

        if (skip == "0")
            lastCharge = -1

        //Si esta logueado llamo muestro el contenedor de la lista y
        //llamo a la funcion asincrona que hace la consulta de las alertas
        if (prefs!!.getString(StatesLog.STATE_LOG, StatesLog.DISCONNECTED) == StatesLog.LOGGED) {
            containerList!!.visibility = View.VISIBLE
            layoutDisconnected!!.visibility = View.GONE
            val alat = AlertListAsyncTask()
            alat.execute(skip)
        } else {
            //Si no esta logueado oculto el contenedor de la lista y muestro el layout que te dice que no estas logueado
            containerList!!.visibility = View.GONE
            layoutDisconnected!!.visibility = View.VISIBLE
        }
    }

    private fun setAdapter() {
        if (alertArrayList!!.size > 0) {
            //Si hay alertas en el array, muestro la lista y pongo el adapter
            withoutAlerts!!.visibility = View.GONE
            alertList!!.visibility = View.VISIBLE

            if (isFirst) {
                alertList!!.adapter = AdapterAlertList(this, alertArrayList!!)
            } else {
                val adapterAlertList = alertList!!.adapter as AdapterAlertList
                adapterAlertList.notifyDataSetChanged()
            }

        } else {
            //Si no hay alertas oculto la lista y muestro un textview que me avisa de que no hay alertas
            withoutAlerts!!.visibility = View.VISIBLE
            alertList!!.visibility = View.GONE
        }

        //Pongo los listeners a la lista
        alertList!!.onItemClickListener = this
        alertList!!.onItemLongClickListener = this
        alertList!!.setOnScrollListener(this)
    }

    override fun onRefresh() {
        //Inicio la lista pasandole 0 para indicarle que la reinicie desde el principio
        initList("0")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.up_fab -> alertList!!.smoothScrollToPositionFromTop(0, 0, 500)
            R.id.layout_disconnected ->
                //Si pulso encima del layout que me sale cuando estoy deslogeuado intenta volver a cargar la lista,
                //si no estoy logueado volvera a aparecer este layout, y si ya me he logueado cargara la lista
                initList("-1")
            R.id.assign_fab -> {
                //Si, desde los detalles de una alerta, pulso en el FAB para assignarme la alerta,
                //llamo a la funcion asincrona que me asigna la alerta
                val adaat = AssignDetailsAlertAsyncTask()
                adaat.execute(alertDetail)
            }
            R.id.close_alert -> {
                val alertToClose = alertDetail

                //Si, desde los detalles de una alerta, pulso en el boton para cerrar la alerta,
                //compruebo que el usuario haya introducido algo en el EditText del parte,
                //si ha escrito un parte, se lo añado a un POJO de alerta y llamo a la asincrona
                if (editTextPart!!.text.toString().isNotEmpty()) {
                    alertToClose!!.notes = editTextPart!!.text.toString()
                    val caat = CloseAlertAsyncTask()
                    caat.execute(alertToClose)
                } else {
                    //Si no ha introducido nada, le aviso para que lo escriba
                    Toast.makeText(context, R.string.writeNotes, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        showAlertDetails(alertArrayList!![pos])
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, pos: Int, id: Long): Boolean {
        return showAssingDialog(alertArrayList!![pos])
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

                /*****Technician*****/
                val stidat = SelectTechnicianIDAsyncTask()
                stidat.execute(alert.idTechnician)

                /*****Province*****/
                cabecera = "Provincia: "
                contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

                writeDetails(cabecera, contenido, provinceDetailAssigned)

                /*****Title*****/
                cabecera = "Asunto Alerta: "
                contenido = alert.affair

                writeDetails(cabecera, contenido, titleDetailAssigned)

                /*****Description*****/
                cabecera = "Descripción: "
                contenido = alert.description

                writeDetails(cabecera, contenido, descriptionDetailAssigned)

                layoutDetail!!.visibility = View.VISIBLE
                containerAssigned!!.visibility = View.VISIBLE
                containerList!!.visibility = View.GONE
                containerUnassigned!!.visibility = View.GONE
                containerAssignedOwner!!.visibility = View.GONE
            } else {
                dateDetailAssignedOwner!!.text = alert.date

                /*****Technician*****/
                cabecera = "Técnico: "
                contenido = prefs!!.getString("surname", "") + ", " + prefs!!.getString("name", "")

                writeDetails(cabecera, contenido, ownerDetailOwner)

                /*****Province*****/
                cabecera = "Provincia: "
                contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

                writeDetails(cabecera, contenido, provinceDetailAssignedOwner)

                /*****Title*****/
                cabecera = "Asunto Alerta: "
                contenido = alert.affair

                writeDetails(cabecera, contenido, titleDetailAssignedOwner)

                /*****Description*****/
                cabecera = "Descripción: "
                contenido = alert.description

                writeDetails(cabecera, contenido, descriptionDetailAssignedOwner)

                layoutDetail!!.visibility = View.VISIBLE
                containerAssigned!!.visibility = View.GONE
                containerList!!.visibility = View.GONE
                containerUnassigned!!.visibility = View.GONE
                containerAssignedOwner!!.visibility = View.VISIBLE
                writeDetails!!.visibility = View.GONE
                partTextView!!.visibility = View.GONE
                editTextPart!!.setText("")
                editTextPart!!.visibility = View.GONE
                shimmerCloseAlert!!.visibility = View.GONE


                if (alert.state == "finished") {
                    /*****Part*****/
                    cabecera = "Parte de la incidencia: "
                    contenido = ""

                    writeDetails(cabecera, contenido, partTextView)

                    writeDetails!!.visibility = View.VISIBLE
                    partTextView!!.visibility = View.VISIBLE
                    editTextPart!!.visibility = View.VISIBLE
                    shimmerCloseAlert!!.visibility = View.VISIBLE
                }
            }
        } else {
            dateDetailUnassigned!!.text = alert.date

            /*****Province*****/
            cabecera = "Provincia: "
            contenido = Character.toUpperCase(alert.province!![0]) + "" + alert.province!!.subSequence(1, alert.province!!.length)

            writeDetails(cabecera, contenido, provinceDetailUnassigned)

            /*****Title*****/
            cabecera = "Asunto Alerta: "
            contenido = alert.affair

            writeDetails(cabecera, contenido, titleDetailUnassigned)

            /*****Description*****/
            cabecera = "Descripción: "
            contenido = alert.description

            writeDetails(cabecera, contenido, descriptionDetailUnassigned)

            layoutDetail!!.visibility = View.VISIBLE
            containerAssigned!!.visibility = View.GONE
            containerList!!.visibility = View.GONE
            containerUnassigned!!.visibility = View.VISIBLE
            containerAssignedOwner!!.visibility = View.GONE
        }

    }

    //Muestro un dialogo para asignarte la alerta que has mantenido
    private fun showAssingDialog(alert: Alert): Boolean {
        val alerta = AlertDialog.Builder(context)

        if (!alert.isAssigned) {
            alerta.setMessage("Asunto: " + alert.affair + "\nFecha: " + alert.date)
                    .setTitle(R.string.titleAssign)
                    .setPositiveButton(R.string.assign) { dialog, id ->
                        val aaat = AssignAlertAsyncTask()
                        aaat.execute(alert)
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, id -> dialog.cancel() }
        } else {
            if (alert.idTechnician == prefs!!.getString("id", "")) {
                alerta.setMessage("Asunto: " + alert.affair + "\nFecha: " + alert.date)
                        .setTitle(R.string.titleAssignedYou)
                        .setPositiveButton(R.string.ok
                        ) { dialog, id -> dialog.cancel() }
            } else {
                alerta.setMessage("Asunto: " + alert.affair + "\nFecha: " + alert.date)
                        .setTitle(R.string.titleAssigned)
                        .setPositiveButton(R.string.ok
                        ) { dialog, id -> dialog.cancel() }
            }
        }
        alerta.show()

        return true
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
        intent.putExtra("TITLE", "Incidencias")
        //send broadcast
        activity.sendBroadcast(intent)

        //Inicio la lista pasandole 0 para que la reinicie desde el principio
        initList("0")

    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        //Solo pongo el refesh en enabled cuando esta en la primera posicion
        refresh!!.isEnabled = firstVisibleItem <= 0

        //Solo muestro el upFAB cuando paso de la posicion 10
        if (firstVisibleItem > 10) {
            upFAB!!.show()
        } else {
            upFAB!!.hide()
        }

        if (firstVisibleItem + 4 >= alertArrayList!!.size - 1) {
            upFAB!!.hide()
        }

        //Si el firstVisibleItem es multiple de 10 y es diferente a 0
        /*if (firstVisibleItem % 10 == 0 && firstVisibleItem != 0) {
            //Log.i("EEE", firstVisibleItem + "");
            //Si firstVisibleItem es mayor que la ultima carga (esto lo hago para que no cargue al subir)
            if (lastCharge < firstVisibleItem) {
                initList(Integer.toString(firstVisibleItem));
                lastCharge = firstVisibleItem;
            }
        }*/

        //Si el scroll esta abajo y firstVisibleItem es mayor que la ultima carga
        //(esto lo hago para que no cargue dos veces en el mismo sitio), lo refresco
        if (firstVisibleItem + 4 >= alertArrayList!!.size - 1 && lastCharge < firstVisibleItem) {
            initList(Integer.toString(firstVisibleItem))
            lastCharge = firstVisibleItem
        }
    }

    //AsyncTask para cargar la lista de las alertas
    internal inner class AlertListAsyncTask : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (!refresh!!.isRefreshing)
                loader!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg strings: String): Boolean? {
            try {
                val jsonObject = JSONObject()
                if (strings[0] == "0")
                    jsonObject.put("limit", Urls.FIRST_LIMIT_ALERTS)
                else
                    jsonObject.put("limit", Urls.LIMIT_ALERTS)

                jsonObject.put("skip", strings[0])

                val url = URL(Urls.GET_ALERT_LIST + prefs!!.getString("id", "") +
                        "/get-alerts-by-owner-province?filter=" + jsonObject + "&access_token=" + prefs!!.getString("access_token", ""))

                //Log.i("CONSULTA_ALERTAS", url.toString());
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")

                connection.connectTimeout = Urls.TIMEOUT_LONG
                connection.connect()

                val respuesta = connection.responseCode

                //Log.i("EEE", "Llego aqui");
                val result = StringBuilder()

                if (respuesta == HttpURLConnection.HTTP_OK) {
                    val br = BufferedReader(InputStreamReader(connection.inputStream))
                    result.append(br.readLine())

                    //Recojo el array de alertas que me devuelve la consulta
                    val alerts = JSONArray(result.toString())

                    if (strings[0] == "0") {
                        alertArrayList = ArrayList()
                        isFirst = true
                    } else {
                        isFirst = false
                    }

                    //Recorro el JSONArray
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

                        //Si el estado no es closed
                        if (state != "closed") {
                            //Si no esta asignada la añado a la array sin mas
                            if (!assigned) {
                                alertArrayList!!.add(Alert(id, affair, description, province, date, assigned, state))
                            } else {
                                //Si esta asignada la añado con el propietario
                                val idTechnician = alert.getString("owner")
                                alertArrayList!!.add(Alert(id, affair, description, province, date, idTechnician, assigned, state))
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

            loader!!.visibility = View.GONE
            refresh!!.isRefreshing = false

            if (correct!!) {
                //Mando un mensaje de broadcast para que en el MainActivity cambie el icono,
                //para que ya no salga el aviso de alerta nueva, ya que ya se han cargado las alertas
                val intent = Intent("MainActivity")
                intent.putExtra("NOTIFICATION", false)
                intent.putExtra("CHANGE_TITLE", false)
                intent.putExtra("REFRESH", false)
                //send broadcast
                activity.sendBroadcast(intent)
                setAdapter()
            } else {
                val dialog = AuthenticationDialog()
                dialog.show(fragmentManager, "CONNECTION_ERROR")
                //setAdapter();
            }
        }
    }

    //AsyncTask para assignar una alerta pasandole la id de la alerta y la id del tecnico
    internal inner class AssignAlertAsyncTask : AsyncTask<Alert, Void, Boolean>() {

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
                initList("0")
            }
        }
    }

    //AsyncTask para assignar una alerta pasandole la id de la alerta y la id del tecnico (desde los detalles)
    // (Diferencia --> El FAB)
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
            } else {
                fabProgressCircle!!.hide()
                Snackbar.make(fabProgressCircle!!, "Error al asignar alerta", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show()
            }
        }
    }

    //AsyncTask para cerrar una alerta pasandole el parte de esta
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
                    val inputStream = BufferedInputStream(connection.inputStream)

                    val reader = BufferedReader(InputStreamReader(inputStream))

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

}
