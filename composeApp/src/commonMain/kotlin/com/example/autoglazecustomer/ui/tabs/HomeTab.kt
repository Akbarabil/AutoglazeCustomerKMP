package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_home
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.home.HomeScreen
import org.jetbrains.compose.resources.painterResource

// Hapus (private val authService: AuthService) dari constructor
class HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Beranda",
            icon = painterResource(Res.drawable.ic_home)
        )

    @Composable
    override fun Content() {
        val authService = remember { AuthService() }
        val homeScreen = remember(authService) { HomeScreen(authService) }

        homeScreen.Content()
    }
}