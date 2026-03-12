package com.example.autoglazecustomer.data.model.transaction

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CabangTerdekatResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<CabangData>
)

@Serializable
data class CabangData(
    @SerialName("KODE_CABANG") val kodeCabang: String,
    @SerialName("NAMA_CABANG") val namaCabang: String,
    @SerialName("ALAMAT") val alamat: String?,
    @SerialName("LATT") val latt: String?,
    @SerialName("LONG") val long: String?,
    @SerialName("distance_km") val distanceKm: Double?,
    @SerialName("IS_USING_PPN") val isUsingPpn: Int?
)
