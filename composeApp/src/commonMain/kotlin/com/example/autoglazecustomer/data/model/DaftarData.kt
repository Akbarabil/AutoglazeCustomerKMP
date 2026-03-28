package com.example.autoglazecustomer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DaftarData(

    val nama: String = "",
    val email: String = "",
    val tglLahir: String = "",
    val phone: String = "",
    val password: String = "",

    val nopol: String = "",
    val noRangka: String = "",
    val idMerek: Int = 0,
    val idTipe: Int = 0,
    val idWarna: Int = 0,
    val tahun: String = "",

    val sumberInfo: String = ""
)
