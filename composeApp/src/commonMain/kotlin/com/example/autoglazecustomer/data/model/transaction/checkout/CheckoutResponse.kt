package com.example.autoglazecustomer.data.model.transaction.checkout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("kode_penjualan") val kodePenjualan: String? = null
)
