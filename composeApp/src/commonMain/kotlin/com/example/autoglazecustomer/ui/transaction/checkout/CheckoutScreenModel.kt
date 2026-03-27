package com.example.autoglazecustomer.ui.transaction.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.ItemCategory
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutDetailPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutPayload
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class CheckoutScreenModel(
    private val authService: AuthService,
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : ScreenModel {

    // Status UI
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successKodePenjualan by mutableStateOf("")

    // Hasil Kalkulasi Finansial
    var subtotalFinal by mutableStateOf(0.0)
    var pajakFinal by mutableStateOf(0.0)
    var nettFinal by mutableStateOf(0.0)
    var diskonFinal by mutableStateOf(0.0) // Nanti untuk voucher

    // JOSJIS: Fungsi Penghitung ala Kasir (PPN & DPP)
    fun calculateTotals(cartItems: List<CartItem>) {
        val totalBayarAsli = cartItems.sumOf { it.subtotal }
        val taxRate = 0.11 // PPN 11%

        // Cek pengaturan PPN cabang (asumsi dari kode lama: 1 = pakai PPN)
        val isUsingPpn = cabang.isUsingPpn == 1

        if (isUsingPpn) {
            val dpp = totalBayarAsli / (1.0 + taxRate)
            nettFinal = dpp.roundToLong().toDouble()
            pajakFinal = totalBayarAsli - nettFinal
        } else {
            nettFinal = totalBayarAsli.roundToLong().toDouble()
            pajakFinal = 0.0
        }

        // Hitung total akhir
        val totalYangHarusDibayar = (nettFinal + pajakFinal) - diskonFinal
        subtotalFinal = if (totalYangHarusDibayar < 0) 0.0 else totalYangHarusDibayar
    }

    // JOSJIS: Mesin Pembentuk Payload
    fun processCheckout(cartItems: List<CartItem>, customerName: String, customerId: String) {
        if (cartItems.isEmpty()) return

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. Deteksi Jenis Penjualan Otomatis
                val isMembership = cartItems.any { it.category == ItemCategory.MEMBERSHIP }
                val jenisPenjualan = if (isMembership) "Membership" else "Reguler"

                // 2. Petakan Keranjang ke Format API
                val detailPayload = cartItems.map { item ->
                    CheckoutDetailPayload(
                        idCabangItem = if (isMembership) null else item.idCabangItem,
                        idMembership = if (isMembership) item.idMembership else null,
                        qty = item.qty,
                        subtotal = item.subtotal,
                        satuan = item.hargaUnit
                    )
                }

                // 3. Bangun Payload Utama
                val payload = CheckoutPayload(
                    idCustomer = customerId,
                    namaPelanggan = customerName,
                    idKendaraan = vehicle.vehicle.idKendaraan?.toString() ?: "",
                    jenisPenjualan = jenisPenjualan,
                    jenisTransaksi = "Reguler",
                    odometer = null, // Opsional
                    kodeCabang = cabang.kodeCabang,
                    namaCabang = cabang.namaCabang,
                    idVoucher = "[]", // Default voucher kosong
                    subtotal = subtotalFinal.roundToLong().toString(),
                    pajak = pajakFinal.roundToLong().toString(),
                    nett = nettFinal.roundToLong().toString(),
                    diskonNominal = diskonFinal.roundToLong().toString(),
                    detail = detailPayload
                )


                 val response = authService.processCheckout(payload, isMembership)
                if (response.status) {
                    successKodePenjualan = response.kodePenjualan ?: "Sukses"
                    isSuccess = true
                } else {
                    errorMessage = response.message
                }

            } catch (e: Exception) {
                errorMessage = "Gagal memproses pembayaran: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}