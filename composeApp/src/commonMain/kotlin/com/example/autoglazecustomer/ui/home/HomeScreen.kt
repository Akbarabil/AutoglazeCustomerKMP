package com.example.autoglazecustomer.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.data.model.*
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class HomeScreen(private val authService: AuthService) : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel(authService) }
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        Scaffold(
            containerColor = Color(0xFFFBFBFB)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- 1. HEADER SECTION ---
                    HeaderSection(screenModel.userName, redPrimer, satoshiMedium)

                    // --- 2. SLIDER / BANNER (With LazyItemScope Fix) ---
                    if (screenModel.sliderList.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().height(190.dp).offset(y = (-25).dp)
                        ) {
                            items(screenModel.sliderList) { slider ->
                                SliderItemUI(slider.gambar)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // --- 3. MENU INFORMASI ---
                    Text(
                        text = "Menu Informasi",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        fontSize = 18.sp,
                        fontFamily = satoshiMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Gunakan Row dengan padding horizontal yang pas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar kotak
                    ) {
                        // Setiap item diberi weight(1f) agar terbagi rata secara otomatis
                        HomeMenuItem(
                            iconRes = Res.drawable.ic_home_service,
                            label = "Home Service",
                            font = satoshiMedium,
                            modifier = Modifier.weight(1f)
                        )
                        HomeMenuItem(
                            iconRes = Res.drawable.ic_home_layanan,
                            label = "Transaksi",
                            font = satoshiMedium,
                            modifier = Modifier.weight(1f)
                        )
                        HomeMenuItem(
                            iconRes = Res.drawable.ic_home_location,
                            label = "Lokasi Cabang",
                            font = satoshiMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // --- 4. MOBIL SAYA ---
                    SectionHeader("Mobil Saya", satoshiMedium)
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

                    // --- 5. PROMO BERLANGSUNG ---
                    SectionHeader("Promo Berlangsung", satoshiMedium)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(screenModel.promoList) { promo ->
                            PromoItem(promo, satoshiBold, satoshiMedium)
                        }
                    }

                    // --- 6. BERITA TERBARU ---
                    SectionHeader("Berita Terbaru", satoshiMedium, true)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 40.dp)
                    ) {
                        items(screenModel.beritaList) { berita ->
                            BeritaItemUI(berita, screenModel, satoshiBold, satoshiMedium)
                        }
                    }
                }

                // Global Loading Overlay
                if (screenModel.isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.White.copy(0.6f)), Alignment.Center) {
                        CircularProgressIndicator(color = redPrimer, strokeWidth = 3.dp)
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderSection(name: String, bgColor: Color, font: FontFamily) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(52.dp), shape = CircleShape, color = Color.White.copy(0.2f)) {
                    Image(painterResource(Res.drawable.ic_profile_white), null, Modifier.padding(12.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Selamat Datang,", color = Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = font)
                    Text(name, color = Color.White, fontSize = 20.sp, fontFamily = font, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)) {
                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                }
            }
        }
    }

    // FIX: Ditambahkan receiver LazyItemScope agar fillParentMaxWidth bisa diakses
    @Composable
    private fun LazyItemScope.SliderItemUI(imageUrl: String?) {
        Card(
            modifier = Modifier.fillParentMaxWidth(0.85f).fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            ProfessionalImage(imageUrl)
        }
    }

    @Composable
    private fun VehicleItem(vehicle: VehicleData, bold: FontFamily, medium: FontFamily) {
        Card(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(Modifier.padding(20.dp)) {
                Surface(
                    color = if (vehicle.hasMembership == 1) Color(0xFFD53B1E) else Color(0xFF757575),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (vehicle.hasMembership == 1) "MEMBER" else "REGULER",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White, fontSize = 10.sp, fontFamily = bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(vehicle.merek ?: "Tanpa Merek", fontSize = 20.sp, fontFamily = bold)
                Text("${vehicle.tipe} • ${vehicle.nopol}", fontSize = 14.sp, color = Color.Gray, fontFamily = medium)

                ProfessionalImage(
                    url = vehicle.gambarTipe,
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 12.dp),
                    contentScale = ContentScale.Fit,
                    errorRes = Res.drawable.sedan
                )
            }
        }
    }

    @Composable
    private fun PromoItem(promo: VoucherItem, bold: FontFamily, medium: FontFamily) {
        Card(
            modifier = Modifier.width(280.dp).height(160.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box {
                // LOGIC: Jika URL kosong, langsung gunakan Image lokal (mencegah crash di Coil)
                if (promo.gambarUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(Res.drawable.dummy_promo_dark),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = promo.gambarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { ShimmerBox() },
                        error = {
                            Image(
                                painter = painterResource(Res.drawable.dummy_promo_dark),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        success = { SubcomposeAsyncImageContent() }
                    )
                }

                // Overlay Gradasi
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(0.7f))
                            )
                        )
                )

                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(
                        text = promo.namaVoucher ?: "Promo Spesial",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = bold,
                        maxLines = 1
                    )
                    Text(
                        text = promo.keterangan ?: "Klik untuk detail lebih lanjut",
                        color = Color.White.copy(0.8f),
                        fontSize = 12.sp,
                        fontFamily = medium,
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    private fun BeritaItemUI(berita: BeritaItem, model: HomeScreenModel, bold: FontFamily, medium: FontFamily) {
        Card(
            modifier = Modifier.width(240.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
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
    private fun ProfessionalImage(
        url: String?,
        modifier: Modifier = Modifier.fillMaxSize(),
        contentScale: ContentScale = ContentScale.Crop,
        errorRes: DrawableResource? = null
    ) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            loading = { ShimmerBox() },
            error = {
                Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), Alignment.Center) {
                    if (errorRes != null) {
                        Image(painterResource(errorRes), null, Modifier.alpha(0.5f))
                    } else {
                        Icon(Icons.Default.BrokenImage, null, tint = Color.LightGray)
                    }
                }
            },
            success = { SubcomposeAsyncImageContent() }
        )
    }

    @Composable
    private fun ShimmerBox() {
        val transition = rememberInfiniteTransition()
        val translateAnim by transition.animateFloat(
            initialValue = 0f, targetValue = 1000f,
            animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart)
        )
        val brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0)),
            start = androidx.compose.ui.geometry.Offset(10f, 10f),
            end = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim)
        )
        Box(Modifier.fillMaxSize().background(brush))
    }

    @Composable
    private fun HomeMenuItem(
        iconRes: DrawableResource,
        label: String,
        font: FontFamily,
        modifier: Modifier = Modifier
    ) {
        // Surface sebagai kotak utama
        Surface(
            modifier = modifier
                .height(100.dp) // Atur tinggi kotak agar seragam
                .clickable { /* Action klik */ },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEEEEEE)), // Border abu-abu tipis
            shadowElevation = 0.dp // Matikan shadow jika ingin flat seperti gambar
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontFamily = font,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String, font: FontFamily, showAll: Boolean = false) {
        Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(title, fontSize = 18.sp, fontFamily = font, fontWeight = FontWeight.Bold)
            if (showAll) Text("Lihat Semua", fontSize = 13.sp, color = Color(0xFFD53B1E), fontFamily = font, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun EmptyState(msg: String, font: FontFamily) {
        Text(msg, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 14.sp, color = Color.Gray, fontFamily = font)
    }
}
