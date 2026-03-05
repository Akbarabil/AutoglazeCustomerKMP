package com.example.autoglazecustomer.ui.register

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.DaftarData
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.LoadingDialog
import com.example.autoglazecustomer.ui.SearchableDropdown
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class RegisterVehicleScreen(private val dataRegistrasi: DaftarData) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val authService = remember { AuthService() }
        val screenModel = rememberScreenModel { RegisterVehicleScreenModel(authService) }
        val state = screenModel.state

        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        val yearList = remember {
            (2000..2026).map { it.toString() }.reversed()
        } //Tahun masih manual karena transisi library clock

        // Definisi warna seragam untuk semua input
        val commonColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.DarkGray,
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.DarkGray, // Fix warna kursor
            selectionColors = TextSelectionColors(
                handleColor = Color.DarkGray,
                backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
            ),
            errorBorderColor = redPrimer,
            errorLabelColor = redPrimer,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )

        LaunchedEffect(Unit) {
            screenModel.initData()
        }

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Scaffold(containerColor = Color.Transparent) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(Res.drawable.img_vehicle_check),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 80.dp)
                                .size(193.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                            color = Color.White
                        ) {

                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {

                                Text(
                                    text = "Isi data kendaraanmu",
                                    fontFamily = satoshiMedium,
                                    fontSize = 28.sp,
                                    color = Color(0xFF9E9E9E)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // MERK MOBIL
                                SearchableDropdown(
                                    label = "Merk Mobil",
                                    items = state.listMerek,
                                    selectedItem = state.merekTerpilih,
                                    getLabel = { it.namaMerek },
                                    onItemSelected = { selected ->
                                        screenModel.onMerekSelected(selected.namaMerek)
                                    },
                                    onTextChanged = { text ->
                                        if (text.isEmpty()) {
                                            screenModel.clearMerek()
                                        }
                                    },
                                    satoshiMedium = satoshiMedium,
                                    isLoading = state.isLoadingMerek,
                                    isError = state.errorField == "merek"
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Box(modifier = Modifier.weight(1.2f)) {

                                        SearchableDropdown(
                                            label = "Tipe",
                                            items = state.listTipe,
                                            selectedItem = state.tipeTerpilih,
                                            getLabel = { it.namaTipeKendaraan },
                                            onItemSelected = { selected ->
                                                screenModel.onTipeSelected(selected.namaTipeKendaraan)
                                            },
                                            satoshiMedium = satoshiMedium,
                                            enabled = state.merekTerpilih != null,
                                            isLoading = state.isLoadingTipe,
                                            autoExpand = true,
                                            isError = state.errorField == "tipe"
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box(modifier = Modifier.weight(0.8f)) {

                                        SearchableDropdown(
                                            label = "Tahun",
                                            items = yearList,
                                            selectedItem = state.tahun.ifEmpty { null },
                                            getLabel = { it },
                                            onItemSelected = { selected ->
                                                screenModel.onTahunSelected(selected)
                                            },
                                            satoshiMedium = satoshiMedium,
                                            isError = state.errorField == "tahun"
                                        )
                                    }
                                }

                                // NOMOR POLISI
                                OutlinedTextField(
                                    value = state.nopol,
                                    onValueChange = {
                                        screenModel.onNopolChange(it)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 14.dp),
                                    label = {
                                        Text("Nomor Polisi", fontFamily = satoshiMedium)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(Res.drawable.ic_plat_nomer),
                                            null,
                                            Modifier.size(24.dp)
                                        )
                                    },
                                    placeholder = {
                                        Text("B1234ABC", color = Color.LightGray)
                                    },
                                    isError = state.errorField == "nopol",
                                    shape = RoundedCornerShape(10.dp),
                                    colors = commonColors,
                                    singleLine = true
                                )

                                // NOMOR RANGKA
                                OutlinedTextField(
                                    value = state.noRangka,
                                    onValueChange = {
                                        screenModel.onNoRangkaChange(it)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    label = {
                                        Text("Nomor Rangka", fontFamily = satoshiMedium)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(Res.drawable.ic_nomer_rangka),
                                            null,
                                            Modifier.size(24.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = commonColors,
                                    singleLine = true
                                )

                                Text(
                                    "Warna Kendaraan",
                                    fontFamily = satoshiMedium,
                                    fontSize = 16.sp
                                )

                                LazyRow(
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    items(state.listWarna) { warna ->

                                        WarnaItem(
                                            warnaObj = warna,
                                            isSelected = warna == state.warnaTerpilih,
                                            onClick = {
                                                screenModel.onWarnaSelected(warna)
                                            },
                                            satoshiMedium = satoshiMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                Button(
                                    onClick = {

                                        screenModel.validateAndCheckNopol(
                                            onSuccess = {
                                                navigator.push(SurveyScreen(it))
                                            },
                                            dataRegistrasi = dataRegistrasi
                                        )

                                    },
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

                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp)
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

            if (state.isLoading) {
                LoadingDialog(color = redPrimer)
            }

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {

                state.errorMessage?.let { msg ->

                    Snackbar(
                        modifier = Modifier.padding(
                            top = 40.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        containerColor = redPrimer,
                        action = {
                            TextButton(
                                onClick = { screenModel.clearError() }
                            ) {
                                Text("OK", color = Color.White)
                            }
                        }
                    ) {
                        Text(msg, color = Color.White)
                    }
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

    @Composable
    fun WarnaItem( warnaObj: WarnaKendaraanResponse, isSelected: Boolean, onClick: () -> Unit, satoshiMedium: FontFamily ) {
        Surface(
            modifier = Modifier .widthIn(min = 100.dp) .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke( width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) Color.DarkGray else Color.LightGray ),
            color = Color.White
        ) {
            Text(
                text = warnaObj.namaWarna,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontFamily = satoshiMedium,
                color = if (isSelected) Color.DarkGray else Color.Gray,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}