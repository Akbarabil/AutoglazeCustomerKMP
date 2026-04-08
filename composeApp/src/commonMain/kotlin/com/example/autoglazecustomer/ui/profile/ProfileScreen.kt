package com.example.autoglazecustomer.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ag_coin_small
import autoglazecustomer.composeapp.generated.resources.ic_logout
import autoglazecustomer.composeapp.generated.resources.ic_profile_white
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.local.TokenManager
import com.example.autoglazecustomer.ui.KmpBackHandler
import com.example.autoglazecustomer.ui.login.LoginScreen
import com.example.autoglazecustomer.ui.profile.editprofile.EditProfileScreen
import com.example.autoglazecustomer.ui.profile.myvehicle.MyVehicleScreen
import com.example.autoglazecustomer.ui.profile.myvoucher.MyVoucherScreen
import com.example.autoglazecustomer.ui.tabs.HomeTab
import com.example.autoglazecustomer.ui.theme.AppFont
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ProfileScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val uriHandler = LocalUriHandler.current

        val satoshiBold = AppFont.satoshiBold()
        val satoshiMedium = AppFont.satoshiMedium()
        val redPrimer = Color(0xFFD53B1E)
        val deepRed = Color(0xFFA62B14)

        var showLogoutDialog by remember { mutableStateOf(false) }
        val shimmerBrush = rememberShimmerBrush()


        LaunchedEffect(Unit) {
            screenModel.fetchProfileAndPoints()
        }

        LaunchedEffect(screenModel.errorMessage) {
            if (screenModel.errorMessage != null) {
                kotlinx.coroutines.delay(4000)
                screenModel.clearError()
            }
        }

        KmpBackHandler {
            if (tabNavigator.current !is HomeTab) {
                tabNavigator.current = HomeTab()
            } else {
                if (navigator.canPop) navigator.pop()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Box {
                    if (screenModel.isLoading && screenModel.profileData == null) {
                        ProfileHeaderShimmer(shimmerBrush)
                    } else {
                        ProfileHeader(
                            name = screenModel.profileData?.nama ?: "User Autoglaze",
                            email = screenModel.profileData?.email ?: "...",
                            photoUrl = screenModel.profileData?.photo,
                            gradientColors = listOf(redPrimer, deepRed),
                            boldFont = satoshiBold,
                            medFont = satoshiMedium
                        )
                    }


                    IconButton(
                        onClick = {
                            if (tabNavigator.current !is HomeTab) {
                                tabNavigator.current = HomeTab()
                            } else if (navigator.canPop) {
                                navigator.pop()
                            }
                        },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 12.dp, top = 12.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {

                    ProfileGroup(title = "Point Umum", font = satoshiBold) {
                        if (screenModel.isLoading && screenModel.points == 0) {
                            Box(Modifier.fillMaxWidth().height(60.dp).background(shimmerBrush))
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Glaze Points",
                                    modifier = Modifier.weight(1f),
                                    fontFamily = satoshiMedium,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = formatNumber(screenModel.points),
                                    fontFamily = satoshiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Image(
                                    painterResource(Res.drawable.ag_coin_small),
                                    null,
                                    Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    ProfileGroup(title = "Info Pengguna", font = satoshiBold) {
                        ProfileMenuItem(Icons.Default.Edit, "Edit Profil", satoshiMedium) {
                            val mainNavigator = navigator.parent ?: navigator
                            val dataJson = screenModel.profileData?.let { Json.encodeToString(it) }
                            mainNavigator.push(EditProfileScreen(dataJson))
                        }

                        HorizontalDivider(
                            Modifier.padding(horizontal = 16.dp),
                            0.5.dp,
                            Color(0xFFEEEEEE)
                        )

                        ProfileMenuItem(
                            Icons.Default.DirectionsCar,
                            "Kendaraan Saya",
                            satoshiMedium
                        ) {
                            val mainNavigator = navigator.parent ?: navigator
                            mainNavigator.push(MyVehicleScreen())
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    ProfileGroup(title = "Benefit", font = satoshiBold) {
                        ProfileMenuItem(
                            Icons.Default.ConfirmationNumber,
                            "Voucher Kendaraan",
                            satoshiMedium
                        ) {
                            val mainNavigator = navigator.parent ?: navigator
                            mainNavigator.push(MyVoucherScreen())
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    ProfileGroup(title = "Bantuan", font = satoshiBold) {
                        ProfileMenuItem(
                            Icons.Default.SupportAgent,
                            "Hubungi Kami (WhatsApp)",
                            satoshiMedium
                        ) {
                            uriHandler.openUri("https://wa.me/628980136066")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Text(
                            text = "Log Out",
                            modifier = Modifier.padding(vertical = 18.dp),
                            textAlign = TextAlign.Center,
                            color = redPrimer,
                            fontFamily = satoshiBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            AnimatedVisibility(
                visible = screenModel.errorMessage != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                screenModel.errorMessage?.let { msg ->
                    androidx.compose.material3.Snackbar(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        containerColor = redPrimer,
                        action = {
                            TextButton(onClick = { screenModel.clearError() }) {
                                Text("OK", color = Color.White, fontFamily = satoshiBold)
                            }
                        }
                    ) {
                        Text(msg, color = Color.White, fontFamily = satoshiMedium)
                    }
                }
            }
        }


        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Image(
                        painterResource(Res.drawable.ic_logout),
                        null,
                        Modifier.size(70.dp)
                    )
                },
                title = {
                    Text(
                        "Konfirmasi Keluar",
                        fontFamily = satoshiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        "Apakah anda yakin ingin keluar dari akun Autoglaze?",
                        fontFamily = satoshiMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            TokenManager.clearAll()
                            navigator.parent?.replaceAll(LoginScreen())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Keluar sekarang", color = Color.White, fontFamily = satoshiBold) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal", color = Color.Gray, fontFamily = satoshiBold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }

    @Composable
    private fun ProfileHeader(
        name: String,
        email: String,
        photoUrl: String?,
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
            Column(
                modifier = Modifier
                    .padding(top = statusBarHeight + 40.dp, bottom = 40.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(105.dp),
                    shape = CircleShape,
                    color = Color.White.copy(0.2f),
                    border = BorderStroke(4.dp, Color.White.copy(0.4f))
                ) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(Res.drawable.ic_profile_white)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = boldFont,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
                Text(email, color = Color.White.copy(0.7f), fontSize = 14.sp, fontFamily = medFont)
            }
        }
    }

    @Composable
    private fun ProfileHeaderShimmer(brush: Brush) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(brush, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        )
    }

    @Composable
    private fun ProfileGroup(
        title: String,
        font: FontFamily,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Column {
            Text(
                title,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                fontSize = 15.sp,
                fontFamily = font,
                color = Color.Black
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) { Column { content() } }
        }
    }

    @Composable
    private fun ProfileMenuItem(
        icon: ImageVector,
        label: String,
        font: FontFamily,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontFamily = font,
                color = Color.Black
            )
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }

    @Composable
    private fun rememberShimmerBrush(): Brush {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition()
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        return Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim)
        )
    }

    private fun formatNumber(number: Int): String =
        number.toString().reversed().chunked(3).joinToString(".").reversed()
}