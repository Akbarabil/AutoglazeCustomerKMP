package com.example.autoglazecustomer.ui.transaction.membership

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipItem
import com.example.autoglazecustomer.data.network.ProductService
import kotlinx.coroutines.launch

class MembershipListScreenModel(
    private val productService: ProductService,
    private val kodeCabang: String
) : ScreenModel {

    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    var searchQuery by mutableStateOf("")

    private var allMemberships: List<MembershipItem> = emptyList()
    var displayedMemberships by mutableStateOf<List<MembershipItem>>(emptyList())

    fun fetchData() {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = productService.getMembership(kodeCabang)

                if (response.status == true) {
                    allMemberships = response.data ?: emptyList()
                    updateDisplayedList()
                } else {
                    val msg = response.message?.trim() ?: ""
                    if (msg.contains("tidak ditemukan", ignoreCase = true) || msg.contains("not found", ignoreCase = true)) {
                        allMemberships = emptyList()
                        updateDisplayedList()
                    }
                    else if (msg.isBlank() || msg.equals("null", ignoreCase = true) || msg.contains("failed", ignoreCase = true) || msg.contains("host", ignoreCase = true) || msg.contains("timeout", ignoreCase = true)) {
                        errorMessage = "Koneksi internet terputus. Mohon periksa jaringan Anda. (Err: Offline)"
                    }
                    else {
                        errorMessage = msg
                    }
                }
            } catch (e: Exception) {
                val exMsg = e.toUserMessage().trim()
                if (exMsg.isBlank() || exMsg.contains("null", ignoreCase = true)) {
                    errorMessage = "Koneksi internet terputus. Mohon periksa jaringan Anda. (Err: Offline)"
                } else {
                    errorMessage = exMsg
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun updateDisplayedList() {
        displayedMemberships = if (searchQuery.isBlank()) {
            allMemberships
        } else {
            allMemberships.filter { it.namaMembership.contains(searchQuery, ignoreCase = true) }
        }
    }
}