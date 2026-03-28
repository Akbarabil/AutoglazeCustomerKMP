package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherKendaraanResponse(
    @SerialName("status") val status: Boolean? = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<VoucherItemId>? = emptyList()
)

@Serializable
data class VoucherItemId(
    @SerialName("ID_VOUCHER_KENDARAAN") val idVoucherKendaraan: Int? = 0,
    @SerialName("ID_CUSTOMER") val idCustomer: Int? = 0,
    @SerialName("ID_KENDARAAN") val idKendaraan: Int? = 0,
    @SerialName("KODE_VOUCHER") val kodeVoucher: String? = null,
    @SerialName("NAMA_VOUCHER") val namaVoucher: String? = null,
    @SerialName("KETERANGAN") val keterangan: String? = null,
    @SerialName("TGL_EXPIRED") val tglExpired: String? = null,


    @SerialName("ALLOW_MULTIPLE") val allowMultiple: Int? = 1,
    @SerialName("ID_PRODUK") val idProduk: String? = null,
    @SerialName("IS_EXPIRED") val isExpired: Int? = 0,
    @SerialName("IS_ACTIVE") val isActive: Int? = 1,
    @SerialName("JENIS_ITEM") val jenisItem: Int? = 0,


    @SerialName("TIPE_DISKON_NON_MEMBER") val tipeDiskonNonMember: Int? = 0,
    @SerialName("POT_HARGA_NON_MEMBER") val potHargaNonMember: Double? = 0.0,
    @SerialName("PRESENTASE_NON_MEMBER") val presentaseNonMember: Double? = 0.0,
    @SerialName("TIPE_DISKON_MEMBER") val tipeDiskonMember: Int? = 0,
    @SerialName("POT_HARGA_MEMBER") val potHargaMember: Double? = 0.0,
    @SerialName("PRESENTASE_MEMBER") val presentaseMember: Double? = 0.0,

    @SerialName("TXT_TIPE_NON_MEMBER") val txtTipeNonMember: String? = null,
    @SerialName("TXT_TIPE_MEMBER") val txtTipeMember: String? = null,
    @SerialName("TIPE_VOUCHER") val tipeVoucher: String? = null
)