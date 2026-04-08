package com.example.autoglazecustomer.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor.KtorNetworkFetcherFactory
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.manager.SessionManager
import com.example.autoglazecustomer.data.manager.VoucherManager
import com.example.autoglazecustomer.ui.login.LoginScreen
import io.ktor.client.HttpClient

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(HttpClient()))
            }
            .build()
    }

    MaterialTheme {
        Navigator(SplashScreen()) { navigator ->
            var showSessionDialog by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                SessionManager.logoutEvent.collect {
                    showSessionDialog = true
                }
            }

            if (showSessionDialog) {
                AlertDialog(
                    onDismissRequest = {
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    ),
                    containerColor = Color.White,
                    title = {
                        Text(
                            text = "Sesi Berakhir",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    text = {
                        Text(
                            text = "Sesi anda telah habis atau tidak valid. Demi keamanan, silakan login kembali untuk melanjutkan.",
                            color = Color.DarkGray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSessionDialog = false
                                TokenManager.clearAll()
                                CartManager.clearCart()
                                VoucherManager.clearVouchers()
                                navigator.replaceAll(LoginScreen())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD53B1E))
                        ) {
                            Text("Login Ulang", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            SlideTransition(navigator)
        }
    }
}