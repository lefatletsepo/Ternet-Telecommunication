package com.ternet.telecom.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ternet.telecom.domain.model.Transaction
import com.ternet.telecom.domain.model.TransactionType
import com.ternet.telecom.domain.model.WalletState
import com.ternet.telecom.ui.components.GlassmorphicCard
import com.ternet.telecom.ui.components.glassmorphic
import com.ternet.telecom.ui.theme.*
import com.ternet.telecom.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSend: () -> Unit,
    onNavigateToMerchant: () -> Unit,
    onLogout: () -> Unit
) {
    val walletState by viewModel.walletState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    // Animation transition triggers
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundWaves")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Waves"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Core Canvas Background with moving organic mint blobs for the Liquid Glass look
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundBase, ForestGreen, BackgroundBase)
                )
            )

            // Dynamic Blob A
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobMint.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.25f + waveOffset),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.25f + waveOffset),
                radius = size.width * 0.65f
            )

            // Dynamic Blob B
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LiquidBlobGreen.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(size.width * 0.15f + waveOffset / 2f, size.height * 0.7f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 0.15f + waveOffset / 2f, size.height * 0.7f),
                radius = size.width * 0.8f
            )
        }

        // 2. Primary Layout Scaffold
        Scaffold(
            containerColor = Color.Transparent, // Let Canvas shine through
            topBar = {
                // Glassmorphic Top Bar
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0x11051C0F), // Ultra transparent dark
                        titleContentColor = TextWhite
                    ),
                    modifier = Modifier.glassmorphic(
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                        containerColor = Color(0x14FFFFFF),
                        borderWidth = 0.8.dp
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Ternet",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .glassmorphic(shape = RoundedCornerShape(6.dp), containerColor = Color(0x4D00FF87))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LESOTHO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDeepDark
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.refreshData() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh wallet",
                                tint = LiquidMint
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Exit session",
                                tint = TextError
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ITEM 1: Lumela / Profile Header Greeting
                item {
                    GreetingHeader(userName = walletState.userName, tier = walletState.accountTier)
                }

                // ITEM 2: Premium Glassmorphic Wallet Balance Card (Centerpiece)
                item {
                    WalletBalanceCard(
                        state = walletState,
                        onToggleVisibility = { viewModel.toggleBalanceVisibility() }
                    )
                }

                // ITEM 3: Quick Action Grid (Send Money, Lipha Merchant, Agent Cashout, Airtime)
                item {
                    Text(
                        text = "QUICK TRANSACTIONS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LiquidMint,
                            letterSpacing = 1.5.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    
                    QuickActionsGrid(
                        onSendSelected = onNavigateToSend,
                        onLiphaSelected = onNavigateToMerchant,
                        onCashOutSelected = {}, // Sheet/modal mock trigger
                        onAirtimeSelected = {}
                    )
                }

                // ITEM 4: Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT LEDGER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LiquidMint,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "See All",
                            color = TextMintSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { /* See all feed */ }
                        )
                    }
                }

                // ITEM 5: List of Transactions inside a unified Glass Container
                if (transactions.isEmpty()) {
                    item {
                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            containerColor = GlassBgWhiteLow
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recorded operations in this session.",
                                    color = TextMintSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(transactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingHeader(userName: String, tier: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Lumela,", // Sotho Greeting
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMintSecondary)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    letterSpacing = (-0.5).sp
                )
            )
        }
        
        // Premium Profile Glass Badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .glassmorphic(shape = CircleShape, containerColor = Color(0x22FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Account Profile",
                tint = LiquidMint,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun WalletBalanceCard(
    state: WalletState,
    onToggleVisibility: () -> Unit
) {
    // Elegant glass card with a high luminance mint glow (Mint border highlight)
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = MintGreen,
        containerColor = GlassBgGreenLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header Row: Wallet Name & Visibility Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallet,
                        contentDescription = null,
                        tint = LiquidMint,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Ternet Telecom Wallet",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Balance Hider Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x1EFFFFFF))
                        .clickable { onToggleVisibility() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isBalanceVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Balance",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Balance Visualizer
            Crossfade(targetState = state.isBalanceVisible, label = "BalanceTransition") { visible ->
                val displayBal = if (visible) state.formattedBalance else state.maskedBalance
                Column {
                    Text(
                        text = displayBal,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            fontSize = 38.sp,
                            letterSpacing = (-1).sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Limits Tracker Line
            Divider(color = Color(0x19FFFFFF), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DAILY CAP REMAINING",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMintSecondary
                        )
                    )
                    Text(
                        text = "M ${String.format("%,.2f", state.dailyLimitRemaining)}",
                        color = LiquidMint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ACCOUNT TYPE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextMintSecondary
                        )
                    )
                    Box(
                        modifier = Modifier
                            .glassmorphic(shape = RoundedCornerShape(10.dp), containerColor = Color(0x2200FF87))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = state.accountTier.uppercase(),
                            color = LiquidMint,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    onSendSelected: () -> Unit,
    onLiphaSelected: () -> Unit,
    onCashOutSelected: () -> Unit,
    onAirtimeSelected: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val actions = listOf(
            QuickActionItem("Send Money", Icons.Default.ArrowUpward, LiquidMint, onSendSelected),
            QuickActionItem("Lipha (Pay)", Icons.Default.QrCodeScanner, Color(0xFF00D2FF), onLiphaSelected),
            QuickActionItem("Cash Out", Icons.Default.LocalAtm, Color(0xFFFFB300), onCashOutSelected),
            QuickActionItem("Buy Airtime", Icons.Default.Smartphone, Color(0xFFFF4D88), onAirtimeSelected)
        )

        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { action.onClick() }
                    .padding(vertical = 8.dp)
            ) {
                // Gorgeous Glass Circle base
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .glassmorphic(shape = CircleShape, containerColor = GlassBgWhiteMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = action.tintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class QuickActionItem(
    val label: String,
    val icon: ImageVector,
    val tintColor: Color,
    val onClick: () -> Unit
)

@Composable
fun TransactionItem(transaction: Transaction) {
    val dateString = remember(transaction.timestamp) {
        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        formatter.format(transaction.timestamp)
    }

    val icon = when (transaction.type) {
        TransactionType.SEND_MONEY -> Icons.Default.CallMade
        TransactionType.PAY_MERCHANT -> Icons.Default.Storefront
        TransactionType.CASH_OUT -> Icons.Default.LocalAtm
        TransactionType.DEPOSIT -> Icons.Default.CallReceived
        TransactionType.BUY_AIRTIME -> Icons.Default.PhoneAndroid
        TransactionType.UTILITY_BILL -> Icons.Default.ReceiptLong
    }

    val itemIconTint = when (transaction.type) {
        TransactionType.DEPOSIT -> LiquidMint
        TransactionType.SEND_MONEY -> Color(0xFFFF9F43)
        TransactionType.PAY_MERCHANT -> Color(0xFF00D2FF)
        else -> TextWhite
    }

    // Frosted glass background for single row item
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        containerColor = Color(0x19FFFFFF), // lighter frosted overlay
        shape = RoundedCornerShape(16.dp),
        borderWidth = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circle icon backing
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .glassmorphic(shape = CircleShape, containerColor = Color(0x1EFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = itemIconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Name and Description Column
                Column {
                    Text(
                        text = transaction.partyName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${transaction.displayType} • $dateString",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMintSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Numeric Value representation
            Column(horizontalAlignment = Alignment.End) {
                val isDeposit = transaction.type == TransactionType.DEPOSIT
                Text(
                    text = transaction.formattedAmount,
                    color = if (isDeposit) LiquidMint else TextWhite,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "ID: ${transaction.id.takeLast(6)}",
                    color = TextMintSecondary.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
