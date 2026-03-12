package com.example.autoglazecustomer.data.model.transaction

import com.example.autoglazecustomer.data.model.VehicleData
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MembershipStatusResponse(
    @SerialName("status") val status: Boolean? = null,
    @SerialName("int") val statusInt: Int? = null,
    @SerialName("has_membership") val hasMembership: Int? = null,
    @SerialName("id_kendaraan") val idKendaraan: Int? = null
)


data class VehicleWithStatus(
    val vehicle: VehicleData,
    val membershipStatusText: String,
    val membershipStatusInt: Int
)
