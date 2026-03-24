package com.example.autoglazecustomer.ui.transaction.membership

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipItem
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class MembershipListScreenModel(
    private val authService: AuthService,
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
                val response = authService.getMembership(kodeCabang)
                if (response.status == true && response.data?.isNotEmpty() == true) {
                    allMemberships = response.data
                    updateDisplayedList()
                } else {
                    errorMessage = "Membership tidak tersedia di cabang ini"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan koneksi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateDisplayedList() {
        displayedMemberships = if (searchQuery.isEmpty()) {
            allMemberships
        } else {
            allMemberships.filter { it.namaMembership.contains(searchQuery, ignoreCase = true) }
        }
    }
}