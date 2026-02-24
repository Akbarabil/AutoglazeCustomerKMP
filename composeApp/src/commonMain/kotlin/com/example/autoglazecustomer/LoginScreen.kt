package com.example.autoglazecustomer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.ic_email
import autoglazecustomer.composeapp.generated.resources.ic_password
import autoglazecustomer.composeapp.generated.resources.ic_visibility
import autoglazecustomer.composeapp.generated.resources.ic_visibility_off
import autoglazecustomer.composeapp.generated.resources.img_hello
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

data class LoginScreen(val initialEmail: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }

        // Memanggil UI Login
        LoginUI(
            emailParam = initialEmail,
            onBack = { navigator.pop() },
            authService = authService
        )
    }
}

@Composable
fun LoginUI(
    emailParam: String,
    onBack: () -> Unit,
    authService: AuthService
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf(emailParam) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
    val redPrimer = Color(0xFFD53B1E)

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Background
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

            // Layer 2: Kontainer Utama
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // --- KONTEN TENGAH (STRUKTUR HARUS SAMA DENGAN CHECKVEHICLE) ---
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo (Padding top 60dp agar identik dengan halaman sebelumnya)
                    Image(
                        painter = painterResource(Res.drawable.img_hello),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 80.dp)
                            .size(193.dp)
                    )

                    // Login Card
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
                                text = "Halo Sobat Glaze",
                                fontFamily = satoshiMedium,
                                fontSize = 29.sp,
                                color = Color(0xFF9E9E9E),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Email Field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Email", fontFamily = satoshiMedium) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_email),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
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

                            Spacer(modifier = Modifier.height(14.dp))

                            // Password Field
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Password", fontFamily = satoshiMedium) },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_password),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            painter = painterResource(
                                                if (isPasswordVisible) Res.drawable.ic_visibility
                                                else Res.drawable.ic_visibility_off
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
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

                            Text(
                                text = "Lupa Password?",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 8.dp)
                                    .clickable { /* Aksi Lupa Password */ },
                                fontFamily = satoshiMedium,
                                color = redPrimer,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Tombol Login
                            Button(
                                onClick = {
                                    scope.launch {
                                        if (email.isBlank() || password.isBlank()) {
                                            snackbarHostState.showSnackbar("Email dan Password wajib diisi")
                                            return@launch
                                        }
                                        isLoading = true
                                        try {
                                            val response = authService.login(email, password)
                                            if (response.success) {
                                                snackbarHostState.showSnackbar("Selamat Datang!")
                                            } else {
                                                snackbarHostState.showSnackbar(response.message)
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Login Gagal")
                                        } finally {
                                            isLoading = false
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
                                    Text("Masuk", fontFamily = satoshiMedium, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Daftar Akun Baru",
                                fontFamily = satoshiMedium,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { /* Aksi Daftar */ },
                                color = redPrimer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}