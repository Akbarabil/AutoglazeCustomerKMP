package com.example.autoglazecustomer.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.model.*
import com.example.autoglazecustomer.data.model.HistoryItem
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class CartScreenModel(private val authService: AuthService) : ScreenModel {

    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var historyList by mutableStateOf<List<HistoryItem>>(emptyList())

    var selectedVehicle by mutableStateOf<VehicleData?>(null)
    var isLoadingVehicles by mutableStateOf(false)
    var isHistoryLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchVehicles()
    }

    private fun fetchVehicles() {
        screenModelScope.launch {
            isLoadingVehicles = true
            val token = "Bearer ${TokenManager.getToken()}"
            runCatching { authService.getVehicles(token) }
                .onSuccess { res ->
                    if (res.success) {
                        vehicleList = res.data
                        if (vehicleList.size == 1) {
                            val singleVehicle = vehicleList.first()
                            selectedVehicle = singleVehicle
                            fetchHistory(singleVehicle.idKendaraan)
                        }
                    }
                }
                .onFailure { errorMessage = "Gagal memuat daftar kendaraan" }
            isLoadingVehicles = false
        }
    }

    // UBAH: Parameter idKendaraan sekarang menjadi Int?
    fun fetchHistory(idKendaraan: Int?) {
        // Guard Clause: Jika id null, jangan tembak API
        if (idKendaraan == null) {
            historyList = emptyList()
            return
        }

        screenModelScope.launch {
            isHistoryLoading = true
            errorMessage = null
            val customerId = TokenManager.getCustomerId()

            runCatching { authService.getHistoryPesanan(customerId, idKendaraan) }
                .onSuccess { res ->
                    historyList = if (res.status) {
                        res.data.sortedByDescending { it.createdAt }
                    } else {
                        emptyList()
                    }
                }
                .onFailure {
                    historyList = emptyList()
                    errorMessage = "Gagal memuat riwayat pesanan"
                }
            isHistoryLoading = false
        }
    }
}