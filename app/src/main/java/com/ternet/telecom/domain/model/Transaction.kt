package com.ternet.telecom.domain.model

import java.util.Date

/**
 * High-fidelity business model representing transactions within the Ternet (M-Pesa Lesotho context).
 * Lesotho currency is the Loti (plural Maloti), denoted by symbol "M" or "LSL".
 */
enum class TransactionType {
    SEND_MONEY,
    PAY_MERCHANT,   // Lipha Merchant
    CASH_OUT,       // Agent withdrawal
    DEPOSIT,        // Cash-in
    BUY_AIRTIME,    // Airtime/Bundles
    UTILITY_BILL    // Services
}

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    FAILED
}

data class Transaction(
    val id: String,                    // e.g., "PP260618.1215.A401"
    val type: TransactionType,
    val amount: Double,                // In Maloti (M)
    val fee: Double,                   // Transaction fee charged in Maloti
    val partyName: String,             // Recipient name, Agent name, or Merchant brand name
    val partyNumber: String,           // Recipient phone, Agent code, or Merchant Lipha number
    val timestamp: Date,
    val status: TransactionStatus,
    val reference: String? = null
) {
    /**
     * Formats the amount for display depending on whether it's incoming or outgoing.
     */
    val formattedAmount: String
        get() {
            val prefix = when (type) {
                TransactionType.DEPOSIT -> "+"
                else -> "-"
            }
            return "$prefix M ${String.format("%.2f", amount)}"
        }

    /**
     * Readable representation of the type in an administrative context.
     */
    val displayType: String
        get() = when (type) {
            TransactionType.SEND_MONEY -> "Send Money"
            TransactionType.PAY_MERCHANT -> "Lipha Merchant"
            TransactionType.CASH_OUT -> "Agent Cash-Out"
            TransactionType.DEPOSIT -> "Agent Deposit"
            TransactionType.BUY_AIRTIME -> "Airtime & Bundles"
            TransactionType.UTILITY_BILL -> "Bill Payment"
        }
}
