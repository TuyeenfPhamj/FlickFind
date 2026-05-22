package com.example.flickfind_ltttbdd.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = NavyDark,
    secondary = CyanSecondary,
    onSecondary = NavyDark,
    background = NavyDark,
    onBackground = WhiteText,
    surface = NavySurface,
    onSurface = WhiteText,
    surfaceVariant = NavySurface,
    onSurfaceVariant = GrayText,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BlueDarkPrimary,
    onPrimary = Color.White,
    secondary = BlueMuted,
    onSecondary = Color.White,
    background = BlueGrayLight,
    onBackground = Color(0xFF0F172A),
    surface = WhiteSurface,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = BlueMuted,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun FlickFindLTTTBDDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Tắt dynamicColor để giữ đúng thiết kế Cyan/Navy của bạn
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
