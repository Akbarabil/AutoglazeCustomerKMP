package com.example.autoglazecustomer.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.*
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class SurveyScreen(val dataRegistrasi: DaftarData) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val authService = remember { AuthService() }

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        // States
        var asalTahuList by remember { mutableStateOf(emptyList<AsalTahuResponse>()) }
        var selectedAsalTahu by remember { mutableStateOf<AsalTahuResponse?>(null) }
        var expanded by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var showSuccessDialog by remember { mutableStateOf(false) }

        // Load data dari API saat init
        LaunchedEffect(Unit) {
            try {
                asalTahuList = authService.getAsalTahu()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data survey"
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Background Pattern
            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Image Header (img_berpikir)
                Image(
                    painter = painterResource(Res.drawable.img_berpikir),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .size(193.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Isi survey singkat",
                            fontFamily = satoshiMedium,
                            fontSize = 29.sp,
                            color = Color(0xFF9E9E9E)
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        // Dropdown "Dapat info dari mana"
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedAsalTahu?.label ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Dapat info Autoglaze dari mana?", fontFamily = satoshiMedium) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.DarkGray,     // Garis saat diklik
                                    unfocusedBorderColor = Color.DarkGray,   // Garis saat diam
                                    focusedLabelColor = Color.DarkGray,      // Warna label saat di atas
                                    unfocusedLabelColor = Color.DarkGray,    // Warna label saat di tengah
                                    cursorColor = Color.DarkGray,            // Warna kursor (meskipun readOnly)
                                    focusedTextColor = Color.Black,          // Warna teks saat aktif
                                    unfocusedTextColor = Color.Black,        // Warna teks saat diam
                                    disabledBorderColor = Color.LightGray,   // Warna saat dropdown mati (tipe sebelum pilih merek)
                                    disabledLabelColor = Color.LightGray,
                                    focusedTrailingIconColor = Color.DarkGray, // Warna panah dropdown
                                    unfocusedTrailingIconColor = Color.DarkGray
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                asalTahuList.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.label, fontFamily = satoshiMedium) },
                                        onClick = {
                                            selectedAsalTahu = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tombol Daftar
                        Button(
                            onClick = {
                                if (selectedAsalTahu != null) {
                                    scope.launch {
                                        isLoading = true
                                        val finalData = dataRegistrasi.copy(
                                            sumberInfo = selectedAsalTahu!!.idGeneral.toString()
                                        )

                                        try {
                                            val response = authService.registerCustomer(finalData)
                                            isLoading = false

                                            if (response.success == true) {
                                                showSuccessDialog = true
                                            } else {
                                                errorMessage = response.message
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            errorMessage = "Terjadi kesalahan pendaftaran: ${e.message}"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            enabled = !isLoading && selectedAsalTahu != null
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Daftar",
                                    fontFamily = satoshiMedium,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // --- DIALOG BERHASIL ---
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { /* Tidak bisa dismiss sembarangan */ },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White,
                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pendaftaran Berhasil",
                                fontFamily = satoshiMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    text = {
                        Text(
                            text = "Akun Anda telah berhasil dibuat. Silakan masuk menggunakan email dan kata sandi Anda.",
                            fontFamily = satoshiMedium,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                navigator.replaceAll(LoginScreen(initialEmail = dataRegistrasi.email))
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Masuk Sekarang", fontFamily = satoshiMedium, color = Color.White)
                        }
                    }
                )
            }

            // Snackbar Error
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK", color = Color.White)
                        }
                    }
                ) { Text(it) }
            }
        }
    }
}