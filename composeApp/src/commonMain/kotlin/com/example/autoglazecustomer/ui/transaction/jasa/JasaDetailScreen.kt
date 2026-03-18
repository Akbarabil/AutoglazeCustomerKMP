package com.example.autoglazecustomer.ui.transaction.jasa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.jasa.LayananItem
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class JasaDetailScreen(
    private val item: LayananItem,
    private val isEligible: Boolean,
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA)

        // 1. Logika Harga
        val originalPrice = item.hargaJual
        val finalPrice = if (isEligible) {
            item.hargaJualMemberCarwash
        } else {
            when (vehicle.membershipStatusInt) {
                1 -> item.hargaJualMemberExpress
                2 -> item.hargaJual
                3 -> item.hargaDuaMember
                4 -> item.hargaVIP
                else -> item.hargaJual
            }
        }

        val cleanDescription = parseHtml(item.deskripsi ?: "Tidak ada deskripsi tambahan untuk layanan ini.")

        Box(modifier = Modifier.fillMaxSize().background(bgLight)) {

            // --- 1. BACKGROUND IMAGE DENGAN PARALLAX EFFECT ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // Sedikit lebih tinggi agar parallax lebih terasa
                    .graphicsLayer {
                        translationY = scrollState.value * 0.4f
                    }
            ) {
                AsyncImage(
                    model = item.gambarUrl,
                    contentDescription = item.namaProduk,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.dummy_promo_dark)
                )

                // Gradasi Atas (Untuk tombol back)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(0.7f), Color.Transparent)))
                        .align(Alignment.TopCenter)
                )

                // Gradasi Bawah (Agar menyatu halus dengan surface putih di bawahnya)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.3f))))
                        .align(Alignment.BottomCenter)
                )
            }

            // --- 2. KONTEN SCROLLABLE ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(340.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 500.dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White,
                    shadowElevation = 0.dp // Shadow dimatikan di sini karena ketutup background gelap
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)) {

                        // Pull bar (Indikator Swipe)
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Nama Produk
                        Text(
                            text = item.namaProduk,
                            fontFamily = satoshiBold,
                            fontSize = 26.sp, // Diperbesar sedikit
                            color = Color(0xFF1A1A1A),
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info Chips
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            InfoChip(icon = Icons.Default.Schedule, text = "${item.durasiMenit} Menit", font = satoshiMedium)

                            Spacer(modifier = Modifier.width(10.dp))

                            if (finalPrice < originalPrice) {
                                InfoChip(
                                    icon = Icons.Default.Verified,
                                    text = "Harga Member",
                                    font = satoshiMedium,
                                    bgColor = redPrimer.copy(alpha = 0.08f),
                                    textColor = redPrimer,
                                    iconColor = redPrimer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Judul Deskripsi
                        Text(
                            text = "Tentang Layanan",
                            fontFamily = satoshiBold,
                            color = Color.Black,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Isi Deskripsi
                        Text(
                            text = cleanDescription,
                            fontFamily = satoshiMedium,
                            color = Color(0xFF555555), // Warna teks dibikin lebih enak dibaca
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        )

                        // Ruang kosong untuk Bottom Bar
                        Spacer(modifier = Modifier.height(130.dp))
                    }
                }
            }

            // --- 3. FLOATING BACK BUTTON ---
            IconButton(
                onClick = { navigator.pop() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 8.dp)
                    .shadow(4.dp, CircleShape) // Tambahan shadow halus
                    .clip(CircleShape)
                    .background(Color.White)
                    .size(42.dp)
            ) {
                Icon(Icons.Default.ArrowBackIosNew, "Kembali", tint = Color.Black, modifier = Modifier.size(18.dp).padding(end = 2.dp))
            }

            // --- 4. SLEEK BOTTOM BAR (JOSJIS UPGRADE) ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = 0.8f), Color.White),
                            startY = 0f,
                            endY = 100f
                        )
                    )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Kiri: Info Harga
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Total Harga", fontFamily = satoshiMedium, color = Color.Gray, fontSize = 12.sp)

                            Row(verticalAlignment = Alignment.Bottom) {
                                val priceText = if (finalPrice == 0.0) "GRATIS" else formatRupiah(finalPrice)
                                Text(
                                    text = priceText,
                                    fontFamily = satoshiBold,
                                    color = if (finalPrice == 0.0) Color(0xFF4CAF50) else redPrimer,
                                    fontSize = 20.sp
                                )

                                if (finalPrice < originalPrice) {
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = formatRupiah(originalPrice),
                                        fontFamily = satoshiMedium,
                                        color = Color.LightGray,
                                        fontSize = 12.sp,
                                        textDecoration = TextDecoration.LineThrough,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                            }
                        }

                        // Kanan: Tombol Lebar Dinamis dengan Icon
                        Button(
                            onClick = {
                                // TODO: navigator.push(CartBottomSheetScreen(...))
                            },
                            modifier = Modifier.height(52.dp), // Lebar dibebaskan agar teks tidak terpotong
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp), // Padding kiri-kanan agar lega
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Keranjang", fontFamily = satoshiBold, fontSize = 15.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // --- HELPER COMPOSABLE UNTUK CHIP ---
    @Composable
    private fun InfoChip(
        icon: ImageVector,
        text: String,
        font: FontFamily,
        bgColor: Color = Color(0xFFF5F5F5),
        textColor: Color = Color.DarkGray,
        iconColor: Color = Color.Gray
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, fontFamily = font, color = textColor, fontSize = 13.sp)
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = kotlin.math.abs(amount).toLong()
        val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp $formattedNumber"
    }

    private fun parseHtml(html: String): String {
        return html
            .replace(Regex("<br\\s*?/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</p>\\s*<p>", RegexOption.IGNORE_CASE), "\n\n")
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .trim()
    }
}