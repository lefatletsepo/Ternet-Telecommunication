package com.ternet.telecom.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ternet.telecom.ui.components.GlassmorphicCard
import com.ternet.telecom.ui.components.GlassmorphicKeypad
import com.ternet.telecom.ui.components.glassmorphic
import com.ternet.telecom.ui.theme.*
import com.ternet.telecom.viewmodel.LoginViewModel

/**
 * A beautiful, modern "Liquid Glass" PIN Authentication Screen.
 * Featuring organic background fluid animations, glass banners, secure PIN metrics,
 * and high-contrast feedback aligned with Ternet brand standards.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val pinState by viewModel.pinState.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Listen for authentication completion trigger
    LaunchedEffect(key1 = true) {
        viewModel.loginSuccess.collect { success ->
            if (success) {
                onLoginSuccess()
            }
        }
    }

    // Organic animation state for floating liquid blobs in the background
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidBlobs")
    val blobOffset1 by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob1"
    )
    val blobOffset2 by infiniteTransition.animateFloat(
        initialValue = 150f,
        targetValue = -150f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false) {} // Consume clicks
    ) {
        // 1. Organic Canvas Backdrop: Emulates liquid color blobs moving underneath the glass layers
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Primary dark background gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundBase, ForestGreen, BackgroundBase)
                )
            )

            // Drawing Liquid Blob 1 (Mint Glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobMint.copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(size.width * 0.25f + blobOffset1, size.height * 0.3f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.25f + blobOffset1, size.height * 0.3f),
                radius = size.width * 0.7f
            )

            // Drawing Liquid Blob 2 (Forest/Emerald Rich Glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobGreen.copy(alpha = 0.7f), Color.Transparent),
                    center = Offset(size.width * 0.75f, size.height * 0.75f + blobOffset2),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width * 0.75f, size.height * 0.75f + blobOffset2),
                radius = size.width * 0.9f
            )
        }

        // 2. Interactive Glass Layer Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Content: Ternet Branding Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glass Logo Overlay
                GlassmorphicCard(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 12.dp),
                    shape = CircleShape,
                    containerColor = Color(0x22FFFFFF)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Gateway",
                            tint = LiquidMint,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "Ternet",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = (-1).sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "TELECOMMUNICATIONS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = LiquidMint,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Secure mobile wallet for Lesotho. Please enter your 4-digit security PIN.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMintSecondary,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
            }

            // Middle Content: PIN Circles indicator & Feedback state
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pin Indicators (Circles filled with solid Mint green as PIN code is entered)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isFilled = index < pinState.length
                        Canvas(modifier = Modifier.size(18.dp)) {
                            drawCircle(
                                color = if (isFilled) LiquidMint else Color(0x33FFFFFF),
                                radius = size.minDimension / 2f
                            )
                            if (!isFilled) {
                                drawCircle(
                                    color = Color(0x66FFFFFF),
                                    radius = size.minDimension / 2f,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error State Feedback
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { msg ->
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp),
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color(0x11FF4D4D),
                            borderBrush = Brush.linearGradient(
                                colors = listOf(Color(0x33FF4D4D), Color(0x05FF4D4D))
                            )
                        ) {
                            Text(
                                text = msg,
                                color = TextError,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            )
                        }
                    }
                }

                // Authentication Loader
                AnimatedVisibility(visible = isAuthenticating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = LiquidMint,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Authorizing transaction session...",
                            color = LiquidMint,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Content: Keypad and Fingerprint backup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlassmorphicKeypad(
                    onKeyPress = { viewModel.onKeyPress(it) },
                    onDeletePress = { viewModel.onDeletePress() },
                    onClearPress = { viewModel.onClearPress() }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Biometrics Entry Toggle
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x19FFFFFF))
                        .clickable {
                            // Mock Biometric validation triggering automated correct PIN injection
                            viewModel.onClearPress()
                            viewModel.onKeyPress('2')
                            viewModel.onKeyPress('6')
                            viewModel.onKeyPress('6')
                            viewModel.onKeyPress('0')
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Bypass",
                        tint = LiquidMint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
