package com.example.autoglazecustomer.data.model.transaction.checkout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutDetailPayload(
    @SerialName("id_cabang_item") val idCabangItem: Int? = null,
    @SerialName("id_membership") val idMembership: Int? = null,
    @SerialName("qty") val qty: Int,
    @SerialName("subtotal") val subtotal: Double,
    @SerialName("satuan") val satuan: Double
)

@Serializable
data class CheckoutPayload(
    @SerialName("id_customer") val idCustomer: Int,
    @SerialName("nama_pelanggan") val namaPelanggan: String,
    @SerialName("id_kendaraan") val idKendaraan: String,
    @SerialName("jenis_penjualan") val jenisPenjualan: String,
    @SerialName("jenis_transaksi") val jenisTransaksi: String,
    @SerialName("odometer") val odometer: String?,
    @SerialName("kode_cabang") val kodeCabang: String,
    @SerialName("nama_cabang") val namaCabang: String,
    @SerialName("id_voucher") val idVoucher: String?,
    @SerialName("subtotal") val subtotal: String,
    @SerialName("pajak") val pajak: String,
    @SerialName("nett") val nett: String,
    @SerialName("diskon_nominal") val diskonNominal: String,
    @SerialName("detail") val detail: List<CheckoutDetailPayload>
)
