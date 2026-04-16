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
        get() = _contextRef?.get()
            ?: throw IllegalStateException("AppContext belum diinisialisasi atau sudah di-reclaim oleh sistem!")

    fun init(context: Context) {
        _contextRef = WeakReference(context.applicationContext)
    }
}

actual fun createSecureSettings(): Settings {
    val context = AppContext.context
    val prefName = "secure_prefs"

    return try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            prefName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        SharedPreferencesSettings(prefs)

    } catch (e: Exception) {
        e.printStackTrace()

        context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply()

        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val newPrefs = EncryptedSharedPreferences.create(
            context,
            prefName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        SharedPreferencesSettings(newPrefs)
    }
}