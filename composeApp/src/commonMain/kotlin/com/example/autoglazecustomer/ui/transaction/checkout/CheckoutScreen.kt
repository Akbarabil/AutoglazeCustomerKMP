package com.example.autoglazecustomer.ui.transaction.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

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

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA)

        LaunchedEffect(cartItems) {
            screenModel.calculateTotals(cartItems)
            if (cartItems.isEmpty() && !screenModel.isSuccess) {
                if (navigator.canPop) navigator.pop()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(bgLight)) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    Surface(color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                        CenterAlignedTopAppBar(
                            title = { Text("Checkout", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black) },
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp), Color.DarkGray)
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                            windowInsets = WindowInsets.statusBars
                        )
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp), // Ruang lega untuk bottom bar
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. HEADER LOKASI CABANG
                    item {
                        LocationCard(cabang.namaCabang, satoshiBold, satoshiMedium, redPrimer)
                    }

                    // 2. DAFTAR BARANG DI KERANJANG
                    item {
                        SectionTitle("Pesanan Anda", satoshiBold)
                    }
                    items(cartItems) { item ->
                        CartItemRow(item, satoshiBold, satoshiMedium, redPrimer) {
                            CartManager.removeItem(item.idProduk)
                        }
                    }

                    // 3. VOUCHER & PROMO
                    item {
                        SectionTitle("Promo & Voucher", satoshiBold)
                        VoucherCard(satoshiBold, satoshiMedium, redPrimer) {
                            // TODO: Buka halaman pilih Voucher
                        }
                    }

                    // 4. RINCIAN PEMBAYARAN
                    item {
                        SectionTitle("Ringkasan Transaksi", satoshiBold)
                        PaymentDetailsCard(screenModel, satoshiBold, satoshiMedium, redPrimer)
                    }
                }
            }

            // 5. BOTTOM BAR (TOMBOL BAYAR)
            CheckoutBottomBar(
                screenModel = screenModel,
                cartItems = cartItems,
                satoshiBold = satoshiBold,
                satoshiMedium = satoshiMedium,
                redPrimer = redPrimer
            )

            // 6. DIALOGS
            HandleDialogs(screenModel, satoshiBold, satoshiMedium, redPrimer, navigator)
        }
    }

    // =========================================================================
    // UI COMPONENTS (DIPISAH AGAR CLEAN & ENTERPRISE GRADE)
    // =========================================================================

    @Composable
    private fun SectionTitle(title: String, bold: FontFamily) {
        Text(
            text = title,
            fontFamily = bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 4.dp)
        )
    }

    @Composable
    private fun LocationCard(namaCabang: String, bold: FontFamily, med: FontFamily, redPrimer: Color) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = redPrimer.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, redPrimer.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = CircleShape, color = redPrimer.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, null, tint = redPrimer, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Lokasi Layanan", fontFamily = med, fontSize = 12.sp, color = redPrimer)
                    Text(namaCabang, fontFamily = bold, fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }

    @Composable
    private fun CartItemRow(item: CartItem, bold: FontFamily, med: FontFamily, red: Color, onDelete: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0)) // Border super tipis modern
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.dummy_promo_dark)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.namaItem, fontFamily = bold, fontSize = 15.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text("${item.qty}x  ${formatRupiah(item.hargaUnit)}", fontFamily = med, fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(formatRupiah(item.subtotal), fontFamily = bold, fontSize = 16.sp, color = red)
                }
                IconButton(onClick = onDelete, modifier = Modifier.background(Color(0xFFFFF0F0), CircleShape).size(36.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    @Composable
    private fun VoucherCard(bold: FontFamily, med: FontFamily, redPrimer: Color, onClick: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ConfirmationNumber, null, tint = redPrimer, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Gunakan Voucher Diskon", fontFamily = med, fontSize = 15.sp, color = Color.Black)
                }
                Text("Pilih >", fontFamily = bold, fontSize = 14.sp, color = redPrimer)
            }
        }
    }

    @Composable
    private fun PaymentDetailsCard(screenModel: CheckoutScreenModel, bold: FontFamily, med: FontFamily, redPrimer: Color) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(8.dp))
                    Text("Rincian Pembayaran", fontFamily = bold, fontSize = 15.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))

                RincianRow("Sub Total Pesanan", screenModel.nettFinal, med)
                Spacer(modifier = Modifier.height(8.dp))
                RincianRow("Biaya PPN (11%)", screenModel.pajakFinal, med)
                Spacer(modifier = Modifier.height(8.dp))
                RincianRow("Potongan Voucher", screenModel.diskonFinal, med, isDiscount = true)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Pembayaran", fontFamily = bold, fontSize = 16.sp, color = Color.Black)
                    Text(formatRupiah(screenModel.subtotalFinal), fontFamily = bold, fontSize = 18.sp, color = redPrimer)
                }
            }
        }
    }

    @Composable
    private fun BoxScope.CheckoutBottomBar(
        screenModel: CheckoutScreenModel,
        cartItems: List<CartItem>,
        satoshiBold: FontFamily,
        satoshiMedium: FontFamily,
        redPrimer: Color
    ) {
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shadowElevation = 16.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Bayar", fontFamily = satoshiMedium, fontSize = 13.sp, color = Color.Gray)
                    Text(formatRupiah(screenModel.subtotalFinal), fontFamily = satoshiBold, fontSize = 22.sp, color = redPrimer)
                }

                Button(
                    onClick = {
                        // TODO: Ambil dari TokenManager/Session
                        screenModel.processCheckout(cartItems, "Nama Customer", "ID_CUSTOMER_123")
                    },
                    modifier = Modifier.height(54.dp).width(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                ) {
                    if (screenModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Bayar", fontFamily = satoshiBold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    private fun HandleDialogs(screenModel: CheckoutScreenModel, bold: FontFamily, med: FontFamily, red: Color, navigator: cafe.adriel.voyager.navigator.Navigator) {
        if (screenModel.isSuccess) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Checkout Berhasil! \uD83C\uDF89", fontFamily = bold) },
                text = { Text("Pesanan Anda sedang diproses. Kode: ${screenModel.successKodePenjualan}", fontFamily = med) },
                confirmButton = {
                    Button(
                        onClick = {
                            CartManager.clearCart()
                            navigator.popUntilRoot()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = red)
                    ) { Text("Selesai", fontFamily = bold) }
                },
                containerColor = Color.White
            )
        }

        if (screenModel.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { screenModel.errorMessage = null },
                title = { Text("Pemberitahuan", fontFamily = bold) },
                text = { Text(screenModel.errorMessage ?: "", fontFamily = med) },
                confirmButton = {
                    TextButton(onClick = { screenModel.errorMessage = null }) { Text("Tutup", color = red, fontFamily = bold) }
                },
                containerColor = Color.White
            )
        }
    }

    @Composable
    private fun RincianRow(label: String, amount: Double, font: FontFamily, isDiscount: Boolean = false) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontFamily = font, fontSize = 14.sp, color = Color.Gray)
            val prefix = if (isDiscount && amount > 0) "-" else ""
            val color = if (isDiscount && amount > 0) Color(0xFF4CAF50) else Color.Black
            Text("$prefix${formatRupiah(amount)}", fontFamily = font, fontSize = 14.sp, color = color)
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = kotlin.math.abs(amount).toLong()
        val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp $formattedNumber"
    }
}