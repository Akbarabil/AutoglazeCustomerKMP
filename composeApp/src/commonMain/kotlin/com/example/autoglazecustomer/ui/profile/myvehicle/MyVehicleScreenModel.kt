package com.example.autoglazecustomer.ui.profile.myvehicle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.launch

class MyVehicleScreenModel(private val vehicleService: VehicleService) : ScreenModel {

    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchVehicles() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = TokenManager.getToken() ?: ""
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = vehicleService.getVehicles(formattedToken)
                if (response.success) {
                    vehicleList = response.data
                } else {
                    errorMessage = "Gagal memuat data kendaraan."
                }
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }
}