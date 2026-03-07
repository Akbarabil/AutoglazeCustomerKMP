package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherUmumResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("data") val data: List<VoucherItem> = emptyList()
)

@Serializable
data class VoucherItem(
    @SerialName("ID_VOUCHER") val idVoucher: Int,
    @SerialName("NAMA_VOUCHER") val namaVoucher: String,
    @SerialName("KETERANGAN") val keterangan: String,
    @SerialName("GAMBAR_PROMO") val gambarUrl: String? = null
)
