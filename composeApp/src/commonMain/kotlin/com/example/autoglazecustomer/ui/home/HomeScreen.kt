package com.example.autoglazecustomer.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.dummy_promo_dark
import autoglazecustomer.composeapp.generated.resources.ic_home_layanan
import autoglazecustomer.composeapp.generated.resources.ic_home_location
import autoglazecustomer.composeapp.generated.resources.ic_home_service
import autoglazecustomer.composeapp.generated.resources.ic_profile_white
import autoglazecustomer.composeapp.generated.resources.sedan
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.example.autoglazecustomer.data.model.BeritaItem
import com.example.autoglazecustomer.data.model.VehicleData
import com.example.autoglazecustomer.data.model.VoucherItem
import com.example.autoglazecustomer.ui.rememberLocationService
import com.example.autoglazecustomer.ui.rememberPermissionHandler
import com.example.autoglazecustomer.ui.tabs.TransactionTab
import com.example.autoglazecustomer.ui.theme.AppFont
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val tabNavigator = LocalTabNavigator.current
        val screenModel = getScreenModel<HomeScreenModel>()
        val scope = rememberCoroutineScope()
        val locationService = rememberLocationService()

        val satoshiBold = AppFont.satoshiBold()
        val satoshiMedium = AppFont.satoshiMedium()
        var selectedBerita by remember { mutableStateOf<BeritaItem?>(null) }

        var showHomeServiceDialog by remember { mutableStateOf(false) }
        var showLocationDialog by remember { mutableStateOf(false) }
        var isLocationButtonClicked by remember { mutableStateOf(false) }

        val redPrimer = Color(0xFFD53B1E)
        val deepRed = Color(0xFFA62B14)
        val headerGradient = listOf(redPrimer, deepRed)

        val permissionHandler = rememberPermissionHandler { isGranted ->
            if (isLocationButtonClicked) {
                if (isGranted) {
                    scope.launch {
                        screenModel.isCabangLoading = true
                        showLocationDialog = true

                        val location = locationService.getCurrentLocation()
                        if (location != null) {
                            screenModel.fetchClosestCabang(location.latitude, location.longitude)
                        } else {
                            screenModel.isCabangLoading = false
                            screenModel.cabangErrorMessage = "Gagal mendapat lokasi GPS. Pastikan GPS aktif."
                        }
                    }
                } else {
                    screenModel.cabangErrorMessage = "Akses lokasi ditolak. Tidak bisa mencari cabang."
                    showLocationDialog = true
                }

                isLocationButtonClicked = false
            }
        }

        LaunchedEffect(Unit) {
            screenModel.loadAllHomeData()
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {

            when {
                screenModel.isLoading && screenModel.sliderList.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = redPrimer)
                    }
                }

                screenModel.errorMessage != null && screenModel.sliderList.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFE0E0E0)
                        )
                        Text(
                            text = screenModel.errorMessage!!,
                            color = Color.Gray,
                            fontFamily = satoshiMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                        )
                        Button(
                            onClick = { screenModel.loadAllHomeData() },
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Coba Lagi", fontFamily = satoshiBold, color = Color.White)
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        HeaderSection(
                            screenModel.userName,
                            screenModel.userAvatar,
                            headerGradient,
                            satoshiBold,
                            satoshiMedium
                        )

                        if (screenModel.sliderList.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth().height(180.dp).offset(y = (-35).dp)
                            ) {
                                items(screenModel.sliderList) { slider ->
                                    SliderItemUI(slider.gambar)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Text(
                            text = "Layanan Utama",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            fontSize = 17.sp,
                            fontFamily = satoshiBold,
                            color = Color.Black
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HomeMenuItem(
                                Res.drawable.ic_home_service,
                                "Home Service",
                                satoshiMedium,
                                Modifier.weight(1f)
                            ) {
                                showHomeServiceDialog = true
                            }

                            HomeMenuItem(
                                Res.drawable.ic_home_layanan,
                                "Transaksi",
                                satoshiMedium,
                                Modifier.weight(1f)
                            ) {
                                tabNavigator.current = TransactionTab()
                            }

                            HomeMenuItem(
                                Res.drawable.ic_home_location,
                                "Lokasi Cabang",
                                satoshiMedium,
                                Modifier.weight(1f)
                            ) {
                                isLocationButtonClicked = true

                                if (permissionHandler.isPermissionGranted()) {
                                    scope.launch {
                                        screenModel.isCabangLoading = true
                                        showLocationDialog = true
                                        val location = locationService.getCurrentLocation()
                                        if (location != null) {
                                            screenModel.fetchClosestCabang(location.latitude, location.longitude)
                                        } else {
                                            screenModel.isCabangLoading = false
                                            screenModel.cabangErrorMessage = "Gagal mengambil GPS."
                                        }
                                        isLocationButtonClicked = false
                                    }
                                } else {
                                    permissionHandler.askPermission()
                                }
                            }
                        }

                        SectionHeader("Mobil Saya", satoshiBold)
                        if (screenModel.vehicleList.isEmpty() && !screenModel.isLoading) {
                            EmptyState("Belum ada kendaraan terdaftar", satoshiMedium)
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(screenModel.vehicleList) { vehicle ->
                                    VehicleItem(vehicle, satoshiBold, satoshiMedium)
                                }
                            }
                        }

                        SectionHeader("Promo Berlangsung", satoshiBold)
                        if (screenModel.promoList.isEmpty() && !screenModel.isLoading) {
                            EmptyState("Belum ada promo saat ini", satoshiMedium)
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(screenModel.promoList) { promo ->
                                    PromoItem(promo, satoshiBold, satoshiMedium)
                                }
                            }
                        }

                        SectionHeader("Berita Terbaru", satoshiBold)
                        if (screenModel.beritaList.isEmpty() && !screenModel.isLoading) {
                            EmptyState("Belum ada berita terbaru", satoshiMedium)
                            Spacer(modifier = Modifier.height(100.dp))
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(bottom = 100.dp)
                            ) {
                                items(screenModel.beritaList) { berita ->
                                    BeritaItemUI(berita, screenModel, satoshiBold, satoshiMedium) {
                                        selectedBerita = berita
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedBerita != null) {
            BeritaDialog(
                berita = selectedBerita!!,
                onDismiss = { selectedBerita = null },
                bold = satoshiBold,
                medium = satoshiMedium,
                accent = redPrimer
            )
        }

        if (showHomeServiceDialog) {
            AlertDialog(
                onDismissRequest = { showHomeServiceDialog = false },
                title = { Text("Segera Hadir", fontFamily = satoshiBold) },
                text = { Text("Layanan Home Service belum tersedia untuk saat ini. Nantikan update terbaru dan nikmati kemudahan dari rumah anda", fontFamily = satoshiMedium) },
                confirmButton = {
                    Button(onClick = { showHomeServiceDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = redPrimer)) {
                        Text("Oke, Mengerti", fontFamily = satoshiBold, color = Color.White)
                    }
                },
                containerColor = Color.White
            )
        }

        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Cabang Terdekat", fontFamily = satoshiBold) },
                text = {
                    when {
                        screenModel.isCabangLoading -> {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = redPrimer)
                                Spacer(Modifier.height(12.dp))
                                Text("Melacak lokasi anda...", fontFamily = satoshiMedium, fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        screenModel.cabangErrorMessage != null -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CloudOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = Color(0xFFE0E0E0)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = screenModel.cabangErrorMessage!!,
                                    fontFamily = satoshiMedium,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        screenModel.closestCabang != null -> {
                            val cabang = screenModel.closestCabang!!
                            Column {
                                Text("Berdasarkan lokasi anda saat ini:", fontFamily = satoshiMedium, fontSize = 14.sp, color = Color.Gray)
                                Spacer(Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = redPrimer.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, redPrimer.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(cabang.namaCabang, fontFamily = satoshiBold, fontSize = 16.sp, color = Color.Black)
                                        Spacer(Modifier.height(4.dp))
                                        Text(cabang.alamat ?: "Alamat tidak tersedia", fontFamily = satoshiMedium, fontSize = 13.sp, color = Color.DarkGray)
                                        Spacer(Modifier.height(8.dp))

                                        val jarakText = cabang.distanceKm?.let {
                                            val rounded = (it * 100).toInt() / 100.0
                                            "~ $rounded KM"
                                        } ?: "-"

                                        Text("Jarak: $jarakText", fontFamily = satoshiBold, fontSize = 13.sp, color = redPrimer)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLocationDialog = false
                            screenModel.cabangErrorMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                    ) {
                        Text("Tutup", fontFamily = satoshiBold, color = Color.White)
                    }
                },
                containerColor = Color.White
            )
        }
    }

    @Composable
    private fun HeaderSection(name: String, avatarUrl: String?, gradientColors: List<Color>, boldFont: FontFamily, medFont: FontFamily) {
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        Box(modifier = Modifier.fillMaxWidth().background(brush = Brush.verticalGradient(gradientColors), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
            Row(modifier = Modifier.padding(top = statusBarHeight + 16.dp, start = 24.dp, end = 24.dp, bottom = 54.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color.White.copy(0.2f), border = BorderStroke(1.dp, Color.White.copy(0.3f))) {
                    ProfessionalImage(url = avatarUrl, modifier = Modifier.fillMaxSize(), errorRes = Res.drawable.ic_profile_white)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Selamat Datang,", color = Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = medFont)
                    Text(name, color = Color.White, fontSize = 18.sp, fontFamily = boldFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(0.15f), CircleShape)) {
                    Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }

    @Composable
    private fun LazyItemScope.SliderItemUI(imageUrl: String?) {
        Card(modifier = Modifier.fillParentMaxWidth(0.85f).fillMaxHeight(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(6.dp)) {
            ProfessionalImage(imageUrl)
        }
    }

    @Composable
    private fun VehicleItem(vehicle: VehicleData, bold: FontFamily, medium: FontFamily) {
        Card(modifier = Modifier.width(300.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
            Column(Modifier.padding(20.dp)) {
                Surface(color = if (vehicle.isMembership == 1) Color(0xFFD53B1E) else Color(0xFF757575), shape = RoundedCornerShape(6.dp)) {
                    Text(text = if (vehicle.isMembership == 1) "MEMBER" else "REGULER", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = Color.White, fontSize = 10.sp, fontFamily = bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(vehicle.merek ?: "Mobil", fontSize = 20.sp, fontFamily = bold, maxLines = 1)
                Text("${vehicle.tipe} • ${vehicle.nopol}", fontSize = 14.sp, color = Color.Gray, fontFamily = medium)
                ProfessionalImage(url = vehicle.gambarTipe, modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 12.dp), contentScale = ContentScale.Fit, errorRes = Res.drawable.sedan)
            }
        }
    }

    @Composable
    private fun PromoItem(promo: VoucherItem, bold: FontFamily, medium: FontFamily) {
        Card(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                    SubcomposeAsyncImage(
                        model = promo.gambarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop, loading = { ShimmerBox() },
                        error = { Image(painterResource(Res.drawable.dummy_promo_dark), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) },
                        success = { SubcomposeAsyncImageContent() }
                    )
                }
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = promo.namaVoucher ?: "Promo Spesial",
                        fontFamily = bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(12.dp))

                    val diskonMember = if ((promo.presentaseMember ?: 0.0) > 0) "${promo.presentaseMember?.toInt()}% OFF" else formatRupiah(promo.potHargaMember ?: 0.0)
                    val diskonNonMember = if ((promo.presentaseNonMember ?: 0.0) > 0) "${promo.presentaseNonMember?.toInt()}% OFF" else formatRupiah(promo.potHargaNonMember ?: 0.0)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Member", fontFamily = medium, fontSize = 12.sp, color = Color.Gray)
                        Text(diskonMember, fontFamily = bold, fontSize = 13.sp, color = Color(0xFFD53B1E))
                    }
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Reguler", fontFamily = medium, fontSize = 12.sp, color = Color.Gray)
                        Text(diskonNonMember, fontFamily = bold, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }

    @Composable
    private fun BeritaItemUI(berita: BeritaItem, model: HomeScreenModel, bold: FontFamily, medium: FontFamily, onClick: () -> Unit) {
        Card(modifier = Modifier.width(240.dp).clickable { onClick() }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
            Column {
                ProfessionalImage(berita.gambarUrl, modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)))
                Column(Modifier.padding(16.dp)) {
                    Text(berita.judul ?: "", fontSize = 15.sp, fontFamily = bold, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    Text(model.formatDate(berita.updatedAt), fontSize = 12.sp, color = Color.Gray, fontFamily = medium)
                }
            }
        }
    }

    @Composable
    private fun BeritaDialog(berita: BeritaItem, onDismiss: () -> Unit, bold: FontFamily, medium: FontFamily, accent: Color) {
        AlertDialog(
            onDismissRequest = onDismiss, containerColor = Color.White, shape = RoundedCornerShape(24.dp),
            title = { Text(berita.judul ?: "Informasi", fontFamily = bold, fontSize = 18.sp, color = Color.Black) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    ProfessionalImage(url = berita.gambarUrl, modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(berita.deskripsi ?: "Detail belum tersedia.", fontFamily = medium, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                }
            },
            confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = accent)) { Text("Tutup", fontFamily = bold, color = Color.White) } },
        )
    }

    @Composable
    private fun ProfessionalImage(url: String?, modifier: Modifier = Modifier.fillMaxSize(), contentScale: ContentScale = ContentScale.Crop, errorRes: DrawableResource? = null) {
        SubcomposeAsyncImage(model = url, contentDescription = null, modifier = modifier, contentScale = contentScale, loading = { ShimmerBox() }, error = { Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), Alignment.Center) { if (errorRes != null) Image(painterResource(errorRes), null, Modifier.alpha(0.5f)) else Icon(Icons.Default.BrokenImage, null, tint = Color.LightGray) } }, success = { SubcomposeAsyncImageContent() })
    }

    @Composable
    private fun ShimmerBox() {
        val transition = rememberInfiniteTransition()
        val translateAnim by transition.animateFloat(initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart))
        val brush = Brush.linearGradient(colors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0)), start = androidx.compose.ui.geometry.Offset(10f, 10f), end = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim))
        Box(Modifier.fillMaxSize().background(brush))
    }

    @Composable
    private fun HomeMenuItem(
        iconRes: DrawableResource,
        label: String,
        font: FontFamily,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = modifier
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(painter = painterResource(iconRes), null, modifier = Modifier.size(32.dp), tint = Color.Unspecified)
                Spacer(modifier = Modifier.height(8.dp))
                Text(label, fontSize = 11.sp, fontFamily = font, textAlign = TextAlign.Center, lineHeight = 14.sp, color = Color.Black)
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String, font: FontFamily) {
        Box(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp)) {
            Text(title, fontSize = 18.sp, fontFamily = font, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun EmptyState(msg: String, font: FontFamily) {
        Text(msg, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 14.sp, color = Color.Gray, fontFamily = font)
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = abs(amount).toLong()
        val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        return "Rp $formattedNumber"
    }
}