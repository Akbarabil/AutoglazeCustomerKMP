package com.example.autoglazecustomer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DaftarData(
    // Step 1
    val nama: String = "",
    val email: String = "",
    val tglLahir: String = "",
    val phone: String = "",
    val password: String = "",
    // Step 2
    val nopol: String = "",
    val noRangka: String = "",
    val idMerek: Int = 0,
    val idTipe: Int = 0,
    val idWarna: Int = 0,
    val tahun: String = "",
    // Step 3
    val sumberInfo: String = ""
)
