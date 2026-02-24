package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthService {
    val client = HttpClient {
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
            url = "https://autoglaze-rewrite.digiponic.co.id/api/login",
            formParameters = parameters {
                append("email", email.trim()) // Gunakan trim untuk hapus spasi typo
                append("password", password.trim())
            }
        ) {
            // Tambahkan header ini agar server tidak bingung
            header(HttpHeaders.Accept, "application/json")
        }.body()
    }
}