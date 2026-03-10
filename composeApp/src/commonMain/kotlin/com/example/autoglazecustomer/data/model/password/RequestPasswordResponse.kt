package com.example.autoglazecustomer.data.model.password

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPasswordResponse(
    @SerialName("status")
    val status: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: GeneratedPasswordData? = null
)
@Serializable
data class GeneratedPasswordData(
    @SerialName("id_customer")
    val idCustomer: Int,

    @SerialName("nama_customer")
    val namaCustomer: String,

    @SerialName("email")
    val email: String,

    @SerialName("telepon")
    val telepon: String,

    @SerialName("password")
    val password: String
)
