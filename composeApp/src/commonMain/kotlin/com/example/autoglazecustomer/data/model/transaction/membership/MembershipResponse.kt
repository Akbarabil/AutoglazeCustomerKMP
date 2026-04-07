package com.example.autoglazecustomer.data.model.transaction.membership

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembershipResponse(
    @SerialName("status") val status: Boolean? = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<MembershipItem>? = emptyList()
)

@Serializable
data class MembershipItem(
    @SerialName("ID_MEMBER_CABANG") val idMemberCabang: Int = 0,
    @SerialName("ID_MEMBERSHIP") val idMembership: Int = 0,
    @SerialName("NAMA_MEMBERSHIP") val namaMembership: String = "",
    @SerialName("KODE_CABANG") val kodeCabang: String? = null,
    @SerialName("MASA_BERLAKU") val masaBerlaku: Int = 0,
    @SerialName("HARGA_DAFTAR") val hargaDaftar: Double = 0.0
)
