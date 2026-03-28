package com.example.autoglazecustomer.data.manager

import com.example.autoglazecustomer.data.model.transaction.VoucherUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VoucherManager {

    private val _selectedVouchers = MutableStateFlow<List<VoucherUIModel>>(emptyList())
    val selectedVouchers: StateFlow<List<VoucherUIModel>> = _selectedVouchers.asStateFlow()

    fun setVouchers(vouchers: List<VoucherUIModel>) {
        _selectedVouchers.value = vouchers
    }

    fun clearVouchers() {
        _selectedVouchers.value = emptyList()
    }
}