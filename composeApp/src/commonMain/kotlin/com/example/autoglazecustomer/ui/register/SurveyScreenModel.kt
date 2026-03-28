package com.example.autoglazecustomer.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.register.SurveyState
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class SurveyScreenModel(private val authService: AuthService) : ScreenModel {

    var state by mutableStateOf(SurveyState())
        private set

    fun initData() {
        screenModelScope.launch {
            try {
                val list = authService.getAsalTahu()
                state = state.copy(asalTahuList = list)
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Gagal memuat data survey")
            }
        }
    }

    fun onAsalTahuSelected(item: com.example.autoglazecustomer.data.model.AsalTahuResponse) {
        state = state.copy(selectedAsalTahu = item, errorField = null)
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }

    fun registerFinal(dataRegistrasi: DaftarData) {
        if (state.selectedAsalTahu == null) {
            state = state.copy(
                errorMessage = "Silakan pilih salah satu opsi survey",
                errorField = "survey"
            )
            return
        }

        screenModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val finalData = dataRegistrasi.copy(
                    sumberInfo = state.selectedAsalTahu?.idGeneral.toString()
                )
                val response = authService.registerCustomer(finalData)

                if (response.success == true) {
                    state = state.copy(showSuccessDialog = true)
                } else {
                    state = state.copy(errorMessage = response.message ?: "Pendaftaran gagal")
                }
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Terjadi kesalahan pendaftaran: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }
}