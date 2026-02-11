package com.harmonic.insight.launcher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val InsightLightColorScheme = lightColorScheme(
    primary = InsightPrimaryLight,
    onPrimary = InsightOnPrimaryLight,
    primaryContainer = InsightPrimaryContainerLight,
    onPrimaryContainer = InsightOnPrimaryContainerLight,
    secondary = InsightSecondaryLight,
    onSecondary = InsightOnSecondaryLight,
    secondaryContainer = InsightSecondaryContainerLight,
    onSecondaryContainer = InsightOnSecondaryContainerLight,
    background = InsightBackgroundLight,
    onBackground = InsightOnBackgroundLight,
    surface = InsightSurfaceLight,
    onSurface = InsightOnSurfaceLight,
    surfaceVariant = InsightSurfaceVariantLight,
    onSurfaceVariant = InsightOnSurfaceVariantLight,
    error = InsightErrorLight,
    onError = InsightOnErrorLight,
    outline = InsightOutlineLight,
)

private val InsightDarkColorScheme = darkColorScheme(
    primary = InsightPrimaryDark,
    onPrimary = InsightOnPrimaryDark,
    primaryContainer = InsightPrimaryContainerDark,
    onPrimaryContainer = InsightOnPrimaryContainerDark,
    secondary = InsightSecondaryDark,
    onSecondary = InsightOnSecondaryDark,
    secondaryContainer = InsightSecondaryContainerDark,
    onSecondaryContainer = InsightOnSecondaryContainerDark,
    background = InsightBackgroundDark,
    onBackground = InsightOnBackgroundDark,
    surface = InsightSurfaceDark,
    onSurface = InsightOnSurfaceDark,
    surfaceVariant = InsightSurfaceVariantDark,
    onSurfaceVariant = InsightOnSurfaceVariantDark,
    error = InsightErrorDark,
    onError = InsightOnErrorDark,
    outline = InsightOutlineDark,
)

@Composable
fun InsightLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> InsightDarkColorScheme
        else -> InsightLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = InsightTypography,
        content = content,
    )
}
