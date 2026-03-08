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

class HomeScreenModel(
    private val authService: AuthService
) : ScreenModel {

    // --- UI State ---
    var userName by mutableStateOf(TokenManager.getUserName() ?: "Sobat Glaze")
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
            isLoading = true
            errorMessage = null

            val token = "Bearer ${TokenManager.getToken()}"

            // 1. Jalankan semua request secara paralel (Deferred)
            val profileJob = async { authService.getProfileData(token) }
            val sliderJob = async { authService.getSlider() }
            val vehicleJob = async { authService.getVehicles(token) }
            val beritaJob = async { authService.getBerita() }
            val voucherJob = async { authService.getVoucherUmum() }

            // 2. Eksekusi masing-masing dengan runCatching agar satu timeout tidak mematikan yang lain

            // Profile & Name
            runCatching { profileJob.await() }.onSuccess { res ->
                if (res.success) {
                    userName = res.data?.nama ?: userName
                    res.data?.nama?.let { TokenManager.saveUserName(it) }
                }
            }.onFailure { println("LOG_HOME: Profile failed -> ${it.message}") }

            // Sliders
            runCatching { sliderJob.await() }.onSuccess { res ->
                if (res.status == "ok") sliderList = res.data
            }

            // Vehicles
            runCatching { vehicleJob.await() }.onSuccess { res ->
                if (res.success) vehicleList = res.data
            }

            // News / Berita
            runCatching { beritaJob.await() }.onSuccess { res ->
                if (res.statusCode == 200) beritaList = res.data
            }.onFailure {
                // Jika berita timeout, kita bisa kasih log khusus
                println("LOG_HOME: Berita timeout/error")
            }

            // Vouchers
            runCatching { voucherJob.await() }.onSuccess { res ->
                if (res.status) promoList = res.data
            }

            // Final check jika semua data kosong (opsional, untuk indikasi error koneksi total)
            if (sliderList.isEmpty() && vehicleList.isEmpty()) {
                errorMessage = "Gagal memuat data. Periksa koneksi internet anda."
            }

            isLoading = false
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

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "Mei"; 6 -> "Jun"
            7 -> "Jul"; 8 -> "Agu"; 9 -> "Sep"; 10 -> "Okt"; 11 -> "Nov"; 12 -> "Des"
            else -> ""
        }
    }
}