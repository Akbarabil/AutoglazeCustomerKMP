package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class UserLocation(val latitude: Double, val longitude: Double)

expect class LocationService {
    suspend fun getCurrentLocation(): UserLocation?
}

@Composable
expect fun rememberLocationService(): LocationService
