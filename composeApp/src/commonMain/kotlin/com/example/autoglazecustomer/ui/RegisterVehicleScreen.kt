package com.example.autoglazecustomer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.MerkKendaraanResponse
import com.example.autoglazecustomer.data.model.TipeKendaraanResponse
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse
import com.example.autoglazecustomer.data.network.AuthService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class RegisterVehicleScreen(val dataRegistrasi: DaftarData) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val authService = remember { AuthService() }

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        // API Data States
        var listMerek by remember { mutableStateOf(emptyList<MerkKendaraanResponse>()) }
        var listTipe by remember { mutableStateOf(emptyList<TipeKendaraanResponse>()) }
        var listWarna by remember { mutableStateOf(emptyList<WarnaKendaraanResponse>()) }

        // Form States
        var merekTerpilih by remember { mutableStateOf<MerkKendaraanResponse?>(null) }
        var tipeTerpilih by remember { mutableStateOf<TipeKendaraanResponse?>(null) }
        var tahun by remember { mutableStateOf("") }
        var nopol by remember { mutableStateOf("") }
        var noRangka by remember { mutableStateOf("") }
        var warnaTerpilih by remember { mutableStateOf<WarnaKendaraanResponse?>(null) }

        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // 1. Load Merek & Warna saat layar dibuka
        LaunchedEffect(Unit) {
            try {
                // Load Merek
                val resMerek = authService.getMerek()
                listMerek = resMerek
                println("DEBUG_AUTOGLAZE: Jumlah Merek = ${listMerek.size}")

                // Load Warna (Sangat Penting agar LazyRow tidak kosong)
                val resWarna = authService.getWarna()
                listWarna = resWarna
                println("DEBUG_AUTOGLAZE: Jumlah Warna = ${listWarna.size}")
            } catch (e: Exception) {
                println("DEBUG_AUTOGLAZE: Error Init = ${e.message}")
                errorMessage = "Gagal memuat data awal dari server"
            }
        }

        // 2. Cascading: Load Tipe saat Merek berubah
        LaunchedEffect(merekTerpilih) {
            merekTerpilih?.let {
                tipeTerpilih = null // Reset pilihan tipe setiap kali merek diganti
                try {
                    listTipe = authService.getTipe(it.idMerek)
                    println("DEBUG_AUTOGLAZE: Jumlah Tipe untuk merek ${it.namaMerek} = ${listTipe.size}")
                } catch (e: Exception) {
                    errorMessage = "Gagal memuat tipe mobil"
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Layer 1: Background
            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Layer 2: Main UI
            Column(modifier = Modifier.fillMaxSize()) {
                // Image Header
                Image(
                    painter = painterResource(Res.drawable.img_vehicle_check),
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
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Isi data kendaraanmu",
                            fontFamily = satoshiMedium,
                            fontSize = 28.sp,
                            color = Color(0xFF9E9E9E)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Dropdown Merk
                        VehicleDropdownField(
                            label = "Merk Mobil",
                            value = merekTerpilih?.namaMerek ?: "",
                            options = listMerek.map { it.namaMerek },
                            onSelected = { nama ->
                                merekTerpilih = listMerek.find { it.namaMerek == nama }
                            },
                            satoshiMedium = satoshiMedium,
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

                        // Row untuk Tipe dan Tahun
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1.2f)) {
                                VehicleDropdownField(
                                    label = "Tipe",
                                    value = tipeTerpilih?.namaTipeKendaraan ?: "",
                                    options = listTipe.map { it.namaTipeKendaraan },
                                    onSelected = { nama ->
                                        tipeTerpilih = listTipe.find { it.namaTipeKendaraan == nama }
                                    },
                                    satoshiMedium = satoshiMedium,
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
                                    ),
                                    enabled = merekTerpilih != null
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(0.8f)) {
                                VehicleDropdownField(
                                    label = "Tahun",
                                    value = tahun,
                                    options = (2000..2026).map { it.toString() }.reversed(),
                                    onSelected = { tahun = it },
                                    satoshiMedium = satoshiMedium,
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
                                    ),
                                    enabled = tipeTerpilih != null
                                )
                            }
                        }

                        // Nomor Polisi
                        OutlinedTextField(
                            value = nopol,
                            onValueChange = { nopol = it.uppercase().replace(" ", "") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            label = { Text("Nomor Polisi", fontFamily = satoshiMedium) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_plat_nomer),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            placeholder = { Text("Contoh: B1234ABC", color = Color.LightGray) },
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                keyboardType = KeyboardType.Text
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedLabelColor = Color.DarkGray,
                                cursorColor = Color.DarkGray
                            )
                        )

                        // Nomor Rangka
                        OutlinedTextField(
                            value = noRangka,
                            onValueChange = { noRangka = it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            label = { Text("Nomor Rangka", fontFamily = satoshiMedium) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_nomer_rangka),
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

                        Text("Warna Kendaraan", fontFamily = satoshiMedium, fontSize = 16.sp)

                        // Horizontal List Warna
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listWarna) { warna ->
                                WarnaItem(
                                    warnaObj = warna,
                                    isSelected = warna == warnaTerpilih,
                                    onClick = { warnaTerpilih = warna },
                                    satoshiMedium = satoshiMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Tombol Selanjutnya
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val nopolTersedia = authService.cekNopol(nopol)
                                        isLoading = false

                                        if (nopolTersedia) {
                                            // Gabungkan data profil dengan data kendaraan
                                            val dataLengkap = dataRegistrasi.copy(
                                                idMerek = merekTerpilih?.idMerek ?: 0,
                                                idTipe = tipeTerpilih?.idTipeKendaraan ?: 0,
                                                idWarna = warnaTerpilih?.idWarna ?: 0,
                                                tahun = tahun,
                                                nopol = nopol,
                                                noRangka = noRangka
                                            )
                                            navigator.push(SurveyScreen(dataLengkap))
                                        } else {
                                            errorMessage = "Maaf, nomor polisi ini sudah terdaftar."
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "Terjadi kesalahan koneksi"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            enabled = !isLoading && warnaTerpilih != null && nopol.isNotBlank() && tipeTerpilih != null
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "Selanjutnya",
                                    fontFamily = satoshiMedium,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Snackbar Notification
            errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK", color = Color.White)
                        }
                    }
                ) { Text(msg) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    satoshiMedium: FontFamily,
    enabled: Boolean = true,
    // Tambahkan parameter colors di sini agar bisa menerima input warna dari luar
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.DarkGray,
        unfocusedBorderColor = Color.DarkGray,
        focusedLabelColor = Color.DarkGray,
        unfocusedLabelColor = Color.DarkGray,
        cursorColor = Color.DarkGray,
        disabledBorderColor = Color(0xFFEEEEEE),
        disabledLabelColor = Color.LightGray
    )
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.padding(bottom = 14.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label, fontFamily = satoshiMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            // Gunakan parameter colors yang dikirim
            colors = colors
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontFamily = satoshiMedium) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WarnaItem(
    warnaObj: WarnaKendaraanResponse,
    isSelected: Boolean,
    onClick: () -> Unit,
    satoshiMedium: FontFamily
) {
    Surface(
        modifier = Modifier
            .widthIn(min = 100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        // Garis pinggir: Grey saat diam, Dark Gray saat dipilih
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color.DarkGray else Color.LightGray
        ),
        // Background: Putih atau abu-abu sangat muda
        color = Color.White
    ) {
        Text(
            text = warnaObj.namaWarna,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontFamily = satoshiMedium,
            // Warna Teks: Grey saat diam, Dark Gray/Hitam saat dipilih
            color = if (isSelected) Color.DarkGray else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}