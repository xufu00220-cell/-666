package com.deepseek.balance.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DeepSeekColorScheme = darkColorScheme(
    primary = DeepBlue,
    onPrimary = TextPrimary,
    secondary = TealAccent,
    onSecondary = DarkNavy,
    tertiary = GoldAccent,
    background = DarkNavy,
    onBackground = TextPrimary,
    surface = MidnightCard,
    onSurface = TextPrimary,
    surfaceVariant = DarkNavyLight,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = CoralAccent,
    onError = TextPrimary
)

@Composable
fun DeepSeekBalanceTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkNavy.toArgb()
            window.navigationBarColor = DarkNavy.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DeepSeekColorScheme,
        typography = AppTypography,
        content = content
    )
}
