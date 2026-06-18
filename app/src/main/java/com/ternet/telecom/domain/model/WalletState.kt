package com.ternet.telecom.domain.model

/**
 * Encapsulates the user's core balance, limit configurations, and subscription type.
 */
data class WalletState(
    val userName: String,
    val phoneNumber: String,          // Lesotho format, e.g., "+266 5812 3456"
    val balance: Double,               // In Maloti (M)
    val isBalanceVisible: Boolean = false,
    val dailyLimitRemaining: Double,   // Daily transaction limits
    val maxSingleTransaction: Double,  // Single cap
    val accountTier: String = "Premium Personal"
) {
    /**
     * Helper to return clean currency representation.
     */
    val formattedBalance: String
        get() = "M ${String.format("%,.2f", balance)}"

    val maskedBalance: String
        get() = "M ••••••••"
}
