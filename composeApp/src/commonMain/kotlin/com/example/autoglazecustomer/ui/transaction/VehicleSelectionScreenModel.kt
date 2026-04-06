package com.example.autoglazecustomer.ui.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.CabangService
import com.example.autoglazecustomer.data.network.ProductService
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class VehicleSelectionScreenModel(
    private val vehicleService: VehicleService,
    private val productService: ProductService,
    private val kodeCabang: String
) : ScreenModel {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var vehicleList by mutableStateOf<List<VehicleWithStatus>>(emptyList())
    var selectedVehicle by mutableStateOf<VehicleWithStatus?>(null)

    private val membershipStatusMap = mapOf(
        0 to "Non-Membership",
        1 to "Express Membership",
        2 to "Carwash Membership",
        3 to "Dual Membership",
        4 to "VIP",
        5 to "Expired"
    )

    fun fetchVehicles() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val rawToken = TokenManager.getToken()
                if (rawToken.isNullOrEmpty()) {
                    errorMessage = "Sesi Anda telah habis. Silakan login kembali."
                    isLoading = false
                    return@launch
                }

                val bearerToken = "Bearer $rawToken"
                val response = vehicleService.getVehicles(bearerToken)
                val vehicles = response.data

                if (vehicles.isEmpty()) {
                    isLoading = false
                    return@launch
                }

                vehicleList = vehicles.map {
                    VehicleWithStatus(it, "Memeriksa status...", 0)
                }

                isLoading = false

                val deferredStatuses = vehicles.map { vehicle ->
                    async {
                        val id = vehicle.idKendaraan ?: 0
                        runCatching { productService.checkMembership(id, kodeCabang) }.getOrDefault(0)
                    }
                }

                val membershipStatuses = deferredStatuses.awaitAll()

                vehicleList = vehicles.mapIndexed { index, vehicle ->
                    val statusInt = membershipStatuses[index]
                    val statusText = membershipStatusMap[statusInt] ?: "Tidak Diketahui"
                    VehicleWithStatus(vehicle, statusText, statusInt)
                }

            } catch (e: Exception) {
                errorMessage = "Gagal memuat kendaraan: ${e.message}"
                isLoading = false
            }
        }
    }
}