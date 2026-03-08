package com.example.autoglazecustomer.data.model.addvehicle

import com.example.autoglazecustomer.data.model.MerkKendaraanResponse
import com.example.autoglazecustomer.data.model.TipeKendaraanResponse
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse

data class AddVehicleState(
    val listMerek: List<MerkKendaraanResponse> = emptyList(),
    val listTipe: List<TipeKendaraanResponse> = emptyList(),
    val listWarna: List<WarnaKendaraanResponse> = emptyList(),
    val merekTerpilih: MerkKendaraanResponse? = null,
    val tipeTerpilih: TipeKendaraanResponse? = null,
    val warnaTerpilih: WarnaKendaraanResponse? = null,
    val nopol: String = "",
    val noRangka: String = "",
    val tahun: String = "",
    val isLoading: Boolean = false,
    val isLoadingMerek: Boolean = false,
    val isLoadingTipe: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val errorField: String? = null
)
