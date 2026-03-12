package com.example.autoglazecustomer.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_transaction
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.autoglazecustomer.data.network.AuthService
import com.example.autoglazecustomer.ui.transaction.TransactionScreen
import org.jetbrains.compose.resources.painterResource

class TransactionTab : Tab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 1u,
            title = "Transaksi",
            icon = painterResource(Res.drawable.ic_transaction)
        )

    @Composable
    override fun Content() {
        // Inisialisasi AuthService secara lazy
        val authService = remember { AuthService() }

        // Panggil TransactionScreen (Halaman pilih cabang yang kita buat tadi)
        val transactionScreen = remember(authService) { TransactionScreen(authService) }

        transactionScreen.Content()
    }
}