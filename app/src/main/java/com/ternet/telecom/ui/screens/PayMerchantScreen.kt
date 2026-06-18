package com.ternet.telecom.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ternet.telecom.ui.components.GlassmorphicCard
import com.ternet.telecom.ui.components.glassmorphic
import com.ternet.telecom.ui.theme.*
import com.ternet.telecom.viewmodel.PayMerchantUiState
import com.ternet.telecom.viewmodel.PayMerchantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayMerchantScreen(
    viewModel: PayMerchantViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val merchantCode by viewModel.merchantCode.collectAsState()
    val amount by viewModel.amount.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Core background with moving organic mint blobs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundBase, ForestGreen, BackgroundBase)
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobMint.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.5f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.5f),
                radius = size.width * 0.7f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextWhite
                    ),
                    title = {
                        Text(
                            text = "Lipha (Pay Merchant)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextWhite
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Crossfade(targetState = uiState, label = "MerchantFlow") { state ->
                    when (state) {
                        is PayMerchantUiState.Idle, is PayMerchantUiState.VerifyingMerchant, is PayMerchantUiState.Error -> {
                            MerchantPayForm(
                                merchantCode = merchantCode,
                                amount = amount,
                                isVerifying = state is PayMerchantUiState.VerifyingMerchant,
                                errorMessage = (state as? PayMerchantUiState.Error)?.message,
                                onCodeChanged = { viewModel.updateMerchantCode(it) },
                                onAmountChanged = { viewModel.updateAmount(it) },
                                onVerify = { viewModel.verifyMerchant() }
                            )
                        }
                        is PayMerchantUiState.MerchantVerified -> {
                            MerchantConfirmation(
                                merchantName = state.merchantName,
                                merchantCode = merchantCode,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                onConfirm = { viewModel.executePayment(state.merchantName) },
                                onCancel = { viewModel.resetState() }
                            )
                        }
                        is PayMerchantUiState.ProcessingPayment -> {
                            PaymentInFlight()
                        }
                        is PayMerchantUiState.Success -> {
                            PaymentSuccessReceipt(
                                receiptId = state.receiptId,
                                merchantName = state.merchantName,
                                amount = state.amount,
                                onDone = {
                                    viewModel.resetState()
                                    onBack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantPayForm(
    merchantCode: String,
    amount: String,
    isVerifying: Boolean,
    errorMessage: String?,
    onCodeChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Viewfinder QR Scanner Mock
        QRScannerMock()

        // Inputs Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = GlassBgWhiteLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = merchantCode,
                    onValueChange = onCodeChanged,
                    label = { Text("Lipha Merchant Till Number") },
                    placeholder = { Text("e.g. 4109") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = LiquidMint,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedLabelColor = LiquidMint,
                        unfocusedLabelColor = TextMintSecondary,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChanged,
                    label = { Text("Payment Amount (M)") },
                    placeholder = { Text("0.00") },
                    prefix = { Text("M ", color = LiquidMint, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = LiquidMint,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedLabelColor = LiquidMint,
                        unfocusedLabelColor = TextMintSecondary,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Error message card
        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphic(shape = RoundedCornerShape(12.dp), containerColor = Color(0x22FF4D4D))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = TextError,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = error, color = TextError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerify,
            enabled = !isVerifying && merchantCode.isNotEmpty() && amount.isNotEmpty(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldGreen,
                disabledContainerColor = Color(0x1EFFFFFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .glassmorphic(
                    shape = RoundedCornerShape(16.dp),
                    containerColor = if (!isVerifying && merchantCode.isNotEmpty() && amount.isNotEmpty()) Color(0x00FFFFFF) else Color(0x1EFFFFFF),
                    borderBrush = TernetTheme.glassBorderAccentBrush
                )
        ) {
            if (isVerifying) {
                CircularProgressIndicator(color = LiquidMint, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "VALIDATE TILL & PAY",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = LiquidMint)
                }
            }
        }
    }
}

@Composable
fun QRScannerMock() {
    val infiniteTransition = rememberInfiniteTransition(label = "Laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserMotion"
    )

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        containerColor = Color(0x0AFFFFFF)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Emulate Camera grid background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeColor = Color(0x11FFFFFF)
                val strokeWidth = 1.dp.toPx()
                val divisions = 10
                
                // Draw camera grid lines
                for (i in 1..divisions) {
                    val x = size.width * (i.toFloat() / divisions)
                    val y = size.height * (i.toFloat() / divisions)
                    drawLine(strokeColor, Offset(x, 0f), Offset(x, size.height), strokeWidth)
                    drawLine(strokeColor, Offset(0f, y), Offset(size.width, y), strokeWidth)
                }

                // Draw Viewfinder Target Corners (Mint corner markers)
                val cornerLen = 20.dp.toPx()
                val cornerThick = 3.dp.toPx()
                val padding = 30.dp.toPx()
                val rectLeft = padding
                val rectTop = padding
                val rectRight = size.width - padding
                val rectBottom = size.height - padding

                // Top-Left Corner
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(rectLeft, rectTop + cornerLen)
                        lineTo(rectLeft, rectTop)
                        lineTo(rectLeft + cornerLen, rectTop)
                    },
                    color = LiquidMint,
                    style = Stroke(width = cornerThick)
                )

                // Top-Right Corner
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(rectRight - cornerLen, rectTop)
                        lineTo(rectRight, rectTop)
                        lineTo(rectRight, rectTop + cornerLen)
                    },
                    color = LiquidMint,
                    style = Stroke(width = cornerThick)
                )

                // Bottom-Left Corner
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(rectLeft, rectBottom - cornerLen)
                        lineTo(rectLeft, rectBottom)
                        lineTo(rectLeft + cornerLen, rectBottom)
                    },
                    color = LiquidMint,
                    style = Stroke(width = cornerThick)
                )

                // Bottom-Right Corner
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(rectRight - cornerLen, rectBottom)
                        lineTo(rectRight, rectBottom)
                        lineTo(rectRight, rectBottom - cornerLen)
                    },
                    color = LiquidMint,
                    style = Stroke(width = cornerThick)
                )

                // Laser scan line
                val laserHeight = size.height * laserY
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, LiquidMint.copy(alpha = 0.8f), Color.Transparent)
                    ),
                    topLeft = Offset(rectLeft, laserHeight - 4.dp.toPx()),
                    size = Size(rectRight - rectLeft, 8.dp.toPx())
                )
            }

            // QR Icon centering
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan Merchant QR",
                    tint = TextWhite.copy(alpha = 0.4f),
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    text = "ALIGN QR WITH VIEWPORT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite.copy(alpha = 0.4f),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MerchantConfirmation(
    merchantName: String,
    merchantCode: String,
    amount: Double,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "VERIFY PAYMENT DETAILS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = LiquidMint,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = Color(0xFF00D2FF), // Custom active Cyan/Mint glow
            containerColor = GlassBgGreenLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x2200D2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = merchantName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "Lipha Till: #$merchantCode",
                        fontSize = 14.sp,
                        color = TextMintSecondary
                    )
                }

                Divider(color = Color(0x19FFFFFF), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "PAYMENT AMOUNT", color = TextMintSecondary, fontSize = 12.sp)
                    Text(text = "M ${String.format("%,.2f", amount)}", color = TextWhite, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "NETWORK FEE", color = TextMintSecondary, fontSize = 12.sp)
                    Text(text = "M 0.00 (FREE)", color = LiquidMint, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D2FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "AUTHORIZE SECURE PAYMENT",
                    color = TextDeepDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "ABORT PAYMENT", color = TextError, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PaymentInFlight() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassmorphicCard(
            modifier = Modifier.size(200.dp),
            containerColor = Color(0x22FFFFFF),
            glowColor = Color(0xFF00D2FF)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF00D2FF),
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "SETTLING MERCHANT INVOICE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D2FF),
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentSuccessReceipt(
    receiptId: String,
    merchantName: String,
    amount: Double,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Receipt Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = Color(0xFF00D2FF),
            containerColor = Color(0x3309311A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x6600D2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Success",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "LIPHA SETTLED SUCCESSFULLY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D2FF),
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "M ${String.format("%,.2f", amount)}",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold, color = TextWhite)
                )

                Text(
                    text = "Invoice cleared with $merchantName",
                    fontSize = 14.sp,
                    color = TextMintSecondary,
                    textAlign = TextAlign.Center
                )

                Divider(color = Color(0x19FFFFFF), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "RECEIPT REF", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = receiptId, fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "LIPHA FEE", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = "M 0.00 (FREE)", fontSize = 11.sp, color = LiquidMint, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "LEDGER TYPE", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = "MERCHANT DISBURSE", fontSize = 11.sp, color = TextWhite)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D2FF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "RETURN TO DASHBOARD", color = TextDeepDark, fontWeight = FontWeight.Bold)
        }
    }
}
