package com.example.autoglazecustomer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddVehicleResponse(
    val success: Boolean,
    val message: String,
    val data: VehicleData? = null
)
