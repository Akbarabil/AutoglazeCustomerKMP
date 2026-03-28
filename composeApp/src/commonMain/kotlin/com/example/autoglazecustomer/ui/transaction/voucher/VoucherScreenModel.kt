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


    var umumVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var kendaraanVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var isLoading by mutableStateOf(false)


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

                val umumDeferred = async { authService.getVoucherSaya(token) }
                val kendaraanDeferred = async { authService.getVouchersByVehicle(idKendaraan) }

                val umumRes = umumDeferred.await()
                val kendaraanRes = kendaraanDeferred.await()


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

        if (selectedVouchers.any { it.idVoucher == voucher.idVoucher }) {
            selectedVouchers = selectedVouchers.filterNot { it.idVoucher == voucher.idVoucher }
            validationMessage = null
            return
        }


        val isNewVoucherExclusive = voucher.allowMultiple == 0


        if (isNewVoucherExclusive && selectedVouchers.isNotEmpty()) {
            validationMessage = "${voucher.namaVoucher} eksklusif dan tidak dapat digabung."
            return
        }


        val existingExclusive = selectedVouchers.firstOrNull { it.allowMultiple == 0 }
        if (existingExclusive != null) {
            validationMessage =
                "${existingExclusive.namaVoucher} sudah eksklusif. Lepas voucher tersebut dulu."
            return
        }


        if (!checkProductApplicability(voucher)) {
            validationMessage = "Produk yang sesuai untuk voucher ini tidak ada di keranjang."
            return
        }


        selectedVouchers = selectedVouchers + voucher
        validationMessage = null
    }

    private fun checkProductApplicability(voucher: VoucherUIModel): Boolean {

        val idString = voucher.idProduk ?: return true
        if (idString.isEmpty()) return true


        val targetProductIds = idString.split(';').mapNotNull { it.toIntOrNull() }
        if (targetProductIds.isEmpty()) return true

        val cartProductIds = cartItems.map { it.idProduk }
        return targetProductIds.any { cartProductIds.contains(it) }
    }


    fun confirmSelection() {
        VoucherManager.setVouchers(selectedVouchers)
    }
}