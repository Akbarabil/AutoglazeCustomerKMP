package com.example.autoglazecustomer.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {
    private const val BASE_URL = "https://autoglaze-canary.digiponic.co.id/api/"

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
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }
}