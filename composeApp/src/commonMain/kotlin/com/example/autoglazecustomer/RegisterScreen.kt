package com.example.autoglazecustomer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }

        RegisterUI(
            onNavigateToNext = { data: DaftarData ->
                navigator.push(RegisterVehicleScreen(data))
            },
            onBackToLogin = { navigator.pop() },
            authService = authService
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUI(
    onNavigateToNext: (DaftarData) -> Unit,
    onBackToLogin: () -> Unit,
    authService: AuthService
) {
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var tglLahir by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val redPrimer = Color(0xFFD53B1E)
    val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
    val datePickerState = rememberDatePickerState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_pattern_grey),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // --- PENGGUNAAN onBackToLogin (Tombol Back Atas) ---
            IconButton(
                onClick = onBackToLogin,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Image(
                painter = painterResource(Res.drawable.img_catat),
                contentDescription = null,
                modifier = Modifier
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
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Yuk, daftar dulu",
                        fontFamily = satoshiMedium,
                        fontSize = 28.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    RegisterTextField(nama, { nama = it }, "Nama Lengkap", Icons.Default.Person, satoshiMedium = satoshiMedium)
                    RegisterTextField(email, { email = it }, "Email", Icons.Default.Email, KeyboardType.Email, satoshiMedium)

                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                        OutlinedTextField(
                            value = tglLahir,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tanggal Lahir", fontFamily = satoshiMedium) },
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.DateRange, null) },
                            shape = RoundedCornerShape(10.dp)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                    }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.all { c -> c.isDigit() }) phone = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        label = { Text("Nomor WhatsApp (812...)", fontFamily = satoshiMedium) },
                        leadingIcon = { Text("+62  ", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold, fontFamily = satoshiMedium) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kata Sandi", fontFamily = satoshiMedium) },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (validate(nama, email, tglLahir, phone, password)) {
                                scope.launch {
                                    isLoading = true
                                    val (isAvailable, message) = authService.cekEmail(email)
                                    isLoading = false

                                    if (isAvailable) {
                                        onNavigateToNext(DaftarData(nama, email, tglLahir, "+62$phone", password))
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            } else {
                                errorMessage = "Harap lengkapi data dengan benar"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                        enabled = !isLoading
                    ) {
                        Text("Selanjutnya", fontFamily = satoshiMedium, fontSize = 20.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- PENGGUNAAN onBackToLogin (Teks Bawah) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sudah punya akun? ", fontFamily = satoshiMedium, color = Color(0xFFBDBDBD), fontSize = 16.sp)
                        Text(
                            text = "Masuk",
                            modifier = Modifier.clickable { onBackToLogin() },
                            fontFamily = satoshiMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { tglLahir = convertMillisToDate(it) }
                        showDatePicker = false
                    }) { Text("Pilih") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        if (isLoading) LoadingDialog(redPrimer)

        errorMessage?.let { msg ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                action = { TextButton(onClick = { errorMessage = null }) { Text("OK", color = Color.White) } }
            ) { Text(msg) }
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    satoshiMedium: FontFamily
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        label = { Text(label, fontFamily = satoshiMedium) },
        leadingIcon = { Icon(icon, null) },
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

fun convertMillisToDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
}

fun validate(n: String, e: String, t: String, p: String, pass: String): Boolean {
    return n.isNotBlank() && e.contains("@") && t.isNotBlank() && p.length >= 7 && pass.length >= 6
}