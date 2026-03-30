package com.example.autoglazecustomer.ui.transaction.produk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.dummy_promo_dark
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.manager.CartManager
import com.example.autoglazecustomer.data.manager.ItemCategory
import com.example.autoglazecustomer.data.model.transaction.CabangData
import com.example.autoglazecustomer.data.model.transaction.VehicleWithStatus
import com.example.autoglazecustomer.data.model.transaction.produk.ProdukItem
import com.example.autoglazecustomer.ui.transaction.components.AddToCartBottomSheet
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class ProdukDetailScreen(
    private val item: ProdukItem,
    private val finalPrice: Double,
    private val cabang: CabangData,
    private val vehicle: VehicleWithStatus
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()


        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)
        val bgLight = Color(0xFFF8F9FA)

        var showBottomSheet by remember { mutableStateOf(false) }

        val originalPrice = item.hargaNonMember

        val cleanDescription =
            parseHtml(item.deskripsi ?: "Tidak ada deskripsi tambahan untuk produk ini.")


        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 100.dp),
                    snackbar = { data ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, redPrimer.copy(alpha = 0.2f)),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = redPrimer.copy(alpha = 0.1f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = redPrimer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = data.visuals.message,
                                    fontFamily = satoshiMedium,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                )
            },
            containerColor = bgLight,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .graphicsLayer { translationY = scrollState.value * 0.4f }
                ) {
                    AsyncImage(
                        model = item.gambarUrl,
                        contentDescription = item.namaProduk,
                        modifier = Modifier.fillMaxSize().background(Color.White),
                        contentScale = ContentScale.Crop,
                        error = painterResource(Res.drawable.dummy_promo_dark)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(0.7f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .align(Alignment.TopCenter)
                    )
                }

                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    Spacer(modifier = Modifier.height(340.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 500.dp),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 24.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.width(40.dp).height(4.dp).clip(CircleShape)
                                    .background(Color(0xFFE0E0E0))
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                item.namaProduk,
                                fontFamily = satoshiBold,
                                fontSize = 26.sp,
                                color = Color(0xFF1A1A1A),
                                lineHeight = 34.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (finalPrice < originalPrice) {
                                Row(
                                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                        .background(redPrimer.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Harga Member",
                                        fontFamily = satoshiMedium,
                                        color = redPrimer,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "Detail Produk",
                                fontFamily = satoshiBold,
                                color = Color.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                cleanDescription,
                                fontFamily = satoshiMedium,
                                color = Color(0xFF555555),
                                fontSize = 15.sp,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(130.dp))
                        }
                    }
                }


                IconButton(
                    onClick = { if (navigator.canPop) navigator.pop() },
                    modifier = Modifier.statusBarsPadding().padding(start = 16.dp, top = 8.dp)
                        .shadow(4.dp, CircleShape).clip(CircleShape).background(Color.White)
                        .size(42.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        "Kembali",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp).padding(end = 2.dp)
                    )
                }


                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 0.8f),
                                Color.White
                            ), startY = 0f, endY = 100f
                        )
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .windowInsetsPadding(WindowInsets.navigationBars).shadow(
                            16.dp,
                            RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp), color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 12.dp,
                                top = 14.dp,
                                bottom = 14.dp
                            ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    "Total Harga",
                                    fontFamily = satoshiMedium,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        formatRupiah(finalPrice),
                                        fontFamily = satoshiBold,
                                        color = redPrimer,
                                        fontSize = 20.sp
                                    )
                                    if (finalPrice < originalPrice) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            formatRupiah(originalPrice),
                                            fontFamily = satoshiMedium,
                                            color = Color.LightGray,
                                            fontSize = 12.sp,
                                            textDecoration = TextDecoration.LineThrough,
                                            modifier = Modifier.padding(bottom = 3.dp)
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Tambah",
                                    fontFamily = satoshiBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                if (showBottomSheet) {
                    AddToCartBottomSheet(
                        namaProduk = item.namaProduk,
                        gambarUrl = item.gambarUrl,
                        finalPrice = finalPrice,
                        isMultiple = true,
                        onDismissRequest = { showBottomSheet = false },
                        onAddToCart = { quantity ->
                            showBottomSheet = false

                            val newCartItem = CartItem(
                                idProduk = item.idProduk,
                                idCabangItem = item.idCabangItem,
                                idMembership = null,
                                namaItem = item.namaProduk,
                                imageUrl = item.gambarUrl,
                                qty = quantity,
                                hargaUnit = finalPrice,
                                category = ItemCategory.PRODUK
                            )
                            CartManager.addItemToCart(newCartItem)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "$quantity ${item.namaProduk} masuk ke keranjang",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = kotlin.math.abs(amount).toLong()
        val formattedNumber =
            absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp $formattedNumber"
    }

    private fun parseHtml(html: String): String {
        return html.replace(Regex("<br\\s*?/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("</p>\\s*<p>", RegexOption.IGNORE_CASE), "\n\n")
            .replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ").replace("&amp;", "&").trim()
    }
}