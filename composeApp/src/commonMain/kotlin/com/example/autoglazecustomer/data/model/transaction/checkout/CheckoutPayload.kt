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
data class InsertDraftPayload(
    @SerialName("id_customer") val idCustomer: Int,
    @SerialName("id_kendaraan") val idKendaraan: String,
    @SerialName("jenis_penjualan") val jenisPenjualan: String,
    @SerialName("jenis_transaksi") val jenisTransaksi: String,
    @SerialName("nama_pelanggan") val namaPelanggan: String,
    @SerialName("kode_cabang") val kodeCabang: String,
    @SerialName("nama_cabang") val namaCabang: String,
    @SerialName("detail") val detail: List<CheckoutDetailPayload>
)


@Serializable
data class UpdateFinalPayload(
    @SerialName("kode_penjualan") val kodePenjualan: String,
    @SerialName("subtotal") val subtotal: String,
    @SerialName("pajak") val pajak: String,
    @SerialName("diskon_nominal") val diskonNominal: String,
    @SerialName("nett") val nett: String,
    @SerialName("id_voucher") val idVoucher: String
)

@Serializable
data class DeleteDraftPayload(
    @SerialName("kode_penjualan") val kodePenjualan: String
)