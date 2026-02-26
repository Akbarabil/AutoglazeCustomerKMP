package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarnaKendaraanResponse(
    @SerialName("ID_WARNA") val idWarna: Int,
    @SerialName("NAMA_WARNA") val namaWarna: String
)
