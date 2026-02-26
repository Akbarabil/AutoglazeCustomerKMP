package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipeKendaraanResponse(
    @SerialName("ID_TIPE_KENDARAAN") val idTipeKendaraan: Int,
    @SerialName("NAMA_TIPE_KENDARAAN") val namaTipeKendaraan: String,
    @SerialName("ID_MEREK_KENDARAAN") val idMerekKendaraan: Int,
    @SerialName("NAMA_MEREK") val namaMerek: String
)