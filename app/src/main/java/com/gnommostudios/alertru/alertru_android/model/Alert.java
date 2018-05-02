package com.gnommostudios.alertru.alertru_android.model;

import java.io.Serializable;

public class Alert implements Serializable {

    private String id;
    private String affair;
    private String date;
    private String idDoctor;
    private boolean assigned;

    public Alert(String affair, String date, boolean assigned) {
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
    }

    public Alert(String id, String affair, String date, boolean assigned) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
    }

    public Alert(String id, String affair, String date, String idDoctor, boolean assigned) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idDoctor = idDoctor;
        this.assigned = assigned;
    }

    public Alert() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        this.idDoctor = idDoctor;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "affair='" + affair + '\'' +
                ", date='" + date + '\'' +
                ", idDoctor='" + idDoctor + '\'' +
                ", assigned=" + assigned +
                '}';
    }
}
