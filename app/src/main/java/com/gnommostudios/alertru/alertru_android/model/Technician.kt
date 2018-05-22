package com.gnommostudios.alertru.alertru_android.model

class Technician(var name: String, var surname: String, var username: String, var email: String, var id: String, var province: String) {

    override fun toString(): String = "Technician{" +
            "name='" + name + '\''.toString() +
            ", surname='" + surname + '\''.toString() +
            ", username='" + username + '\''.toString() +
            ", email='" + email + '\''.toString() +
            ", id='" + id + '\''.toString() +
            ", province='" + province + '\''.toString() +
            '}'.toString()

}
