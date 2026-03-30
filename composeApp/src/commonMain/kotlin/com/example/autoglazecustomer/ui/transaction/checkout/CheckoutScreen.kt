package com.example.autoglazecustomer.ui.transaction.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.dummy_promo_dark
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.VoucherUIModel
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.transaction.voucher.VoucherScreen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

class CheckoutScreen(
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus,
    private val authService: AuthService
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CheckoutScreenModel(authService, cabang, vehicle) }
        val cartItems by CartManager.cartItems.collectAsState()
        val selectedVouchers by VoucherManager.selectedVouchers.collectAsState()

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        LaunchedEffect(cartItems) {
            val currentProductIds = cartItems.map { it.idProduk.toString() }
            VoucherManager.removeInvalidVouchers(currentProductIds)
        }

        LaunchedEffect(cartItems, selectedVouchers) {
            screenModel.calculateTotals(cartItems)
            if (cartItems.isEmpty() && !screenModel.isSuccess) {
                VoucherManager.clearVouchers()
                if (navigator.canPop) navigator.pop()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Surface(color = Color.White, shadowElevation = 2.dp) {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "Checkout",
                                    fontFamily = satoshiBold,
                                    fontSize = 18.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp))
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                        )
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 140.dp)
                ) {

                    item {
                        SectionTitle("Informasi Layanan", satoshiBold)
                        Spacer(Modifier.height(10.dp))
                        TransactionInfoBanner(
                            cabang,
                            vehicle,
                            satoshiBold,
                            satoshiMedium,
                            redPrimer
                        )
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionTitle("Pesanan Anda", satoshiBold)
                        Spacer(Modifier.height(10.dp))
                    }
                    items(cartItems) { item ->
                        CartItemRow(item, satoshiBold, satoshiMedium, redPrimer) {
                            CartManager.removeItem(item.idProduk)
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        SectionTitle("Promo & Voucher", satoshiBold)
                        Spacer(Modifier.height(10.dp))

                        val isMember = vehicle.vehicle.isMembership == 1

                        VoucherCard(
                            selectedVouchers = selectedVouchers,
                            isMember = isMember,
                            cartItems = cartItems,
                            bold = satoshiBold,
                            med = satoshiMedium,
                            red = redPrimer
                        ) {
                            navigator.push(
                                VoucherScreen(
                                    authService = authService,
                                    idKendaraan = vehicle.vehicle.idKendaraan ?: -1,
                                    cartItems = cartItems,
                                    isMember = isMember
                                )
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    item {
                        SectionTitle("Ringkasan Transaksi", satoshiBold)
                        Spacer(Modifier.height(10.dp))
                        PaymentDetailsCard(screenModel, satoshiBold, satoshiMedium, redPrimer)
                    }
                }
            }

            CheckoutBottomBar(screenModel, cartItems, satoshiBold, satoshiMedium, redPrimer)
            HandleDialogs(screenModel, satoshiBold, satoshiMedium, redPrimer, navigator)
        }
    }

    @Composable
    private fun SectionTitle(title: String, bold: FontFamily) {
        Text(
            title,
            fontFamily = bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }

    @Composable
    private fun TransactionInfoBanner(
        cabang: CabangData,
        vehicle: VehicleWithStatus,
        bold: FontFamily,
        med: FontFamily,
        accent: Color
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = accent.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(accent, Color(0xFF9E2A15))))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Assignment,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        cabang.namaCabang,
                        color = Color.White,
                        fontFamily = bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        vehicle.vehicle.merek ?: "Kendaraan",
                        color = Color.White.copy(0.9f),
                        fontFamily = med,
                        fontSize = 14.sp
                    )
                    Text(
                        vehicle.vehicle.nopol ?: "-",
                        color = Color.White.copy(0.7f),
                        fontFamily = med,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun CartItemRow(
        item: CartItem,
        bold: FontFamily,
        med: FontFamily,
        red: Color,
        onDelete: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFEFEFEF))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9F9F9)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.dummy_promo_dark)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.namaItem,
                            fontFamily = bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.background(Color(0xFFFFF0F0), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Hapus",
                                tint = red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        formatRupiah(item.hargaUnit),
                        fontFamily = med,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "Qty: ${item.qty}",
                                fontFamily = med,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            formatRupiah(item.subtotal),
                            fontFamily = bold,
                            fontSize = 16.sp,
                            color = red
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun VoucherCard(
        selectedVouchers: List<VoucherUIModel>,
        isMember: Boolean,
        cartItems: List<CartItem>,
        bold: FontFamily,
        med: FontFamily,
        red: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(
                1.dp,
                if (selectedVouchers.isNotEmpty()) red.copy(alpha = 0.4f) else Color(0xFFEFEFEF)
            )
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = red.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Icon(
                                Icons.Default.LocalActivity,
                                null,
                                tint = red,
                                modifier = Modifier.padding(8.dp).size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))

                        val text =
                            if (selectedVouchers.isNotEmpty()) "${selectedVouchers.size} Voucher Terpakai" else "Gunakan Voucher Diskon"
                        Text(
                            text,
                            fontFamily = if (selectedVouchers.isNotEmpty()) bold else med,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                    Text("Pilih >", fontFamily = bold, fontSize = 14.sp, color = red)
                }

                if (selectedVouchers.isNotEmpty()) {
                    HorizontalDivider(
                        color = Color(0xFFF5F5F5),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(red.copy(alpha = 0.03f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedVouchers.forEach { voucher ->
                            val potHarga =
                                if (isMember) voucher.potHargaMember else voucher.potHargaNonMember
                            val persen =
                                if (isMember) voucher.presentaseMember else voucher.presentaseNonMember

                            val idProdRaw = voucher.idProduk?.toString()?.trim() ?: ""
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
                                cartItems.sumOf { it.subtotal }
                            }

                            var discountValue =
                                if (persen > 0) targetedSubtotal * (persen / 100.0) else potHarga
                            if (isSpecificProduct && discountValue > targetedSubtotal) {
                                discountValue = targetedSubtotal.toDouble()
                            }

                            val tagText =
                                if (persen > 0) "Diskon ${persen.toInt()}%" else "Potongan Harga"

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        voucher.namaVoucher,
                                        fontFamily = bold,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        tagText,
                                        fontFamily = med,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    "-${formatRupiah(discountValue)}",
                                    fontFamily = bold,
                                    fontSize = 13.sp,
                                    color = red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PaymentDetailsCard(
        screenModel: CheckoutScreenModel,
        bold: FontFamily,
        med: FontFamily,
        red: Color
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEFEFEF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RincianRow("Sub Total Pesanan", screenModel.nettFinal, med)
                RincianRow("Biaya PPN (11%)", screenModel.pajakFinal, med)
                RincianRow("Potongan Voucher", screenModel.diskonFinal, med, isDiscount = true)
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Pembayaran", fontFamily = bold, fontSize = 16.sp)
                    Text(
                        formatRupiah(screenModel.subtotalFinal),
                        fontFamily = bold,
                        fontSize = 18.sp,
                        color = red
                    )
                }
            }
        }
    }

    @Composable
    private fun BoxScope.CheckoutBottomBar(
        screenModel: CheckoutScreenModel,
        cartItems: List<CartItem>,
        bold: FontFamily,
        med: FontFamily,
        red: Color
    ) {
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shadowElevation = 16.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Bayar", fontFamily = med, fontSize = 13.sp, color = Color.Gray)
                    Text(
                        formatRupiah(screenModel.subtotalFinal),
                        fontFamily = bold,
                        fontSize = 22.sp,
                        color = red
                    )
                }
                Button(
                    onClick = { screenModel.processCheckout(cartItems) },
                    modifier = Modifier.height(54.dp).width(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    if (screenModel.isLoading) CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    else Text("Proses", fontFamily = bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    @Composable
    private fun HandleDialogs(
        sm: CheckoutScreenModel,
        bold: FontFamily,
        med: FontFamily,
        red: Color,
        navigator: cafe.adriel.voyager.navigator.Navigator
    ) {
        if (sm.isSuccess) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Berhasil", fontFamily = bold) },
                text = {
                    Text(
                        "Pesanan telah diproses. Silahkan ke halaman pesanan untuk melihat kode QR!",
                        fontFamily = med
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { CartManager.clearCart(); VoucherManager.clearVouchers(); navigator.popUntilRoot() },
                        colors = ButtonDefaults.buttonColors(containerColor = red)
                    ) { Text("Selesai", fontFamily = bold) }
                },
                containerColor = Color.White
            )
        }
        if (sm.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { sm.errorMessage = null },
                title = { Text("Pemberitahuan", fontFamily = bold) },
                text = { Text(sm.errorMessage!!, fontFamily = med) },
                confirmButton = {
                    TextButton(onClick = { sm.errorMessage = null }) {
                        Text(
                            "Tutup",
                            color = red,
                            fontFamily = bold
                        )
                    }
                },
                containerColor = Color.White
            )
        }
    }

    @Composable
    private fun RincianRow(
        label: String,
        amount: Double,
        font: FontFamily,
        isDiscount: Boolean = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontFamily = font, fontSize = 14.sp, color = Color.Gray)
            val prefix = if (isDiscount && amount > 0) "-" else ""
            val color = if (isDiscount && amount > 0) Color(0xFF4CAF50) else Color.Black
            Text(
                "$prefix${formatRupiah(amount)}",
                fontFamily = font,
                fontSize = 14.sp,
                color = color
            )
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = abs(amount).toLong()
        val formattedNumber =
            absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        return "${if (amount < 0) "-" else ""}Rp $formattedNumber"
    }
}