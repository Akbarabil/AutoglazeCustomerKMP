package com.example.autoglazecustomer.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.model.MenuGridCategory
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.transaction.jasa.JasaListScreen
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
                            Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                    windowInsets = WindowInsets.statusBars
                )
            },
            containerColor = Color.White
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    TransactionInfoBanner(cabang, vehicle, satoshiBold, satoshiMedium, redPrimer)
                }

                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text("Apa yang Anda butuhkan?", fontFamily = satoshiBold, fontSize = 20.sp, color = Color.Black)
                        Text("Pilih kategori layanan di bawah ini", fontFamily = satoshiMedium, fontSize = 14.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                    }
                }

                items(
                    items = menus,
                    key = { it.id },
                    span = { menu ->
                        GridItemSpan(if (menu.id == "MEMBER") 2 else 1)
                    }
                ) { menu ->
                    MenuGridItem(menu, satoshiBold, redPrimer) {
                        when (menu.id) {
                            "JASA" -> navigator.push(JasaListScreen(cabang, vehicle, authService))
                            "PRODUK" -> { /* TODO: Arahkan ke ProdukListScreen */ }
                            "MEMBER" -> { /* TODO: Arahkan ke MemberListScreen */ }
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
                        Icon(Icons.Default.Assignment, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(cabang.namaCabang, color = Color.White, fontFamily = bold, fontSize = 20.sp)
                    Text(vehicle.vehicle.merek ?: "Kendaraan", color = Color.White.copy(0.9f), fontFamily = med, fontSize = 14.sp)
                    Text(vehicle.vehicle.nopol ?: "-", color = Color.White.copy(0.7f), fontFamily = med, fontSize = 13.sp)
                }
            }
        }
    }

    @Composable
    private fun MenuGridItem(
        menu: MenuGridCategory,
        bold: FontFamily,
        accent: Color,
        onClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (menu.id == "MEMBER") 110.dp else 160.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            color = accent.copy(alpha = 0.04f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.15f))
        ) {
            if (menu.id == "MEMBER") {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(menu.icon),
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(20.dp))
                    Text(
                        text = menu.title,
                        fontFamily = bold,
                        fontSize = 17.sp, // Konsisten ukuran teks
                        color = Color.Black
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        painter = painterResource(menu.icon),
                        contentDescription = null,
                        modifier = Modifier.size(54.dp), // Konsisten besar ikon
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = menu.title,
                        fontFamily = bold,
                        fontSize = 17.sp, // Konsisten ukuran teks
                        lineHeight = 22.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}