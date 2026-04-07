package com.example.autoglazecustomer.ui.transaction.voucher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.VoucherUIModel
import com.example.autoglazecustomer.data.model.transaction.toUIModel
import com.example.autoglazecustomer.data.network.TransactionService
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class VoucherScreenModel(
    private val transactionService: TransactionService,
    private val idKendaraan: Int,
    private val cartItems: List<CartItem>
) : ScreenModel {

    var umumVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var kendaraanVouchers by mutableStateOf<List<VoucherUIModel>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectedVouchers by mutableStateOf<List<VoucherUIModel>>(VoucherManager.selectedVouchers.value)
    var validationMessage by mutableStateOf<String?>(null)

    init {
        fetchVouchers()
    }

    fun fetchVouchers() {
        val token = TokenManager.getToken() ?: return
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                supervisorScope {
                    val umumDeferred = async { transactionService.getVoucherSaya(token) }
                    val kendaraanDeferred = async { transactionService.getVouchersByVehicle(idKendaraan) }

                    val umumRes = umumDeferred.await()
                    val kendaraanRes = kendaraanDeferred.await()

                    umumVouchers = (umumRes.data ?: emptyList()).map { it.toUIModel() }
                    kendaraanVouchers = (kendaraanRes.data ?: emptyList()).map { it.toUIModel() }
                }
            } catch (e: Exception) {
                val rawMsg = e.toUserMessage()
                errorMessage = if (rawMsg.contains("null", ignoreCase = true)) {
                    "Koneksi internet terputus. Mohon periksa jaringan Anda. (Err: Offline)"
                } else {
                    rawMsg
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleVoucher(voucher: VoucherUIModel) {
        val currentList = selectedVouchers.toMutableList()
        val isAlreadySelected = currentList.any { it.idVoucher == voucher.idVoucher }

        if (isAlreadySelected) {
            currentList.removeAll { it.idVoucher == voucher.idVoucher }
            selectedVouchers = currentList
            validationMessage = null
            return
        }

        if (voucher.allowMultiple == 0 && currentList.isNotEmpty()) {
            validationMessage = "Voucher eksklusif tidak dapat digabung."
            return
        }

        if (currentList.any { it.allowMultiple == 0 }) {
            validationMessage = "Lepas voucher eksklusif terlebih dahulu."
            return
        }

        if (!isVoucherApplicable(voucher)) {
            validationMessage = "Voucher tidak berlaku untuk isi keranjang Anda."
            return
        }

        currentList.add(voucher)
        selectedVouchers = currentList
        validationMessage = null
    }

    private fun isVoucherApplicable(voucher: VoucherUIModel): Boolean {
        val idRaw = voucher.idProduk?.toString()?.trim() ?: ""

        val isGeneralOrVehicleVoucher = idRaw.isEmpty() || idRaw == "0" || idRaw == "null"
        if (isGeneralOrVehicleVoucher) return true

        val targetIds = idRaw.split(';').map { it.trim() }
        val cartIds = cartItems.map { it.idProduk.toString().trim() }

        return targetIds.any { it in cartIds }
    }

    fun confirmSelection() {
        VoucherManager.setVouchers(selectedVouchers)
    }
}