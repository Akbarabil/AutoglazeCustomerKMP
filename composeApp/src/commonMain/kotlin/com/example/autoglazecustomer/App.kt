package com.example.autoglazecustomer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Preview
@Composable
fun App() {
    MaterialTheme {
        // Navigator dimulai dengan halaman CheckVehicleScreen
        Navigator(CheckVehicleScreen()) { navigator ->
            // Opsional: Tambahkan animasi transisi antar halaman
            SlideTransition(navigator)
        }
    }
}