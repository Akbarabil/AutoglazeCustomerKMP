package com.example.autoglazecustomer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import org.jetbrains.compose.resources.Font

object AppFont {

    @Composable
    fun satoshiBold(): FontFamily {
        val font = Font(Res.font.satoshi_bold, FontWeight.Bold)
        return remember(font) { FontFamily(font) }
    }

    @Composable
    fun satoshiMedium(): FontFamily {
        val font = Font(Res.font.satoshi_medium, FontWeight.Medium)
        return remember(font) { FontFamily(font) }
    }

}