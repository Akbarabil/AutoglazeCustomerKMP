package com.example.autoglazecustomer.ui.transaction.jasa

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.transaction.jasa.LayananItem
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class JasaListScreenModel(
    private val authService: AuthService,
    private val kodeCabang: String,
    private val idKendaraan: Int,
    private val membershipStatusInt: Int
) : ScreenModel {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private var allServices = listOf<LayananItem>()
    private var eligibleForCarwashPriceIds = setOf<Int>()

    var searchQuery by mutableStateOf("")
    val categories = listOf("Car Wash", "Express", "Detailing", "Bundling")
    var selectedCategory by mutableStateOf(categories[0])
    var displayedServices by mutableStateOf<List<LayananItem>>(emptyList())

    fun fetchData() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val servicesDeferred = async { authService.getAllServices(kodeCabang) }
                val eligibilityDeferred = async { authService.checkMembershipCarwash(idKendaraan) }

                val servicesResponse = servicesDeferred.await()
                val eligibilityResponse = eligibilityDeferred.await()

                if (servicesResponse.status) {
                    allServices = servicesResponse.data ?: emptyList()
                } else {
                    errorMessage = servicesResponse.message.ifEmpty { "Gagal memuat layanan." }
                }

                if (eligibilityResponse.status) {
                    eligibleForCarwashPriceIds =
                        eligibilityResponse.data?.map { it.idProduk }?.toSet() ?: emptySet()
                }

                updateDisplayedList()
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan jaringan."
            } finally {
                isLoading = false
            }
        }
    }

    fun updateDisplayedList() {
        displayedServices = allServices.filter { item ->
            val categoryLayanan = if (item.KATEGORI.equals(
                    "Carwash",
                    ignoreCase = true
                )
            ) "Car Wash" else item.KATEGORI

            val matchCategory = categoryLayanan.equals(selectedCategory, ignoreCase = true)
            val matchSearch = item.namaProduk.contains(searchQuery, ignoreCase = true)

            matchCategory && matchSearch
        }
    }

    fun calculatePrice(item: LayananItem): Pair<Double, Double> {
        val originalPrice = item.hargaJual
        val isEligibleForCarwashPrice = eligibleForCarwashPriceIds.contains(item.idProduk)

        val finalPrice = if (isEligibleForCarwashPrice) {
            item.hargaJualMemberCarwash
        } else {
            when (membershipStatusInt) {
                1 -> item.hargaJualMemberExpress
                2 -> item.hargaJual
                3 -> item.hargaJualMemberExpress
                4 -> item.hargaVIP
                else -> item.hargaJual
            }
        }

        return Pair(originalPrice, finalPrice)
    }

}