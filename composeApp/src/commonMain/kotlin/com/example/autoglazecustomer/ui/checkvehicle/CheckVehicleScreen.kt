package com.example.autoglazecustomer.ui.checkvehicle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.img_vehicle_check
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.login.LoginScreen
import com.example.autoglazecustomer.ui.password.RequestPasswordScreen
import com.example.autoglazecustomer.ui.register.RegisterScreen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class CheckVehicleScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }
        val screenModel = rememberScreenModel { CheckVehicleScreenModel(authService) }

        val redPrimer = Color(0xFFD53B1E)
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))

        val commonTextFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.DarkGray,
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.DarkGray,
            selectionColors = TextSelectionColors(
                handleColor = Color.DarkGray,
                backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
            ),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f),
                contentScale = ContentScale.Crop
            )

            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.img_vehicle_check),
                        contentDescription = null,
                        modifier = Modifier.padding(top = 60.dp).size(193.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(horizontal = 24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(32.dp))
                            Text(
                                "Cek Data Kendaraan",
                                fontFamily = satoshiMedium,
                                fontSize = 29.sp,
                                color = Color(0xFF9E9E9E)
                            )
                            Text(
                                "Masukkan info kendaraan & kontak anda",
                                fontFamily = satoshiMedium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                            )

                            OutlinedTextField(
                                value = screenModel.nopol,
                                onValueChange = { screenModel.nopol = it.uppercase() },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nomor Polisi", fontFamily = satoshiMedium) },
                                placeholder = {
                                    Text(
                                        "Contoh: AG1234KBV",
                                        fontFamily = satoshiMedium,
                                        color = Color.LightGray
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        null,
                                        tint = Color.Black
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = commonTextFieldColors
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { screenModel.checkVehicle() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                enabled = !screenModel.isLoading
                            ) {
                                if (screenModel.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Cek Kendaraan",
                                        fontFamily = satoshiBold,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Sudah punya akun? ",
                                    fontFamily = satoshiMedium,
                                    color = Color(0xFFBDBDBD),
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Masuk",
                                    modifier = Modifier.clickable { navigator.push(LoginScreen("")) },
                                    fontFamily = satoshiBold, color = Color.Black, fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }


            if (screenModel.vehicleData != null) {
                val data = screenModel.vehicleData!!
                Dialog(
                    onDismissRequest = { screenModel.resetState() },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Data Ditemukan", fontFamily = satoshiBold, fontSize = 22.sp)
                            Spacer(Modifier.height(16.dp))

                            VehicleInfoItem("Nama Pemilik", data.nama ?: "-", satoshiMedium)
                            VehicleInfoItem("Tipe Mobil", data.tipeMobil ?: "-", satoshiMedium)
                            VehicleInfoItem("Nomor Polisi", data.nopol ?: "-", satoshiMedium)
                            VehicleInfoItem("Nomor Rangka", data.noRangka ?: "-", satoshiMedium)
                            VehicleInfoItem("Email", data.email ?: "-", satoshiMedium)
                            VehicleInfoItem("Telepon", data.telepon ?: "-", satoshiMedium)

                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    val email = data.email ?: ""
                                    screenModel.resetState()
                                    navigator.push(LoginScreen(email))
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Masuk Ke Akun",
                                    fontFamily = satoshiBold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            TextButton(onClick = {
                                val email = data.email ?: ""
                                val phone = data.telepon ?: ""
                                screenModel.resetState()
                                navigator.push(RequestPasswordScreen(email, phone))
                            }, modifier = Modifier.padding(top = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Lupa password? ",
                                        fontFamily = satoshiMedium,
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "Minta password",
                                        fontFamily = satoshiBold,
                                        color = redPrimer,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            TextButton(onClick = { screenModel.resetState() }) {
                                Text(
                                    "Bukan Saya",
                                    color = Color.LightGray,
                                    fontFamily = satoshiMedium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }


            if (screenModel.showNotFoundDialog) {
                Dialog(
                    onDismissRequest = { screenModel.showNotFoundDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                null,
                                tint = redPrimer,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Data Tidak Ditemukan",
                                fontFamily = satoshiBold,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Nomor polisi belum terdaftar di sistem kami. Silakan daftar akun baru untuk menikmati layanan Autoglaze.",
                                fontFamily = satoshiMedium,
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    screenModel.showNotFoundDialog = false
                                    navigator.push(RegisterScreen())
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Daftar Sekarang",
                                    color = Color.White,
                                    fontFamily = satoshiBold
                                )
                            }

                            TextButton(
                                onClick = { screenModel.showNotFoundDialog = false },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Batal", color = Color.Gray, fontFamily = satoshiBold)
                            }
                        }
                    }
                }
            }


            if (screenModel.showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { screenModel.showErrorDialog = false },
                    icon = {
                        Icon(
                            Icons.Default.ErrorOutline,
                            null,
                            tint = redPrimer,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = { Text("Gagal Memuat Data", fontFamily = satoshiBold) },
                    text = {
                        Text(
                            screenModel.errorMessage,
                            textAlign = TextAlign.Center,
                            fontFamily = satoshiMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { screenModel.showErrorDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                        ) {
                            Text("Oke", color = Color.White)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(28.dp)
                )
            }
        }
    }
}

@Composable
fun VehicleInfoItem(label: String, value: String, font: FontFamily) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = font)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontFamily = font
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
        )
    }
}