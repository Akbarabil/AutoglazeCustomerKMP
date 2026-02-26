package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.* // Import ini untuk defaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthService {
    private val BASE_URL = "https://autoglaze-rewrite.digiponic.co.id/api/"

    val client = HttpClient {
        // 1. Konfigurasi Base URL agar tidak perlu tulis lengkap di setiap fungsi
        defaultRequest {
            url(BASE_URL)
            header(HttpHeaders.Accept, "application/json")
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("KTOR_LOG: $message")
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return client.submitForm(
            url = "login", // Karena sudah ada defaultRequest, cukup tulis sisanya
            formParameters = parameters {
                append("email", email.trim())
                append("password", password.trim())
            }
        ).body()
    }

    suspend fun cekKendaraan(nopol: String): ChekKendaraanResponse {
        return client.submitForm(
            url = "cek-kendaraan",
            formParameters = parameters {
                append("search_type", "nopol")
                append("search_value", nopol)
            }
        ).body()
    }

    suspend fun getMerek(): List<MerkKendaraanResponse> {
        return try {
            client.get("kendaraan/merek").body<List<MerkKendaraanResponse>>()
        } catch (e: Exception) {
            println("Error Merek: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTipe(idMerek: Int): List<TipeKendaraanResponse> {
        return try {
            client.get("kendaraan/tipe") {
                url { parameters.append("id_merek", idMerek.toString()) }
            }.body<List<TipeKendaraanResponse>>()
        } catch (e: Exception) {
            println("Error Tipe: ${e.message}")
            emptyList()
        }
    }

    suspend fun getWarna(): List<WarnaKendaraanResponse> {
        return try {
            client.get("warna").body<List<WarnaKendaraanResponse>>()
        } catch (e: Exception) {
            println("Error Warna: ${e.message}")
            emptyList()
        }
    }

    suspend fun cekNopol(nopol: String): Boolean {
        return try {
            val response = client.get("cek-nopol") {
                url { parameters.append("nopol", nopol.trim()) }
            }.body<DaftarResponse>()

            response.isSuccessful
        } catch (e: Exception) {
            println("Error cekNopol: ${e.message}")
            false // Jika error (RTO, 404, parsing error), anggap gagal/tidak tersedia
        }
    }

    suspend fun cekEmail(email: String): Pair<Boolean, String> {
        return try {
            val response = client.get("cek-email") {
                url { parameters.append("email", email.trim()) }
            }.body<DaftarResponse>()

            // Menggunakan helper isSuccessful yang menangani status String maupun success Boolean
            Pair(response.isSuccessful, response.message ?: "Email sudah terdaftar")
        } catch (e: Exception) {
            // Log error ke console untuk debugging
            println("Ktor Error: ${e.message}")
            Pair(false, "Terjadi gangguan koneksi: ${e.message}")
        }
    }

    suspend fun getAsalTahu(): List<AsalTahuResponse> {
        // Menggunakan wrapper ApiResponse karena server mengembalikan field "data"
        val response = client.get("general/asal-tahu").body<ApiResponse<List<AsalTahuResponse>>>()
        return response.data
    }

    // Gunakan fungsi ini untuk Step 3 (SurveyScreen)
    suspend fun registerCustomer(data: DaftarData): RegisterResponse {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "register-customer",
                formData = formData {
                    append("nama", data.nama)
                    append("email", data.email)
                    append("tgl_lahir", data.tglLahir)
                    append("telepon", data.phone)
                    append("password", data.password)
                    append("asal_tahu", data.sumberInfo)
                    append("id_merek", data.idMerek.toString())
                    append("id_tipe", data.idTipe.toString())
                    append("tahun", data.tahun)
                    append("nopol", data.nopol)
                    append("no_rangka", data.noRangka)
                    append("id_warna", data.idWarna.toString())
                }
            )
            // Tambahkan tipe spesifik di sini
            response.body<RegisterResponse>()
        } catch (e: Exception) {
            println("DEBUG_AUTOGLAZE: Error saat hit register = ${e.message}")
            RegisterResponse(
                success = false,
                message = "Terjadi kesalahan: ${e.message}",
                status = "error"
            )
        }
    }
}