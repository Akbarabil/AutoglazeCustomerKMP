package com.example.autoglazecustomer.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val authService: AuthService
) : ScreenModel {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isPasswordVisible by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun clearError() {
        errorMessage = null
    }

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email dan Password wajib diisi"
            return
        }

        if (!email.matches(emailPattern)) {
            errorMessage = "Format email tidak valid"
            return
        }

        screenModelScope.launch {
            isLoading = true
            try {
                val response = authService.login(email, password)
                if (response.success) {
                    onSuccess("Selamat Datang!")
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "Login Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}