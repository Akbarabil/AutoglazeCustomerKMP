package com.example.autoglazecustomer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.ic_email
import autoglazecustomer.composeapp.generated.resources.ic_visibility
import autoglazecustomer.composeapp.generated.resources.ic_visibility_off
import autoglazecustomer.composeapp.generated.resources.img_hello
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import autoglazecustomer.composeapp.generated.resources.ic_password

@Composable
fun LoginScreen(
    // Default parameter untuk menghindari crash saat Preview
    authService: AuthService = remember { AuthService() }
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Layer 1: Box sebagai kontainer utama untuk menumpuk Background + UI
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 2: Background Image (paling bawah)
        Image(
            painter = painterResource(Res.drawable.bg_pattern_grey),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Layer 3: Scaffold transparan untuk menampung UI & Snackbar (Toast)
        Scaffold(
            containerColor = Color.Transparent, // PENTING: Agar background tetap terlihat
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->

            // Isi Konten Login
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Mobil
                Image(
                    painter = painterResource(Res.drawable.img_hello),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 60.dp).size(193.dp)
                )

                // Login Card (Surface Putih)
                Surface(
                    modifier = Modifier.fillMaxSize().padding(top = 0.dp),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Halo Sobat Glaze",
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
                            label = { Text("Email") },
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
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Red,
                                cursorColor = Color.Red
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password") },
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
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Red,
                                cursorColor = Color.Red
                            )
                        )

                        Text(
                            text = "Lupa Password?",
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp),
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tombol Login
                        Button(
                            onClick = {
                                scope.launch {
                                    if (email.isBlank() || password.isBlank()) {
                                        snackbarHostState.showSnackbar("Email dan Password tidak boleh kosong")
                                        return@launch
                                    }

                                    isLoading = true
                                    try {
                                        val response = authService.login(email, password)
                                        if (response.success) {
                                            snackbarHostState.showSnackbar("Berhasil! Token: ${response.token}")
                                        } else {
                                            snackbarHostState.showSnackbar("Gagal: ${response.message}")
                                        }
                                    } catch (e: io.ktor.client.plugins.ResponseException) {
                                        snackbarHostState.showSnackbar("Login Gagal: Email atau Password salah")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Terjadi kesalahan: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Masuk", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Daftar Akun Baru",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}