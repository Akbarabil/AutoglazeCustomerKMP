package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject

@Composable
actual fun rememberPermissionHandler(onResult: (Boolean) -> Unit): PermissionHandler {

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                val status = manager.authorizationStatus
                when (status) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> onResult(true)

                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> onResult(false)

                    else -> {}
                }
            }
        }
    }

    return remember {
        object : PermissionHandler {
            private val locationManager = CLLocationManager().apply {
                this.delegate = delegate
            }

            override fun askPermission() {
                locationManager.requestWhenInUseAuthorization()
            }

            override fun isPermissionGranted(): Boolean {
                val status = locationManager.authorizationStatus
                return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                        status == kCLAuthorizationStatusAuthorizedAlways
            }

            override fun openAppSettings() {
                val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (settingsUrl != null) {
                    if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
                        UIApplication.sharedApplication.openURL(
                            url = settingsUrl,
                            options = emptyMap<Any?, Any?>(),
                            completionHandler = null
                        )
                    }
                }
            }
        }
    }
}