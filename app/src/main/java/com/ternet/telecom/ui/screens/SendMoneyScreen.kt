package com.ternet.telecom.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ternet.telecom.ui.components.GlassmorphicCard
import com.ternet.telecom.ui.components.glassmorphic
import com.ternet.telecom.ui.theme.*
import com.ternet.telecom.viewmodel.SendMoneyUiState
import com.ternet.telecom.viewmodel.SendMoneyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    viewModel: SendMoneyViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val recipientNumber by viewModel.recipientNumber.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val calculatedFee by viewModel.calculatedFee.collectAsState()

    // Clean up state on entry or leave
    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Organic Liquid Backdrop Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundBase, ForestGreen, BackgroundBase)
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobMint.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.4f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.4f),
                radius = size.width * 0.8f
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
                            text = "Send Money",
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
                // Interactive Transitions between forms
                Crossfade(targetState = uiState, label = "SendMoneyFlow") { state ->
                    when (state) {
                        is SendMoneyUiState.Idle, is SendMoneyUiState.VerifyingRecipient, is SendMoneyUiState.Error -> {
                            SendMoneyForm(
                                recipientNumber = recipientNumber,
                                amount = amount,
                                fee = calculatedFee,
                                isVerifying = state is SendMoneyUiState.VerifyingRecipient,
                                errorMessage = (state as? SendMoneyUiState.Error)?.message,
                                onNumberChanged = { viewModel.updateRecipientNumber(it) },
                                onAmountChanged = { viewModel.updateAmount(it) },
                                onVerify = { viewModel.verifyRecipient() }
                            )
                        }
                        is SendMoneyUiState.RecipientVerified -> {
                            RecipientConfirmation(
                                recipientName = state.name,
                                recipientNumber = recipientNumber,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                fee = calculatedFee,
                                onConfirm = { viewModel.executeTransfer(state.name) },
                                onCancel = { viewModel.resetState() }
                            )
                        }
                        is SendMoneyUiState.ProcessingTransfer -> {
                            TransferInFlight()
                        }
                        is SendMoneyUiState.Success -> {
                            TransferSuccessReceipt(
                                txId = state.transactionId,
                                name = state.recipientName,
                                amount = state.amount,
                                fee = state.fee,
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
fun SendMoneyForm(
    recipientNumber: String,
    amount: String,
    fee: Double,
    isVerifying: Boolean,
    errorMessage: String?,
    onNumberChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Description Card
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LiquidMint,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Transfer money instantly to any active mobile wallet in Lesotho. Funds are validated before transfer.",
                    fontSize = 13.sp,
                    color = TextMintSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // Form Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = GlassBgWhiteLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Number Field
                OutlinedTextField(
                    value = recipientNumber,
                    onValueChange = onNumberChanged,
                    label = { Text("Recipient Phone Number") },
                    placeholder = { Text("e.g. 58123456") },
                    prefix = { Text("+266 ", color = TextMintSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                // Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChanged,
                    label = { Text("Transfer Amount") },
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

                // Dynamic Fee Display Card
                if (fee > 0.0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassmorphic(shape = RoundedCornerShape(12.dp), containerColor = Color(0x19FFFFFF))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ESTIMATED TRANSFER FEE:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMintSecondary
                            )
                            Text(
                                text = "M ${String.format("%.2f", fee)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LiquidMint
                            )
                        }
                    }
                }
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

        // Confirm / Send Button (Ternet Premium button)
        Button(
            onClick = onVerify,
            enabled = !isVerifying && recipientNumber.isNotEmpty() && amount.isNotEmpty(),
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
                    containerColor = if (!isVerifying && recipientNumber.isNotEmpty() && amount.isNotEmpty()) Color(0x00FFFFFF) else Color(0x1EFFFFFF),
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
                        text = "VERIFY TRANSACTION RECIPIENT",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LiquidMint)
                }
            }
        }
    }
}

@Composable
fun RecipientConfirmation(
    recipientName: String,
    recipientNumber: String,
    amount: Double,
    fee: Double,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "VERIFY TRANSFER DETAILS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = LiquidMint,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        // Main Glass Confirmation Plate
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = MintGreen,
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
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x3300FF87)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = LiquidMint,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = recipientName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                    )
                    Text(
                        text = "+266 $recipientNumber",
                        fontSize = 14.sp,
                        color = TextMintSecondary
                    )
                }

                Divider(color = Color(0x19FFFFFF), thickness = 1.dp)

                // Transaction stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "SEND AMOUNT", color = TextMintSecondary, fontSize = 12.sp)
                    Text(text = "M ${String.format("%,.2f", amount)}", color = TextWhite, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TRANSACTION FEE", color = TextMintSecondary, fontSize = 12.sp)
                    Text(text = "M ${String.format("%,.2f", fee)}", color = TextWhite, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TOTAL DEDUCTION", color = LiquidMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "M ${String.format("%,.2f", amount + fee)}", color = LiquidMint, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Confirm / Cancel Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "CONFIRM & TRANSMIT FUNDS",
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
                Text(text = "CANCEL TRANSACTION", color = TextError, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TransferInFlight() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .size(200.dp),
            containerColor = Color(0x22FFFFFF),
            glowColor = LiquidMint
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = LiquidMint,
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "COMMITTING TRANSACTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidMint,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Securing funds...",
                    fontSize = 12.sp,
                    color = TextWhite,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TransferSuccessReceipt(
    txId: String,
    name: String,
    amount: Double,
    fee: Double,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Large Success Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            glowColor = LiquidMint,
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
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x662ED573)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "TRANSACTION SUCCESSFUL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidMint,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "M ${String.format("%,.2f", amount)}",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold, color = TextWhite)
                )

                Text(
                    text = "Sent securely to $name",
                    fontSize = 14.sp,
                    color = TextMintSecondary,
                    textAlign = TextAlign.Center
                )

                Divider(color = Color(0x19FFFFFF), thickness = 1.dp)

                // Receipts logs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "REFERENCE ID", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = txId, fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "CHARGE FEE", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = "M ${String.format("%.2f", fee)}", fontSize = 11.sp, color = TextWhite)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "CHANNEL", fontSize = 11.sp, color = TextMintSecondary)
                    Text(text = "TERNET PRESERVED", fontSize = 11.sp, color = LiquidMint, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "RETURN TO DASHBOARD", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}
