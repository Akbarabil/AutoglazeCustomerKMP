package com.example.autoglazecustomer.ui.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.network.CabangService
import kotlinx.coroutines.launch

class TransactionScreenModel(private val cabangService: CabangService) : ScreenModel {

    var cabangList by mutableStateOf<List<CabangData>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var searchQuery by mutableStateOf("")
    val filteredCabang
        get() = if (searchQuery.isEmpty()) {
            cabangList
        } else {
            cabangList.filter { it.namaCabang.contains(searchQuery, ignoreCase = true) }
        }

    fun fetchCabangTerdekat(lat: Double, lon: Double) {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = cabangService.getCabangTerdekat(lon, lat)

                if (response.status) {
                    if (response.data.isEmpty()) {
                        errorMessage = "Tidak ada cabang Autoglaze yang beroperasi di sekitar lokasi anda saat ini."
                    } else {
                        cabangList = response.data
                    }
                } else {
                    errorMessage = response.message ?: "Gagal memuat data cabang terdekat."
                }
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }
}