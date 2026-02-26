package com.example.autoglazecustomer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MerkKendaraanResponse(
    @SerialName("ID_MEREK") val idMerek: Int,
    @SerialName("NAMA_MEREK") val namaMerek: String,
    @SerialName("GAMBAR") val gambar: String? = null
)
