package com.gnommostudios.alertru.alertru_android.model;

import java.io.Serializable;

public class Alert implements Serializable {

    private String id;
    private String affair;
    private String date;
    private String idTechnician;
    private String province;
    private boolean assigned;
    private boolean finished;
    private String notes;

    public Alert(String affair, String province, String date, boolean assigned) {
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, boolean assigned) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.assigned = assigned;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, String idTechnician, boolean assigned) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idTechnician = idTechnician;
        this.assigned = assigned;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, String idTechnician, boolean assigned, boolean finished) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idTechnician = idTechnician;
        this.assigned = assigned;
        this.finished = finished;
        this.province = province;
    }

    public Alert(String id, String affair, String province, String date, String idTechnician, boolean assigned, boolean finished, String notes) {
        this.id = id;
        this.affair = affair;
        this.date = date;
        this.idTechnician = idTechnician;
        this.assigned = assigned;
        this.finished = finished;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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
