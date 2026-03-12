package com.example.autoglazecustomer.ui.transaction

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.*
import com.example.autoglazecustomer.data.model.transaction.CabangData
import kotlinx.coroutines.launch
import com.example.autoglazecustomer.data.network.AuthService

class TransactionScreenModel(private val authService: AuthService) : ScreenModel {

    var cabangList by mutableStateOf<List<CabangData>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Fungsi pencarian/filter lokal
    var searchQuery by mutableStateOf("")
    val filteredCabang get() = if (searchQuery.isEmpty()) {
        cabangList
    } else {
        cabangList.filter { it.namaCabang.contains(searchQuery, ignoreCase = true) }
    }

    fun fetchCabangTerdekat(lat: Double, lon: Double) {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Pastikan di AuthService kamu sudah ada fungsi getCabangTerdekat
                val response = authService.getCabangTerdekat(lon, lat)
                if (response.status) {
                    cabangList = response.data
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}