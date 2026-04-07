package com.example.autoglazecustomer.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.ProfileData
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.TransactionService
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val authService: AuthService,
    private val transactionService: TransactionService
) : ScreenModel {

    var profileData by mutableStateOf<ProfileData?>(null)
    var points by mutableStateOf(0)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchProfileAndPoints()
    }

    fun fetchProfileAndPoints() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            val tokenFromStorage = TokenManager.getToken()

            if (tokenFromStorage == null) {
                errorMessage = "Sesi telah berakhir. Silakan login kembali."
                isLoading = false
                return@launch
            }

            val formattedToken = if (tokenFromStorage.startsWith("Bearer ")) {
                tokenFromStorage
            } else {
                "Bearer $tokenFromStorage"
            }

            try {
                val profileResponse = authService.getProfileData(formattedToken)

                if (profileResponse.success) {
                    profileData = profileResponse.data

                    profileResponse.data?.nama?.let { namaBaru ->
                        TokenManager.saveUserName(namaBaru)
                    }

                    profileResponse.data?.id?.let { customerId ->
                        val pointResponse = transactionService.getPoint(customerId)

                        if (pointResponse.status) {
                            points = pointResponse.data?.point ?: 0
                        }
                    }
                } else {
                    errorMessage = "Gagal memuat data profil. Pastikan sesi anda aktif."
                }
            } catch (exception: Exception) {
                errorMessage = exception.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}