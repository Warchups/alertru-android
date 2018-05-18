package com.gnommostudios.alertru.alertru_android.util

object Urls {
    //TimeOuts para las conexiones
    val TIMEOUT = 15000
    val TIMEOUT_LONG = 30000

    val FIRST_LIMIT_ALERTS = "20"
    val LIMIT_ALERTS = "10"

    //Urls para las conexiones
    //IP jorge -> 36
    //IP mia -> 25
    val LOGIN = "http://192.168.1.36:3000/api/AppUsers/login"
    val SELECT_ID = "http://192.168.1.36:3000/api/AppUsers/"

    val CHECK_TOKEN = "http://192.168.1.36:3000/api/AppUsers/"

    val LOGOUT = "http://192.168.1.36:3000/api/AppUsers/logout?access_token="

    val ASSIGN_ALERT = "http://192.168.1.36:3000/api/AppUsers/"

    val GET_ALERT_LIST = "http://192.168.1.36:3000/api/AppUsers/"

    val GET_CLOSED_ALERT_LIST = "http://192.168.1.36:3000/api/AppUsers/"

    val CLOSE_ALERTS = "http://192.168.1.36:3000/api/Alerts/"

}
