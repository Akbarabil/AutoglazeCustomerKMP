package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.ApiResponse
import com.example.autoglazecustomer.data.model.AsalTahuResponse
import com.example.autoglazecustomer.data.model.BeritaResponse
import com.example.autoglazecustomer.data.model.SliderResponse
import io.ktor.client.call.body
import io.ktor.client.request.get

class HomeService {

    suspend fun getSlider(): SliderResponse {
        return ApiClient.client.get("slider").body()
    }

    suspend fun getBerita(): BeritaResponse {
        return ApiClient.client.get("berita").body()
    }

}