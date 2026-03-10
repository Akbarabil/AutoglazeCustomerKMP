package com.example.autoglazecustomer.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.ui.checkvehicle.CheckVehicleScreen
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

data class OnboardingItem(
    val image: DrawableResource,
    val title: String,
    val description: String
)

class OnboardingScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        // Inisialisasi Settings untuk menyimpan status onboarding
        val settings = remember { Settings() }

        // Font & Colors
        val satoshiBold = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Bold))
        val myRed = Color(0xFFE30613)
        val greyText = Color(0xFF9E9E9E)
        val backgroundGrey = Color(0xFFFAFAFA)

        val items = listOf(
            OnboardingItem(
                Res.drawable.img_onboard1,
                "Berbagai Perawatan Mobil",
                "Autoglaze hadir dengan berbagai layanan perawatan kendaraan seperti carwash, detailing, fogging, hingga interior cleaning semuanya dalam satu aplikasi."
            ),
            OnboardingItem(
                Res.drawable.img_onboard2,
                "Pilih Membership Kamu",
                "Dapatkan keuntungan eksklusif dari berbagai jenis membership Autoglaze. Pilih sesuai kebutuhan mobil dan nikmati layanan prioritas!"
            ),
            OnboardingItem(
                Res.drawable.img_onboard3,
                "Layanan Home Service",
                "Mau perawatan mobil di rumah? Kami datang langsung ke lokasi anda! Praktis, fleksibel, dan tetap berkualitas."
            )
        )

        val pagerState = rememberPagerState(pageCount = { items.size })

        val smoothScrollSpec = tween<Float>(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )

        fun finishOnboarding() {
            settings.putBoolean("is_first_time_onboarding", false)
            navigator.replace(CheckVehicleScreen())
        }

        Box(modifier = Modifier.fillMaxSize().background(backgroundGrey)) {

            // --- LAYER 1: ViewPager (Konten Utama) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(items[page].image),
                        contentDescription = null,
                        modifier = Modifier.size(300.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = items[page].title,
                        fontFamily = satoshiBold,
                        fontSize = 24.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = items[page].description,
                        fontSize = 14.sp,
                        color = greyText,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // --- LAYER 2: HEADER (Nomor Halaman & Lewati) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 20.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${items.size}",
                    fontFamily = satoshiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    text = "Lewati",
                    fontFamily = satoshiBold,
                    fontSize = 18.sp,
                    color = myRed,
                    modifier = Modifier.clickable { finishOnboarding() }
                )
            }

            // --- LAYER 3: FOOTER (Navigasi Bawah) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp, start = 32.dp, end = 32.dp)
            ) {
                // Tombol Kembali
                if (pagerState.currentPage > 0) {
                    Text(
                        text = "Kembali",
                        fontFamily = satoshiBold,
                        fontSize = 18.sp,
                        color = greyText,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage - 1,
                                        animationSpec = smoothScrollSpec
                                    )
                                }
                            }
                    )
                }

                // Indicators (Dot)
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(items.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) myRed else Color.LightGray
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Tombol Lanjut / Mulai
                Text(
                    text = if (pagerState.currentPage == items.size - 1) "Mulai" else "Lanjut",
                    fontFamily = satoshiBold,
                    fontSize = 18.sp,
                    color = myRed,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            scope.launch {
                                if (pagerState.currentPage < items.size - 1) {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = smoothScrollSpec
                                    )
                                } else {
                                    finishOnboarding()
                                }
                            }
                        }
                )
            }
        }
    }
}