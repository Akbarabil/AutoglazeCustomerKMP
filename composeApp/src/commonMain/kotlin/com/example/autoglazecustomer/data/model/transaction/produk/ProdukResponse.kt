package com.example.autoglazecustomer.data.model.transaction.produk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProdukResponse(
    @SerialName("status") val status: Boolean? = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<ProdukItem>? = emptyList()
)

@Serializable
data class ProdukItem(
    @SerialName("ID_PRODUK") val idProduk: Int = 0,
    @SerialName("NAMA_PRODUK") val namaProduk: String = "",
    @SerialName("GAMBAR") val gambarUrl: String? = null,
    @SerialName("DESKRIPSI") val deskripsi: String? = null,
    @SerialName("HARGA_JUAL") val hargaNonMember: Double = 0.0,
    @SerialName("HARGA_JUAL_MEMBER_EXPRESS") val hargaExpress: Double = 0.0,
    @SerialName("HARGA_JUAL_MEMBER_CARWASH") val hargaCarwash: Double = 0.0,
    @SerialName("HARGA_DUA_MEMBER") val hargaDual: Double = 0.0,
    @SerialName("HARGA_VIP") val hargaVIP: Double = 0.0,
    @SerialName("ID_CABANG_ITEM") val idCabangItem: Int = 0
)
