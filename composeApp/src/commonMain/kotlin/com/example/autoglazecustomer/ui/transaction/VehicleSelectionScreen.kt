package com.example.autoglazecustomer.ui.transaction

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import org.jetbrains.compose.resources.Font

class VehicleSelectionScreen(
    private val cabang: CabangData,
    private val authService: AuthService
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { VehicleSelectionScreenModel(authService, cabang.kodeCabang) }
        val navigator = LocalNavigator.currentOrThrow

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        LaunchedEffect(Unit) {
            screenModel.fetchVehicles()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Pilih Kendaraan", fontFamily = satoshiBold, fontSize = 18.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Kembali", modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ),
                    windowInsets = WindowInsets.statusBars
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            screenModel.selectedVehicle?.let { vehicleWithStatus ->
                                navigator.push(MenuTransactionScreen(cabang, vehicleWithStatus))
                            }
                        },
                        enabled = screenModel.selectedVehicle != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = redPrimer,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .height(54.dp)
                    ) {
                        Text("Lanjutkan", fontFamily = satoshiBold, fontSize = 16.sp, color = Color.White)
                    }
                }
            },
            containerColor = Color(0xFFFBFBFB)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Surface(
                    color = Color(0xFFFFF0ED),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = redPrimer, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Cabang Terpilih", fontFamily = satoshiMedium, fontSize = 12.sp, color = Color.Gray)
                            Text(cabang.namaCabang, fontFamily = satoshiBold, fontSize = 16.sp, color = redPrimer)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        screenModel.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = redPrimer
                            )
                        }
                        screenModel.errorMessage != null -> {
                            Text(
                                text = screenModel.errorMessage!!,
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center).padding(24.dp)
                            )
                        }
                        screenModel.vehicleList.isEmpty() && !screenModel.isLoading -> {
                            Text(
                                text = "Anda belum memiliki kendaraan terdaftar.",
                                fontFamily = satoshiMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center).padding(24.dp)
                            )
                        }
                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(screenModel.vehicleList) { item ->
                                    val isSelected = screenModel.selectedVehicle?.vehicle?.idKendaraan == item.vehicle.idKendaraan

                                    VehicleSelectableCard(
                                        item = item,
                                        isSelected = isSelected,
                                        redPrimer = redPrimer,
                                        bold = satoshiBold,
                                        med = satoshiMedium,
                                        onClick = { screenModel.selectedVehicle = item }
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
    private fun VehicleSelectableCard(
        item: VehicleWithStatus,
        isSelected: Boolean,
        redPrimer: Color,
        bold: FontFamily,
        med: FontFamily,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) redPrimer.copy(alpha = 0.05f) else Color.White
            ),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) redPrimer else Color(0xFFEEEEEE)
            )
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DirectionsCar, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.vehicle.merek ?: "Merek Tidak Diketahui", fontFamily = bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = item.vehicle.nopol ?: "Nopol Kosong", fontFamily = med, fontSize = 14.sp, color = Color.DarkGray)

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = item.membershipStatusText,
                        fontFamily = bold,
                        fontSize = 12.sp,
                        color = if (item.membershipStatusInt > 0) redPrimer else if (item.membershipStatusText == "Mengecek status...") Color.LightGray else Color.Gray
                    )
                }

                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, null, tint = redPrimer, modifier = Modifier.size(28.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(2.dp, Color.LightGray, CircleShape)
                    )
                }
            }
        }
    }
}