package com.example.autoglazecustomer.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.autoglazecustomer.ui.tabs.CartTab
import com.example.autoglazecustomer.ui.tabs.HomeTab
import com.example.autoglazecustomer.ui.tabs.ProfileTab
import com.example.autoglazecustomer.ui.tabs.TransactionTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        // Kita mulai dengan HomeTab sebagai default
        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(TransactionTab)
                        TabNavigationItem(CartTab)
                        TabNavigationItem(ProfileTab)
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
        val redPrimer = Color(0xFFD53B1E)

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            label = { Text(tab.options.title) },
            icon = {
                Icon(
                    painter = tab.options.icon!!,
                    contentDescription = tab.options.title,
                    tint = if (tabNavigator.current == tab) redPrimer else Color.Gray
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent, // Agar tidak ada background lonjong saat diklik
                selectedTextColor = redPrimer,
                unselectedTextColor = Color.Gray
            )
        )
    }
}