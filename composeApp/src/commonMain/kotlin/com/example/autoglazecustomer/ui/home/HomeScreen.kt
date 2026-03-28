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
import androidx.compose.ui.window.DialogProperties
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
        var selectedBerita by remember { mutableStateOf<BeritaItem?>(null) }
        val redPrimer = Color(0xFFD53B1E)
        val deepRed = Color(0xFFA62B14)
        val headerGradient = listOf(redPrimer, deepRed)

        LaunchedEffect(Unit) {
            screenModel.loadAllHomeData()
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderSection(screenModel.userName, screenModel.userAvatar, headerGradient, satoshiBold, satoshiMedium)

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
                    HomeMenuItem(Res.drawable.ic_home_service, "Home Service", satoshiMedium, Modifier.weight(1f))
                    HomeMenuItem(Res.drawable.ic_home_layanan, "Transaksi", satoshiMedium, Modifier.weight(1f))
                    HomeMenuItem(Res.drawable.ic_home_location, "Lokasi Cabang", satoshiMedium, Modifier.weight(1f))
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
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(screenModel.promoList) { promo ->
                        PromoItem(promo, satoshiBold, satoshiMedium)
                    }
                }

                SectionHeader("Berita Terbaru", satoshiBold, true)
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

        if (selectedBerita != null) {
            BeritaDialog(
                berita = selectedBerita!!,
                onDismiss = { selectedBerita = null },
                bold = satoshiBold,
                medium = satoshiMedium,
                accent = redPrimer
            )
        }
    }

    @Composable
    private fun HeaderSection(
        name: String,
        avatarUrl: String?,
        gradientColors: List<Color>,
        boldFont: FontFamily,
        medFont: FontFamily
    ) {
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(top = statusBarHeight + 16.dp, start = 24.dp, end = 24.dp, bottom = 54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color.White.copy(0.2f),
                    border = BorderStroke(1.dp, Color.White.copy(0.3f))
                ) {
                    ProfessionalImage(
                        url = avatarUrl,
                        modifier = Modifier.fillMaxSize(),
                        errorRes = Res.drawable.ic_profile_white
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text("Selamat Datang,", color = Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = medFont)
                    Text(name, color = Color.White, fontSize = 18.sp, fontFamily = boldFont, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.background(Color.White.copy(0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }

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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(Modifier.padding(20.dp)) {
                Surface(
                    color = if (vehicle.isMembership == 1) Color(0xFFD53B1E) else Color(0xFF757575),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (vehicle.isMembership == 1) "MEMBER" else "REGULER",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White, fontSize = 10.sp, fontFamily = bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(vehicle.merek ?: "Mobil", fontSize = 20.sp, fontFamily = bold, maxLines = 1)
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
        Card(modifier = Modifier.width(280.dp).height(160.dp), shape = RoundedCornerShape(20.dp)) {
            Box(Modifier.fillMaxSize()) {
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
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    },
                    success = { SubcomposeAsyncImageContent() }
                )
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(promo.namaVoucher ?: "Promo", color = Color.White, fontSize = 16.sp, fontFamily = bold, maxLines = 1)
                    Text(promo.keterangan ?: "", color = Color.White.copy(0.8f), fontSize = 12.sp, fontFamily = medium, maxLines = 1)
                }
            }
        }
    }

    @Composable
    private fun BeritaItemUI(berita: BeritaItem, model: HomeScreenModel, bold: FontFamily, medium: FontFamily, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .width(240.dp)
                .clickable { onClick() },
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
    private fun BeritaDialog(berita: BeritaItem, onDismiss: () -> Unit, bold: FontFamily, medium: FontFamily, accent: Color) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = berita.judul ?: "Informasi Berita",
                    fontFamily = bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    ProfessionalImage(
                        url = berita.gambarUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = berita.deskripsi ?: "Detail berita ini belum tersedia untuk saat ini.",
                        fontFamily = medium,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tutup", fontFamily = bold, color = Color.White)
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    @Composable
    private fun ProfessionalImage(url: String?, modifier: Modifier = Modifier.fillMaxSize(), contentScale: ContentScale = ContentScale.Crop, errorRes: DrawableResource? = null) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            loading = { ShimmerBox() },
            error = {
                Box(Modifier.fillMaxSize().background(Color(0xFFF5F5F5)), Alignment.Center) {
                    if (errorRes != null) Image(painterResource(errorRes), null, Modifier.alpha(0.5f))
                    else Icon(Icons.Default.BrokenImage, null, tint = Color.LightGray)
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
    private fun HomeMenuItem(iconRes: DrawableResource, label: String, font: FontFamily, modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier.height(100.dp).clickable { },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
                Icon(painter = painterResource(iconRes), null, modifier = Modifier.size(32.dp), tint = Color.Unspecified)
                Spacer(modifier = Modifier.height(8.dp))
                Text(label, fontSize = 11.sp, fontFamily = font, textAlign = TextAlign.Center, lineHeight = 14.sp, color = Color.Black)
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