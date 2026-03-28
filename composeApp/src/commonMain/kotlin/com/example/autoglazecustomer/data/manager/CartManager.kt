package com.example.autoglazecustomer.data.manager

import com.example.autoglazecustomer.data.manager.VoucherManager.clearVouchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

enum class ItemCategory {
    JASA, PRODUK, MEMBERSHIP
}


data class CartItem(
    val idProduk: Int,
    val idCabangItem: Int?,   // Isi jika Jasa/Produk
    val idMembership: Int?,   // Isi jika Membership
    val namaItem: String,
    val imageUrl: String?,
    val qty: Int,
    val hargaUnit: Double,
    val category: ItemCategory
) {
    val subtotal: Double
        get() = qty * hargaUnit
}

// 3. Otak Utama Keranjang (Singleton Object / Global State)
object CartManager {

    // Menyimpan daftar barang. (Private agar tidak bisa diubah sembarangan dari luar)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    // Publik StateFlow yang hanya bisa di-observe (dibaca) oleh UI
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // --------------------------------------------------------
    // FITUR 1: Tambah Barang ke Keranjang
    // --------------------------------------------------------
    fun addItemToCart(newItem: CartItem) {
        val currentList = _cartItems.value.toMutableList()

        // Cari apakah barang ini sudah ada di keranjang (Cek ID Produk)
        val existingIndex = currentList.indexOfFirst { it.idProduk == newItem.idProduk }

        if (existingIndex != -1) {
            // JIKA SUDAH ADA: Tambahkan qty-nya saja
            val existingItem = currentList[existingIndex]
            val updatedItem = existingItem.copy(qty = existingItem.qty + newItem.qty)
            currentList[existingIndex] = updatedItem
        } else {
            // JIKA BELUM ADA: Masukkan sebagai barang baru
            currentList.add(newItem)
        }

        // Tembakkan perubahan ke seluruh aplikasi
        _cartItems.value = currentList
    }

    // --------------------------------------------------------
    // FITUR 2: Hapus Satu Barang (Misal di halaman Checkout di-swipe hapus)
    // --------------------------------------------------------
    fun removeItem(idProduk: Int) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.idProduk == idProduk }
        _cartItems.value = currentList
    }

    // --------------------------------------------------------
    // FITUR 3: Update Qty Manual (Jika di Checkout user tambah/kurang)
    // --------------------------------------------------------
    fun updateItemQty(idProduk: Int, newQty: Int) {
        if (newQty <= 0) {
            removeItem(idProduk) // Kalau 0, sekalian hapus
            return
        }

        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.idProduk == idProduk }

        if (index != -1) {
            currentList[index] = currentList[index].copy(qty = newQty)
            _cartItems.value = currentList
        }
    }

    // --------------------------------------------------------
    // FITUR 4: Hapus SEMUA Isi Keranjang (Saat user ganti menu)
    // --------------------------------------------------------
    fun clearCart() {
        _cartItems.value = emptyList()
        clearVouchers()
    }
}