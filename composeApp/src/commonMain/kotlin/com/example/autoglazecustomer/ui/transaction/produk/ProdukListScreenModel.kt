package com.example.autoglazecustomer.ui.transaction.produk

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.transaction.produk.ProdukItem
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.ProductService
import com.example.autoglazecustomer.data.network.TransactionService
import kotlinx.coroutines.launch

class ProdukListScreenModel(
    private val productService: ProductService,
    private val kodeCabang: String,
    private val membershipStatusInt: Int
) : ScreenModel {

    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    var searchQuery by mutableStateOf("")

    private var allProducts: List<ProdukItem> = emptyList()
    var displayedProducts by mutableStateOf<List<ProdukItem>>(emptyList())

    fun fetchData() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = productService.getProduk(kodeCabang)
                if (response.status == true) {
                    allProducts = response.data ?: emptyList()
                    updateDisplayedList()
                } else {
                    errorMessage = response.message ?: "Gagal memuat produk."
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan koneksi."
            } finally {
                isLoading = false
            }
        }
    }

    fun updateDisplayedList() {
        displayedProducts = if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter { it.namaProduk.contains(searchQuery, ignoreCase = true) }
        }
    }

    fun calculatePrice(item: ProdukItem): Pair<Double, Double> {
        val originalPrice = item.hargaNonMember
        val finalPrice = when (membershipStatusInt) {
            1 -> item.hargaExpress
            2 -> item.hargaCarwash
            3 -> item.hargaDual
            4 -> item.hargaVIP
            else -> originalPrice
        }

        val safeFinalPrice =
            if (finalPrice == 0.0 && originalPrice > 0.0) originalPrice else finalPrice

        return Pair(originalPrice, safeFinalPrice)
    }
}