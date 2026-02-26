package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AsalTahuResponse(
    @SerialName("id_general") val idGeneral: Int,
    @SerialName("label") val label: String
)
