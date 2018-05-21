package com.gnommostudios.alertru.alertru_android.model

class Technician {
    var name: String? = null
    var surname: String? = null
    var username: String? = null
    var email: String? = null
    var id: String? = null
    var province: String? = null

    constructor(name: String, surname: String, username: String, email: String, id: String, province: String) {
        this.name = name
        this.surname = surname
        this.username = username
        this.email = email
        this.id = id
        this.province = province
    }

    override fun toString(): String = "Technician{" +
            "name='" + name + '\''.toString() +
            ", surname='" + surname + '\''.toString() +
            ", username='" + username + '\''.toString() +
            ", email='" + email + '\''.toString() +
            ", id='" + id + '\''.toString() +
            ", province='" + province + '\''.toString() +
            '}'.toString()

}
