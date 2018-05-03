package com.gnommostudios.alertru.alertru_android.util;

public class Urls {
    public static final int TIMEOUT = 15000;
    public static final int TIMEOUT_LONG = 30000;

    public static final String LOGIN = "http://192.168.1.25:3000/api/Doctors/login";
    public static final String SELECT_ID = "http://192.168.1.25:3000/api/Doctors/";
    public static final String LOGOUT = "http://192.168.1.25:3000/api/Doctors/logout?access_token=";

    public static final String ASSIGN_ALERT = "http://192.168.1.25:3000/api/Doctors/";

    public static final String GET_ALERT_LIST = "http://192.168.1.25:3000/api/Doctors/get-alerts-by-province/";

}
