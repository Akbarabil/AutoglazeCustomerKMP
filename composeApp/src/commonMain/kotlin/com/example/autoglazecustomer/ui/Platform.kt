package com.example.autoglazecustomer.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform