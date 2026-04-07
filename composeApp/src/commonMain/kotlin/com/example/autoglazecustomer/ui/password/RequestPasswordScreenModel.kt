package com.example.autoglazecustomer.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class RequestPasswordScreenModel(private val authService: AuthService) : ScreenModel {
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var generatedPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    fun requestPassword(data: Map<String, String>) {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val res = authService.generatePassword(data)

                if (res.status && res.data != null) {
                    generatedPassword = res.data.password
                    isSuccess = true
                } else {
                    errorMessage = res.message ?: "Gagal membuat sandi baru. Pastikan data sesuai."
                }
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}