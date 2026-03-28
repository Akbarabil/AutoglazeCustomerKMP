package com.example.autoglazecustomer.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.img_catat
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

data class RequestPasswordScreen(val email: String, val phone: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current
        val clipboardManager = LocalClipboardManager.current
        val authService = remember { AuthService() }
        val screenModel = rememberScreenModel { RequestPasswordScreenModel(authService) }
        val scope = rememberCoroutineScope()

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val redPrimer = Color(0xFFD53B1E)
        val greenSuccess = Color(0xFF2E7D32)

        var selectedMethod by remember { mutableStateOf<String?>(null) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var generatedPass by remember { mutableStateOf("") }
        var snackbarVisible by remember { mutableStateOf(false) }

        LaunchedEffect(screenModel.isSuccess) {
            if (screenModel.isSuccess) {
                generatedPass = screenModel.generatedPassword
                showSuccessDialog = true
            }
        }

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
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_catat),
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
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = "Minta Sandi",
                                    fontFamily = satoshiMedium,
                                    fontSize = 29.sp,
                                    color = Color(0xFF9E9E9E)
                                )

                                Text(
                                    text = "Silakan pilih metode pemulihan sandi",
                                    fontFamily = satoshiMedium,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                MethodSelector(
                                    label = "Email",
                                    value = email,
                                    icon = Icons.Default.Email,
                                    isSelected = selectedMethod == "email",
                                    onClick = { selectedMethod = "email" },
                                    font = satoshiMedium,
                                    accentColor = redPrimer
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                MethodSelector(
                                    label = "No. Telepon",
                                    value = phone,
                                    icon = Icons.Default.Phone,
                                    isSelected = selectedMethod == "phone",
                                    onClick = { selectedMethod = "phone" },
                                    font = satoshiMedium,
                                    accentColor = redPrimer
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        val data =
                                            if (selectedMethod == "email") mapOf("email" to email)
                                            else mapOf("telepon" to phone)
                                        screenModel.requestPassword(data)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                    enabled = selectedMethod != null && !screenModel.isLoading
                                ) {
                                    if (screenModel.isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            "Kirim",
                                            fontFamily = satoshiMedium,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Data tidak sesuai? ",
                                        fontFamily = satoshiMedium,
                                        color = Color(0xFFBDBDBD)
                                    )
                                    Text(
                                        "Hubungi admin",
                                        modifier = Modifier.clickable { uriHandler.openUri("https://wa.me/628980136066") },
                                        fontFamily = satoshiMedium,
                                        color = redPrimer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }

                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 12.dp, top = 12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }


            AnimatedVisibility(
                visible = snackbarVisible,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Snackbar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    containerColor = greenSuccess,
                ) {
                    Text(
                        "Sandi berhasil disalin ke papan klip",
                        color = Color.White,
                        fontFamily = satoshiMedium
                    )
                }
            }


            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {
                        Button(
                            onClick = { navigator.pop() },
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tutup & Masuk Ke Akun", fontFamily = satoshiBold)
                        }
                    },
                    title = {
                        Text(
                            "Sandi Baru Berhasil Dibuat",
                            fontFamily = satoshiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Gunakan sandi ini untuk login:",
                                fontFamily = satoshiMedium,
                                color = Color.Gray
                            )

                            Surface(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(generatedPass))
                                        scope.launch {
                                            snackbarVisible = true
                                            delay(3000)
                                            snackbarVisible = false
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF5F5F5),
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 12.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = generatedPass,
                                        fontSize = 24.sp,
                                        fontFamily = satoshiBold,
                                        color = redPrimer
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin",
                                        tint = redPrimer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Klik pada sandi untuk menyalin",
                                fontFamily = satoshiMedium,
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    containerColor = Color.White
                )
            }
        }
    }

    @Composable
    private fun MethodSelector(
        label: String,
        value: String,
        icon: ImageVector,
        isSelected: Boolean,
        onClick: () -> Unit,
        font: FontFamily,
        accentColor: Color
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else Color.LightGray
            ),
            color = if (isSelected) accentColor.copy(alpha = 0.05f) else Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isSelected) accentColor else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(label, fontSize = 12.sp, color = Color.Gray, fontFamily = font)
                    Text(
                        value,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.weight(1f))
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                )
            }
        }
    }
}