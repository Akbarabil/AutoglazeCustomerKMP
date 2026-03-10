package com.example.autoglazecustomer.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.autoglaze_white
import autoglazecustomer.composeapp.generated.resources.bg_pattern_white
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import autoglazecustomer.composeapp.generated.resources.wave_black
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.ui.checkvehicle.CheckVehicleScreen
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val satoshiBold = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Bold))

        val settings = remember { Settings() }

        val scale = remember { Animatable(1.2f) }

        // Sequence Animasi: Zoom In -> Zoom Out -> Meledak
        LaunchedEffect(Unit) {
            // 1. ZOOM IN (Membesar pelan)
            scale.animateTo(
                targetValue = 1.7f,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
            )

            // 2. ZOOM OUT SEDIKIT (Efek bernapas/antisipasi)
            scale.animateTo(
                targetValue = 1.4f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )

            delay(150) // Jeda dramatis sebelum meledak

            // 3. MELEDAK (Full Zoom memenuhi layar)
            scale.animateTo(
                targetValue = 15f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )

            // 4. LOGIKA NAVIGASI JOSJIS
            // Cek apakah user sudah pernah melewati onboarding
            val isFirstTime = settings.getBoolean("is_first_time_onboarding", true)

            if (isFirstTime) {
                // Jika baru pertama kali, arahkan ke Onboarding
                navigator.replace(OnboardingScreen())
            } else {
                // Jika sudah pernah, langsung ke Check Vehicle (Home)
                navigator.replace(CheckVehicleScreen())
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {

            // LAYER 1: Background Pattern
            Image(
                painter = painterResource(Res.drawable.bg_pattern_white),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // LAYER 2: Wave Black (Animasi Scale)
            Image(
                painter = painterResource(Res.drawable.wave_black),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 532.dp, height = 469.dp)
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value
                    ),
                contentScale = ContentScale.Fit
            )

            // LAYER 3: Logo & Tagline
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.autoglaze_white),
                    contentDescription = "Autoglaze Logo",
                    modifier = Modifier.size(width = 196.dp, height = 55.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Drive it clean and healthy",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = satoshiBold
                )
            }
        }
    }
}