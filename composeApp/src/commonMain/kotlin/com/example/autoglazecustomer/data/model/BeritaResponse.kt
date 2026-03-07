package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeritaResponse(
    @SerialName("status_code") val statusCode: Int,
    @SerialName("data") val data: List<BeritaItem> = emptyList()
)

@Serializable
data class BeritaItem(
    @SerialName("id") val id: Int,
    @SerialName("judul") val judul: String,
    @SerialName("deskripsi") val deskripsi: String,
    @SerialName("gambar") val gambarUrl: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
