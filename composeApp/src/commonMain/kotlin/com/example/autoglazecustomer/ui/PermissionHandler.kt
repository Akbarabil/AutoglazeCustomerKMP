package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable

interface PermissionHandler {
    fun askPermission()
    fun isPermissionGranted(): Boolean
    fun openAppSettings()
}

@Composable
expect fun rememberPermissionHandler(onResult: (Boolean) -> Unit): PermissionHandler