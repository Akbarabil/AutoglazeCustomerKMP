package com.example.autoglazecustomer.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.GetCekKendaraan
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class CheckVehicleScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }

        // Memanggil UI Content
        CheckVehicleUI(
            onNavigateToLogin = { email ->
                // Navigator push akan menumpuk halaman Login di atas halaman Cek Kendaraan
                navigator.push(LoginScreen(initialEmail = email))
            },
            authService = authService
        )
    }
}

@Composable
fun CheckVehicleUI(
    onNavigateToLogin: (String) -> Unit,
    authService: AuthService
) {
    var nopol by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var vehicleData by remember { mutableStateOf<GetCekKendaraan?>(null) }
    var showNotFoundDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val redPrimer = Color(0xFFD53B1E)
    val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_pattern_grey),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_vehicle_check),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .size(193.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Cek Data Kendaraan",
                            fontFamily = satoshiMedium,
                            fontSize = 29.sp,
                            color = Color(0xFF9E9E9E)
                        )

                        Text(
                            text = "Masukkan info kendaraan & kontak anda",
                            fontFamily = satoshiMedium,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )

                        OutlinedTextField(
                            value = nopol,
                            onValueChange = { nopol = it.uppercase() },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nomor Polisi", fontFamily = satoshiMedium) },
                            placeholder = { Text("Contoh: B 1234 ABC", fontFamily = satoshiMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedLabelColor = Color.DarkGray,
                                cursorColor = Color.DarkGray
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (nopol.isNotBlank()) {
                                    scope.launch {
                                        isLoading = true
                                        try {
                                            val response = authService.cekKendaraan(nopol.replace(" ", ""))
                                            if (response.success && response.data != null) {
                                                vehicleData = response.data
                                            } else {
                                                showNotFoundDialog = true
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Gagal memuat data")
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Cek Kendaraan", fontFamily = satoshiMedium, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sudah punya akun? ", fontFamily = satoshiMedium, color = Color(0xFFBDBDBD), fontSize = 16.sp)
                            Text(
                                text = "Masuk",
                                modifier = Modifier.clickable { onNavigateToLogin("") },
                                fontFamily = satoshiMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Belum punya akun? ", fontFamily = satoshiMedium, color = Color(0xFFBDBDBD), fontSize = 16.sp)
                            Text(
                                text = "Daftar",
                                modifier = Modifier.clickable { /* Aksi Daftar */ },
                                fontFamily = satoshiMedium,
                                fontWeight = FontWeight.Bold,
                                color = redPrimer,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // --- DIALOGS (LOADING, FOUND, NOT FOUND) ---
        if (isLoading) {
            LoadingDialog(redPrimer)
        }

        if (vehicleData != null) {
            Dialog(onDismissRequest = { vehicleData = null },properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Data Ditemukan", fontFamily = satoshiMedium, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        VehicleInfoItem("Nama Pemilik", vehicleData?.nama ?: "-", satoshiMedium)
                        VehicleInfoItem("Tipe Mobil", vehicleData?.tipeMobil ?: "-", satoshiMedium)
                        VehicleInfoItem("Nomor Polisi", vehicleData?.nopol ?: "-", satoshiMedium)
                        VehicleInfoItem("Nomor Rangka", vehicleData?.noRangka ?: "-", satoshiMedium)
                        VehicleInfoItem("Email", vehicleData?.email ?: "-", satoshiMedium)
                        VehicleInfoItem("Telepon", vehicleData?.telepon ?: "-", satoshiMedium)


                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val email = vehicleData?.email ?: ""
                                vehicleData = null
                                onNavigateToLogin(email)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ya, Masuk Ke Akun", fontFamily = satoshiMedium, color = Color.White)
                        }

                        TextButton(onClick = { vehicleData = null }) {
                            Text("Bukan Saya", fontFamily = satoshiMedium, color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showNotFoundDialog) {
            AlertDialog(
                onDismissRequest = { showNotFoundDialog = false },
                title = { Text("Data Tidak Ditemukan", fontFamily = satoshiMedium) },
                text = { Text("Nomor polisi belum terdaftar. Silakan daftar akun baru.", fontFamily = satoshiMedium) },
                confirmButton = {
                    Button(onClick = { showNotFoundDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = redPrimer)) {
                        Text("Daftar Sekarang", fontFamily = satoshiMedium)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNotFoundDialog = false }) {
                        Text("Batal", fontFamily = satoshiMedium)
                    }
                }
            )
        }
    }
}

@Composable
fun VehicleInfoItem(label: String, value: String, fontFamily: FontFamily) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = fontFamily)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, fontFamily = fontFamily)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))
    }
}