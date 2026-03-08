package com.example.autoglazecustomer.ui.profile.myvehicle

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.profile.myvehicle.addvehicle.AddVehicleScreen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class MyVehicleScreen(private val authService: AuthService) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MyVehicleScreenModel(authService) }
        LaunchedEffect(navigator.lastItem) {
            if (navigator.lastItem is MyVehicleScreen) {
                screenModel.fetchVehicles()
            }
        }
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))

        // --- ANIMASI SHIMMER ---
        val transition = rememberInfiniteTransition()
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        val shimmerColors = listOf(
            Color(0xFFEBEBEB),
            Color(0xFFF5F5F5),
            Color(0xFFEBEBEB),
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim)
        )

        // Ambil data saat screen dibuka
        LaunchedEffect(Unit) {
            screenModel.fetchVehicles()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Kendaraan Saya", fontFamily = satoshiBold, fontSize = 19.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFFAFAFA)
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // LIST KENDARAAN (Scrollable)
                Box(modifier = Modifier.weight(1f)) {
                    if (screenModel.isLoading) {
                        // TAMPILKAN SHIMMER LIST
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(3) { // Tampilkan 3 placeholder shimmer
                                ShimmerVehicleItem(brush)
                            }
                        }
                    } else if (screenModel.vehicleList.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Belum ada kendaraan",
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(screenModel.vehicleList) { vehicle ->
                                VehicleCard(vehicle, satoshiBold, satoshiMedium)
                            }
                        }
                    }
                }

                // TOMBOL TAMBAH KENDARAAN (Statis di bawah)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .clickable {
                             navigator.push(AddVehicleScreen(authService))
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFD53B1E), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Tambah Kendaraan",
                            color = Color(0xFFD53B1E),
                            fontFamily = satoshiBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ShimmerVehicleItem(brush: Brush) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Badge Shimmer
                Box(modifier = Modifier.size(width = 80.dp, height = 16.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                // Info Shimmer
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(width = 120.dp, height = 24.dp).background(brush, RoundedCornerShape(4.dp)))
                    Box(modifier = Modifier.size(width = 100.dp, height = 24.dp).background(brush, RoundedCornerShape(4.dp)))
                }
                Spacer(Modifier.height(8.dp))
                // Sub-info Shimmer
                Box(modifier = Modifier.size(width = 180.dp, height = 16.dp).background(brush, RoundedCornerShape(4.dp)))
                // Image Shimmer
                Box(modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 16.dp).background(brush, RoundedCornerShape(12.dp)))
            }
        }
    }

    @Composable
    private fun VehicleCard(vehicle: VehicleData, bold: FontFamily, med: FontFamily) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header: Badge Status
                Surface(
                    color = if (vehicle.hasMembership == 1) Color(0xFFD53B1E) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (vehicle.hasMembership == 1) "MEMBER" else "REGULER",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        color = if (vehicle.hasMembership == 1) Color.White else Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Info Utama: Merek & Plat
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(vehicle.merek ?: "-", fontFamily = bold, fontSize = 22.sp, color = Color.Black)
                    Text(vehicle.nopol ?: "-", fontFamily = bold, fontSize = 20.sp, color = Color.Black)
                }

                // Info Sekunder: Tipe & Warna
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(vehicle.tipe ?: "-", fontFamily = med, fontSize = 16.sp, color = Color(0xFF757575))
                    Text(vehicle.warna ?: "-", fontFamily = med, fontSize = 16.sp, color = Color.Black)
                }

                // Gambar Kendaraan
                AsyncImage(
                    model = vehicle.gambarTipe,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 12.dp),
                    contentScale = ContentScale.Fit,
                    error = painterResource(Res.drawable.sedan)
                )
            }
        }
    }
}