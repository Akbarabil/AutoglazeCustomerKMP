package com.example.autoglazecustomer.ui.profile.myvoucher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.model.VoucherItemId
import com.example.autoglazecustomer.data.network.TransactionService
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.launch

class MyVoucherScreenModel(
    private val vehicleService: VehicleService,
    private val transactionService: TransactionService
) : ScreenModel {

    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var isLoadingVehicles by mutableStateOf(false)
    var vehicleErrorMessage by mutableStateOf<String?>(null)

    var expandedStates = mutableStateMapOf<Int, Boolean>()

    var voucherCache = mutableStateMapOf<Int, List<VoucherItemId>>()
    var loadingVouchers = mutableStateMapOf<Int, Boolean>()
    var voucherErrorCache = mutableStateMapOf<Int, String?>()

    fun fetchVehicles() {
        screenModelScope.launch {
            isLoadingVehicles = true
            vehicleErrorMessage = null
            try {
                val token = "Bearer ${TokenManager.getToken()}"
                val res = vehicleService.getVehicles(token)
                if (res.success) {
                    vehicleList = res.data
                } else {
                    vehicleErrorMessage = "Gagal memuat data kendaraan."
                }
            } catch (e: Exception) {
                vehicleErrorMessage = e.toUserMessage()
            } finally {
                isLoadingVehicles = false
            }
        }
    }

    fun toggleExpand(idKendaraan: Int) {
        val isCurrentlyExpanded = expandedStates[idKendaraan] ?: false
        expandedStates[idKendaraan] = !isCurrentlyExpanded

        if (!isCurrentlyExpanded && (!voucherCache.containsKey(idKendaraan) || voucherErrorCache[idKendaraan] != null)) {
            fetchVouchers(idKendaraan)
        }
    }

    fun retryFetchVouchers(idKendaraan: Int) {
        fetchVouchers(idKendaraan)
    }

    private fun fetchVouchers(idKendaraan: Int) {
        screenModelScope.launch {
            loadingVouchers[idKendaraan] = true
            voucherErrorCache[idKendaraan] = null
            try {
                val res = transactionService.getVouchersByVehicle(idKendaraan)
                voucherCache[idKendaraan] = res.data ?: emptyList()
            } catch (e: Exception) {
                voucherErrorCache[idKendaraan] = e.toUserMessage()
            } finally {
                loadingVouchers[idKendaraan] = false
            }
        }
    }
}