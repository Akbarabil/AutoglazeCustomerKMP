package com.example.autoglazecustomer.ui.transaction.membership

import androidx.compose.foundation.background
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
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipItem
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font

class MembershipListScreen(
    private val cabang: CabangData,
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

        LaunchedEffect(Unit) { screenModel.fetchData() }

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
                                    IconButton(onClick = { navigator.pop() }) {
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
                        // JOSJIS: Layout Kartu Putih Bersih
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
                                        // TODO: Arahkan ke CheckoutScreen
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Membuka halaman Checkout: ${item.namaMembership}..."
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
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
        // Kartu Putih yang Konsisten dengan Jasa/Produk
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
            color = Color.White
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                // Watermark Badge di Latar Belakang agar tidak terlalu polos
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = Color(0xFFF0F0F0), // Abu-abu sangat muda
                    modifier = Modifier.size(100.dp).align(Alignment.BottomEnd).offset(x = 10.dp, y = 10.dp)
                )

                Column {
                    // Row atas: Nama dan Label Bulan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top // Top agar teks yang turun ke bawah tidak membuat badge ikut turun
                    ) {
                        Text(
                            text = item.namaMembership,
                            fontFamily = bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.weight(1f).padding(end = 12.dp),
                            lineHeight = 24.sp
                            // maxLines dan overflow DIHAPUS agar teks panjang bisa turun ke baris baru
                        )

                        // Badge Masa Berlaku
                        Surface(
                            color = brandRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${item.masaBerlaku} Bulan",
                                fontFamily = bold,
                                fontSize = 12.sp,
                                color = brandRed,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Row bawah: Harga dan Tombol Panah
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Total Harga", fontFamily = med, fontSize = 13.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = formatRupiah(item.hargaDaftar),
                                fontFamily = bold,
                                fontSize = 22.sp,
                                color = brandRed
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