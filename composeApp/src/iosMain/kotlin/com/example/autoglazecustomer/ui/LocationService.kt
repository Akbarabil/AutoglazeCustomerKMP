package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class LocationService {
    private val locationManager = CLLocationManager()

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): UserLocation? = suspendCoroutine { continuation ->

        locationManager.requestWhenInUseAuthorization()

        val location = locationManager.location
        if (location != null) {
            val coordinates = location.coordinate.useContents {
                UserLocation(
                    latitude = latitude,
                    longitude = longitude
                )
            }
            continuation.resume(coordinates)
        } else {
            continuation.resume(null)
        }
    }
}

@Composable
actual fun rememberLocationService(): LocationService = remember { LocationService() }