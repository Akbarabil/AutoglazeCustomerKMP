package com.example.autoglazecustomer.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.autoglazecustomer.data.local.LocalShowcaseState
import com.example.autoglazecustomer.data.local.ShowcaseItem
import com.example.autoglazecustomer.data.local.ShowcaseOverlay
import com.example.autoglazecustomer.data.local.rememberShowcaseState
import com.example.autoglazecustomer.data.local.showcaseTarget
import com.russhwolf.settings.Settings

class MainTabScreen : Screen {
    @Composable
    override fun Content() {
        val showcaseState = rememberShowcaseState()
        val settings = remember { Settings() }

        val showcaseItems = remember {
            listOf(
                ShowcaseItem(
                    "Home Service",
                    "Pesan layanan home service dengan mudah sesuai kebutuhan anda."
                ),
                ShowcaseItem(
                    "Transaksi",
                    "Lakukan pemesanan layanan dengan memilih cabang terdekat, kendaraan, dan jenis layanan."
                ),
                ShowcaseItem(
                    "Lokasi Cabang",
                    "Temukan dan lihat lokasi cabang terdekat dari posisi anda."
                ),
                ShowcaseItem(
                    "Transaksi",
                    "Buat pesanan layanan dengan memilih cabang, kendaraan, dan layanan yang diinginkan."
                ),
                ShowcaseItem(
                    "Pesanan",
                    "Pilih kendaraan anda untuk melihat daftar dan status pesanan yang telah dibuat."
                ),
                ShowcaseItem(
                    "Profil",
                    "Lihat jumlah poin, kelola profil, dan atur data kendaraan anda."
                )
            )
        }

        val hasSeenShowcase = remember { settings.getBoolean("has_seen_showcase", false) }

        val isTargetReady = showcaseState.targets.containsKey(0)

        LaunchedEffect(isTargetReady) {
            if (!hasSeenShowcase && isTargetReady) {
                kotlinx.coroutines.delay(300)
                showcaseState.start {
                    settings.putBoolean("has_seen_showcase", true)
                }
            }
        }

        val homeTab = remember { HomeTab() }
        val transactionTab = remember { TransactionTab() }
        val cartTab = remember { CartTab() }
        val profileTab = remember { ProfileTab() }

        CompositionLocalProvider(LocalShowcaseState provides showcaseState) {
            Box(modifier = Modifier.fillMaxSize()) {
                TabNavigator(homeTab) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp
                            ) {
                                TabNavigationItem(homeTab, null)
                                TabNavigationItem(transactionTab, 3)
                                TabNavigationItem(cartTab, 4)
                                TabNavigationItem(profileTab, 5)
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = paddingValues.calculateBottomPadding())
                        ) {
                            CurrentTab()
                        }
                    }
                }

                ShowcaseOverlay(state = showcaseState, items = showcaseItems)
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab, showcaseIndex: Int?) {
        val tabNavigator = LocalTabNavigator.current
        val showcaseState = LocalShowcaseState.current
        val isSelected = tabNavigator.current.options.index == tab.options.index
        val redPrimer = Color(0xFFD53B1E)

        NavigationBarItem(
            selected = isSelected,
            onClick = { tabNavigator.current = tab },
            label = { Text(tab.options.title) },
            modifier = Modifier,
            icon = {
                val iconPainter = tab.options.icon
                Box(
                    modifier = if (showcaseIndex != null) Modifier.showcaseTarget(
                        showcaseIndex,
                        showcaseState
                    ) else Modifier
                ) {
                    if (iconPainter != null) {
                        Icon(
                            iconPainter,
                            contentDescription = tab.options.title,
                            tint = if (isSelected) redPrimer else Color.Gray
                        )
                    } else {
                        Icon(
                            Icons.Default.HelpOutline,
                            null,
                            tint = if (isSelected) redPrimer else Color.Gray
                        )
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = redPrimer.copy(alpha = 0.1f),
                selectedTextColor = redPrimer,
                unselectedTextColor = Color.Gray,
                selectedIconColor = redPrimer,
                unselectedIconColor = Color.Gray
            )
        )
    }
}