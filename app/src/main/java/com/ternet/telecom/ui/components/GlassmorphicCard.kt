package com.ternet.telecom.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ternet.telecom.ui.theme.GlassBgWhiteLow
import com.ternet.telecom.ui.theme.GlassBorderHigh
import com.ternet.telecom.ui.theme.GlassBorderLow

/**
 * A highly reusable, specialized UI component representing our "Liquid Glass" aesthetic.
 * It combines multi-layer translucent overlays, a custom gradient border simulating specularity,
 * OS-level blur effects (on API 31+), and a beautiful organic inner drop-shadow.
 */

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    containerColor: Color = GlassBgWhiteLow,
    blurRadius: Dp = 20.dp,
    borderWidth: Dp = 1.2.dp,
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(GlassBorderHigh, GlassBorderLow),
        start = Offset(0f, 0f),
        end = Offset.Infinite
    ),
    glowColor: Color? = null, // Optional outer/inner mint/emerald glow for custom premium effects
    content: @Composable BoxScope.() -> Unit
) {
    // 1. Core glass layout applying clip, optional blur (API 31+), background color and border highlights.
    val baseModifier = modifier
        .clip(shape)
        .run {
            // Apply real OS-level background blur on Android 12 (API 31) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.blur(blurRadius)
            } else {
                this
            }
        }
        .background(containerColor)
        .border(
            width = borderWidth,
            brush = borderBrush,
            shape = shape
        )
        // Add subtle light glow effect if a glowColor is supplied
        .run {
            if (glowColor != null) {
                this.drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint().apply {
                            this.color = Color.Transparent.toArgb()
                            // Simulated neon/liquid glow around the glass plate
                            setShadowLayer(
                                12.dp.toPx(),
                                0f,
                                4.dp.toPx(),
                                glowColor.copy(alpha = 0.25f).toArgb()
                            )
                        }
                        canvas.drawRoundRect(
                            left = 0f,
                            top = 0f,
                            right = size.width,
                            bottom = size.height,
                            rx = 24.dp.toPx(), // match corner radius
                            ry = 24.dp.toPx(),
                            paint = androidx.compose.ui.graphics.Paint().apply {
                                asFrameworkPaint().set(paint)
                            }
                        )
                    }
                }
            } else {
                this
            }
        }

    Box(
        modifier = baseModifier,
        content = content
    )
}

/**
 * Extension Modifier that enables any Compose UI element to adopt the Liquid Glassmorphism properties.
 * Provides rapid development convenience.
 */
fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(20.dp),
    containerColor: Color = GlassBgWhiteLow,
    borderBrush: Brush = Brush.linearGradient(
        listOf(GlassBorderHigh, GlassBorderLow),
        start = Offset(0f, 0f),
        end = Offset.Infinite
    ),
    borderWidth: Dp = 1.dp,
    blurRadius: Dp = 16.dp
): Modifier = this
    .clip(shape)
    .run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.blur(blurRadius)
        } else {
            this
        }
    }
    .background(containerColor)
    .border(borderWidth, borderBrush, shape)
