package com.example.autoglazecustomer.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.BeritaItem
import com.example.autoglazecustomer.data.model.SliderItem
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.model.VoucherItem
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.CabangService
import com.example.autoglazecustomer.data.network.HomeService
import com.example.autoglazecustomer.data.network.TransactionService
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val authService: AuthService,
    private val homeService: HomeService,
    private val vehicleService: VehicleService,
    private val transactionService: TransactionService,
    private val cabangService: CabangService
) : ScreenModel {

    var userName by mutableStateOf(TokenManager.getUserName() ?: "Sobat Glaze")
    var userAvatar by mutableStateOf<String?>(null)
    var vehicleList by mutableStateOf<List<VehicleData>>(emptyList())
    var sliderList by mutableStateOf<List<SliderItem>>(emptyList())
    var beritaList by mutableStateOf<List<BeritaItem>>(emptyList())
    var promoList by mutableStateOf<List<VoucherItem>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var closestCabang by mutableStateOf<CabangData?>(null)
    var isCabangLoading by mutableStateOf(false)
    var cabangErrorMessage by mutableStateOf<String?>(null)

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
                    errorMessage = "Sesi anda telah berakhir. Silakan masuk kembali ke aplikasi."
                    return@launch
                }

                val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val profileJob = async { runCatching { authService.getProfileData(formattedToken) } }
                val sliderJob = async { runCatching { homeService.getSlider() } }
                val vehicleJob = async { runCatching { vehicleService.getVehicles(formattedToken) } }
                val beritaJob = async { runCatching { homeService.getBerita() } }
                val voucherJob = async { runCatching { transactionService.getVoucherUmum() } }

                profileJob.await().onSuccess { res ->
                    if (res.success) {
                        userName = res.data?.nama ?: userName
                        userAvatar = res.data?.photo
                        res.data?.nama?.let { TokenManager.saveUserName(it) }
                    }
                }.onFailure { throw it }

                sliderJob.await().onSuccess { res ->
                    if (res.status == "ok") sliderList = res.data
                }.onFailure { throw it }

                vehicleJob.await().onSuccess { res ->
                    if (res.success) vehicleList = res.data
                }.onFailure { throw it }

                beritaJob.await().onSuccess { res ->
                    if (res.statusCode == 200) beritaList = res.data
                }.onFailure { throw it }

                voucherJob.await().onSuccess { res ->
                    if (res.status == true) promoList = res.data ?: emptyList()
                }.onFailure { throw it }

            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchClosestCabang(lat: Double, lon: Double) {
        screenModelScope.launch {
            isCabangLoading = true
            cabangErrorMessage = null
            try {
                val response = cabangService.getCabangTerdekat(lon, lat)

                if (response.status && response.data.isNotEmpty()) {
                    closestCabang = response.data.first()
                } else {
                    cabangErrorMessage = response.message ?: "Tidak ada cabang terdekat ditemukan"
                }
            } catch (e: Exception) {
                cabangErrorMessage = e.toUserMessage()
            } finally {
                isCabangLoading = false
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