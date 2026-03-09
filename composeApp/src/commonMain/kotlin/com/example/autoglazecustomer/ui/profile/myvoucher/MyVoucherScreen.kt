package com.example.autoglazecustomer.ui.profile.myvoucher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.model.VoucherItemId
import com.example.autoglazecustomer.data.network.AuthService
import org.jetbrains.compose.resources.Font

class MyVoucherScreen(private val authService: AuthService) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MyVoucherScreenModel(authService) }

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        var selectedId by remember { mutableStateOf<Int?>(null) }
        var searchQuery by remember { mutableStateOf("") }

        val filteredVehicles = remember(searchQuery, screenModel.vehicleList) {
            screenModel.vehicleList.filter {
                it.nopol?.contains(searchQuery, ignoreCase = true) == true ||
                        it.merek?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        LaunchedEffect(Unit) {
            screenModel.fetchVehicles()
        }

        LaunchedEffect(filteredVehicles) {
            if (filteredVehicles.isNotEmpty()) {
                if (selectedId == null || filteredVehicles.none { it.idKendaraan == selectedId }) {
                    val first = filteredVehicles.first()
                    selectedId = first.idKendaraan
                    screenModel.toggleExpand(selectedId!!)
                }
            } else {
                selectedId = null
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Voucher Saya", fontFamily = satoshiBold, fontSize = 19.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFF7F7F7)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                // --- SEARCH BAR AREA ---
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text(
                        text = "Cari Kendaraan",
                        fontFamily = satoshiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ketik Nomor Polisi atau Merk kendaraan", fontSize = 14.sp, fontFamily = satoshiMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                        // --- FITUR CLEAR FILTER ---
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Clear", modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        // --- FIX COLORS BERDASARKAN KANDIDAT FUNGSI KAMU ---
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = redPrimer,
                            focusedBorderColor = redPrimer,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLeadingIconColor = redPrimer,
                            unfocusedLeadingIconColor = Color.Gray,
                            focusedLabelColor = redPrimer,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }

                // --- HORIZONTAL TABS ---
                if (screenModel.isLoadingVehicles) {
                    Row(modifier = Modifier.padding(horizontal = 24.dp)) {
                        repeat(3) {
                            Box(modifier = Modifier.size(width = 100.dp, height = 50.dp).background(Color(0xFFEBEBEB), RoundedCornerShape(12.dp)))
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                } else if (filteredVehicles.isEmpty() && searchQuery.isNotEmpty()) {
                    Text(
                        "Kendaraan tidak ditemukan",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = satoshiMedium,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredVehicles) { vehicle ->
                            val isActive = selectedId == vehicle.idKendaraan
                            VehicleTabItem(vehicle, isActive, satoshiBold, satoshiMedium, redPrimer) {
                                selectedId = vehicle.idKendaraan
                                screenModel.toggleExpand(vehicle.idKendaraan!!)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- CONTAINER VOUCHER SHELF ---
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 8.dp
                ) {
                    val vouchers = screenModel.voucherCache[selectedId ?: 0]
                    val isLoading = screenModel.loadingVouchers[selectedId ?: 0] ?: false
                    val vehicleAktif = screenModel.vehicleList.find { it.idKendaraan == selectedId }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .size(width = 40.dp, height = 4.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )

                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = redPrimer)
                            }
                        } else if (vouchers.isNullOrEmpty()) {
                            EmptyVoucherModern(satoshiMedium)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(vouchers) { voucher ->
                                    ModernTicketVoucher(
                                        voucher = voucher,
                                        isMember = vehicleAktif?.hasMembership == 1,
                                        bold = satoshiBold,
                                        med = satoshiMedium,
                                        red = redPrimer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun VehicleTabItem(
        vehicle: VehicleData,
        isActive: Boolean,
        bold: FontFamily,
        med: FontFamily,
        red: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier.clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            color = if (isActive) red else Color.White,
            border = BorderStroke(1.dp, if (isActive) red else Color(0xFFEEEEEE)),
            shadowElevation = if (isActive) 4.dp else 0.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = vehicle.nopol ?: "-",
                    fontFamily = bold,
                    fontSize = 14.sp,
                    color = if (isActive) Color.White else Color.Black
                )
                Text(
                    text = vehicle.merek ?: "-",
                    fontFamily = med,
                    fontSize = 11.sp,
                    color = if (isActive) Color.White.copy(alpha = 0.8f) else Color.Gray
                )
            }
        }
    }

    @Composable
    private fun ModernTicketVoucher(
        voucher: VoucherItemId,
        isMember: Boolean?,
        bold: FontFamily,
        med: FontFamily,
        red: Color
    ) {
        val memberStatus = isMember ?: false
        val potHarga = if (memberStatus) voucher.potHargaMember else voucher.potHargaNonMember

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text(voucher.namaVoucher, fontFamily = bold, fontSize = 16.sp, color = Color.Black)
                    if (potHarga > 0) {
                        Text(
                            text = "Potongan Rp ${formatRupiah(potHarga)}",
                            fontFamily = bold, fontSize = 18.sp, color = red
                        )
                    }
                    Text(
                        text = if (memberStatus) "*Promo Khusus Member" else "*Promo Non-Member",
                        fontFamily = med, fontSize = 10.sp, color = Color.Gray
                    )
                    if (!voucher.keterangan.isNullOrEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(voucher.keterangan!!, fontFamily = med, fontSize = 11.sp, color = Color(0xFF00796B))
                    }
                }

                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color(0xFFEEEEEE)))

                Column(
                    modifier = Modifier.background(Color(0xFFFAFAFA)).fillMaxHeight().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("KODE", fontFamily = bold, fontSize = 10.sp, color = Color.Gray)
                    Text(voucher.kodeVoucher, fontFamily = bold, fontSize = 14.sp, color = Color.Black)
                    Spacer(Modifier.height(12.dp))
                    Text("Berlaku s/d", fontFamily = med, fontSize = 9.sp, color = Color.Gray)
                    Text(voucher.tglExpired ?: "-", fontFamily = med, fontSize = 10.sp, color = Color.DarkGray)
                }
            }
        }
    }

    @Composable
    private fun EmptyVoucherModern(med: FontFamily) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(64.dp), tint = Color(0xFFE0E0E0))
            Spacer(Modifier.height(16.dp))
            Text("Belum ada voucher tersedia\nuntuk kendaraan ini.", textAlign = TextAlign.Center, fontFamily = med, fontSize = 16.sp, color = Color.Gray)
        }
    }

    private fun formatRupiah(amount: Int): String {
        return amount.toString().reversed().chunked(3).joinToString(".").reversed()
    }
}