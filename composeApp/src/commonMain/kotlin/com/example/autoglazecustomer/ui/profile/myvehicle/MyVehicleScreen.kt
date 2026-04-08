package com.example.autoglazecustomer.ui.profile.myvehicle

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.sedan
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.ui.profile.myvehicle.addvehicle.AddVehicleScreen
import com.example.autoglazecustomer.ui.theme.AppFont
import org.jetbrains.compose.resources.painterResource

class MyVehicleScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<MyVehicleScreenModel>()
        LaunchedEffect(navigator.lastItem) {
            if (navigator.lastItem is MyVehicleScreen) {
                screenModel.fetchVehicles()
            }
        }
        val satoshiBold = AppFont.satoshiBold()
        val satoshiMedium = AppFont.satoshiMedium()


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


        LaunchedEffect(Unit) {
            screenModel.fetchVehicles()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Kendaraan Saya", fontFamily = satoshiBold, fontSize = 19.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFFAFAFA)
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {

                Box(modifier = Modifier.weight(1f)) {

                    if (screenModel.isLoading) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(3) {
                                ShimmerVehicleItem(brush)
                            }
                        }
                    } else if (screenModel.errorMessage != null) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = Color(0xFFE0E0E0)
                            )
                            Text(
                                text = screenModel.errorMessage!!,
                                color = Color.Gray,
                                fontFamily = satoshiMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                            )
                            Button(
                                onClick = { screenModel.fetchVehicles() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFD53B1E
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Coba Lagi", fontFamily = satoshiBold, color = Color.White)
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

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .clickable {
                            navigator.push(AddVehicleScreen())
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
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color(0xFFD53B1E),
                            modifier = Modifier.size(24.dp)
                        )
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

                Box(
                    modifier = Modifier.size(width = 80.dp, height = 16.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.size(width = 120.dp, height = 24.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier.size(width = 100.dp, height = 24.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier.size(width = 180.dp, height = 16.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )

                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 16.dp)
                        .background(brush, RoundedCornerShape(12.dp))
                )
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

                Surface(
                    color = if (vehicle.isMembership == 1) Color(0xFFD53B1E) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (vehicle.isMembership == 1) "MEMBER" else "REGULER",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        color = if (vehicle.isMembership == 1) Color.White else Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = bold
                    )
                }

                Spacer(Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        vehicle.merek ?: "-",
                        fontFamily = bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                    Text(
                        vehicle.nopol ?: "-",
                        fontFamily = bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        vehicle.tipe ?: "-",
                        fontFamily = med,
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        vehicle.warna ?: "-",
                        fontFamily = med,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }


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