package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.ApiResponse
import com.example.autoglazecustomer.data.model.AsalTahuResponse
import com.example.autoglazecustomer.data.model.transaction.CabangTerdekatResponse
import io.ktor.client.call.body
import io.ktor.client.request.get

class CabangService {

    suspend fun getCabangTerdekat(longitude: Double, latitude: Double): CabangTerdekatResponse {
        return ApiClient.client.get("cabang-by-long-lat") {
            url {
                parameters.append("long", longitude.toString())
                parameters.append("lat", latitude.toString())
            }
        }.body()
    }

    suspend fun getAsalTahu(): List<AsalTahuResponse> {
        val response = ApiClient.client.get("general/asal-tahu").body<ApiResponse<List<AsalTahuResponse>>>()
        return response.data
    }
}