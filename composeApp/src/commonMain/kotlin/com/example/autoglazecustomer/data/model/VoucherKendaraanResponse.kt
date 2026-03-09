package com.example.autoglazecustomer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherKendaraanResponse(
    @SerialName("status")
    val status: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: List<VoucherItemId> = emptyList()
)
@Serializable
data class VoucherItemId(
    @SerialName("ID_VOUCHER_KENDARAAN")
    val idVoucherKendaraan: Int,
    @SerialName("ID_CUSTOMER")
    val idCustomer: Int,
    @SerialName("ID_KENDARAAN")
    val idKendaraan: Int,
    @SerialName("TGL_EXPIRED")
    val tglExpired: String? = null,
    @SerialName("KODE_VOUCHER")
    val kodeVoucher: String,
    @SerialName("TIPE_VOUCHER")
    val tipeVoucher: String? = null,
    @SerialName("POT_HARGA_MEMBER")
    val potHargaMember: Int = 0,
    @SerialName("TIPE_DISKON_MEMBER")
    val tipeDiskonMember: Int? = null,
    @SerialName("PRESENTASE_MEMBER")
    val presentaseMember: Double = 0.0,
    @SerialName("TIPE_DISKON_NON_MEMBER")
    val tipeDiskonNonMember: Int? = null,
    @SerialName("POT_HARGA_NON_MEMBER")
    val potHargaNonMember: Int = 0,
    @SerialName("PRESENTASE_NON_MEMBER")
    val presentaseNonMember: Double = 0.0,
    @SerialName("TXT_TIPE_NON_MEMBER")
    val txtTipeNonMember: String? = null,
    @SerialName("NAMA_VOUCHER")
    val namaVoucher: String,
    @SerialName("KETERANGAN")
    val keterangan: String? = null
)
