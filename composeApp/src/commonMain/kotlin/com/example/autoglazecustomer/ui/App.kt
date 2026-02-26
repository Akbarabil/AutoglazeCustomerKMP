package com.example.autoglazecustomer.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Preview
@Composable
fun App() {
    MaterialTheme {
        // Navigator sekarang dimulai dengan SplashScreen
        Navigator(SplashScreen()) { navigator ->
            // Memberikan animasi transisi geser antar halaman
            SlideTransition(navigator)
        }
    }
}