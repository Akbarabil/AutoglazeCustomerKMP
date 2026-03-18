package com.example.autoglazecustomer.ui.transaction.jasa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Schedule
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
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.jasa.LayananItem
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class JasaListScreen(
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus,
    private val authService: AuthService
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            JasaListScreenModel(authService, cabang.kodeCabang, vehicle.vehicle.idKendaraan ?: -1, vehicle.membershipStatusInt)
        }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA) // Latar belakang abu-abu sangat muda khas aplikasi modern

        LaunchedEffect(Unit) { screenModel.fetchData() }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Scaffold(
                containerColor = bgLight,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Surface(
                        color = Color.White,
                        shadowElevation = 4.dp, // Shadow halus di bawah header
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1. App Bar
                            CenterAlignedTopAppBar(
                                title = { Text("Daftar Jasa", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black) },
                                navigationIcon = {
                                    IconButton(onClick = { navigator.pop() }) {
                                        Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp), Color.DarkGray)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                                windowInsets = WindowInsets.statusBars
                            )

                            // 2. Modern Search Bar (Tanpa outline kaku)
                            TextField(
                                value = screenModel.searchQuery,
                                onValueChange = {
                                    screenModel.searchQuery = it
                                    screenModel.updateDisplayedList()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .height(54.dp),
                                placeholder = { Text("Ketik layanan yang diinginkan", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 15.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF0F2F5),
                                    unfocusedContainerColor = Color(0xFFF0F2F5),
                                    focusedIndicatorColor = Color.Transparent, // Hilangkan garis bawah
                                    unfocusedIndicatorColor = Color.Transparent, // Hilangkan garis bawah
                                    cursorColor = redPrimer,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            // 3. Autoglaze Pill Categories
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                screenModel.categories.forEach { category ->
                                    val isSelected = screenModel.selectedCategory == category
                                    val label = if (category == "Car Wash") "Carwash" else category

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                screenModel.selectedCategory = category
                                                screenModel.updateDisplayedList()
                                            },
                                        shape = CircleShape,
                                        color = if (isSelected) redPrimer else Color.White,
                                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                        shadowElevation = if (isSelected) 4.dp else 0.dp
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontFamily = if (isSelected) satoshiBold else satoshiMedium,
                                                color = if (isSelected) Color.White else Color.DarkGray,
                                                fontSize = 12.sp, // Dikecilkan sedikit agar kata "Detailing" dan "Bundling" muat
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding).windowInsetsPadding(WindowInsets.navigationBars)) {
                    if (screenModel.isLoading) {
                        CircularProgressIndicator(color = redPrimer, modifier = Modifier.align(Alignment.Center))
                    } else if (screenModel.displayedServices.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = screenModel.errorMessage ?: "Tidak ada layanan ditemukan.",
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(screenModel.displayedServices) { item ->
                                val (original, final) = screenModel.calculatePrice(item)
                                val isEligible = screenModel.isEligible(item.idProduk)

                                ServiceCardItem(
                                    item = item,
                                    originalPrice = original,
                                    finalPrice = final,
                                    bold = satoshiBold,
                                    med = satoshiMedium,
                                    redPrimer = redPrimer,
                                    onClick = {
                                        navigator.push(
                                            JasaDetailScreen(
                                                item = item,
                                                isEligible = isEligible,
                                                cabang = cabang,
                                                vehicle = vehicle
                                            )
                                        )
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
    private fun ServiceCardItem(
        item: LayananItem,
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
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.gambarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.dummy_promo_dark)
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.namaProduk,
                        fontFamily = bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    // Indikator Durasi Modern
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${item.durasiMenit} Menit", fontFamily = med, fontSize = 13.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.height(12.dp))

                    if (finalPrice < originalPrice) {
                        Column {
                            // Harga Coret
                            Text(
                                text = formatRupiah(originalPrice),
                                fontFamily = med,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                            // Harga Final + Badge Promo
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formatRupiah(finalPrice), fontFamily = bold, fontSize = 17.sp, color = redPrimer)
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    color = redPrimer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Member", fontFamily = bold, fontSize = 10.sp, color = redPrimer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    } else {
                        // Harga Normal / Gratis
                        val priceText = if (finalPrice == 0.0) "GRATIS" else formatRupiah(finalPrice)
                        Text(
                            text = priceText,
                            fontFamily = bold,
                            fontSize = 17.sp,
                            color = if (finalPrice == 0.0) Color(0xFF4CAF50) else Color.Black
                        )
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