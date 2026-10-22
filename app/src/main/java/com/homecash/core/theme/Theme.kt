package com.homecash.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = SurfaceWhite,
    primaryContainer = SecondaryColor,
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryColor,
    onSecondary = PrimaryColor,
    background = SurfaceLight,
    onBackground = NeutralDark,
    surface = SurfaceWhite,
    onSurface = NeutralDark,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = NeutralVariant,
    outline = OutlineColor,
    outlineVariant = OutlineVariantColor,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun HomeCashTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = HomeCashTypography
    ) {
        // Enforce RTL layout direction for Arabic
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}
