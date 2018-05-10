package com.gnommostudios.alertru.alertru_android.util;

public class Urls {
    //TimeOuts para las conexiones
    public static final int TIMEOUT = 15000;
    public static final int TIMEOUT_LONG = 30000;

    //Urls para las conexiones
    public static final String LOGIN = "http://192.168.1.36:3000/api/AppUsers/login";
    public static final String SELECT_ID = "http://192.168.1.36:3000/api/AppUsers/";

    public static final String CHECK_TOKEN = "http://192.168.1.36:3000/api/AppUsers/";

    public static final String LOGOUT = "http://192.168.1.36:3000/api/AppUsers/logout?access_token=";

    public static final String ASSIGN_ALERT = "http://192.168.1.36:3000/api/AppUsers/";

    public static final String GET_ALERT_LIST = "http://192.168.1.36:3000/api/AppUsers/";

    public static final String CLOSE_ALERTS = "http://192.168.1.36:3000/api/Alerts/";

}
