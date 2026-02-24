package com.example.autoglazecustomer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform