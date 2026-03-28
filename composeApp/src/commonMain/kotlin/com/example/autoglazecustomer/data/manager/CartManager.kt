package com.example.autoglazecustomer.data.manager

import com.example.autoglazecustomer.data.manager.VoucherManager.clearVouchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ItemCategory {
    JASA, PRODUK, MEMBERSHIP
}


data class CartItem(
    val idProduk: Int,
    val idCabangItem: Int?,
    val idMembership: Int?,
    val namaItem: String,
    val imageUrl: String?,
    val qty: Int,
    val hargaUnit: Double,
    val category: ItemCategory
) {
    val subtotal: Double
        get() = qty * hargaUnit
}


object CartManager {


    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())


    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()


    fun addItemToCart(newItem: CartItem) {
        val currentList = _cartItems.value.toMutableList()


        val existingIndex = currentList.indexOfFirst { it.idProduk == newItem.idProduk }

        if (existingIndex != -1) {

            val existingItem = currentList[existingIndex]
            val updatedItem = existingItem.copy(qty = existingItem.qty + newItem.qty)
            currentList[existingIndex] = updatedItem
        } else {

            currentList.add(newItem)
        }


        _cartItems.value = currentList
    }


    fun removeItem(idProduk: Int) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.idProduk == idProduk }
        _cartItems.value = currentList
    }


    fun updateItemQty(idProduk: Int, newQty: Int) {
        if (newQty <= 0) {
            removeItem(idProduk)
            return
        }

        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.idProduk == idProduk }

        if (index != -1) {
            currentList[index] = currentList[index].copy(qty = newQty)
            _cartItems.value = currentList
        }
    }


    fun clearCart() {
        _cartItems.value = emptyList()
        clearVouchers()
    }
}