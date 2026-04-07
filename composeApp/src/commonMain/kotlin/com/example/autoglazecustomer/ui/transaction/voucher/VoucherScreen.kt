package com.example.autoglazecustomer.ui.transaction.voucher

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.autoglazecustomer.data.manager.CartItem
import com.example.autoglazecustomer.data.model.transaction.VoucherUIModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.core.parameter.parametersOf

class VoucherScreen(
    private val idKendaraan: Int,
    private val cartItems: List<CartItem>,
    private val isMember: Boolean
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<VoucherScreenModel> { parametersOf(idKendaraan, cartItems) }
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        val tabs = listOf("Voucher Umum", "Voucher Kendaraan")
        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            containerColor = Color(0xFFF8F9FA),
            topBar = {
                Surface(color = Color.White, shadowElevation = 2.dp) {
                    Column {
                        CenterAlignedTopAppBar(
                            title = { Text("Pilih Voucher", fontFamily = satoshiBold, fontSize = 18.sp, color = Color.Black) },
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(Icons.Default.ArrowBackIosNew, null, Modifier.size(20.dp), tint = Color.Black)
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                        )

                        TabRow(
                            selectedTabIndex = pagerState.currentPage,
                            containerColor = Color.White,
                            contentColor = redPrimer,
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    color = redPrimer
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                                    text = {
                                        Text(
                                            text = title,
                                            fontFamily = if (pagerState.currentPage == index) satoshiBold else satoshiMedium,
                                            color = if (pagerState.currentPage == index) redPrimer else Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    shadowElevation = 16.dp, color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp).windowInsetsPadding(WindowInsets.navigationBars)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${screenModel.selectedVouchers.size} Voucher Dipilih", fontFamily = satoshiBold, color = Color.Black)
                        }

                        if (!screenModel.validationMessage.isNullOrEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(screenModel.validationMessage!!, fontFamily = satoshiMedium, fontSize = 12.sp, color = redPrimer)
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                screenModel.confirmSelection()
                                navigator.pop()
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer)
                        ) {
                            Text("Gunakan Voucher", fontFamily = satoshiBold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        ) { padding ->
            if (screenModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = redPrimer)
                }
            } else if (screenModel.errorMessage != null) {
                val safeErrorMsg = screenModel.errorMessage!!
                    .replace("null", "Koneksi terputus", ignoreCase = true)
                    .ifBlank { "Gagal memuat daftar voucher" }
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = safeErrorMsg,
                        fontFamily = satoshiMedium,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { screenModel.fetchVouchers() },
                        colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Coba Lagi", fontFamily = satoshiBold, color = Color.White)
                    }
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    val voucherList = if (page == 0) screenModel.umumVouchers else screenModel.kendaraanVouchers

                    if (voucherList.isEmpty()) {
                        EmptyVoucherState(satoshiMedium)
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = voucherList,
                                key = { it.idVoucher }
                            ) { voucher ->
                                val isSelected = screenModel.selectedVouchers.any { it.idVoucher == voucher.idVoucher }
                                VoucherCardItem(
                                    voucher = voucher,
                                    isSelected = isSelected,
                                    isMember = isMember,
                                    bold = satoshiBold,
                                    med = satoshiMedium,
                                    red = redPrimer
                                ) {
                                    screenModel.toggleVoucher(voucher)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyVoucherState(med: FontFamily) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(Modifier.height(16.dp))
            Text(
                "Tidak ada voucher tersedia",
                textAlign = TextAlign.Center,
                fontFamily = med,
                color = Color.Gray
            )
        }
    }

    @Composable
    private fun VoucherCardItem(
        voucher: VoucherUIModel,
        isSelected: Boolean,
        isMember: Boolean,
        bold: FontFamily,
        med: FontFamily,
        red: Color,
        onClick: () -> Unit
    ) {
        val discountLabel = if (isMember) {
            when {
                voucher.presentaseMember > 0 -> "Potongan Member ${voucher.presentaseMember.toInt()}%"
                voucher.potHargaMember > 0 -> "Potongan Member ${formatRupiah(voucher.potHargaMember)}"
                else -> "Promo Spesial Member"
            }
        } else {
            when {
                voucher.presentaseNonMember > 0 -> "Potongan ${voucher.presentaseNonMember.toInt()}%"
                voucher.potHargaNonMember > 0 -> "Potongan ${formatRupiah(voucher.potHargaNonMember)}"
                else -> "Promo Spesial"
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) red.copy(alpha = 0.05f) else Color.White,
            border = BorderStroke(1.dp, if (isSelected) red else Color(0xFFE0E0E0))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (isSelected) red else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = discountLabel,
                            fontFamily = bold,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else red,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(voucher.namaVoucher, fontFamily = bold, fontSize = 15.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    val expText = voucher.tglExpired ?: "Tanpa batas waktu"
                    Text("Berlaku hingga: $expText", fontFamily = med, fontSize = 12.sp, color = Color.Gray)
                    if (voucher.allowMultiple == 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Eksklusif (Tidak bisa digabung)", fontFamily = med, fontSize = 11.sp, color = Color(0xFFE67E22))
                    }
                }
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (isSelected) red else Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    private fun formatRupiah(amount: Double): String {
        val absoluteAmount = kotlin.math.abs(amount).toLong()
        val formattedNumber = absoluteAmount.toString().reversed().chunked(3).joinToString(".").reversed()
        return "Rp $formattedNumber"
    }
}