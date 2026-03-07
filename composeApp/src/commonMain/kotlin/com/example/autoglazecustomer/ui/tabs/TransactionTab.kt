package com.example.autoglazecustomer.ui.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_transaction
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.painterResource

object TransactionTab : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(index = 1u, title = "Transaction", icon = painterResource(Res.drawable.ic_transaction))

    @Composable override fun Content() { Text("Halaman Transaksi") }
}