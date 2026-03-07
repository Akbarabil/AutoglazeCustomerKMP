package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.painterResource
import autoglazecustomer.composeapp.generated.resources.*
import androidx.compose.material3.Text

object CartTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 2u, title = "Cart", icon = painterResource(Res.drawable.ic_cart))

    @Composable
    override fun Content() { Text("Halaman Keranjang") }
}