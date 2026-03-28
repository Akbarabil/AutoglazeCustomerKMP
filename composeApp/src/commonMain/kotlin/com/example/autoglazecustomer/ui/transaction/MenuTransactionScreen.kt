package com.example.autoglazecustomer.ui.transaction

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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_carwash_category
import autoglazecustomer.composeapp.generated.resources.ic_daftar_member
import autoglazecustomer.composeapp.generated.resources.ic_produk
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.MenuGridCategory
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.transaction.jasa.JasaListScreen
import com.example.autoglazecustomer.ui.transaction.membership.MembershipListScreen
import com.example.autoglazecustomer.ui.transaction.produk.ProdukListScreen
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class MenuTransactionScreen(
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val authService = remember { AuthService() }

        val menus = remember {
            val list = mutableListOf(
                MenuGridCategory("JASA", "Jasa Layanan", Res.drawable.ic_carwash_category),
                MenuGridCategory("PRODUK", "Produk", Res.drawable.ic_produk)
            )

            if (vehicle.membershipStatusInt != 4) {
                val memberLabel = when (vehicle.membershipStatusInt) {
                    0 -> "Daftar Member"
                    1, 2, 3 -> "Upgrade Member"
                    else -> "Daftar Member"
                }
                list.add(MenuGridCategory("MEMBER", memberLabel, Res.drawable.ic_daftar_member))
            }
            list
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Menu Transaksi", fontFamily = satoshiBold, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                    windowInsets = WindowInsets.statusBars
                )
            },
            containerColor = Color(0xFFFBFBFB)
        ) { padding ->

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TransactionInfoBanner(cabang, vehicle, satoshiBold, satoshiMedium, redPrimer)
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Column {
                        Text(
                            "Apa yang Anda butuhkan?",
                            fontFamily = satoshiBold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Text(
                            "Pilih kategori layanan di bawah ini",
                            fontFamily = satoshiMedium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                items(items = menus, key = { it.id }) { menu ->
                    val subtitle = when (menu.id) {
                        "JASA" -> "Pilih layanan cuci dan detailing"
                        "PRODUK" -> "Beli perlengkapan & produk perawatan"
                        "MEMBER" -> "Nikmati harga khusus & promo"
                        else -> ""
                    }

                    MenuListItem(
                        menu = menu,
                        subtitle = subtitle,
                        bold = satoshiBold,
                        med = satoshiMedium,
                        accent = redPrimer
                    ) {
                        when (menu.id) {
                            "JASA" -> navigator.push(JasaListScreen(cabang, vehicle, authService))
                            "PRODUK" -> navigator.push(
                                ProdukListScreen(
                                    cabang,
                                    vehicle,
                                    authService
                                )
                            )

                            "MEMBER" -> navigator.push(
                                MembershipListScreen(
                                    cabang,
                                    vehicle,
                                    authService
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TransactionInfoBanner(
        cabang: CabangData,
        vehicle: VehicleWithStatus,
        bold: FontFamily,
        med: FontFamily,
        accent: Color
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = accent.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(accent, Color(0xFF9E2A15))))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Assignment,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        cabang.namaCabang,
                        color = Color.White,
                        fontFamily = bold,
                        fontSize = 20.sp
                    )
                    Text(
                        vehicle.vehicle.merek ?: "Kendaraan",
                        color = Color.White.copy(0.9f),
                        fontFamily = med,
                        fontSize = 14.sp
                    )
                    Text(
                        vehicle.vehicle.nopol ?: "-",
                        color = Color.White.copy(0.7f),
                        fontFamily = med,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun MenuListItem(
        menu: MenuGridCategory,
        subtitle: String,
        bold: FontFamily,
        med: FontFamily,
        accent: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(20.dp))
                .clickable { onClick() },
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = CircleShape,
                        color = accent.copy(alpha = 0.08f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(menu.icon),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = menu.title,
                            fontFamily = bold,
                            fontSize = 17.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontFamily = med,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFFD3D3D3),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}