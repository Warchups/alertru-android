package com.gnommostudios.alertru.alertru_android.model

import java.io.Serializable

class Alert : Serializable {

    var id: String? = null
    var affair: String? = null
    var description: String? = null
    var date: String? = null
    var idTechnician: String? = null
    var province: String? = null
    var isAssigned: Boolean = false
    var state: String? = null
    var notes: String? = null

    constructor(id: String, affair: String, description: String, province: String, date: String, assigned: Boolean, state: String) {
        this.id = id
        this.affair = affair
        this.date = date
        this.description = description
        this.isAssigned = assigned
        this.state = state
        this.province = province
    }

    constructor(id: String, affair: String, description: String, province: String, date: String, idTechnician: String, assigned: Boolean, state: String) {
        this.id = id
        this.affair = affair
        this.date = date
        this.idTechnician = idTechnician
        this.description = description
        this.isAssigned = assigned
        this.state = state
        this.province = province
    }

    constructor(id: String, affair: String, description: String, province: String, date: String, idTechnician: String, assigned: Boolean, state: String, notes: String) {
        this.id = id
        this.affair = affair
        this.date = date
        this.idTechnician = idTechnician
        this.description = description
        this.isAssigned = assigned
        this.state = state
        this.notes = notes
        this.province = province
    }
}
