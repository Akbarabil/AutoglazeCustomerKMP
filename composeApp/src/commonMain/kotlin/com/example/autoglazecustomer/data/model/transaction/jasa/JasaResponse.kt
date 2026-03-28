package com.example.autoglazecustomer.data.model.transaction.jasa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JasaResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<LayananItem>? = null
)

@Serializable
data class LayananItem(
    @SerialName("ID_PRODUK") val idProduk: Int,
    @SerialName("NAMA_PRODUK") val namaProduk: String,
    @SerialName("GAMBAR") val gambarUrl: String? = null,
    @SerialName("DURASI_PENGERJAAN") val durasiMenit: Int? = 0,
    @SerialName("ID_KATEGORI") val idKategori: Int,
    @SerialName("KATEGORI") val KATEGORI: String,
    @SerialName("HARGA_JUAL") val hargaJual: Double,
    @SerialName("HARGA_JUAL_MEMBER_EXPRESS") val hargaJualMemberExpress: Double,
    @SerialName("HARGA_JUAL_MEMBER_CARWASH") val hargaJualMemberCarwash: Double,
    @SerialName("HARGA_DUA_MEMBER") val hargaDuaMember: Double,
    @SerialName("HARGA_VIP") val hargaVIP: Double,
    @SerialName("DESKRIPSI") val deskripsi: String? = null,
    @SerialName("IS_MULTIPLE") val isMultiple: Int? = 0,
    @SerialName("ID_CABANG_ITEM") val idCabangItem: Int
)
