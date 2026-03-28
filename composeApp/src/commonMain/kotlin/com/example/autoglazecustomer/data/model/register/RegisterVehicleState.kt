package com.example.autoglazecustomer.data.model.register

import com.example.autoglazecustomer.data.model.MerkKendaraanResponse
import com.example.autoglazecustomer.data.model.TipeKendaraanResponse
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse

data class RegisterVehicleState(

    val listMerek: List<MerkKendaraanResponse> = emptyList(),
    val listTipe: List<TipeKendaraanResponse> = emptyList(),
    val listWarna: List<WarnaKendaraanResponse> = emptyList(),


    val merekTerpilih: MerkKendaraanResponse? = null,
    val tipeTerpilih: TipeKendaraanResponse? = null,
    val tahun: String = "",
    val nopol: String = "",
    val noRangka: String = "",
    val warnaTerpilih: WarnaKendaraanResponse? = null,


    val isLoadingMerek: Boolean = false,
    val isLoadingTipe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorField: String? = null
)