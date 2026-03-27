package com.example.autoglazecustomer.ui.transaction.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import org.jetbrains.compose.resources.Font

@Composable
fun FloatingCheckoutBar(
    visible: Boolean,
    totalQty: Int,
    totalPrice: Double,
    onClick: () -> Unit
) {
    val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
    val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
    val redPrimer = Color(0xFFD53B1E)

    // Animasi muncul dari bawah
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }), // Muncul dari bawah
        exit = slideOutVertically(targetOffsetY = { it })   // Hilang ke bawah
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = redPrimer.copy(alpha = 0.5f))
                    .clickable { onClick() },
                shape = RoundedCornerShape(16.dp),
                color = redPrimer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Kiri: Info Jumlah dan Harga
                    Column {
                        Text(
                            text = "$totalQty Item di Keranjang",
                            fontFamily = satoshiMedium,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatRupiah(totalPrice),
                            fontFamily = satoshiBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    // Kanan: Icon dan Teks Checkout
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Checkout",
                            fontFamily = satoshiBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCartCheckout,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatRupiah(amount: Double): String {
    val absoluteAmount = kotlin.math.abs(amount).toLong()
    val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
    val sign = if (amount < 0) "-" else ""
    return "${sign}Rp $formattedNumber"
}