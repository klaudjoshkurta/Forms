package com.shkurta.medicationtracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Neutral200,
    onPrimary = Neutral900,
    primaryContainer = Neutral800,
    onPrimaryContainer = Neutral200,
    secondary = Neutral400,
    onSecondary = Neutral900,
    background = Neutral900,
    onBackground = Neutral100,
    surface = Neutral800,
    onSurface = Neutral100,
    surfaceVariant = Neutral700,
    onSurfaceVariant = Neutral200,
    outline = Neutral600
)

private val LightColorScheme = lightColorScheme(
    primary = Neutral800,
    onPrimary = White,
    primaryContainer = SageGreen40, // Very soft green for containers
    onPrimaryContainer = Neutral900,
    secondary = Neutral600,
    onSecondary = White,
    background = Neutral50, // Almost white
    onBackground = Neutral900,
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral700,
    outline = Neutral300,
    outlineVariant = Neutral200
)

@Composable
fun MedicationTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled dynamic color to enforce neutral theme
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
        typography = AppTypography,
        content = content
    )
}