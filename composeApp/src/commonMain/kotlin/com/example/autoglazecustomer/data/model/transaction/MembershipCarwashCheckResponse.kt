package com.example.autoglazecustomer.data.model.transaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembershipCarwashCheckResponse(
    @SerialName("status") val status: Boolean,
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<MembershipItemDetail>? = null
)
@Serializable
data class MembershipItemDetail(
    @SerialName("ID_MEMBERSHIP_ITEM") val idMembershipItem: Int,
    @SerialName("ID_PRODUK") val idProduk: Int
)
