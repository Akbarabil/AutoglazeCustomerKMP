package com.example.autoglazecustomer.data.model.transaction

import com.example.autoglazecustomer.data.model.VoucherItem
import com.example.autoglazecustomer.data.model.VoucherItemId

data class VoucherUIModel(
    val idVoucher: Int,
    val namaVoucher: String,
    val keterangan: String,
    val kodeVoucher: String?,
    val allowMultiple: Int, // 0 = Eksklusif, 1 = Boleh digabung
    val idProduk: String?,  // Batasan ID Produk
    val tglExpired: String?,
    val isForVehicle: Boolean,
    val potHargaMember: Double,
    val presentaseMember: Double,
    val potHargaNonMember: Double,
    val presentaseNonMember: Double
)

// ==========================================================
// MAPPER (PENERJEMAH) DARI API KE UI MODEL
// ==========================================================

// 1. Penerjemah untuk Voucher Umum
fun VoucherItem.toUIModel(): VoucherUIModel {
    return VoucherUIModel(
        idVoucher = this.idVoucher ?: 0,
        namaVoucher = this.namaVoucher ?: "Voucher Tanpa Nama",
        keterangan = this.keterangan ?: "",
        kodeVoucher = this.kodeVoucher,
        allowMultiple = this.allowMultiple ?: 1,
        idProduk = this.idProduk,
        tglExpired = this.tglExpired,
        isForVehicle = false,
        potHargaMember = this.potHargaMember ?: 0.0,
        presentaseMember = this.presentaseMember ?: 0.0,
        potHargaNonMember = this.potHargaNonMember ?: 0.0,
        presentaseNonMember = this.presentaseNonMember ?: 0.0
    )
}

// 2. Penerjemah untuk Voucher Kendaraan
fun VoucherItemId.toUIModel(): VoucherUIModel {
    return VoucherUIModel(
        idVoucher = this.idVoucherKendaraan ?: 0,
        namaVoucher = this.namaVoucher ?: "Voucher Tanpa Nama",
        keterangan = this.keterangan ?: "",
        kodeVoucher = this.kodeVoucher,
        allowMultiple = this.allowMultiple ?: 1,
        idProduk = this.idProduk,
        tglExpired = this.tglExpired,
        isForVehicle = true,
        potHargaMember = this.potHargaMember ?: 0.0,
        presentaseMember = this.presentaseMember ?: 0.0,
        potHargaNonMember = this.potHargaNonMember ?: 0.0,
        presentaseNonMember = this.presentaseNonMember ?: 0.0
    )
}