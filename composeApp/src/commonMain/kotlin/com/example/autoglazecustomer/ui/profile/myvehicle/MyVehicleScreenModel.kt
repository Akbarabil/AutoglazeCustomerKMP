package com.example.autoglazecustomer.ui.profile.myvehicle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class MyVehicleScreenModel(private val authService: AuthService) : ScreenModel {

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

                val response = authService.getVehicles(formattedToken)
                if (response.success) {
                    vehicleList = response.data
                } else {
                    errorMessage = "Gagal memuat data kendaraan."
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan jaringan."
            } finally {
                isLoading = false
            }
        }
    }
}