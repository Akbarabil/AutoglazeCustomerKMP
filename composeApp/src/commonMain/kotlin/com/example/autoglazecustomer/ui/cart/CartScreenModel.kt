package com.example.autoglazecustomer.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.HistoryItem
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.network.TransactionService
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.launch

class CartScreenModel(
    private val vehicleService: VehicleService,
    private val transactionService: TransactionService
) : ScreenModel {

    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var historyList by mutableStateOf<List<HistoryItem>>(emptyList())

    var selectedVehicle by mutableStateOf<VehicleData?>(null)
    var isLoadingVehicles by mutableStateOf(false)
    var isHistoryLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchVehicles()
    }

    fun fetchVehicles() {
        screenModelScope.launch {
            isLoadingVehicles = true
            errorMessage = null
            try {
                val token = "Bearer ${TokenManager.getToken()}"
                val res = vehicleService.getVehicles(token)
                if (res.success) {
                    vehicleList = res.data
                    if (vehicleList.size == 1) {
                        val singleVehicle = vehicleList.first()
                        selectedVehicle = singleVehicle
                        fetchHistory(singleVehicle.idKendaraan)
                    }
                } else {
                    errorMessage = "Gagal memuat kendaraan."
                }
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoadingVehicles = false
            }
        }
    }

    fun fetchHistory(idKendaraan: Int?) {
        if (idKendaraan == null) {
            historyList = emptyList()
            return
        }

        screenModelScope.launch {
            isHistoryLoading = true
            errorMessage = null
            try {
                val customerId = TokenManager.getCustomerId()
                val res = transactionService.getHistoryPesanan(customerId, idKendaraan)

                historyList = if (res.status) {
                    res.data.sortedByDescending { it.createdAt }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                historyList = emptyList()
                errorMessage = e.toUserMessage()
            } finally {
                isHistoryLoading = false
            }
        }
    }
}