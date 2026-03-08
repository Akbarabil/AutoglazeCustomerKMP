package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(
    val status: Boolean,
    val message: String,
    val data: PointData?
)

@Serializable
data class PointData(
    @SerialName("POINT") val point: Int
)
