package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<HistoryItem> = emptyList()
)

@Serializable
data class HistoryItem(
    @SerialName("KODE_PENJUALAN") val kodePenjualan: String,
    @SerialName("NAMA_CABANG") val namaCabang: String,
    @SerialName("NETT") val nett: Double,
    @SerialName("SUBTOTAL") val subtotal: Double,
    @SerialName("CREATED_AT") val createdAt: String,
    @SerialName("KODE_CABANG") val kodeCabang: String,
    @SerialName("NOMOR_POLISI") val nopol: String? = null
)

@Serializable
data class QrPayload(
    val kode_penjualan: String,
    val kode_cabang: String,
    val tanggal: String
)
