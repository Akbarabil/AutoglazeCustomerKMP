// CheckoutScreenModel.kt
package com.example.autoglazecustomer.ui.transaction.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.ItemCategory
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutDetailPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutPayload
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.network.TransactionService
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.roundToLong

class CheckoutScreenModel(
    private val transactionService: TransactionService,
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : ScreenModel {

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successKodePenjualan by mutableStateOf("")

    var subtotalFinal by mutableStateOf(0.0)
    var pajakFinal by mutableStateOf(0.0)
    var nettFinal by mutableStateOf(0.0)
    var diskonFinal by mutableStateOf(0.0)

    fun calculateTotals(cartItems: List<CartItem>) {
        val totalBayarAsli = cartItems.sumOf { it.subtotal }
        val taxRate = 0.11
        val isUsingPpn = cabang.isUsingPpn == 1

        val selectedVouchers = VoucherManager.selectedVouchers.value
        println("🛒 [DEBUG 3 - CHECKOUT] calculateTotals dipanggil!")
        println("🛒 [DEBUG 3] Total Bayar Asli: $totalBayarAsli")
        println("🛒 [DEBUG 3] Jumlah Voucher yang terbaca di Checkout: ${selectedVouchers.size}")
        val hasMembership = vehicle.vehicle.isMembership == 1

        var tempTotalDiskon = 0.0

        selectedVouchers.forEach { voucher ->
            val potHarga = if (hasMembership) voucher.potHargaMember else voucher.potHargaNonMember
            val persen = if (hasMembership) voucher.presentaseMember else voucher.presentaseNonMember

            val idProdRaw = voucher.idProduk?.toString()?.trim() ?: ""
            println("🛒 [DEBUG 3] Memproses Voucher: ${voucher.namaVoucher} | ID_PRODUK: '$idProdRaw'")
            val isSpecificProduct = idProdRaw.isNotEmpty() && idProdRaw != "0" && idProdRaw != "null"

            val targetedSubtotal = if (isSpecificProduct) {
                val allowedIds = idProdRaw.split(';').map { it.trim() }
                val matchedItems = cartItems.filter { it.idProduk.toString().trim() in allowedIds }

                if (matchedItems.isNotEmpty()) {
                    matchedItems.maxOf { it.hargaUnit }.toDouble()
                } else {
                    0.0
                }
            } else {
                totalBayarAsli.toDouble()
            }
             println("🛒 [DEBUG 3] Targeted Subtotal untuk voucher ini: $targetedSubtotal")

            var currentDiscount = if (persen > 0) {
                targetedSubtotal * (persen / 100.0)
            } else {
                potHarga
            }

            if (isSpecificProduct && currentDiscount > targetedSubtotal) {
                currentDiscount = targetedSubtotal.toDouble()
            }

            tempTotalDiskon += currentDiscount
        }

        diskonFinal = tempTotalDiskon
        println("🛒 [DEBUG 3] TOTAL DISKON FINAL: $diskonFinal")

        if (isUsingPpn) {
            val dpp = totalBayarAsli / (1.0 + taxRate)
            nettFinal = kotlin.math.round(dpp).toDouble()
            pajakFinal = totalBayarAsli - nettFinal
        } else {
            nettFinal = totalBayarAsli.toDouble()
            pajakFinal = 0.0
        }

        val finalAmount = (nettFinal + pajakFinal) - diskonFinal
        subtotalFinal = if (finalAmount < 0) 0.0 else finalAmount
    }

    fun processCheckout(cartItems: List<CartItem>) {
        if (cartItems.isEmpty()) return

        val customerIdInt = TokenManager.getCustomerId()
        val customerName = TokenManager.getUserName()

        if (customerIdInt == -1) {
            errorMessage = "Sesi login tidak valid. Silakan login kembali."
            return
        }

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val isMembership = cartItems.any { it.category == ItemCategory.MEMBERSHIP }
                val jenisPenjualan = if (isMembership) "Membership" else "Reguler"

                val detailPayload = cartItems.map { item ->
                    CheckoutDetailPayload(
                        idCabangItem = if (isMembership) null else item.idCabangItem,
                        idMembership = if (isMembership) item.idMembership else null,
                        qty = item.qty,
                        subtotal = item.subtotal,
                        satuan = item.hargaUnit
                    )
                }

                val selectedVoucherIds = VoucherManager.selectedVouchers.value.map { it.idVoucher }
                val idVoucherPayload =
                    if (selectedVoucherIds.isEmpty()) "[]" else selectedVoucherIds.toString()

                val payload = CheckoutPayload(
                    idCustomer = customerIdInt,
                    namaPelanggan = customerName,
                    idKendaraan = vehicle.vehicle.idKendaraan?.toString() ?: "",
                    jenisPenjualan = jenisPenjualan,
                    jenisTransaksi = "Reguler",
                    odometer = null,
                    kodeCabang = cabang.kodeCabang,
                    namaCabang = cabang.namaCabang,
                    idVoucher = idVoucherPayload,
                    subtotal = subtotalFinal.roundToLong().toString(),
                    pajak = pajakFinal.roundToLong().toString(),
                    nett = nettFinal.roundToLong().toString(),
                    diskonNominal = diskonFinal.roundToLong().toString(),
                    detail = detailPayload
                )

                try {
                    val jsonString = Json { prettyPrint = true }.encodeToString(payload)
                    println("🚀 ====== PAYLOAD CHECKOUT DENGAN VOUCHER ======\n$jsonString\n==============================")
                } catch (e: Exception) {
                }

                val response = transactionService.processCheckout(payload, isMembership)
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