package com.example.autoglazecustomer.ui.profile.editprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.network.AuthService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
                try {
                    val responseBody = e.response.bodyAsText()
                    val json = Json { ignoreUnknownKeys = true }
                    val errorObj = json.parseToJsonElement(responseBody).jsonObject

                    val firstError = errorObj["errors"]?.jsonObject?.values?.firstOrNull()
                        ?.jsonArray?.firstOrNull()?.jsonPrimitive?.content

                    errorMessage = firstError ?: errorObj["message"]?.jsonPrimitive?.content
                            ?: "Data tidak valid"
                } catch (parseEx: Exception) {
                    errorMessage = "Gagal memproses data server. Silakan cek koneksi Anda."
                }
            } catch (e: Exception) {
                errorMessage = "Gagal terhubung ke server. Pastikan internet Anda stabil."
            } finally {
                isLoading = false
            }
        }
    }
}