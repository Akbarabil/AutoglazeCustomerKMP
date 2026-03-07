package com.example.autoglazecustomer.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.autoglazecustomer.ui.tabs.HomeTab
// import com.example.autoglazecustomer.ui.tabs.TransactionTab (Nanti buat ini)
// import com.example.autoglazecustomer.ui.tabs.ProfileTab (Nanti buat ini)

class MainTabScreen : Screen {
    @Composable
    override fun Content() {
        // TabNavigator mengatur perpindahan antar Tab
        TabNavigator(HomeTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        // Daftarkan tab-tab kamu di sini
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(TransactionTab)
                        TabNavigationItem(CartTab)
                        TabNavigationItem(ProfileTab)
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CurrentTab() // Menampilkan isi dari Tab yang sedang aktif
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            label = { Text(tab.options.title) },
            icon = {
                Icon(
                    painter = tab.options.icon!!,
                    contentDescription = tab.options.title
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFD53B1E), // Warna merah Autoglaze
                selectedTextColor = Color(0xFFD53B1E),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFD53B1E).copy(alpha = 0.1f)
            )
        )
    }
}