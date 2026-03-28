package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("token") val token: String? = null,
    @SerialName("user") val user: LoginUser? = null
)

@Serializable
data class LoginUser(
    @SerialName("id") val id: Int,
    @SerialName("nama") val name: String,
    @SerialName("email") val email: String
)
