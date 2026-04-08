package com.example.autoglazecustomer.data.local

import com.russhwolf.settings.Settings

object TokenManager {
    private val settings: Settings = createSecureSettings()

    private const val KEY_TOKEN = "auth_token"
    private const val KEY_CUSTOMER_ID = "customer_id"
    private const val KEY_USER_NAME = "user_name"

    fun saveToken(token: String) {
        settings.putString(KEY_TOKEN, token)
    }

    fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)

    fun saveCustomerId(id: Int) {
        settings.putInt(KEY_CUSTOMER_ID, id)
    }

    fun getCustomerId(): Int = settings.getInt(KEY_CUSTOMER_ID, -1)

    fun saveUserName(name: String) {
        settings.putString(KEY_USER_NAME, name)
    }

    fun getUserName(): String = settings.getString(KEY_USER_NAME, "Guest")


    fun clearAll() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_CUSTOMER_ID)
        settings.remove(KEY_USER_NAME)
    }
}