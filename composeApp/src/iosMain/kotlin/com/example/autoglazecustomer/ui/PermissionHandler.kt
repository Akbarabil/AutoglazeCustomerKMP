package com.example.autoglazecustomer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreLocation.*
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject

@Composable
actual fun rememberPermissionHandler(onResult: (Boolean) -> Unit): PermissionHandler {
    return remember {
        object : PermissionHandler {
            private val locationManager = CLLocationManager()

            private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    val status = manager.authorizationStatus
                    if (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                        status == kCLAuthorizationStatusAuthorizedAlways) {
                        onResult(true)
                    } else if (status == kCLAuthorizationStatusDenied) {
                        onResult(false)
                    }
                }
            }

            init {
                locationManager.delegate = delegate
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
                val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                }
            }
        }
    }
}