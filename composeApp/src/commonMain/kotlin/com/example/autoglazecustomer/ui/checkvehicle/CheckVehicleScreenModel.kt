package com.example.autoglazecustomer.ui.checkvehicle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.GetCekKendaraan
import com.example.autoglazecustomer.data.network.VehicleService
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.launch

class CheckVehicleScreenModel(private val vehicleService: VehicleService) : ScreenModel {

    var nopol by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var vehicleData by mutableStateOf<GetCekKendaraan?>(null)

    var showNotFoundDialog by mutableStateOf(false)
    var showErrorDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun checkVehicle() {
        if (nopol.isBlank()) return

        screenModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = vehicleService.cekKendaraan(nopol.replace(" ", ""))

                if (response.success && response.data != null) {
                    vehicleData = response.data
                } else {
                    showNotFoundDialog = true
                }
            } catch (e: Exception) {
                if (e is ClientRequestException && e.response.status.value == 404) {
                    showNotFoundDialog = true
                } else {
                    errorMessage = e.toUserMessage()
                    showErrorDialog = true
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        vehicleData = null
        showNotFoundDialog = false
        showErrorDialog = false
        errorMessage = ""
    }
}