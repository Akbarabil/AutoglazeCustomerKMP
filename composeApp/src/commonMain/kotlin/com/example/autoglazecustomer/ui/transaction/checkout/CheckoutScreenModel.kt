package com.example.autoglazecustomer.ui.transaction.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.local.toUserMessage
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.ItemCategory
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.checkout.CheckoutDetailPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.DeleteDraftPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.InsertDraftPayload
import com.example.autoglazecustomer.data.model.transaction.checkout.UpdateFinalPayload
import com.example.autoglazecustomer.data.network.TransactionService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class CheckoutScreenModel(
    private val transactionService: TransactionService,
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : ScreenModel {

    var isCreatingDraft by mutableStateOf(true)
    var draftError by mutableStateOf<String?>(null)
    var kodePenjualanDraft by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successKodePenjualan by mutableStateOf("")

    var subtotalFinal by mutableStateOf(0.0)
    var pajakFinal by mutableStateOf(0.0)
    var nettFinal by mutableStateOf(0.0)
    var diskonFinal by mutableStateOf(0.0)

    fun createDraftOrder(cartItems: List<CartItem>) {
        if (cartItems.isEmpty() || kodePenjualanDraft.isNotEmpty()) return

        val customerIdInt = TokenManager.getCustomerId()
        val customerName = TokenManager.getUserName()

        if (customerIdInt == -1) {
            draftError = "Sesi login tidak valid. Silakan login kembali."
            isCreatingDraft = false
            return
        }

        screenModelScope.launch {
            isCreatingDraft = true
            draftError = null

            try {
                val isMembership = cartItems.any { it.category == ItemCategory.MEMBERSHIP }
                val jenisPenjualan = if (isMembership) "Membership" else "Reguler"
                val jenisTransaksi = "Reguler"

                val detailPayload = cartItems.map { item ->
                    CheckoutDetailPayload(
                        idCabangItem = if (isMembership) null else item.idCabangItem,
                        idMembership = if (isMembership) item.idMembership else null,
                        qty = item.qty,
                        subtotal = item.subtotal,
                        satuan = item.hargaUnit
                    )
                }

                val payload = InsertDraftPayload(
                    idCustomer = customerIdInt,
                    idKendaraan = vehicle.vehicle.idKendaraan?.toString() ?: "",
                    jenisPenjualan = jenisPenjualan,
                    jenisTransaksi = jenisTransaksi,
                    namaPelanggan = customerName,
                    kodeCabang = cabang.kodeCabang,
                    namaCabang = cabang.namaCabang,
                    detail = detailPayload
                )

                val response = transactionService.insertDraftPenjualan(payload, isMembership)

                if (response.status && !response.kodePenjualan.isNullOrEmpty()) {
                    kodePenjualanDraft = response.kodePenjualan
                    calculateTotals(cartItems)
                } else {
                    draftError = response.message ?: "Gagal menyiapkan pesanan. Silakan coba lagi."
                }
            } catch (e: Exception) {
                draftError = e.toUserMessage()
            } finally {
                isCreatingDraft = false
            }
        }
    }

    fun calculateTotals(cartItems: List<CartItem>) {
        val totalBayarAsli = cartItems.sumOf { it.subtotal }
        val taxRate = 0.11
        val isUsingPpn = cabang.isUsingPpn == 1

        val selectedVouchers = VoucherManager.selectedVouchers.value
        val hasMembership = vehicle.vehicle.isMembership == 1

        var tempTotalDiskon = 0.0

        selectedVouchers.forEach { voucher ->
            val potHarga = if (hasMembership) voucher.potHargaMember else voucher.potHargaNonMember
            val persen = if (hasMembership) voucher.presentaseMember else voucher.presentaseNonMember

            val idProdRaw = voucher.idProduk?.toString()?.trim() ?: ""
            val isSpecificProduct = idProdRaw.isNotEmpty() && idProdRaw != "0" && idProdRaw != "null"

            val targetedSubtotal = if (isSpecificProduct) {
                val allowedIds = idProdRaw.split(';').map { it.trim() }
                val matchedItems = cartItems.filter { it.idProduk.toString().trim() in allowedIds }
                if (matchedItems.isNotEmpty()) matchedItems.maxOf { it.hargaUnit }.toDouble() else 0.0
            } else {
                totalBayarAsli.toDouble()
            }

            var currentDiscount = if (persen > 0) targetedSubtotal * (persen / 100.0) else potHarga
            if (isSpecificProduct && currentDiscount > targetedSubtotal) currentDiscount = targetedSubtotal.toDouble()

            tempTotalDiskon += currentDiscount
        }

        diskonFinal = tempTotalDiskon

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

    fun processFinalCheckout() {
        if (kodePenjualanDraft.isEmpty()) {
            errorMessage = "Data pesanan tidak valid (Draft tidak ditemukan)."
            return
        }

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val selectedVoucherIds = VoucherManager.selectedVouchers.value.map { it.idVoucher }
                val idVoucherPayload = if (selectedVoucherIds.isEmpty()) "[]" else selectedVoucherIds.toString()

                val payload = UpdateFinalPayload(
                    kodePenjualan = kodePenjualanDraft,
                    subtotal = subtotalFinal.roundToLong().toString(),
                    pajak = pajakFinal.roundToLong().toString(),
                    diskonNominal = diskonFinal.roundToLong().toString(),
                    nett = nettFinal.roundToLong().toString(),
                    idVoucher = idVoucherPayload
                )

                val response = transactionService.updateFinalPenjualan(payload)
                if (response.status) {
                    successKodePenjualan = response.kodePenjualan ?: "Sukses"
                    isSuccess = true
                } else {
                    errorMessage = response.message ?: "Gagal memproses pembayaran. Silakan coba lagi."
                }
            } catch (e: Exception) {
                errorMessage = e.toUserMessage()
            } finally {
                isLoading = false
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun cancelAndHapusDraft() {
        if (kodePenjualanDraft.isNotEmpty() && !isSuccess) {
            GlobalScope.launch {
                try {
                    val payload = DeleteDraftPayload(kodePenjualan = kodePenjualanDraft)
                    transactionService.deleteDraftPenjualan(payload)
                } catch (e: Exception) {
                    // Fail silently, karena jika user sedang offline, cron-job server yang akan membersihkannya nanti
                }
            }
        }
    }
}