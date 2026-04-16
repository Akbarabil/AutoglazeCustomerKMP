package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: ProfileData? = null
)

@Serializable
data class ProfileData(
    @SerialName("id") val id: Int? = 0,
    @SerialName("nama") val nama: String? = "",
    @SerialName("email") val email: String? = "",
    @SerialName("tgl_lahir") val tglLahir: String? = null,
    @SerialName("telepon") val telepon: String? = null,
    @SerialName("photo") val photo: String? = null
)
