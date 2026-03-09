package com.example.autoglazecustomer.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeScreenModel(private val authService: AuthService) : ScreenModel {

    var userName by mutableStateOf(TokenManager.getUserName() ?: "Sobat Glaze")
    var userAvatar by mutableStateOf<String?>(null)
    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var sliderList by mutableStateOf<List<SliderItem>>(emptyList())
    var beritaList by mutableStateOf<List<BeritaItem>>(emptyList())
    var promoList by mutableStateOf<List<VoucherItem>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadAllHomeData()
    }

    fun loadAllHomeData() {
        screenModelScope.launch {
            if (sliderList.isEmpty()) isLoading = true
            errorMessage = null

            try {
                val token = TokenManager.getToken()
                if (token.isNullOrBlank()) {
                    errorMessage = "Sesi berakhir, silakan login kembali."
                    return@launch
                }

                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                // Jalankan Parallel
                val profileJob = async { runCatching { authService.getProfileData(formattedToken) } }
                val sliderJob = async { runCatching { authService.getSlider() } }
                val vehicleJob = async { runCatching { authService.getVehicles(formattedToken) } }
                val beritaJob = async { runCatching { authService.getBerita() } }
                val voucherJob = async { runCatching { authService.getVoucherUmum() } }

                // Evaluasi satu per satu agar error di satu API tidak mematikan yang lain
                profileJob.await().onSuccess { res ->
                    if (res.success) {
                        userName = res.data?.nama ?: userName
                        userAvatar = res.data?.photo
                        res.data?.nama?.let { TokenManager.saveUserName(it) }
                    }
                }

                sliderJob.await().onSuccess { res ->
                    if (res.status == "ok") sliderList = res.data
                }

                vehicleJob.await().onSuccess { res ->
                    if (res.success) vehicleList = res.data
                }

                beritaJob.await().onSuccess { res ->
                    if (res.statusCode == 200) beritaList = res.data
                }

                voucherJob.await().onSuccess { res ->
                    if (res.status) promoList = res.data
                }

            } catch (e: Exception) {
                errorMessage = "Gagal memuat data terbaru."
            } finally {
                isLoading = false
            }
        }
    }

    fun formatDate(timestamp: String?): String {
        if (timestamp.isNullOrEmpty()) return "-"
        return runCatching {
            val datePart = timestamp.substringBefore(" ")
            val (y, m, d) = datePart.split("-")
            "$d ${getMonthName(m.toIntOrNull() ?: 0)} $y"
        }.getOrDefault(timestamp)
    }

    private fun getMonthName(month: Int): String = when (month) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "Mei"; 6 -> "Jun"
        7 -> "Jul"; 8 -> "Agu"; 9 -> "Sep"; 10 -> "Okt"; 11 -> "Nov"; 12 -> "Des"
        else -> ""
    }
}