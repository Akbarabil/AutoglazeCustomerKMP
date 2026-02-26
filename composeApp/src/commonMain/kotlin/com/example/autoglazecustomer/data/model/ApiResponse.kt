package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("status") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T,
    @SerialName("total") val total: Int? = null
)
