package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.AddVehicleResponse
import com.example.autoglazecustomer.data.model.ChekKendaraanResponse
import com.example.autoglazecustomer.data.model.DaftarResponse
import com.example.autoglazecustomer.data.model.MerkKendaraanResponse
import com.example.autoglazecustomer.data.model.TipeKendaraanResponse
import com.example.autoglazecustomer.data.model.VehicleResponse
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters

class VehicleService {

    suspend fun getVehicles(token: String): VehicleResponse {
        return ApiClient.client.get("kendaraan") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }

    suspend fun cekKendaraan(nopol: String): ChekKendaraanResponse {
        return ApiClient.client.submitForm(
            url = "cek-kendaraan",
            formParameters = parameters {
                append("search_type", "nopol")
                append("search_value", nopol)
            }
        ).body()
    }

    suspend fun cekNopol(nopol: String): DaftarResponse {
        return ApiClient.client.get("cek-nopol") {
            url { parameters.append("nopol", nopol.trim()) }
        }.body()
    }

    suspend fun getMerek(): List<MerkKendaraanResponse> {
        return try {
            ApiClient.client.get("kendaraan/merek").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTipe(idMerek: Int): List<TipeKendaraanResponse> {
        return try {
            ApiClient.client.get("kendaraan/tipe") {
                url { parameters.append("id_merek", idMerek.toString()) }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWarna(): List<WarnaKendaraanResponse> {
        return try {
            ApiClient.client.get("warna").body()
        } catch (e: Exception) {
            emptyList()
        }
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
        return ApiClient.client.post("kendaraan") {
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
}