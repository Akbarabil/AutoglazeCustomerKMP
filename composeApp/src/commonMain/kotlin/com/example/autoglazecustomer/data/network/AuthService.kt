package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
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
            level = LogLevel.INFO
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
    }

    // --- AUTH & LOGIN ---
    suspend fun login(email: String, password: String): LoginResponse {
        return client.submitForm(
            url = "login",
            formParameters = parameters {
                append("email", email.trim())
                append("password", password.trim())
            }
        ).body()
    }

    // --- REGISTER FLOW ---
    suspend fun registerCustomer(data: DaftarData): RegisterResponse {
        return client.submitForm(
            url = "register-customer",
            formParameters = parameters {
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
                append("no_rangka", data.noRangka ?: "")
                append("id_warna", data.idWarna.toString())
            }
        ).body()
    }

    // --- HOMEPAGE DATA ---
    suspend fun getProfileData(token: String): ProfileResponse {
        return client.get("profile") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }

    suspend fun getVehicles(token: String): VehicleResponse {
        return client.get("kendaraan") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }

    suspend fun getSlider(): SliderResponse {
        return client.get("slider").body()
    }

    suspend fun getBerita(): BeritaResponse {
        return client.get("berita").body()
    }

    suspend fun getVoucherUmum(): VoucherUmumResponse {
        return client.get("list-voucher-umum").body()
    }

    // --- VALIDATION & HELPERS ---
    suspend fun cekKendaraan(nopol: String): ChekKendaraanResponse {
        return client.submitForm(
            url = "cek-kendaraan",
            formParameters = parameters {
                append("search_type", "nopol")
                append("search_value", nopol)
            }
        ).body()
    }

    suspend fun cekNopol(nopol: String): DaftarResponse {
        return client.get("cek-nopol") {
            url { parameters.append("nopol", nopol.trim()) }
        }.body()
    }

    suspend fun cekEmail(email: String): DaftarResponse {
        return client.get("cek-email") {
            url { parameters.append("email", email.trim()) }
        }.body()
    }

    suspend fun getMerek(): List<MerkKendaraanResponse> {
        return try {
            client.get("kendaraan/merek").body()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getTipe(idMerek: Int): List<TipeKendaraanResponse> {
        return try {
            client.get("kendaraan/tipe") {
                url { parameters.append("id_merek", idMerek.toString()) }
            }.body()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getWarna(): List<WarnaKendaraanResponse> {
        return try {
            client.get("warna").body()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getAsalTahu(): List<AsalTahuResponse> {
        val response = client.get("general/asal-tahu").body<ApiResponse<List<AsalTahuResponse>>>()
        return response.data
    }
}