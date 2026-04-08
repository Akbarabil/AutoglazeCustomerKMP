package com.example.autoglazecustomer.ui.profile.myvehicle.addvehicle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import autoglazecustomer.composeapp.generated.resources.ic_nomer_rangka
import autoglazecustomer.composeapp.generated.resources.ic_plat_nomer
import autoglazecustomer.composeapp.generated.resources.img_vehicle_check
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.WarnaKendaraanResponse
import com.example.autoglazecustomer.ui.LoadingDialog
import com.example.autoglazecustomer.ui.SearchableDropdown
import com.example.autoglazecustomer.ui.theme.AppFont
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

class AddVehicleScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<AddVehicleScreenModel>()
        val state = screenModel.state

        val satoshiBold = AppFont.satoshiBold()
        val satoshiMedium = AppFont.satoshiMedium()
        val redPrimer = Color(0xFFD53B1E)

        val yearList = remember { (2000..2026).map { it.toString() }.reversed() }

        val commonColors = OutlinedTextFieldDefaults.colors(
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

        LaunchedEffect(Unit) { screenModel.initData() }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.bg_pattern_grey),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Scaffold(containerColor = Color.Transparent) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                                    text = "Tambah kendaraan baru",
                                    fontFamily = satoshiMedium,
                                    fontSize = 28.sp,
                                    color = Color(0xFF9E9E9E)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                SearchableDropdown(
                                    label = "Merk Mobil",
                                    items = state.listMerek,
                                    selectedItem = state.merekTerpilih,
                                    getLabel = { it.namaMerek },
                                    onItemSelected = { screenModel.onMerekSelected(it.namaMerek) },
                                    onTextChanged = { if (it.isEmpty()) screenModel.clearMerek() },
                                    satoshiMedium = satoshiMedium,
                                    isLoading = state.isLoadingMerek,
                                    isError = state.errorField == "merek"
                                )

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.weight(1.2f)) {
                                        SearchableDropdown(
                                            label = "Tipe",
                                            items = state.listTipe,
                                            selectedItem = state.tipeTerpilih,
                                            getLabel = { it.namaTipeKendaraan },
                                            onItemSelected = { screenModel.onTipeSelected(it.namaTipeKendaraan) },
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
                                            onItemSelected = { screenModel.onTahunSelected(it) },
                                            satoshiMedium = satoshiMedium,
                                            enabled = state.tipeTerpilih != null,
                                            isError = state.errorField == "tahun"
                                        )
                                    }
                                }


                                Spacer(modifier = Modifier.height(14.dp))

                                VehicleInputField(
                                    label = "Nomor Polisi",
                                    value = state.nopol,
                                    icon = Res.drawable.ic_plat_nomer,
                                    isError = state.errorField == "nopol",
                                    font = satoshiMedium,
                                    colors = commonColors,
                                    onValueChange = { screenModel.onNopolChange(it) }
                                )

                                VehicleInputField(
                                    label = "Nomor Rangka",
                                    value = state.noRangka,
                                    icon = Res.drawable.ic_nomer_rangka,
                                    isError = state.errorField == "rangka",
                                    font = satoshiMedium,
                                    colors = commonColors,
                                    modifier = Modifier.padding(bottom = 20.dp),
                                    onValueChange = { screenModel.onNoRangkaChange(it) }
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
                                        WarnaItemUI(
                                            warna = warna,
                                            isSelected = (warna == state.warnaTerpilih),
                                            font = satoshiMedium
                                        ) { screenModel.onWarnaSelected(warna) }
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                Button(
                                    onClick = { screenModel.validateAndSave { } },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
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
                                            "Simpan Kendaraan",
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
                            Icons.Default.ArrowBackIosNew,
                            "Kembali",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (state.isLoading) LoadingDialog(color = redPrimer)

            if (state.isSuccess) {
                AlertDialog(
                    onDismissRequest = { },
                    icon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                    },
                    title = {
                        Text(
                            "Berhasil!",
                            fontFamily = satoshiBold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            "Kendaraan berhasil ditambahkan ke akun anda.",
                            fontFamily = satoshiMedium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { navigator.pop() },
                                modifier = Modifier.fillMaxWidth(0.7f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Selesai",
                                    color = Color.White,
                                    fontFamily = satoshiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color.White
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
                        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
                        containerColor = redPrimer,
                        action = {
                            TextButton(onClick = { screenModel.clearError() }) {
                                Text(
                                    "OK",
                                    color = Color.White
                                )
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

    @Composable
    private fun VehicleInputField(
        label: String,
        value: String,
        icon: DrawableResource,
        isError: Boolean,
        font: FontFamily,
        colors: TextFieldColors,
        modifier: Modifier = Modifier.padding(bottom = 14.dp),
        onValueChange: (String) -> Unit
    ) {
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth(),
            label = { Text(label, fontFamily = font) },
            leadingIcon = { Icon(painterResource(icon), null, Modifier.size(24.dp)) },
            isError = isError, shape = RoundedCornerShape(10.dp), singleLine = true, colors = colors
        )
    }

    @Composable
    private fun WarnaItemUI(
        warna: WarnaKendaraanResponse,
        isSelected: Boolean,
        font: FontFamily,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier.widthIn(min = 100.dp).clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.DarkGray else Color.LightGray
            ),
            color = Color.White
        ) {
            Text(
                text = warna.namaWarna,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontFamily = font,
                color = if (isSelected) Color.DarkGray else Color.Gray,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}