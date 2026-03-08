package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<VehicleData> = emptyList()
)

@Serializable
data class VehicleData(
    @SerialName("id_kendaraan") val idKendaraan: Int? = null,
    @SerialName("merek") val merek: String? = null,
    @SerialName("tipe") val tipe: String? = null,
    @SerialName("tahun") val tahun: Int? = null,
    @SerialName("nopol") val nopol: String? = null,
    @SerialName("no_rangka") val noRangka: String? = null,
    @SerialName("warna") val warna: String? = null,
    @SerialName("has_membership") val hasMembership: Int = 0,
    @SerialName("gambar_tipe") val gambarTipe: String? = null
)
