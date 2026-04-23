package com.example.autoglazecustomer.ui

import android.app.Application
import com.example.autoglazecustomer.data.di.appModule
import com.example.autoglazecustomer.data.local.AppContext
import dev.skymansandy.wiretap.helper.launcher.enableWiretapLauncher
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        enableWiretapLauncher()
        AppContext.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}