package com.gnommostudios.alertru.alertru_android.model;

import java.io.Serializable;

public class Alert implements Serializable {

    private String affair;
    private String date;
    private boolean assigned;

    public Alert(String affair, String date, boolean assigned) {
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
    }

    public Alert() {
    }

    public String getAffair() {
        return affair;
    }

    public void setAffair(String affair) {
        this.affair = affair;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "affair='" + affair + '\'' +
                ", date='" + date + '\'' +
                ", assigned=" + assigned +
                '}';
    }
}
