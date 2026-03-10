package com.example.autoglazecustomer.ui.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.autoglazecustomer.data.model.HistoryItem
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.tabs.HomeTab
import io.github.alexzhirkevich.qrose.options.*
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.Font
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CartScreen(private val authService: AuthService) : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { CartScreenModel(authService) }
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        var showQrDialog by remember { mutableStateOf(false) }
        var selectedItemForQr by remember { mutableStateOf<HistoryItem?>(null) }
        var searchQuery by remember { mutableStateOf("") }

        // --- FILTER KENDARAAN ---
        val filteredVehicles = remember(searchQuery, screenModel.vehicleList) {
            screenModel.vehicleList.filter {
                it.nopol?.contains(searchQuery, ignoreCase = true) == true ||
                        it.merek?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        // --- JURUS JOSJIS: Reset Selection jika hasil filter kosong ---
        LaunchedEffect(filteredVehicles) {
            if (filteredVehicles.isEmpty() && searchQuery.isNotEmpty()) {
                screenModel.selectedVehicle = null
                screenModel.historyList = emptyList() // Pastikan list history ikut bersih
            }
        }

        KmpBackHandler {
            if (tabNavigator.current !is HomeTab) {
                tabNavigator.current = HomeTab()
            } else if (navigator.canPop) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                // Menghilangkan shadow elevation (0.dp)
                Surface(shadowElevation = 0.dp) {
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (tabNavigator.current !is HomeTab) {
                                    tabNavigator.current = HomeTab()
                                } else if (navigator.canPop) {
                                    navigator.pop()
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                        ) {
                            Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                        }
                        Text("Riwayat Pesanan", fontFamily = satoshiBold, fontSize = 19.sp)
                    }
                }
            },
            containerColor = Color(0xFFFBFBFB)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                // --- 1. SEARCH BAR AREA ---
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Cari Kendaraan", fontFamily = satoshiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ketik Nomor Polisi atau Merk", fontSize = 14.sp, fontFamily = satoshiMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = redPrimer,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = redPrimer
                        )
                    )
                }

                // --- 2. HORIZONTAL VEHICLE SELECTOR ---
                if (filteredVehicles.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredVehicles) { vehicle ->
                            val isSelected = screenModel.selectedVehicle?.idKendaraan == vehicle.idKendaraan
                            Surface(
                                onClick = {
                                    screenModel.selectedVehicle = vehicle
                                    screenModel.fetchHistory(vehicle.idKendaraan)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) redPrimer else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) redPrimer else Color(0xFFEEEEEE)),
                                // Menghapus shadow (0.dp)
                                shadowElevation = 0.dp
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                                    Text(
                                        text = vehicle.nopol ?: "-",
                                        fontFamily = satoshiBold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                    Text(
                                        text = vehicle.merek ?: "-",
                                        fontFamily = satoshiMedium,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White.copy(0.8f) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                } else if (searchQuery.isNotEmpty()) {
                    // Tampilan jika hasil cari kosong
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Kendaraan tidak ditemukan",
                            fontFamily = satoshiMedium,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- 3. CONTAINER AREA (RIWAYAT) ---
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    // Menghapus shadow (0.dp)
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .size(40.dp, 4.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )

                        when {
                            screenModel.isHistoryLoading -> {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    CircularProgressIndicator(color = redPrimer)
                                }
                            }
                            screenModel.selectedVehicle == null -> {
                                EmptyStateUI("Pilih kendaraan untuk melihat riwayat", satoshiMedium)
                            }
                            screenModel.historyList.isEmpty() -> {
                                EmptyStateUI("Belum ada riwayat untuk mobil ini", satoshiMedium)
                            }
                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(screenModel.historyList) { item ->
                                        HistoryCardItem(item, redPrimer, satoshiBold, satoshiMedium) {
                                            selectedItemForQr = item
                                            showQrDialog = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showQrDialog && selectedItemForQr != null) {
            QrCodeDialog(selectedItemForQr!!, { showQrDialog = false }, redPrimer, satoshiBold, satoshiMedium)
        }
    }

    // --- FUNGSI PENDUKUNG (TETAP SAMA) ---

    @OptIn(ExperimentalEncodingApi::class)
    @Composable
    private fun QrCodeDialog(item: HistoryItem, onDismiss: () -> Unit, accent: Color, bold: FontFamily, med: FontFamily) {
        val payload = """{"kode_penjualan":"${item.kodePenjualan}","kode_cabang":"${item.kodeCabang}"}"""
        val encodedPayload = Base64.encode(payload.encodeToByteArray())

        Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Kode Penjualan", fontFamily = bold, fontSize = 20.sp)
                    Text("Tunjukkan ke Kasir", fontFamily = bold, fontSize = 12.sp, color = accent, modifier = Modifier.padding(bottom = 20.dp))

                    val painter = rememberQrCodePainter(encodedPayload) {
                        shapes { ball = QrBallShape.circle(); darkPixel = QrPixelShape.roundCorners() }
                    }
                    Box(modifier = Modifier.size(210.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp)).padding(12.dp)) {
                        Image(painter, null, Modifier.fillMaxSize())
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(item.kodePenjualan, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(50.dp), colors = ButtonDefaults.buttonColors(accent), shape = RoundedCornerShape(12.dp)) {
                        Text("Kembali", fontFamily = bold, color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    private fun HistoryCardItem(item: HistoryItem, accent: Color, bold: FontFamily, med: FontFamily, onQrClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onQrClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.namaCabang.uppercase(), fontFamily = bold, fontSize = 13.sp)
                    Text(item.kodePenjualan, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text("Total Pembayaran", fontSize = 10.sp, color = Color.Gray, fontFamily = med)
                    Text(formatRupiah(item.subtotal), color = accent, fontFamily = bold, fontSize = 18.sp)
                }
                Icon(Icons.Default.QrCode, null, tint = accent, modifier = Modifier.size(32.dp).background(accent.copy(0.1f), RoundedCornerShape(8.dp)).padding(8.dp))
            }
        }
    }

    @Composable
    private fun EmptyStateUI(msg: String, med: FontFamily) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(64.dp), Color(0xFFE0E0E0))
            Text(msg, color = Color.Gray, fontFamily = med, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp))
        }
    }

    private fun formatRupiah(amount: Double): String = "Rp " + amount.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
}