package com.example.autoglazecustomer.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager // Pastikan import ini ada
import com.example.autoglazecustomer.data.network.AuthService
import io.ktor.client.statement.bodyAsText
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
            errorMessage = null
            try {
                val response = authService.login(email, password)

                if (response.success) {
                    val token = response.token
                    val user = response.user

                    if (token != null && user != null) {
                        TokenManager.saveToken(token)
                        TokenManager.saveCustomerId(user.id)
                        TokenManager.saveUserName(user.name)

                        onSuccess(user.name)
                    } else {
                        errorMessage = "Data login tidak lengkap"
                    }
                } else {
                    errorMessage = response.message ?: "Login gagal, silakan cek kembali data Anda"
                }
            } catch (e: io.ktor.client.plugins.ResponseException) {
                try {
                    val errorBody = e.response.bodyAsText()
                    val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    val errorResponse = json.decodeFromString<com.example.autoglazecustomer.data.model.LoginResponse>(errorBody)

                    errorMessage = errorResponse.message ?: "Email atau Password salah"
                } catch (parseException: Exception) {
                    errorMessage = when (e.response.status.value) {
                        401 -> "Email atau Password salah"
                        404 -> "Akun tidak ditemukan"
                        500 -> "Server sedang bermasalah"
                        else -> "Terjadi kesalahan sistem (${e.response.status.value})"
                    }
                }
            } catch (e: Exception) {
                // Error koneksi (timeout/no internet)
                errorMessage = "Gagal terhubung ke server. Pastikan internet Anda aktif."
            } finally {
                isLoading = false
            }
        }
    }
}