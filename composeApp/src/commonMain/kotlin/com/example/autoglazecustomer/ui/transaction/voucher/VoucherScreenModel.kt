package com.example.autoglazecustomer.ui.transaction.voucher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.VoucherUIModel
import com.example.autoglazecustomer.data.model.transaction.toUIModel
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class VoucherScreenModel(
    private val authService: AuthService,
    private val idKendaraan: Int,
    private val cartItems: List<CartItem>
) : ScreenModel {

    // State Data API (Telah Diterjemahkan ke Model Penengah UI)
    var umumVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var kendaraanVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var isLoading by mutableStateOf(false)

    // State Interaksi User
    var selectedVouchers by mutableStateOf(VoucherManager.selectedVouchers.value)
    var validationMessage by mutableStateOf<String?>(null)

    init {
        fetchVouchers()
    }

    private fun fetchVouchers() {
        val token = TokenManager.getToken() ?: return
        screenModelScope.launch {
            isLoading = true
            try {
                // JOSJIS: Hit 2 API secara bersamaan!
                val umumDeferred = async { authService.getVoucherSaya(token) }
                val kendaraanDeferred = async { authService.getVouchersByVehicle(idKendaraan) }

                val umumRes = umumDeferred.await()
                val kendaraanRes = kendaraanDeferred.await()

                // Terjemahkan List<VoucherItem> & List<VoucherItemId> menjadi List<VoucherUIModel>
                umumVouchers = (umumRes.data ?: emptyList()).map { it.toUIModel() }
                kendaraanVouchers = (kendaraanRes.data ?: emptyList()).map { it.toUIModel() }
            } catch (e: Exception) {
                validationMessage = "Gagal memuat voucher: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleVoucher(voucher: VoucherUIModel) {
        // 1. Jika sudah terpilih, maka HAPUS (Uncheck) menggunakan Functional Filter
        if (selectedVouchers.any { it.idVoucher == voucher.idVoucher }) {
            selectedVouchers = selectedVouchers.filterNot { it.idVoucher == voucher.idVoucher }
            validationMessage = null
            return
        }

        // --- Mulai dari sini adalah logika untuk MENAMBAH voucher baru ---

        // 2. Cek Eksklusivitas (0 = Eksklusif, tidak bisa digabung)
        val isNewVoucherExclusive = voucher.allowMultiple == 0

        // Jika voucher yang baru di-klik itu Eksklusif, tapi sudah ada voucher lain yang terpilih
        if (isNewVoucherExclusive && selectedVouchers.isNotEmpty()) {
            validationMessage = "${voucher.namaVoucher} eksklusif dan tidak dapat digabung."
            return
        }

        // Jika sebelumnya user sudah memilih voucher Eksklusif, tidak boleh tambah voucher apa-apa lagi
        val existingExclusive = selectedVouchers.firstOrNull { it.allowMultiple == 0 }
        if (existingExclusive != null) {
            validationMessage = "${existingExclusive.namaVoucher} sudah eksklusif. Lepas voucher tersebut dulu."
            return
        }

        // 3. Cek Batasan Produk di Keranjang
        if (!checkProductApplicability(voucher)) {
            validationMessage = "Produk yang sesuai untuk voucher ini tidak ada di keranjang."
            return
        }

        // 4. Sukses Ditambahkan (Gaya Functional Immutable)
        selectedVouchers = selectedVouchers + voucher
        validationMessage = null
    }

    private fun checkProductApplicability(voucher: VoucherUIModel): Boolean {
        // Jika idProduk null/kosong, berarti berlaku untuk semua barang
        val idString = voucher.idProduk ?: return true
        if (idString.isEmpty()) return true

        // Pecah string "101;53" menjadi List of Integers [101, 53]
        val targetProductIds = idString.split(';').mapNotNull { it.toIntOrNull() }
        if (targetProductIds.isEmpty()) return true

        val cartProductIds = cartItems.map { it.idProduk }
        return targetProductIds.any { cartProductIds.contains(it) }
    }

    // Dipanggil saat tombol "Gunakan Voucher" diklik di UI
    fun confirmSelection() {
        VoucherManager.setVouchers(selectedVouchers)
    }
}