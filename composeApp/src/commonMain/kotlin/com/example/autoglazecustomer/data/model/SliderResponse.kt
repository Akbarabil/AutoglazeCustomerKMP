package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SliderResponse(
    @SerialName("status") val status: String,
    @SerialName("data") val data: List<SliderItem> = emptyList()
)

@Serializable
data class SliderItem(
    @SerialName("ID_SLIDER") val idSlider: Int,
    @SerialName("GAMBAR") val gambar: String
)
