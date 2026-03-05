package com.example.autoglazecustomer.data.model.register

import com.example.autoglazecustomer.data.model.AsalTahuResponse

data class SurveyState(
    val asalTahuList: List<AsalTahuResponse> = emptyList(),
    val selectedAsalTahu: AsalTahuResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val errorField: String? = null
)