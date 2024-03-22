package com.github.trueddd.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Stable
object Colors {
    val Warning = Color(0xFFFBBF24)
    val Success = Color(0xFF4ADE80)
    val Error = Color(0xFFF87171)
    val White = Color.White
    object Genre {
        val Runner = Color(0xFFF87171)
        val Business = Color(0xFF60A5FA)
        val Puzzle = Color(0xFFA78BFA)
        val PointAndClick = Color(0xFF4ADE80)
        val Shooter = Color(0xFFFBBF24)
        val ThreeInRow = Color(0xFFFB923C)
        val Special = Color(0xFF9CA3AF)
        val Default = Color(0xFFECFDF5)
    }
    object WheelItem {
        val Event = Color(0xFF06B6D4)
        val PendingEvent = Color(0xFF14B8A6)
        val InventoryItem = Color(0xFFF59E0B)
        val Buff = Color(0xFF22C55E)
        val Debuff = Color(0xFFEF4444)
    }
}
