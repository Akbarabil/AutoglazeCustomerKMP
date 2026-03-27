package com.example.autoglazecustomer.ui.transaction.membership

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.manager.CartItem // JOSJIS: Pastikan import ini
import com.example.autoglazecustomer.data.manager.CartManager // JOSJIS: Pastikan import ini
import com.example.autoglazecustomer.data.manager.ItemCategory // JOSJIS: Pastikan import ini
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipItem
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.transaction.checkout.CheckoutScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font


class MembershipListScreen(
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus,
    private val authService: AuthService
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MembershipListScreenModel(authService, cabang.kodeCabang) }

        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

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

        // JOSJIS 3: Tahan navigasi dengan KmpBackHandler
        KmpBackHandler {
            onLeaveAttempt()
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Scaffold(
                containerColor = bgLight,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Surface(color = Color.White, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1. App Bar
                            CenterAlignedTopAppBar(
                                title = { Text("Pilih Membership", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black) },
                                navigationIcon = {
                                    // JOSJIS 4: Pasang penahan di tombol back UI
                                    IconButton(onClick = { onLeaveAttempt() }) {
                                        Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp), Color.DarkGray)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                                windowInsets = WindowInsets.statusBars
                            )

                            // 2. Search Bar
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
                                placeholder = { Text("Ketik paket membership", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 15.sp) },
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
                    } else if (screenModel.displayedMemberships.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(screenModel.errorMessage ?: "Membership tidak ditemukan.", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 16.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(screenModel.displayedMemberships) { item ->
                                AutoglazeMemberCard(
                                    item = item,
                                    bold = satoshiBold,
                                    med = satoshiMedium,
                                    brandRed = redPrimer,
                                    onClick = {
                                        // JOSJIS 5: Langsung masukkan Membership ke CartManager
                                        CartManager.clearCart() // Bersihkan sisa cart lama (karena membership cuma bisa beli 1)

                                        val newCartItem = CartItem(
                                            idProduk = item.idMembership, // Pakai ID Membership sbg unique identifier
                                            idCabangItem = null, // Karena ini Membership
                                            idMembership = item.idMembership,
                                            namaItem = item.namaMembership,
                                            imageUrl = null, // Membership biasanya tidak punya gambar khusus
                                            qty = 1, // Pasti 1
                                            hargaUnit = item.hargaDaftar,
                                            category = ItemCategory.MEMBERSHIP
                                        )
                                        CartManager.addItemToCart(newCartItem)

                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Membuka Checkout untuk ${item.namaMembership}...")
                                        }

                                         navigator.push(
                                             CheckoutScreen(
                                                 cabang = cabang,
                                                 vehicle = vehicle,
                                                 authService)
                                         )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // JOSJIS 6: UI Dialog Peringatan Hapus Keranjang Membership
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
                                CartManager.clearCart() // Hapus memori cart saat fix keluar
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
    private fun AutoglazeMemberCard(
        item: MembershipItem,
        bold: FontFamily,
        med: FontFamily,
        brandRed: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.05f)
                )
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onClick() },
            color = Color.White
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = brandRed.copy(alpha = 0.08f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = brandRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.namaMembership,
                            fontFamily = bold,
                            fontSize = 17.sp,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Masa Aktif ${item.masaBerlaku} Bulan",
                            fontFamily = med,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFF5F5F5),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFCFCFC))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Biaya Membership",
                            fontFamily = med,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatRupiah(item.hargaDaftar),
                            fontFamily = bold,
                            fontSize = 20.sp,
                            color = brandRed
                        )
                    }

                    Surface(
                        color = brandRed,
                        shape = CircleShape,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Pilih",
                                fontFamily = bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
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