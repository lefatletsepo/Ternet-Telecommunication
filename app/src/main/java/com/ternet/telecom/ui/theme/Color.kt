package com.ternet.telecom.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Ternet Telecommunications Brand Colors
 * Focus: Vibrant Forest Greens, High-fidelity Emerald, and Fluid Mint Accents.
 * Optimized for Modern "Liquid Glass" / Glassmorphic UI overlays.
 */

// Brand Identity Solid Greens
val ForestGreen = Color(0xFF09311A)        // Deep base color for backing layers
val DeepEmerald = Color(0xFF0D4B2A)        // Mid-tone rich green
val EmeraldGreen = Color(0xFF1E8A54)       // Primary branding green
val MintGreen = Color(0xFF2ED573)          // Bright mint accent
val MintLight = Color(0xFF90F3C2)          // Highly legible light green
val LiquidMint = Color(0xFF00FF87)         // Vibrant high-lumance liquid-mint accent

// Glassmorphic Layering & Blending Palette
// Glass backgrounds use low-alpha values with specific blending expectations
val GlassBgWhiteTiny = Color(0x0DFFFFFF)     // 5% alpha white
val GlassBgWhiteLow = Color(0x1EFFFFFF)      // 12% alpha white (frosted card standard)
val GlassBgWhiteMedium = Color(0x33FFFFFF)   // 20% alpha white (for active actions)
val GlassBgGreenLow = Color(0x2A0D4B2A)     // 16% alpha Emerald for rich dark glass depth

// Glass Border & Highlight Gradients (Simulates reflective edge highlights)
val GlassBorderHigh = Color(0x66FFFFFF)     // 40% white (top-left light source edge)
val GlassBorderLow = Color(0x14FFFFFF)      // 8% white (bottom-right shadow edge)
val GlassBorderAccent = Color(0x4D00FF87)   // 30% liquid mint border accent

// Typographical Contrast Palette
val TextWhite = Color(0xFFFFFFFF)
val TextMintSecondary = Color(0xFFA6EFD2)   // Soft green-tinted secondary text
val TextDeepDark = Color(0xFF051C0F)        // Heavy dark green for maximum legibility on bright backgrounds
val TextError = Color(0xFFFF4D4D)           // High-contrast warning/error red

// Gradient Composites for background blob rendering
val BackgroundBase = Color(0xFF041209)       // Deep, dark rich baseline representing the abyss of the liquid background
val LiquidBlobGreen = Color(0xFF0B5730)     // Large backing fluid blob
val LiquidBlobMint = Color(0xFF128F54)      // Mid backing fluid blob
