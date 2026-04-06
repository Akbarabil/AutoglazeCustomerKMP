package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.HistoryResponse
import com.example.autoglazecustomer.data.model.PointResponse
import com.example.autoglazecustomer.data.model.VoucherKendaraanResponse
import com.example.autoglazecustomer.data.model.VoucherUmumResponse
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class TransactionService {

    suspend fun processCheckout(payload: CheckoutPayload, isMembership: Boolean): CheckoutResponse {
        return try {
            val targetUrl = if (isMembership) "insert-penjualan-customer-membership" else "insert-penjualan-customer"
            ApiClient.client.post(targetUrl) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        } catch (e: Exception) {
            CheckoutResponse(status = false, message = "Terjadi kesalahan koneksi: ${e.message}", kodePenjualan = null)
        }
    }

    suspend fun getHistoryPesanan(idCustomer: Int, idKendaraan: Int): HistoryResponse {
        return ApiClient.client.get("history-pesanan") {
            url {
                parameters.append("id_customer", idCustomer.toString())
                parameters.append("id_kendaraan", idKendaraan.toString())
            }
        }.body()
    }

    suspend fun getPoint(idCustomer: Int): PointResponse {
        return ApiClient.client.get("get-point") {
            url { parameters.append("id_customer", idCustomer.toString()) }
        }.body()
    }

    suspend fun getVoucherUmum(): VoucherUmumResponse {
        return ApiClient.client.get("list-voucher-umum").body()
    }

    suspend fun getVoucherSaya(token: String): VoucherUmumResponse {
        return ApiClient.client.get("list-voucher-umum") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }

    suspend fun getVouchersByVehicle(idKendaraan: Int): VoucherKendaraanResponse {
        return ApiClient.client.get("list-voucher-by-kendaraan") {
            url { parameters.append("id_kendaraan", idKendaraan.toString()) }
        }.body()
    }
}