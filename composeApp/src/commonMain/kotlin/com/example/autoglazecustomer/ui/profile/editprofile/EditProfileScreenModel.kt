package com.example.autoglazecustomer.ui.profile.editprofile

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.local.TokenManager
import io.ktor.client.plugins.* import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.* // Tambahkan ini untuk parsing manual

class EditProfileScreenModel(private val authService: AuthService) : ScreenModel {
    var nama by mutableStateOf("")
    var email by mutableStateOf("")
    var telepon by mutableStateOf("")
    var currentPhotoUrl by mutableStateOf<String?>(null)
    var selectedImageBytes by mutableStateOf<ByteArray?>(null)

    var isEditing by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var showSuccessDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private fun validateInputs(): Boolean {
        if (nama.length < 3) {
            errorMessage = "Nama terlalu pendek (minimal 3 karakter)"
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            errorMessage = "Format email tidak valid"
            return false
        }
        if (telepon.length < 10) {
            errorMessage = "Nomor telepon minimal 10 digit"
            return false
        }
        return true
    }

    fun updateProfile() {
        if (!validateInputs()) return

        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = TokenManager.getToken() ?: ""
                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val response = authService.updateProfile(
                    token = formattedToken,
                    nama = nama,
                    email = email,
                    telepon = telepon,
                    imageBytes = selectedImageBytes
                )

                if (response.success) {
                    TokenManager.saveUserName(nama)
                    showSuccessDialog = true
                } else {
                    errorMessage = response.message
                }
            } catch (e: ClientRequestException) {
                // Menangani error 4xx (terutama 422 Unprocessable Entity)
                try {
                    val responseBody = e.response.bodyAsText()
                    val json = Json { ignoreUnknownKeys = true }
                    val errorObj = json.parseToJsonElement(responseBody).jsonObject

                    val firstError = errorObj["errors"]?.jsonObject?.values?.firstOrNull()
                        ?.jsonArray?.firstOrNull()?.jsonPrimitive?.content

                    // Jika ada detail error pakai itu, jika tidak pakai message umum dari server
                    errorMessage = firstError ?: errorObj["message"]?.jsonPrimitive?.content ?: "Data tidak valid"

                    println("KTOR_LOG: DETAIL ERROR SERVER -> $responseBody")
                } catch (parseEx: Exception) {
                    errorMessage = "Gagal memproses data server. Silakan cek koneksi Anda."
                }
            } catch (e: Exception) {
                errorMessage = "Gagal terhubung ke server. Pastikan internet Anda stabil."
                println("ERROR_GENERAL_DEBUG: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}