package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ChekKendaraanResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: GetCekKendaraan? = null
)

@Serializable
data class GetCekKendaraan(
    val nama: String,
    @SerialName("tipe_mobil") val tipeMobil: String,
    val nopol: String,
    @SerialName("no_rangka") val noRangka: String,
    val email: String,
    val telepon: String
)