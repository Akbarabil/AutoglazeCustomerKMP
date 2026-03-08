package com.example.autoglazecustomer.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.register.Country
import com.example.autoglazecustomer.data.model.register.RegisterState
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch

class RegisterScreenModel(private val authService: AuthService) : ScreenModel {

    // Menggunakan state tunggal agar UI hanya memantau satu sumber data
    var state by mutableStateOf(RegisterState())
        private set

    fun onNamaChange(newValue: String) {
        state = state.copy(nama = newValue, errorField = if(state.errorField == "nama") null else state.errorField)
    }
    fun onEmailChange(newValue: String) {
        state = state.copy(email = newValue, errorField = if(state.errorField == "email") null else state.errorField)
    }
    fun onTglLahirChange(newValue: String) {
        state = state.copy(tglLahir = newValue, errorField = if(state.errorField == "tglLahir") null else state.errorField)
    }
    fun onPhoneChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            state = state.copy(phone = newValue, errorField = if(state.errorField == "phone") null else state.errorField)
        }
    }
    fun onPasswordChange(newValue: String) {
        state = state.copy(password = newValue, errorField = if(state.errorField == "password") null else state.errorField)
    }

    fun togglePasswordVisibility() { state = state.copy(isPasswordVisible = !state.isPasswordVisible) }

    // Modifikasi clearError agar menghapus keduanya
    fun clearError() { state = state.copy(errorMessage = null, errorField = null) }

    fun validateAndCheckEmail(onSuccess: (DaftarData) -> Unit) {
        val s = state

        // Logika Validasi dengan penentuan field yang error
        val validation = when {
            s.nama.isBlank() -> "nama" to "Nama lengkap tidak boleh kosong"
            s.nama.length < 3 -> "nama" to "Nama terlalu pendek"
            s.email.isBlank() -> "email" to "Email tidak boleh kosong"
            !isValidEmail(s.email) -> "email" to "Format email tidak valid"
            s.tglLahir.isBlank() -> "tglLahir" to "Silakan pilih tanggal lahir anda"
            s.phone.isBlank() -> "phone" to "Nomor WhatsApp tidak boleh kosong"
            s.phone.length < s.selectedCountry.minDigit -> "phone" to "Nomor ${s.selectedCountry.name} minimal ${s.selectedCountry.minDigit} digit"
            s.phone.length > s.selectedCountry.maxDigit -> "phone" to "Nomor ${s.selectedCountry.name} maksimal ${s.selectedCountry.maxDigit} digit"
            s.password.isBlank() -> "password" to "Kata sandi tidak boleh kosong"
            s.password.length < 6 -> "password" to "Kata sandi minimal harus 6 karakter"
            else -> null
        }

        if (validation != null) {
            state = state.copy(errorField = validation.first, errorMessage = validation.second)
            return
        }

        screenModelScope.launch {
            state = state.copy(isLoading = true, errorField = null)
            try {
                // 1. Ambil objek response utuh
                val response = authService.cekEmail(s.email)

                // 2. Gunakan helper .isSuccessful yang sudah kamu buat di DaftarResponse
                if (response.isSuccessful) {
                    val fullPhone = if (s.selectedCountry.phoneCode.isEmpty()) s.phone else "+${s.selectedCountry.phoneCode}${s.phone}"
                    onSuccess(DaftarData(s.nama, s.email, s.tglLahir, fullPhone, s.password))
                } else {
                    // 3. Jika gagal, ambil pesan error dari properti .message
                    state = state.copy(
                        errorMessage = response.message ?: "Email sudah terdaftar",
                        errorField = "email"
                    )
                }
            } catch (e: Exception) {
                state = state.copy(errorMessage = "Gagal menghubungi server: ${e.message}")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return email.matches(emailRegex)
    }

    fun onCountrySelected(country: Country) {
        state = state.copy(
            selectedCountry = country,
            errorField = if (state.errorField == "phone") null else state.errorField
        )
    }
}