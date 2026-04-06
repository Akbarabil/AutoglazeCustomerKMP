package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.transaction.MembershipCarwashCheckResponse
import com.example.autoglazecustomer.data.model.transaction.MembershipStatusResponse
import com.example.autoglazecustomer.data.model.transaction.jasa.JasaResponse
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipResponse
import com.example.autoglazecustomer.data.model.transaction.produk.ProdukResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ProductService {

    suspend fun getAllServices(kodeCabang: String): JasaResponse {
        return try {
            ApiClient.client.get("get-all-produk-services") {
                url { parameters.append("kode_cabang", kodeCabang) }
            }.body()
        } catch (e: Exception) {
            JasaResponse(status = false, message = "Gagal memuat layanan: ${e.message}", data = emptyList())
        }
    }

    suspend fun getProduk(kodeCabang: String): ProdukResponse {
        return try {
            ApiClient.client.get("get-only-produk") {
                url { parameters.append("kode_cabang", kodeCabang) }
            }.body()
        } catch (e: Exception) {
            ProdukResponse(status = false, message = "Gagal memuat produk: ${e.message}", data = emptyList())
        }
    }

    suspend fun getMembership(kodeCabang: String): MembershipResponse {
        return try {
            ApiClient.client.get("get-all-membership") {
                url { parameters.append("kode_cabang", kodeCabang) }
            }.body()
        } catch (e: Exception) {
            MembershipResponse(status = false, data = emptyList())
        }
    }

    suspend fun checkMembership(idKendaraan: Int, kodeCabang: String): Int {
        return try {
            val response: MembershipStatusResponse = ApiClient.client.get("check-membership-customer") {
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

    suspend fun checkMembershipCarwash(idKendaraan: Int): MembershipCarwashCheckResponse {
        val now = kotlin.time.Clock.System.now()
        val instant = Instant.fromEpochMilliseconds(now.toEpochMilliseconds())
        val timeZone = TimeZone.currentSystemDefault()
        val today = instant.toLocalDateTime(timeZone).date

        return try {
            ApiClient.client.get("check-membership-carwash") {
                url {
                    parameters.append("id_kendaraan", idKendaraan.toString())
                    parameters.append("tgl_transaksi", today.toString())
                }
            }.body()
        } catch (e: Exception) {
            MembershipCarwashCheckResponse(status = false, message = "Gagal cek status carwash: ${e.message}", data = emptyList())
        }
    }
}