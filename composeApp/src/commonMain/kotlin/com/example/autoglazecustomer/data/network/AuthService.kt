package com.example.autoglazecustomer.data.network

import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.DaftarResponse
import com.example.autoglazecustomer.data.model.LoginResponse
import com.example.autoglazecustomer.data.model.ProfileResponse
import com.example.autoglazecustomer.data.model.RegisterResponse
import com.example.autoglazecustomer.data.model.UpdateProfileResponse
import com.example.autoglazecustomer.data.model.password.RequestPasswordResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters

class AuthService {

    suspend fun login(email: String, password: String): LoginResponse {
        return ApiClient.client.submitForm(
            url = "login",
            formParameters = parameters {
                append("email", email.trim())
                append("password", password.trim())
            }
        ).body()
    }

    suspend fun registerCustomer(data: DaftarData): RegisterResponse {
        return ApiClient.client.submitForm(
            url = "register-customer",
            formParameters = parameters {
                append("nama", data.nama)
                append("email", data.email)
                append("tgl_lahir", data.tglLahir)
                append("telepon", data.phone)
                append("password", data.password)
                append("asal_tahu", data.sumberInfo)
                append("id_merek", data.idMerek.toString())
                append("id_tipe", data.idTipe.toString())
                append("tahun", data.tahun)
                append("nopol", data.nopol)
                append("no_rangka", data.noRangka ?: "")
                append("id_warna", data.idWarna.toString())
            }
        ).body()
    }

    suspend fun getProfileData(token: String): ProfileResponse {
        return ApiClient.client.get("profile") {
            header(HttpHeaders.Authorization, token)
        }.body()
    }

    suspend fun cekEmail(email: String): DaftarResponse {
        return ApiClient.client.get("cek-email") {
            url { parameters.append("email", email.trim()) }
        }.body()
    }

    suspend fun updateProfile(
        token: String,
        nama: String,
        email: String,
        telepon: String,
        imageBytes: ByteArray? = null
    ): UpdateProfileResponse {
        return ApiClient.client.post("profile") {
            header(HttpHeaders.Authorization, token)
            header(HttpHeaders.Accept, "application/json")
            header("X-HTTP-Method-Override", "PUT")

            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("_method", "PUT")
                        append("nama", nama)
                        append("email", email)
                        append("telepon", telepon)

                        if (imageBytes != null) {
                            append("photo", imageBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"photo\"; filename=\"profile.jpg\""
                                )
                            })
                        }
                    }
                ))
        }.body()
    }

    suspend fun generatePassword(requestData: Map<String, String>): RequestPasswordResponse {
        return ApiClient.client.post("customer/generate-password") {
            setBody(FormDataContent(parameters {
                requestData.forEach { (key, value) ->
                    append(key, value)
                }
            }))
        }.body()
    }
}