package com.example.autoglazecustomer.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

actual class LocationService(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): UserLocation? {
        return try {
            val location = fusedLocationClient.lastLocation.await()

            if (location != null) {
                UserLocation(latitude = location.latitude, longitude = location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            println("GPS_ERROR: ${e.message}")
            null
        }
    }
}

@Composable
actual fun rememberLocationService(): LocationService {
    val context = LocalContext.current
    return remember(context) { LocationService(context) }
}