package com.example.autoglazecustomer.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.bg_pattern_grey
import autoglazecustomer.composeapp.generated.resources.img_catat
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.register.Country
import com.example.autoglazecustomer.data.model.register.allCountries
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class RegisterScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }
        val screenModel = rememberScreenModel { RegisterScreenModel(authService) }
        val state = screenModel.state

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        val datePickerState = rememberDatePickerState()
        var showDatePicker by remember { mutableStateOf(false) }
        var showCountryPicker by remember { mutableStateOf(false) }

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

        // JOSJIS: Tambahkan background putih solid untuk mencegah kebocoran iOS
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
                                    // JOSJIS: Menutup area bawah iOS dengan warna putih
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .padding(horizontal = 24.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text("Yuk, daftar dulu", fontFamily = satoshiMedium, fontSize = 28.sp, color = Color(0xFF9E9E9E))
                                Spacer(modifier = Modifier.height(24.dp))

                                RegisterTextField(
                                    value = state.nama,
                                    onValueChange = { screenModel.onNamaChange(it) },
                                    label = "Nama Lengkap",
                                    icon = Icons.Default.Person,
                                    satoshiMedium = satoshiMedium,
                                    isError = state.errorField == "nama",
                                    colors = commonTextFieldColors
                                )

                                RegisterTextField(
                                    value = state.email,
                                    onValueChange = { screenModel.onEmailChange(it) },
                                    label = "Email",
                                    icon = Icons.Default.Email,
                                    keyboardType = KeyboardType.Email,
                                    satoshiMedium = satoshiMedium,
                                    isError = state.errorField == "email",
                                    colors = commonTextFieldColors
                                )

                                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                                    OutlinedTextField(
                                        value = state.tglLahir,
                                        onValueChange = {},
                                        isError = state.errorField == "tglLahir",
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Tanggal Lahir", fontFamily = satoshiMedium) },
                                        readOnly = true,
                                        leadingIcon = { Icon(Icons.Default.DateRange, null) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = commonTextFieldColors
                                    )
                                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                                }

                                OutlinedTextField(
                                    value = state.phone,
                                    onValueChange = { screenModel.onPhoneChange(it) },
                                    isError = state.errorField == "phone",
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                                    label = { Text("Nomor Telephone", fontFamily = satoshiMedium) },
                                    leadingIcon = {
                                        Row(
                                            modifier = Modifier.clickable { showCountryPicker = true }.padding(start = 12.dp, end = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(state.selectedCountry.flag, fontSize = 20.sp)
                                            Spacer(Modifier.width(4.dp))
                                            Text("+${state.selectedCountry.phoneCode}", fontWeight = FontWeight.Medium, fontFamily = satoshiMedium)
                                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = commonTextFieldColors
                                )

                                OutlinedTextField(
                                    value = state.password,
                                    onValueChange = { screenModel.onPasswordChange(it) },
                                    isError = state.errorField == "password",
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Kata Sandi", fontFamily = satoshiMedium) },
                                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                                    trailingIcon = {
                                        IconButton(onClick = { screenModel.togglePasswordVisibility() }) {
                                            Icon(if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                        }
                                    },
                                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = commonTextFieldColors
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                Button(
                                    onClick = {
                                        screenModel.validateAndCheckEmail {
                                             navigator.push(RegisterVehicleScreen(it))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                    enabled = !state.isLoading
                                ) {
                                    if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    else Text("Selanjutnya", fontFamily = satoshiMedium, fontSize = 20.sp, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sudah punya akun? ", fontFamily = satoshiMedium, color = Color(0xFFBDBDBD))
                                    Text("Masuk", modifier = Modifier.clickable { navigator.pop() }, fontWeight = FontWeight.Bold, color = redPrimer)
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }

                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier
                            .statusBarsPadding()
                            .align(Alignment.TopStart)
                            .padding(start = 8.dp, top = 8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                state.errorMessage?.let { msg ->
                    Snackbar(
                        modifier = Modifier.statusBarsPadding().padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        containerColor = redPrimer,
                        action = { TextButton(onClick = { screenModel.clearError() }) { Text("OK", color = Color.White) } }
                    ) { Text(msg, color = Color.White) }
                }
            }

            if (showCountryPicker) {
                CountryPickerDialog(
                    onDismiss = { showCountryPicker = false },
                    onCountrySelected = {
                        screenModel.onCountrySelected(it)
                        showCountryPicker = false
                    }
                )
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { screenModel.onTglLahirChange(formatMillisToDate(it)) }
                            showDatePicker = false
                        }) { Text("Pilih", color = redPrimer) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = Color.Gray) }
                    },
                    colors = DatePickerDefaults.colors(containerColor = Color.White)
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            titleContentColor = Color.DarkGray,
                            headlineContentColor = Color.DarkGray,
                            selectedDayContainerColor = redPrimer,
                            selectedDayContentColor = Color.White,
                            todayContentColor = redPrimer,
                            todayDateBorderColor = redPrimer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CountryPickerDialog(onDismiss: () -> Unit, onCountrySelected: (Country) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pilih Negara", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                Spacer(Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(allCountries) { country ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onCountrySelected(country) }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(country.flag, fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(country.name, modifier = Modifier.weight(1f), color = Color.DarkGray)
                            Text("+${country.phoneCode}", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterTextField(
    value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text, satoshiMedium: FontFamily,
    isError: Boolean = false, colors: androidx.compose.material3.TextFieldColors
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, isError = isError,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        label = { Text(label, fontFamily = satoshiMedium) },
        leadingIcon = { Icon(icon, null) },
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = colors
    )
}

fun formatMillisToDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
}