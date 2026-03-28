package com.example.autoglazecustomer.ui.profile.myvoucher

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.model.*
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class MyVoucherScreenModel(private val authService: AuthService) : ScreenModel {

    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var isLoadingVehicles by mutableStateOf(false)

    // Simpan status expand per ID Kendaraan
    var expandedStates = mutableStateMapOf<Int, Boolean>()

    // Simpan data voucher per ID Kendaraan (Cache)
    var voucherCache = mutableStateMapOf<Int, List<VoucherItemId>>()
    var loadingVouchers = mutableStateMapOf<Int, Boolean>()

    fun fetchVehicles() {
        screenModelScope.launch {
            isLoadingVehicles = true
            try {
                val token = "Bearer ${TokenManager.getToken()}"
                val res = authService.getVehicles(token)
                if (res.success) vehicleList = res.data
            } catch (e: Exception) { /* Handle error */ }
            isLoadingVehicles = false
        }
    }

    fun toggleExpand(idKendaraan: Int) {
        val isCurrentlyExpanded = expandedStates[idKendaraan] ?: false
        expandedStates[idKendaraan] = !isCurrentlyExpanded

        if (!isCurrentlyExpanded && !voucherCache.containsKey(idKendaraan)) {
            fetchVouchers(idKendaraan)
        }
    }

    private fun fetchVouchers(idKendaraan: Int) {
        screenModelScope.launch {
            loadingVouchers[idKendaraan] = true
            try {
                val res = authService.getVouchersByVehicle(idKendaraan)
                voucherCache[idKendaraan] = res.data ?: emptyList()
            } catch (e: Exception) {
                voucherCache[idKendaraan] = emptyList()
            }
            loadingVouchers[idKendaraan] = false
        }
    }
}