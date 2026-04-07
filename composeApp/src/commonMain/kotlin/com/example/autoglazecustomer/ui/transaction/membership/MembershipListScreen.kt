package com.example.autoglazecustomer.ui.transaction.membership

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.manager.ItemCategory
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.membership.MembershipItem
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.transaction.checkout.CheckoutScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.Font
import org.koin.core.parameter.parametersOf


class MembershipListScreen(
    private val cabangJson: String,
    private val vehicleJson: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val cabang = remember { Json.decodeFromString<CabangData>(cabangJson) }
        val vehicle = remember { Json.decodeFromString<VehicleWithStatus>(vehicleJson) }
        val screenModel = getScreenModel<MembershipListScreenModel> {
            parametersOf(cabang.kodeCabang)
        }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA)

        val cartItems by CartManager.cartItems.collectAsState()
        var showExitDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { screenModel.fetchData() }

        val onLeaveAttempt = {
            if (cartItems.isNotEmpty()) {
                showExitDialog = true
            } else {
                if (navigator.canPop) {
                    navigator.pop()
                }
            }
        }

        KmpBackHandler {
            onLeaveAttempt()
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Scaffold(
                containerColor = bgLight,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Surface(
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {

                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        "Pilih Membership",
                                        fontFamily = satoshiBold,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { onLeaveAttempt() }) {
                                        Icon(
                                            Icons.Default.ArrowBackIosNew,
                                            null,
                                            Modifier.size(20.dp),
                                            Color.DarkGray
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.White
                                ),
                                windowInsets = WindowInsets.statusBars
                            )

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
                                placeholder = {
                                    Text(
                                        "Ketik paket membership",
                                        fontFamily = satoshiMedium,
                                        color = Color.Gray,
                                        fontSize = 15.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        null,
                                        tint = Color.Gray
                                    )
                                },
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
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    if (screenModel.isLoading) {
                        CircularProgressIndicator(
                            color = redPrimer,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (screenModel.errorMessage != null) {
                        val isDataNotFound = screenModel.errorMessage?.contains("tidak ditemukan", ignoreCase = true) == true

                        val safeErrorMsg = screenModel.errorMessage!!
                            .replace("null", "Koneksi terputus", ignoreCase = true)
                            .ifBlank { "Gagal memuat data" }

                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (isDataNotFound) Icons.Default.Search else Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (isDataNotFound) "Belum ada paket membership yang tersedia untuk cabang ini" else safeErrorMsg,
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                fontSize = 15.sp,
                                textAlign = Center
                            )

                            if (!isDataNotFound) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { screenModel.fetchData() },
                                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Coba Lagi", fontFamily = satoshiBold, color = Color.White)
                                }
                            }
                        }
                    } else if (screenModel.displayedMemberships.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val emptyText = if (screenModel.searchQuery.isNotEmpty()) {
                                "Paket membership '${screenModel.searchQuery}' tidak ditemukan"
                            } else {
                                "Belum ada paket membership yang tersedia"
                            }

                            Text(
                                text = emptyText,
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = Center
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 20.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(screenModel.displayedMemberships) { item ->
                                AutoglazeMemberCard(
                                    item = item,
                                    bold = satoshiBold,
                                    med = satoshiMedium,
                                    brandRed = redPrimer,
                                    onClick = {
                                        CartManager.clearCart()
                                        VoucherManager.clearVouchers()

                                        val newCartItem = CartItem(
                                            idProduk = item.idMembership,
                                            idCabangItem = null,
                                            idMembership = item.idMembership,
                                            namaItem = item.namaMembership,
                                            imageUrl = null,
                                            qty = 1,
                                            hargaUnit = item.hargaDaftar,
                                            category = ItemCategory.MEMBERSHIP
                                        )
                                        CartManager.addItemToCart(newCartItem)

                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Membuka Checkout untuk ${item.namaMembership}...")
                                        }

                                        navigator.push(
                                            CheckoutScreen(
                                                cabangJson, vehicleJson
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    containerColor = Color.White,
                    title = {
                        Text(
                            "Perhatian",
                            fontFamily = satoshiBold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
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
        val formattedNumber =
            absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp $formattedNumber"
    }
}