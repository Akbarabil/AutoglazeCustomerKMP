package com.example.autoglazecustomer.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.img_berpikir
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.login.LoginScreen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class SurveyScreen(private val dataRegistrasi: DaftarData) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SurveyScreenModel>()
        val state = screenModel.state

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val redPrimer = Color(0xFFD53B1E)

        var expanded by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { screenModel.initData() }

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
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_berpikir),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 60.dp)
                                .size(193.dp)
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
                                    text = "Isi survey singkat",
                                    fontFamily = satoshiMedium,
                                    fontSize = 29.sp,
                                    color = Color(0xFF9E9E9E)
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = state.selectedAsalTahu?.label ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        isError = state.errorField == "survey",
                                        label = {
                                            Text(
                                                "Dapat info Autoglaze dari mana?",
                                                fontFamily = satoshiMedium
                                            )
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = commonTextFieldColors
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        state.asalTahuList.forEach { item ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        item.label,
                                                        fontFamily = satoshiMedium
                                                    )
                                                },
                                                onClick = {
                                                    screenModel.onAsalTahuSelected(item)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = { screenModel.registerFinal(dataRegistrasi) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                    enabled = !state.isLoading
                                ) {
                                    if (state.isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Daftar",
                                            fontFamily = satoshiBold,
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )
                                    }
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
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Kembali",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }


            if (state.showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White,
                    title = {
                        Text(
                            text = "Pendaftaran Berhasil",
                            fontFamily = satoshiBold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Akun anda telah berhasil dibuat. Silakan masuk menggunakan email dan kata sandi anda.",
                            fontFamily = satoshiMedium,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                navigator.replaceAll(LoginScreen(initialEmail = dataRegistrasi.email))
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Masuk Sekarang", fontFamily = satoshiBold, color = Color.White)
                        }
                    }
                )
            }


            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                state.errorMessage?.let { msg ->
                    Snackbar(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        containerColor = redPrimer,
                        action = {
                            TextButton(onClick = { screenModel.clearError() }) {
                                Text("OK", color = Color.White)
                            }
                        }
                    ) { Text(msg, color = Color.White) }
                }
            }
        }

        LaunchedEffect(state.errorMessage) {
            if (state.errorMessage != null) {
                kotlinx.coroutines.delay(4000)
                screenModel.clearError()
            }
        }
    }
}