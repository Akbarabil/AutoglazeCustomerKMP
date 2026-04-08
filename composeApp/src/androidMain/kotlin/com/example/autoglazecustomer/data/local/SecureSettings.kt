package com.example.autoglazecustomer.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import java.lang.ref.WeakReference

object AppContext {
    private var _contextRef: WeakReference<Context>? = null

    val context: Context
        get() = _contextRef?.get() ?: throw IllegalStateException("AppContext belum diinisialisasi atau sudah di-reclaim oleh sistem!")

    fun init(context: Context) {
        // Tetap gunakan applicationContext agar aman dari leak
        _contextRef = WeakReference(context.applicationContext)
    }
}

actual fun createSecureSettings(): Settings {
    val masterKey = MasterKey.Builder(AppContext.context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val prefs = EncryptedSharedPreferences.create(
        AppContext.context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return SharedPreferencesSettings(prefs)
}