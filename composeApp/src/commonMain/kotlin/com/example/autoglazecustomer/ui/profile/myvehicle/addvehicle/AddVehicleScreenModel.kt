package com.example.autoglazecustomer.ui.profile.myvehicle.addvehicle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.model.*
import com.example.autoglazecustomer.data.model.addvehicle.AddVehicleState
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class AddVehicleScreenModel(private val authService: AuthService) : ScreenModel {
    var state by mutableStateOf(AddVehicleState())

    fun initData() {
        screenModelScope.launch {
            state = state.copy(isLoadingMerek = true)
            try {
                val merekRes = authService.getMerek()
                val warnaRes = authService.getWarna()
                state = state.copy(
                    listMerek = merekRes,
                    listWarna = warnaRes,
                    isLoadingMerek = false
                )
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Gagal memuat data awal", isLoadingMerek = false)
            }
        }
    }

    fun onMerekSelected(merekName: String) {
        val selected = state.listMerek.find { it.namaMerek == merekName }
        state = state.copy(
            merekTerpilih = selected,
            tipeTerpilih = null, // Reset Tipe
            tahun = "",          // Reset Tahun
            listTipe = emptyList(),
            errorField = null
        )
        selected?.let { fetchTipe(it.idMerek) }
    }

    private fun fetchTipe(idMerek: Int) {
        screenModelScope.launch {
            state = state.copy(isLoadingTipe = true)
            try {
                val tipeRes = authService.getTipe(idMerek)
                state = state.copy(listTipe = tipeRes, isLoadingTipe = false)
            } catch (e: Exception) {
                state = state.copy(isLoadingTipe = false)
            }
        }
    }

    fun onTipeSelected(tipeName: String) {
        val selected = state.listTipe.find { it.namaTipeKendaraan == tipeName }
        state = state.copy(
            tipeTerpilih = selected,
            tahun = "", // Reset Tahun saat tipe berubah
            errorField = null
        )
    }

    fun onTahunSelected(tahun: String) {
        state = state.copy(tahun = tahun, errorField = null)
    }

    fun onNopolChange(nopol: String) {
        state = state.copy(nopol = nopol.uppercase().replace(" ", ""), errorField = null)
    }

    fun onNoRangkaChange(rangka: String) {
        state = state.copy(noRangka = rangka.uppercase(), errorField = null)
    }

    fun onWarnaSelected(warna: WarnaKendaraanResponse) {
        state = state.copy(warnaTerpilih = warna)
    }

    fun clearMerek() {
        state = state.copy(
            merekTerpilih = null,
            tipeTerpilih = null,
            tahun = "",
            listTipe = emptyList()
        )
    }

    fun validateAndSave(onSuccess: () -> Unit) {
        val s = state
        val validation = when {
            s.merekTerpilih == null -> "merek" to "Silakan pilih merk dari daftar"
            s.tipeTerpilih == null -> "tipe" to "Silakan pilih tipe dari daftar"
            s.tahun.isBlank() -> "tahun" to "Silakan pilih tahun kendaraan dari daftar"
            s.nopol.isBlank() -> "nopol" to "Nomor polisi tidak boleh kosong"
            s.nopol.length < 3 -> "nopol" to "Nomor polisi minimal 3 karakter"
            s.warnaTerpilih == null -> "warna" to "Pilih warna kendaraan"
            else -> null
        }

        if (validation != null) {
            state = state.copy(errorField = validation.first, errorMessage = validation.second)
            return
        }

        screenModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            try {
                val token = TokenManager.getToken() ?: ""

                // 1. Cek Nopol
                val responseNopol = authService.cekNopol(s.nopol)

                if (responseNopol.isSuccessful) {
                    // 2. Simpan Kendaraan
                    val res = authService.addVehicle(
                        token = "Bearer $token",
                        idMerek = s.merekTerpilih!!.idMerek,
                        idTipe = s.tipeTerpilih!!.idTipeKendaraan,
                        tahun = s.tahun.toInt(),
                        nopol = s.nopol,
                        noRangka = s.noRangka,
                        idWarna = s.warnaTerpilih!!.idWarna
                    )

                    if (res.success) {
                        state = state.copy(isSuccess = true)
                        onSuccess()
                    } else {
                        state = state.copy(errorMessage = res.message)
                    }
                } else {
                    state = state.copy(
                        errorMessage = responseNopol.message ?: "Nomor polisi sudah terdaftar",
                        errorField = "nopol"
                    )
                }
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Terjadi gangguan koneksi")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun clearError() { state = state.copy(errorMessage = null, errorField = null) }
}