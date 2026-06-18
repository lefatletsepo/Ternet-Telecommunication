package com.ternet.telecom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Solid Dark-Mode Scheme customized for Ternet Telecommunications.
 * A dark backdrop enhances the specular reflection, refractive highlights,
 * and high-contrast glowing elements of our "Liquid Glass" design system.
 */
private val TernetColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    secondary = MintGreen,
    tertiary = MintLight,
    background = BackgroundBase,
    surface = ForestGreen,
    onPrimary = TextDeepDark,
    onSecondary = TextDeepDark,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = TextError
)

object TernetTheme {
    /**
     * Deep vertical gradient acting as the core background canvas of the application.
     * Interacts with overlayed glass elements to create a premium, fluid sense of depth.
     */
    val liquidBackgroundBrush: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                BackgroundBase,
                ForestGreen,
                BackgroundBase
            )
        )

    /**
     * Diagnostic/Speculative linear gradient to represent the directional lighting source of glass panels.
     * Starts bright white (top-left) and cascades down to low-visibility translucent white.
     */
    val glassBorderBrush: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                GlassBorderHigh,
                GlassBorderLow
            )
        )

    /**
     * Accent linear gradient starting with high-vibrancy mint for specialized active buttons/states.
     */
    val glassBorderAccentBrush: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(
                GlassBorderAccent,
                Color(0x0500FF87)
            )
        )

    /**
     * Premium Emerald/Mint gradient used for solid buttons or critical CTA backings.
     */
    val primaryLiquidGradient: Brush
        @Composable
        get() = Brush.horizontalGradient(
            colors = listOf(
                EmeraldGreen,
                MintGreen
            )
        )
}

@Composable
fun TernetTelecomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TernetColorScheme,
        typography = TernetTypography,
        content = content
    )
}
