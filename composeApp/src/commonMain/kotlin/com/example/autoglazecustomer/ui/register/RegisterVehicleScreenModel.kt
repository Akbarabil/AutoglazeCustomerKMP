package com.example.autoglazecustomer.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.register.RegisterVehicleState
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.VehicleService
import kotlinx.coroutines.launch

class RegisterVehicleScreenModel(
    private val vehicleService: VehicleService
) : ScreenModel {

    var state by mutableStateOf(RegisterVehicleState())
        private set

    fun initData() {
        screenModelScope.launch {
            state = state.copy(isLoadingMerek = true)
            try {
                val merek = vehicleService.getMerek()
                val warna = vehicleService.getWarna()

                state = state.copy(
                    listMerek = merek,
                    listWarna = warna,
                    isLoadingMerek = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    errorMessage = "Gagal memuat data awal",
                    isLoadingMerek = false
                )
            }
        }
    }

    fun onMerekSelected(nama: String) {
        val merek = state.listMerek.find { it.namaMerek == nama }

        state = state.copy(
            merekTerpilih = merek,
            tipeTerpilih = null,
            tahun = "",
            listTipe = emptyList(),
            isLoadingTipe = true,
            errorField = null
        )

        merek?.let {
            screenModelScope.launch {
                try {
                    val tipe = vehicleService.getTipe(it.idMerek)
                    state = state.copy(
                        listTipe = tipe,
                        isLoadingTipe = false
                    )
                } catch (e: Exception) {
                    state = state.copy(
                        errorMessage = "Gagal memuat tipe mobil",
                        isLoadingTipe = false
                    )
                }
            }
        }
    }

    fun onTipeSelected(nama: String) {
        val tipe = state.listTipe.find { it.namaTipeKendaraan == nama }
        state = state.copy(
            tipeTerpilih = tipe,
            tahun = "",
            errorField = null
        )
    }

    fun onTahunSelected(value: String) {
        state = state.copy(tahun = value, errorField = null)
    }

    fun onNopolChange(value: String) {
        state = state.copy(
            nopol = value.uppercase().replace(" ", ""),
            errorField = null
        )
    }

    fun onNoRangkaChange(value: String) {
        state = state.copy(
            noRangka = value.uppercase(),
            errorField = null
        )
    }

    fun onWarnaSelected(warna: com.example.autoglazecustomer.data.model.WarnaKendaraanResponse) {
        state = state.copy(warnaTerpilih = warna)
    }

    fun clearError() {
        state = state.copy(errorMessage = null, errorField = null)
    }

    fun clearMerek() {
        state = state.copy(
            merekTerpilih = null,
            tipeTerpilih = null,
            tahun = "",
            listTipe = emptyList()
        )
    }

    fun validateAndCheckNopol(
        onSuccess: (DaftarData) -> Unit,
        dataRegistrasi: DaftarData
    ) {
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
            state = state.copy(
                errorField = validation.first,
                errorMessage = validation.second
            )
            return
        }

        screenModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            try {
                val response = vehicleService.cekNopol(s.nopol)

                if (response.isSuccessful) {
                    val finalData = dataRegistrasi.copy(
                        idMerek = s.merekTerpilih?.idMerek ?: 0,
                        idTipe = s.tipeTerpilih?.idTipeKendaraan ?: 0,
                        idWarna = s.warnaTerpilih?.idWarna ?: 0,
                        tahun = s.tahun,
                        nopol = s.nopol,
                        noRangka = s.noRangka
                    )
                    onSuccess(finalData)
                } else {
                    state = state.copy(
                        errorMessage = response.message ?: "Nomor polisi sudah terdaftar",
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
}