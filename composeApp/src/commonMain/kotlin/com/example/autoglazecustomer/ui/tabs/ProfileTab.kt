package com.example.autoglazecustomer.ui.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_profile
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.painterResource

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 3u, title = "Profile", icon = painterResource(Res.drawable.ic_profile))

    @Composable override fun Content() { Text("Halaman Profil") }
}