package com.example.autoglazecustomer.data.local

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.errors.IOException

fun Throwable.toUserMessage(): String {
    val errorMsg = this.message?.lowercase() ?: ""
    val exceptionName = this::class.simpleName?.lowercase() ?: ""

    return when {
        this is ConnectTimeoutException ->
            "Gagal terhubung ke server. Silakan pastikan koneksi internet anda stabil. (Err: Timeout)"

        this is HttpRequestTimeoutException ->
            "Waktu permintaan habis. Server sedang mengalami kendala atau kepadatan trafik. (Err: RTO)"

        this is ServerResponseException ->
            "Terjadi kesalahan pada sistem pusat. Mohon coba beberapa saat lagi. (Err: ${this.response.status.value})"

        this is ClientRequestException -> {
            val code = this.response.status.value
            when (code) {
                401 -> "Sesi anda telah berakhir. Silakan masuk kembali ke aplikasi. (Err: 401)"
                403 -> "Akses ditolak. Anda tidak memiliki izin untuk melihat data ini. (Err: 403)"
                404 -> "Data tidak ditemukan di server. (Err: 404)"
                else -> "Permintaan tidak dapat diproses. (Err: $code)"
            }
        }

        this is IOException ||
                errorMsg.contains("unknownhost") ||
                errorMsg.contains("failed to connect") ||
                errorMsg.contains("network is unreachable") ||
                errorMsg.contains("nodename nor servname") ||
                errorMsg.contains("connection refused") ||
                exceptionName.contains("unresolvedaddress") -> {
            if (errorMsg.contains("ssl", ignoreCase = true)) {
                "Terjadi kendala pada sertifikat keamanan server. (Err: SSL)"
            } else {
                "Koneksi internet terputus. Mohon periksa koneksi jaringan anda. (Err: Offline)"
            }
        }

        else -> {
            val originalException = this::class.simpleName ?: "Unknown"
            "Terjadi kesalahan yang tidak terduga. Silakan hubungi layanan pelanggan atau coba lagi nanti. (Err: $originalException)"
        }
    }
}