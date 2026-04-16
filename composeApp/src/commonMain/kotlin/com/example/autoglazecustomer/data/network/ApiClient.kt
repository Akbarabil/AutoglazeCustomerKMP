package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.manager.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
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

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                val clientException = exception as? ClientRequestException
                    ?: return@handleResponseExceptionWithRequest
                if (clientException.response.status.value == 401) {
                    SessionManager.forceLogout()
                }
            }
        }
    }
}