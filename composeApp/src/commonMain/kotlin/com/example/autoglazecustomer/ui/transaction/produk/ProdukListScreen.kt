package com.example.autoglazecustomer.ui.transaction.produk

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.produk.ProdukItem
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.transaction.checkout.CheckoutScreen
import com.example.autoglazecustomer.ui.transaction.components.FloatingCheckoutBar
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class ProdukListScreen(
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus,
    private val authService: AuthService
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            ProdukListScreenModel(authService, cabang.kodeCabang, vehicle.membershipStatusInt)
        }
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA)

        // JOSJIS 1: State Dialog & Observasi Keranjang
        val cartItems by CartManager.cartItems.collectAsState()
        var showExitDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { screenModel.fetchData() }

        // JOSJIS 2: Fungsi Mencegat Keluar (Interceptor)
        val onLeaveAttempt = {
            if (cartItems.isNotEmpty()) {
                showExitDialog = true
            } else {
                if (navigator.canPop) {
                    navigator.pop()
                }
            }
        }

        // JOSJIS 3: Tahan navigasi dengan KmpBackHandler (Aman untuk iOS)
        KmpBackHandler {
            onLeaveAttempt()
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Scaffold(
                containerColor = bgLight,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    Surface(color = Color.White, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1. App Bar
                            CenterAlignedTopAppBar(
                                title = { Text("Daftar Produk", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black) },
                                navigationIcon = {
                                    // JOSJIS 4: Terapkan penahan di tombol back UI
                                    IconButton(onClick = { onLeaveAttempt() }) {
                                        Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp), Color.DarkGray)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                                windowInsets = WindowInsets.statusBars
                            )

                            // 2. Search Bar Modern
                            TextField(
                                value = screenModel.searchQuery,
                                onValueChange = {
                                    screenModel.searchQuery = it
                                    screenModel.updateDisplayedList()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                                    .height(54.dp),
                                placeholder = { Text("Ketik nama produk", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 15.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF0F2F5),
                                    unfocusedContainerColor = Color(0xFFF0F2F5),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = redPrimer,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding).windowInsetsPadding(WindowInsets.navigationBars)) {
                    if (screenModel.isLoading) {
                        CircularProgressIndicator(color = redPrimer, modifier = Modifier.align(Alignment.Center))
                    } else if (screenModel.displayedProducts.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(screenModel.errorMessage ?: "Produk kosong pada cabang ini", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 16.sp)
                        }
                    } else {
                        // Layout 2 Kolom (Grid)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(screenModel.displayedProducts) { item ->
                                val (original, final) = screenModel.calculatePrice(item)

                                ProductCardGridItem(
                                    item = item,
                                    originalPrice = original,
                                    finalPrice = final,
                                    bold = satoshiBold,
                                    med = satoshiMedium,
                                    redPrimer = redPrimer,
                                    onClick = {
                                        navigator.push(ProdukDetailScreen(item, finalPrice = final, cabang, vehicle))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            val totalQty = cartItems.sumOf { it.qty }
            val totalPrice = cartItems.sumOf { it.subtotal }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                FloatingCheckoutBar(
                    visible = cartItems.isNotEmpty(),
                    totalQty = totalQty,
                    totalPrice = totalPrice,
                    onClick = {
                         navigator.push(CheckoutScreen(cabang, vehicle, authService))
                    }
                )
            }

            // JOSJIS 5: UI Dialog Peringatan Hapus Keranjang
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    containerColor = Color.White,
                    title = {
                        Text("Perhatian", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black)
                    },
                    text = {
                        Text(
                            "Jika anda kembali, data dalam keranjang akan dihapus. Anda yakin?",
                            fontFamily = satoshiMedium,
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitDialog = false
                                CartManager.clearCart()
                                VoucherManager.clearVouchers()
                                if (navigator.canPop) navigator.pop()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                        ) {
                            Text("Yakin Keluar", fontFamily = satoshiBold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("Batal", fontFamily = satoshiBold, color = Color.Gray)
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ProductCardGridItem(
        item: ProdukItem,
        originalPrice: Double,
        finalPrice: Double,
        bold: FontFamily,
        med: FontFamily,
        redPrimer: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
            color = Color.White
        ) {
            Column {
                AsyncImage(
                    model = item.gambarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.dummy_promo_dark)
                )

                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = item.namaProduk,
                        fontFamily = bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A1A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    if (finalPrice < originalPrice) {
                        Text(
                            text = formatRupiah(originalPrice),
                            fontFamily = med,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(formatRupiah(finalPrice), fontFamily = bold, fontSize = 15.sp, color = redPrimer)
                        }
                    } else {
                        val priceText = if (finalPrice == 0.0) "GRATIS" else formatRupiah(finalPrice)
                        Text(priceText, fontFamily = bold, fontSize = 15.sp, color = if (finalPrice == 0.0) Color(0xFF4CAF50) else Color.Black)
                    }
                }
            }
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = kotlin.math.abs(amount).toLong()
        val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp $formattedNumber"
    }
}