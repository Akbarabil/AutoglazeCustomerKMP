package com.example.autoglazecustomer.ui.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
        val screenModel =
            rememberScreenModel { VehicleSelectionScreenModel(authService, cabang.kodeCabang) }
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
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Kembali",
                                modifier = Modifier.size(20.dp)
                            )
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
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .height(56.dp)
                    ) {
                        Text(
                            "Lanjutkan",
                            fontFamily = satoshiBold,
                            fontSize = 16.sp,
                            color = if (screenModel.selectedVehicle != null) Color.White else Color.Gray
                        )
                    }
                }
            },
            containerColor = Color(0xFFFBFBFB)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = redPrimer.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(redPrimer, Color(0xFF9E2A15))))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "Cabang Terpilih",
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = satoshiMedium,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                cabang.namaCabang,
                                color = Color.White,
                                fontFamily = satoshiBold,
                                fontSize = 18.sp
                            )
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
                                contentPadding = PaddingValues(
                                    start = 24.dp,
                                    end = 24.dp,
                                    bottom = 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(screenModel.vehicleList) { item ->
                                    val isSelected =
                                        screenModel.selectedVehicle?.vehicle?.idKendaraan == item.vehicle.idKendaraan

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
        val borderColor by animateColorAsState(if (isSelected) redPrimer else Color(0xFFEEEEEE))
        val elevation by animateDpAsState(if (isSelected) 8.dp else 0.dp)
        val iconColor by animateColorAsState(if (isSelected) redPrimer else Color.Gray)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = if (isSelected) redPrimer.copy(alpha = 0.3f) else Color.Black.copy(
                        alpha = 0.05f
                    )
                )
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onClick() },
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) redPrimer.copy(alpha = 0.06f) else Color(0xFFFBFBFB),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) redPrimer.copy(alpha = 0.1f) else Color(0xFFF0F0F0)
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.vehicle.nopol ?: "NOPOL KOSONG",
                        fontFamily = bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A1A),
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = item.vehicle.merek ?: "Merek Tidak Diketahui",
                        fontFamily = med,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(8.dp))

                    val isMember = item.membershipStatusInt > 0
                    val badgeColor = if (isMember) redPrimer else Color(0xFF9E9E9E)
                    val badgeBg = if (isMember) redPrimer.copy(alpha = 0.1f) else Color(0xFFF5F5F5)

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.membershipStatusText.uppercase(),
                            fontFamily = bold,
                            fontSize = 11.sp,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Surface(
                    shape = CircleShape,
                    color = if (isSelected) redPrimer else Color.White,
                    border = if (isSelected) null else BorderStroke(2.dp, Color(0xFFDCDCDC)),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}