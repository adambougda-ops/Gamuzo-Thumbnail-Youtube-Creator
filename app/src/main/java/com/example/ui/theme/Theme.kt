package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GamingDarkColorScheme = darkColorScheme(
    primary = Color(0xFFE50914), // Gamuzo Red
    secondary = Color.White,
    tertiary = Color(0xFFE50914),
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF141414),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFD1D5DB)
)

private val GamingLightColorScheme = lightColorScheme(
    primary = Color.Black, // Black
    secondary = Color(0xFF1F2937),
    tertiary = Color.Black,
    background = Color(0xFFF9FAFB),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF374151)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GamingDarkColorScheme else GamingLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val statusBarColor = colorScheme.background.toArgb()
                window.statusBarColor = statusBarColor
                window.navigationBarColor = statusBarColor
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
