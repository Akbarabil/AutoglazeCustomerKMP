package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import kotlin.coroutines.resume

actual class LocationService {
    private val locationManager = CLLocationManager()

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): UserLocation? = suspendCancellableCoroutine { continuation ->
        val location = locationManager.location

        if (location != null) {
            val coordinates = location.coordinate.useContents {
                UserLocation(
                    latitude = latitude,
                    longitude = longitude
                )
            }
            if (continuation.isActive) {
                continuation.resume(coordinates)
            }
        } else {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }

        continuation.invokeOnCancellation {
            locationManager.stopUpdatingLocation()
        }
    }
}

@Composable
actual fun rememberLocationService(): LocationService = remember { LocationService() }