package com.example.autoglazecustomer.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.data.network.AuthService

class MainTabScreen : Screen {
    @Composable
    override fun Content() {
        // 1. Inisialisasi AuthService tunggal
        val authService = remember { AuthService() }

        // 2. Inisialisasi Tab (Home & Cart sudah Class, sisanya Placeholder)
        val homeTab = remember { HomeTab() }
        val cartTab = remember { CartTab() }
        val placeholderTab = remember { PlaceholderTab }

        TabNavigator(homeTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        // Menu 1: Home (Index 0)
                        TabNavigationItem(homeTab)

                        // Menu 2: Transaction (Index 1) - Sementara Placeholder
                        TabNavigationItem(placeholderTab.copyOptions(1u, "Transaction"))

                        // Menu 3: Riwayat/Cart (Index 2) - Fokus Kita
                        TabNavigationItem(cartTab)

                        // Menu 4: Profile (Index 3) - Sementara Placeholder
                        TabNavigationItem(placeholderTab.copyOptions(3u, "Profile"))
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        // Cek seleksi berdasarkan index agar akurat
        val isSelected = tabNavigator.current.options.index == tab.options.index
        val redPrimer = Color(0xFFD53B1E)

        NavigationBarItem(
            selected = isSelected,
            onClick = { tabNavigator.current = tab },
            label = { Text(tab.options.title) },
            icon = {
                if (tab.options.icon != null) {
                    Icon(
                        painter = tab.options.icon!!,
                        contentDescription = tab.options.title,
                        // Warna manual jika ingin kontrol penuh, atau biarkan mengikuti colors
                        tint = if (isSelected) redPrimer else Color.Gray
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = if (isSelected) redPrimer else Color.Gray
                    )
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

/**
 * Placeholder untuk Tab yang belum diimplementasikan
 */
object PlaceholderTab : Tab {
    private var customIndex: UShort = 1u
    private var customTitle: String = "Coming Soon"

    fun copyOptions(index: UShort, title: String): Tab {
        customIndex = index
        customTitle = title
        return this
    }

    override val options: TabOptions
        @Composable get() = TabOptions(customIndex, customTitle, null)

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Halaman $customTitle", style = MaterialTheme.typography.titleMedium)
                Text("Sedang dalam pengembangan", color = Color.Gray)
            }
        }
    }
}