package com.example.autoglazecustomer.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.register.RegisterScreen
import com.example.autoglazecustomer.ui.tabs.MainTabScreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

data class LoginScreen(val initialEmail: String = "") : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }
        val screenModel = rememberScreenModel { LoginScreenModel(authService) }

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        LaunchedEffect(initialEmail) {
            if (initialEmail.isNotEmpty()) screenModel.onEmailChange(initialEmail)
        }

        // Timer untuk menyembunyikan snackbar otomatis
        LaunchedEffect(screenModel.errorMessage) {
            if (screenModel.errorMessage != null) {
                delay(4000)
                screenModel.clearError()
            }
        }

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
            errorBorderColor = redPrimer,
            errorLabelColor = redPrimer,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Scaffold(containerColor = Color.Transparent) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_hello),
                            contentDescription = null,
                            modifier = Modifier.padding(top = 80.dp).size(193.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Halo Sobat Glaze",
                                    fontFamily = satoshiMedium,
                                    fontSize = 29.sp,
                                    color = Color(0xFF9E9E9E)
                                )

                                Text(
                                    text = "Silakan masuk ke akun Anda",
                                    fontFamily = satoshiMedium,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                OutlinedTextField(
                                    value = screenModel.email,
                                    onValueChange = { screenModel.onEmailChange(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Email", fontFamily = satoshiMedium) },
                                    leadingIcon = { Icon(painterResource(Res.drawable.ic_email), null, Modifier.size(24.dp)) },
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = commonTextFieldColors
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = screenModel.password,
                                    onValueChange = { screenModel.onPasswordChange(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Password", fontFamily = satoshiMedium) },
                                    visualTransformation = if (screenModel.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    leadingIcon = { Icon(painterResource(Res.drawable.ic_password), null, Modifier.size(24.dp)) },
                                    trailingIcon = {
                                        IconButton(onClick = { screenModel.togglePasswordVisibility() }) {
                                            Icon(
                                                painter = painterResource(
                                                    if (screenModel.isPasswordVisible) Res.drawable.ic_visibility
                                                    else Res.drawable.ic_visibility_off
                                                ),
                                                null, Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = commonTextFieldColors
                                )

                                Text(
                                    text = "Lupa Password?",
                                    modifier = Modifier.align(Alignment.End).padding(top = 8.dp).clickable { },
                                    fontFamily = satoshiMedium,
                                    color = redPrimer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        screenModel.login { msg ->
                                            navigator.replaceAll(MainTabScreen())
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                    enabled = !screenModel.isLoading
                                ) {
                                    if (screenModel.isLoading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("Masuk", fontFamily = satoshiMedium, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
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
                                        modifier = Modifier.clickable { navigator.push(RegisterScreen()) },
                                        fontFamily = satoshiMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = redPrimer,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp).align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // SNACKBAR DI ATAS (Sesuai permintaan)
            AnimatedVisibility(
                visible = screenModel.errorMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                screenModel.errorMessage?.let { msg ->
                    Snackbar(
                        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
                        containerColor = redPrimer,
                        action = {
                            TextButton(onClick = { screenModel.clearError() }) {
                                Text("OK", color = Color.White)
                            }
                        }
                    ) {
                        Text(msg, color = Color.White)
                    }
                }
            }
        }
    }
}