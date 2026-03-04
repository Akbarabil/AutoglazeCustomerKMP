package com.example.autoglazecustomer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.register.RegisterScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

data class LoginScreen(val initialEmail: String = "") : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }

        // Memanggil UI Login dengan menyertakan fungsi navigasi
        LoginUI(
            emailParam = initialEmail,
            onBack = { navigator.pop() },
            onNavigateToRegister = { navigator.push(RegisterScreen()) },
            authService = authService
        )
    }
}

@Composable
fun LoginUI(
    emailParam: String,
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
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
        // Background
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

            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Image
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
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Halo Sobat Glaze",
                                fontFamily = satoshiMedium,
                                fontSize = 29.sp,
                                color = Color(0xFF9E9E9E),
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Silakan masuk ke akun Anda",
                                fontFamily = satoshiMedium,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 4.dp)
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
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
                                                // navigator.replaceAll(HomeScreen()) // Contoh jika sudah ada Home
                                            } else {
                                                snackbarHostState.showSnackbar(response.message)
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Login Gagal: ${e.message}")
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

                            Spacer(modifier = Modifier.height(32.dp))

                            // --- NAVIGASI KE REGISTER ---
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Belum punya akun? ",
                                    fontFamily = satoshiMedium,
                                    color = Color(0xFFBDBDBD),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Daftar",
                                    modifier = Modifier.clickable { onNavigateToRegister() },
                                    fontFamily = satoshiMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = redPrimer,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                // Tombol Back di pojok kiri atas
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