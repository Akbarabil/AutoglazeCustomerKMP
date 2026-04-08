package com.example.autoglazecustomer.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.KeychainSettings
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun createSecureSettings(): Settings {
    return KeychainSettings("com.autoglaze.secure")
}