package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.model.HistoryResponse
import com.example.autoglazecustomer.data.model.PointResponse
import com.example.autoglazecustomer.data.model.VoucherKendaraanResponse
import com.example.autoglazecustomer.data.model.VoucherUmumResponse
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutResponse
import com.example.autoglazecustomer.data.model.transaction.checkout.DeleteDraftPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.InsertDraftPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.UpdateFinalPayload
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class TransactionService {

    suspend fun insertDraftPenjualan(payload: InsertDraftPayload, isMembership: Boolean): CheckoutResponse {
        return try {
            val targetUrl =
                if (isMembership) "insert-penjualan-customer-membership" else "insert-penjualan-customer"
            ApiClient.client.post(targetUrl) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        } catch (e: Exception) {
            CheckoutResponse(status = false, message = e.toUserMessage(), kodePenjualan = null)
        }
    }

    suspend fun updateFinalPenjualan(payload: UpdateFinalPayload): CheckoutResponse {
        return try {
            ApiClient.client.post("update-harga-voucher-customer") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        } catch (e: Exception) {
            CheckoutResponse(status = false, message = e.toUserMessage(), kodePenjualan = null)
        }
    }

    suspend fun deleteDraftPenjualan(payload: DeleteDraftPayload): CheckoutResponse {
        return try {
            ApiClient.client.post("delete-penjualan-recieved") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()
        } catch (e: Exception) {
            CheckoutResponse(status = false, message = e.toUserMessage(), kodePenjualan = null)
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

    suspend fun getVoucherUmum(kodePenjualan: String? = null): VoucherUmumResponse {
        return ApiClient.client.get("list-voucher-umum") {
            url {
                if (!kodePenjualan.isNullOrEmpty()) {
                    parameters.append("kode_penjualan", kodePenjualan)
                }
            }
        }.body()
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