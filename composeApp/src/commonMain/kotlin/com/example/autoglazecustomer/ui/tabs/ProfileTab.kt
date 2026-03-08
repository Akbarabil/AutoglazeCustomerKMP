package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_profile
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.profile.ProfileScreen
import org.jetbrains.compose.resources.painterResource

// Menggunakan class tanpa parameter agar aman dari Serialization crash di Android
class ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Profile",
            icon = painterResource(Res.drawable.ic_profile)
        )

    @Composable
    override fun Content() {
        // Inisialisasi AuthService di dalam Content (Lazy Initialization)
        val authService = remember { AuthService() }

        // Inisialisasi ProfileScreen dengan parameter authService
        val profileScreen = remember(authService) { ProfileScreen(authService) }

        profileScreen.Content()
    }
}