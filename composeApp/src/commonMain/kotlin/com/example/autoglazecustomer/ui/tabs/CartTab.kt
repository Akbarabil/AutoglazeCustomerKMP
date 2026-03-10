package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.cart.CartScreen
import org.jetbrains.compose.resources.painterResource

class CartTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Pesanan",
            icon = painterResource(Res.drawable.ic_cart)
        )

    @Composable
    override fun Content() {
        val authService = remember { AuthService() }
        val cartScreen = remember(authService) { CartScreen(authService) }

        cartScreen.Content()
    }
}