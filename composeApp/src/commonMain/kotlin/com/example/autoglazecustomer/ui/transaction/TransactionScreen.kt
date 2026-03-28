package com.example.autoglazecustomer.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMeDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.rememberLocationService
import com.example.autoglazecustomer.ui.rememberPermissionHandler
import com.example.autoglazecustomer.ui.tabs.HomeTab
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font

class TransactionScreen(private val authService: AuthService) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { TransactionScreenModel(authService) }
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val locationService = rememberLocationService()
        val scope = rememberCoroutineScope()

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)


        var isPermissionDenied by remember { mutableStateOf(false) }


        val onBackPress = {
            if (tabNavigator.current !is HomeTab) {
                tabNavigator.current = HomeTab()
            } else if (navigator.canPop) {
                navigator.pop()
            }
        }


        val permissionHandler = rememberPermissionHandler { isGranted ->
            if (isGranted) {
                isPermissionDenied = false
                scope.launch {
                    screenModel.isLoading = true
                    val location = locationService.getCurrentLocation()
                    if (location != null) {
                        screenModel.fetchCabangTerdekat(location.latitude, location.longitude)
                    } else {
                        screenModel.isLoading = false
                        screenModel.errorMessage = "Gagal mengambil lokasi. Coba lagi."
                    }
                }
            } else {

                isPermissionDenied = true
                screenModel.isLoading = false
                screenModel.errorMessage =
                    "Izin lokasi ditolak. Aplikasi butuh izin untuk mencari cabang."
            }
        }


        fun loadData() {
            if (permissionHandler.isPermissionGranted()) {
                isPermissionDenied = false
                scope.launch {
                    screenModel.isLoading = true
                    screenModel.errorMessage = null
                    val location = locationService.getCurrentLocation()
                    if (location != null) {
                        screenModel.fetchCabangTerdekat(location.latitude, location.longitude)
                    } else {
                        screenModel.isLoading = false
                        screenModel.errorMessage = "Gagal mendapatkan lokasi. Pastikan GPS aktif."
                    }
                }
            } else {

                isPermissionDenied = true
                screenModel.isLoading = false
                screenModel.errorMessage = "Akses lokasi diperlukan untuk mencari cabang terdekat."

                permissionHandler.askPermission()
            }
        }


        LaunchedEffect(Unit) {
            loadData()
        }


        KmpBackHandler {
            onBackPress()
        }

        Scaffold(
            topBar = {

                CenterAlignedTopAppBar(
                    title = {
                        Text("Pilih Cabang", fontFamily = satoshiBold, fontSize = 18.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPress) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = "Kembali",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ),
                    windowInsets = WindowInsets.statusBars
                )
            },
            containerColor = Color(0xFFFBFBFB)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {


                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cabang Terdekat", fontFamily = satoshiBold, fontSize = 14.sp)

                        IconButton(
                            onClick = { loadData() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                null,
                                tint = redPrimer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = screenModel.searchQuery,
                        onValueChange = { screenModel.searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Cari cabang Autoglaze...",
                                fontSize = 14.sp,
                                fontFamily = satoshiMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (screenModel.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { screenModel.searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = redPrimer,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            cursorColor = redPrimer
                        )
                    )
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .size(40.dp, 4.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )

                        Box(modifier = Modifier.fillMaxSize()) {
                            when {
                                screenModel.isLoading -> {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        Arrangement.Center,
                                        Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            color = redPrimer,
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            "Mencari lokasi...",
                                            fontFamily = satoshiMedium,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                screenModel.errorMessage != null -> {
                                    ErrorStateUI(
                                        msg = screenModel.errorMessage!!,
                                        med = satoshiMedium,
                                        accent = redPrimer,
                                        isPermissionDenied = isPermissionDenied,
                                        onSettingsClick = { permissionHandler.openAppSettings() },
                                        onRetry = { loadData() }
                                    )
                                }

                                screenModel.filteredCabang.isEmpty() -> {
                                    EmptyStateUI("Tidak ada cabang yang cocok", satoshiMedium)
                                }

                                else -> {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(24.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(screenModel.filteredCabang) { cabang ->
                                            CabangCardItem(
                                                cabang,
                                                redPrimer,
                                                satoshiBold,
                                                satoshiMedium
                                            ) {
                                                navigator.parent?.push(
                                                    VehicleSelectionScreen(
                                                        cabang = cabang
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CabangCardItem(
        cabang: CabangData,
        accent: Color,
        bold: FontFamily,
        med: FontFamily,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF5F5F5))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = accent.copy(0.08f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cabang.namaCabang,
                        fontFamily = bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = cabang.alamat ?: "Alamat tidak tersedia",
                        fontFamily = med,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    Surface(
                        modifier = Modifier.padding(top = 6.dp),
                        color = Color(0xFFF9F9F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                null,
                                tint = accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = cabang.distanceKm?.let {
                                    val rounded = (it * 100).toInt() / 100.0
                                    "$rounded km"
                                } ?: "-",
                                fontFamily = bold,
                                fontSize = 11.sp,
                                color = accent
                            )
                        }
                    }
                }

                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    @Composable
    private fun ErrorStateUI(
        msg: String,
        med: FontFamily,
        accent: Color,
        isPermissionDenied: Boolean = false,
        onSettingsClick: () -> Unit = {},
        onRetry: () -> Unit
    ) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp), accent)
            Spacer(Modifier.height(16.dp))
            Text(
                msg,
                color = Color.Gray,
                fontFamily = med,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))

            if (isPermissionDenied) {

                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Buka Pengaturan", color = Color.White, fontFamily = med)
                }
                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onRetry) {
                    Text("Saya sudah izinkan", color = accent, fontFamily = med)
                }
            } else {

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Coba Lagi", color = Color.White, fontFamily = med)
                }
            }
        }
    }

    @Composable
    private fun EmptyStateUI(msg: String, med: FontFamily) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.NearMeDisabled, null, Modifier.size(64.dp), Color(0xFFEEEEEE))
            Spacer(Modifier.height(16.dp))
            Text(msg, color = Color.LightGray, fontFamily = med, textAlign = TextAlign.Center)
        }
    }
}