package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("customer_id") val customerId: Int? = 0
)
