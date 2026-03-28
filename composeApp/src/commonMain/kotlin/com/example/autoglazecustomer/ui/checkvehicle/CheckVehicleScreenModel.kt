package com.example.autoglazecustomer.ui.checkvehicle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.ChekKendaraanResponse
import com.example.autoglazecustomer.data.model.GetCekKendaraan
import com.example.autoglazecustomer.data.network.AuthService
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class CheckVehicleScreenModel(private val authService: AuthService) : ScreenModel {

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
                val response = authService.cekKendaraan(nopol.replace(" ", ""))

                if (response.success && response.data != null) {
                    vehicleData = response.data
                } else {
                    showNotFoundDialog = true
                }
            } catch (e: io.ktor.client.plugins.ResponseException) {
                val errorBody = e.response.bodyAsText()
                try {
                    val jsonResponse =
                        Json { ignoreUnknownKeys = true }.decodeFromString<ChekKendaraanResponse>(
                            errorBody
                        )
                    errorMessage = jsonResponse.message ?: "Data tidak ditemukan."
                } catch (parseException: Exception) {
                    errorMessage = when (e.response.status.value) {
                        404 -> "Nomor Polisi tidak ditemukan."
                        500 -> "Server sedang bermasalah, coba lagi nanti."
                        else -> "Terjadi kesalahan pada server."
                    }
                }

                if (errorMessage.contains("tidak ditemukan", ignoreCase = true)) {
                    showNotFoundDialog = true
                } else {
                    showErrorDialog = true
                }

            } catch (e: Exception) {
                errorMessage = "Koneksi gagal. Pastikan internetmu aktif."
                showErrorDialog = true
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        vehicleData = null
        showNotFoundDialog = false
        showErrorDialog = false
    }
}