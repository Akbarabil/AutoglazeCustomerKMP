package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_profile
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.ui.profile.ProfileScreen
import org.jetbrains.compose.resources.painterResource


class ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Profil",
            icon = painterResource(Res.drawable.ic_profile)
        )

    @Composable
    override fun Content() {
        ProfileScreen().Content()
    }
}