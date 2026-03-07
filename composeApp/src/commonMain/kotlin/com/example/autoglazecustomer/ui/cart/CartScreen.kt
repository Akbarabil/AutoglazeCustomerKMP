package com.example.autoglazecustomer.ui.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.autoglazecustomer.data.model.HistoryItem
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.SearchableDropdown // Pastikan import ini sesuai path Anda
import io.github.alexzhirkevich.qrose.options.*
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.Font
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CartScreen(private val authService: AuthService) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { CartScreenModel(authService) }
        val redPrimer = Color(0xFFD53B1E)
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))

        // State untuk Dialog QR
        var showQrDialog by remember { mutableStateOf(false) }
        var selectedItemForQr by remember { mutableStateOf<HistoryItem?>(null) }

        Scaffold(
            containerColor = Color(0xFFFBFBFB),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Riwayat Pesanan",
                            fontFamily = satoshiMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

                SearchableDropdown(
                    label = "Pilih Kendaraan",
                    items = screenModel.vehicleList,
                    selectedItem = screenModel.selectedVehicle,
                    getLabel = { it.nopol ?: "-" },
                    onItemSelected = { vehicle ->
                        screenModel.selectedVehicle = vehicle
                        screenModel.fetchHistory(vehicle.idKendaraan)
                    },
                    satoshiMedium = satoshiMedium,
                    isLoading = false,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    }
                )

                Spacer(Modifier.height(24.dp))

                when {
                    screenModel.isHistoryLoading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator(color = redPrimer)
                        }
                    }
                    screenModel.selectedVehicle == null -> {
                        EmptyStateUI("Pilih plat kendaraan untuk melihat riwayat", satoshiMedium)
                    }
                    screenModel.historyList.isEmpty() -> {
                        EmptyStateUI("Tidak ada riwayat pesanan untuk kendaraan ini", satoshiMedium)
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(screenModel.historyList) { item ->
                                HistoryCardItem(item, redPrimer, satoshiMedium) {
                                    selectedItemForQr = item
                                    showQrDialog = true
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. QR Dialog ---
        if (showQrDialog && selectedItemForQr != null) {
            QrCodeDialog(
                item = selectedItemForQr!!,
                onDismiss = { showQrDialog = false },
                accentColor = redPrimer,
                satoshiMedium = satoshiMedium
            )
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Composable
    private fun QrCodeDialog(
        item: HistoryItem,
        onDismiss: () -> Unit,
        accentColor: Color,
        satoshiMedium: FontFamily
    ) {
        val payload = """{"kode_penjualan":"${item.kodePenjualan}","kode_cabang":"${item.kodeCabang}","tanggal":"${item.createdAt}"}"""
        val encodedPayload = Base64.encode(payload.encodeToByteArray())

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kode Penjualan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = satoshiMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    Text(
                        text = "Tunjukkan ke Kasir",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = satoshiMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = accentColor
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    val painter = rememberQrCodePainter(encodedPayload) {
                        shapes {
                            ball = QrBallShape.circle()
                            darkPixel = QrPixelShape.roundCorners()
                            frame = QrFrameShape.roundCorners(.25f)
                        }
                        colors { dark = QrBrush.solid(Color.Black) }
                    }

                    Box(
                        modifier = Modifier.size(210.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp)).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painter, contentDescription = "QR Code", modifier = Modifier.fillMaxSize())
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = item.kodePenjualan,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali", fontFamily = satoshiMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // --- CARD ITEM PREMIUM ---
    @Composable
    private fun HistoryCardItem(
        item: HistoryItem,
        accentColor: Color,
        satoshiMedium: FontFamily,
        onQrClick: () -> Unit
    ) {
        val borderColor = Color(0xFFF5F5F5)

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onQrClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.namaCabang.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = satoshiMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.kodePenjualan,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = accentColor.copy(alpha = 0.08f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCode, null, tint = accentColor, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = borderColor)
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Total Pembayaran", fontSize = 10.sp, color = Color.Gray, fontFamily = satoshiMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = formatRupiah(item.subtotal),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = accentColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = formatFullDate(item.createdAt),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyStateUI(msg: String, satoshiMedium: FontFamily) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(64.dp), Color.LightGray)
            Spacer(Modifier.height(16.dp))
            Text(msg, color = Color.Gray, fontFamily = satoshiMedium, textAlign = TextAlign.Center, fontSize = 14.sp)
        }
    }

    // --- HELPER FORMATTING ---
    private fun formatRupiah(amount: Double): String {
        val formatted = amount.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
        return "Rp$formatted"
    }

    private fun formatFullDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "-"
        return try {
            val parts = dateString.split(" ")
            val datePart = parts[0]
            val timePart = if (parts.size > 1) parts[1].substringBeforeLast(":") else ""
            val dateSplit = datePart.split("-")
            val monthName = when (dateSplit[1]) {
                "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
                "05" -> "Mei"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Agu"
                "09" -> "Sep"; "10" -> "Okt"; "11" -> "Nov"; "12" -> "Des"
                else -> dateSplit[1]
            }
            "${dateSplit[2]} $monthName ${dateSplit[0]}, $timePart"
        } catch (e: Exception) { dateString }
    }
}