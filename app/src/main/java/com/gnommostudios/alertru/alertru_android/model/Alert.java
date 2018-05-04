package com.gnommostudios.alertru.alertru_android.model;

import java.io.Serializable;

public class Alert implements Serializable {

    private String id;
    private String affair;
    private String date;
    private String idTechnician;
    private String province;
    private boolean assigned;
    private String state;
    private String notes;

    public Alert(String id, String affair, String province, String date, boolean assigned, String state) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
        this.state = state;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, String idTechnician, boolean assigned, String state) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idTechnician = idTechnician;
        this.assigned = assigned;
        this.state = state;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, String idTechnician, boolean assigned, String state, String notes) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idTechnician = idTechnician;
        this.assigned = assigned;
        this.state = state;
        this.notes = notes;
        this.province = province;
    }

    public Alert() {
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getIdTechnician() {
        return idTechnician;
    }

    public void setIdTechnician(String idTechnician) {
        this.idTechnician = idTechnician;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "affair='" + affair + '\'' +
                ", date='" + date + '\'' +
                ", idTechnician='" + idTechnician + '\'' +
                ", assigned=" + assigned +
                '}';
    }
}
