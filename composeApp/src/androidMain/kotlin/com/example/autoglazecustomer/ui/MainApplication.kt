package com.example.autoglazecustomer.ui

import android.app.Application
import com.example.autoglazecustomer.data.di.appModule
import com.example.autoglazecustomer.data.local.AppContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}