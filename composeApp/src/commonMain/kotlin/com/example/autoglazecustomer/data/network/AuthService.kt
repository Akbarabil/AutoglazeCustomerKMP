package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.AddVehicleResponse
import com.example.autoglazecustomer.data.model.ApiResponse
import com.example.autoglazecustomer.data.model.AsalTahuResponse
import com.example.autoglazecustomer.data.model.BeritaResponse
import com.example.autoglazecustomer.data.model.ChekKendaraanResponse
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.DaftarResponse
import com.example.autoglazecustomer.data.model.HistoryResponse
import com.example.autoglazecustomer.data.model.LoginResponse
import com.example.autoglazecustomer.data.model.MerkKendaraanResponse
import com.example.autoglazecustomer.data.model.PointResponse
import com.example.autoglazecustomer.data.model.ProfileResponse
import com.example.autoglazecustomer.data.model.RegisterResponse
import com.example.autoglazecustomer.data.model.SliderResponse
import com.example.autoglazecustomer.data.model.TipeKendaraanResponse
import com.example.autoglazecustomer.data.model.UpdateProfileResponse
import com.example.autoglazecustomer.data.model.VehicleResponse
import com.example.autoglazecustomer.data.model.VoucherKendaraanResponse
import com.example.autoglazecustomer.data.model.VoucherUmumResponse
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse
import com.example.autoglazecustomer.data.model.password.RequestPasswordResponse
import com.example.autoglazecustomer.data.model.transaction.CabangTerdekatResponse
import com.example.autoglazecustomer.data.model.transaction.MembershipCarwashCheckResponse
import com.example.autoglazecustomer.data.model.transaction.MembershipStatusResponse
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutResponse
import com.example.autoglazecustomer.data.model.transaction.jasa.JasaResponse
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipResponse
import com.example.autoglazecustomer.data.model.transaction.produk.ProdukResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json


class AuthService {
    private val BASE_URL = "https://autoglaze-canary.digiponic.co.id/api/"

    val client = HttpClient {
        expectSuccess = true
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


    suspend fun login(email: String, password: String): LoginResponse {
        return client.submitForm(
            url = "login",
            formParameters = parameters {
                append("email", email.trim())
                append("password", password.trim())
            }
        ).body()
    }


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
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTipe(idMerek: Int): List<TipeKendaraanResponse> {
        return try {
            client.get("kendaraan/tipe") {
                url { parameters.append("id_merek", idMerek.toString()) }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWarna(): List<WarnaKendaraanResponse> {
        return try {
            client.get("warna").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAsalTahu(): List<AsalTahuResponse> {
        val response = client.get("general/asal-tahu").body<ApiResponse<List<AsalTahuResponse>>>()
        return response.data
    }

    suspend fun getHistoryPesanan(idCustomer: Int, idKendaraan: Int): HistoryResponse {
        return client.get("history-pesanan") {
            url {
                parameters.append("id_customer", idCustomer.toString())
                parameters.append("id_kendaraan", idKendaraan.toString())
            }
        }.body()
    }

    suspend fun getPoint(idCustomer: Int): PointResponse {
        return client.get("get-point") {
            url {
                parameters.append("id_customer", idCustomer.toString())
            }
        }.body()
    }

    suspend fun getVoucherSaya(token: String): VoucherUmumResponse {
        return client.get("list-voucher-umum") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }


    suspend fun updateProfile(
        token: String,
        nama: String,
        email: String,
        telepon: String,
        imageBytes: ByteArray? = null
    ): UpdateProfileResponse {
        return client.post("profile") {
            header(HttpHeaders.Authorization, token)
            header(HttpHeaders.Accept, "application/json")
            header("X-HTTP-Method-Override", "PUT")

            setBody(
                MultiPartFormDataContent(
                formData {

                    append("_method", "PUT")

                    append("nama", nama)
                    append("email", email)
                    append("telepon", telepon)

                    if (imageBytes != null) {
                        append("photo", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"photo\"; filename=\"profile.jpg\""
                            )
                        })
                    }
                }
            ))
        }.body()
    }

    suspend fun addVehicle(
        token: String,
        idMerek: Int,
        idTipe: Int,
        tahun: Int,
        nopol: String,
        noRangka: String,
        idWarna: Int
    ): AddVehicleResponse {
        return client.post("kendaraan") {
            header(HttpHeaders.Authorization, token)
            setBody(FormDataContent(parameters {
                append("id_merek", idMerek.toString())
                append("id_tipe", idTipe.toString())
                append("tahun", tahun.toString())
                append("nopol", nopol)
                append("no_rangka", noRangka)
                append("id_warna", idWarna.toString())
            }))
        }.body()
    }

    suspend fun getVouchersByVehicle(idKendaraan: Int): VoucherKendaraanResponse {
        return client.get("list-voucher-by-kendaraan") {
            url { parameters.append("id_kendaraan", idKendaraan.toString()) }
        }.body()
    }

    suspend fun generatePassword(requestData: Map<String, String>): RequestPasswordResponse {
        return client.post("customer/generate-password") {
            setBody(FormDataContent(parameters {
                requestData.forEach { (key, value) ->
                    append(key, value)
                }
            }))
        }.body()
    }

    suspend fun getCabangTerdekat(longitude: Double, latitude: Double): CabangTerdekatResponse {
        return client.get("cabang-by-long-lat") {
            url {
                parameters.append("long", longitude.toString())
                parameters.append("lat", latitude.toString())
            }
        }.body()
    }

    suspend fun checkMembership(idKendaraan: Int, kodeCabang: String): Int {
        return try {
            val response: MembershipStatusResponse = client.get("check-membership-customer") {
                url {
                    parameters.append("id_kendaraan", idKendaraan.toString())
                    parameters.append("kode_cabang", kodeCabang)
                }
            }.body()

            response.statusInt ?: 0
        } catch (e: Exception) {
            println("Error check membership: ${e.message}")
            0
        }
    }

    suspend fun getAllServices(kodeCabang: String): JasaResponse {
        return try {
            client.get("get-all-produk-services") {
                url {
                    parameters.append("kode_cabang", kodeCabang)
                }
            }.body()
        } catch (e: Exception) {
            JasaResponse(
                status = false,
                message = "Gagal memuat layanan: ${e.message}",
                data = emptyList()
            )
        }
    }

    suspend fun checkMembershipCarwash(idKendaraan: Int): MembershipCarwashCheckResponse {
        val now = kotlin.time.Clock.System.now()
        val instant = Instant.fromEpochMilliseconds(now.toEpochMilliseconds())
        val timeZone = TimeZone.currentSystemDefault()
        val today = instant.toLocalDateTime(timeZone).date

        val tglTransaksi = today.toString()

        return try {
            client.get("check-membership-carwash") {
                url {
                    parameters.append("id_kendaraan", idKendaraan.toString())
                    parameters.append("tgl_transaksi", tglTransaksi)
                }
            }.body()
        } catch (e: Exception) {
            MembershipCarwashCheckResponse(
                status = false,
                message = "Gagal cek status carwash: ${e.message}",
                data = emptyList()
            )
        }
    }

    suspend fun getProduk(kodeCabang: String): ProdukResponse {
        return try {
            client.get("get-only-produk") {
                url {
                    parameters.append("kode_cabang", kodeCabang)
                }
            }.body()
        } catch (e: Exception) {
            ProdukResponse(
                status = false,
                message = "Gagal memuat produk: ${e.message}",
                data = emptyList()
            )
        }
    }

    suspend fun getMembership(kodeCabang: String): MembershipResponse {
        return try {
            client.get("get-all-membership") {
                url {
                    parameters.append("kode_cabang", kodeCabang)
                }
            }.body()
        } catch (e: Exception) {
            MembershipResponse(status = false, data = emptyList())
        }
    }

    suspend fun processCheckout(
        payload: CheckoutPayload,
        isMembership: Boolean
    ): CheckoutResponse {
        return try {
            val targetUrl = if (isMembership) {
                "insert-penjualan-customer-membership"
            } else {
                "insert-penjualan-customer"
            }


            client.post(targetUrl) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()

        } catch (e: Exception) {
            CheckoutResponse(
                status = false,
                message = "Terjadi kesalahan koneksi: ${e.message}",
                kodePenjualan = null
            )
        }
    }

}