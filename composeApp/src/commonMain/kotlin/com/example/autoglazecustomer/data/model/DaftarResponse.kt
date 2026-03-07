package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DaftarResponse(
    @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("success")
    val success: Boolean? = null
) {
    // Helper property untuk mengecek keberhasilan secara aman
    val isSuccessful: Boolean
        get() = success == true || status == "success" || status == "true"
}
