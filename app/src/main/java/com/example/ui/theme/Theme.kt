package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.repository.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = BlueAccent,
    onSecondary = Color(0xFF003355),
    secondaryContainer = Slate800,
    onSecondaryContainer = Color(0xFFCCE5FF),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF003822),
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate850,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = BlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = SlateLightBg,
    onBackground = SlateLightText,
    surface = SlateLightSurface,
    onSurface = SlateLightText,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateLightTextSecondary,
    outline = SlateLightBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
