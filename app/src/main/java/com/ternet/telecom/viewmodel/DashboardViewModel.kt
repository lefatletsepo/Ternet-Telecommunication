package com.ternet.telecom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ternet.telecom.domain.model.Transaction
import com.ternet.telecom.domain.model.TransactionStatus
import com.ternet.telecom.domain.model.TransactionType
import com.ternet.telecom.domain.model.WalletState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for managing the state, visibility, and user actions on the Home Dashboard.
 */
class DashboardViewModel : ViewModel() {

    private val _walletState = MutableStateFlow(
        WalletState(
            userName = "Nthabiseng Lepolesa",
            phoneNumber = "+266 5892 4110", // Lesotho Mobile Number format
            balance = 14520.75,             // In Lesotho Maloti (M)
            isBalanceVisible = false,        // Default hidden for privacy
            dailyLimitRemaining = 25000.00,
            maxSingleTransaction = 5000.00,
            accountTier = "Ternet Gold Tier"
        )
    )
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        loadMockTransactions()
    }

    /**
     * Toggles the visibility of the primary balance on the dashboard card.
     */
    fun toggleBalanceVisibility() {
        _walletState.update { current ->
            current.copy(isBalanceVisible = !current.isBalanceVisible)
        }
    }

    /**
     * Simulates refreshing the wallet state and transactions.
     */
    fun refreshData() {
        viewModelScope.launch {
            // Emulate slight delay for modern network requests
            _walletState.update { current ->
                current.copy(balance = current.balance + (Math.random() * 10 - 5)) // slight mock flux
            }
        }
    }

    private fun loadMockTransactions() {
        _transactions.value = listOf(
            Transaction(
                id = "TX-260618-102",
                type = TransactionType.PAY_MERCHANT,
                amount = 450.00,
                fee = 4.50,
                partyName = "Shoprite Pioneer Mall", // Lesotho commercial landmark
                partyNumber = "LIPHA-4109",
                timestamp = Date(System.currentTimeMillis() - 3600000 * 2), // 2 hrs ago
                status = TransactionStatus.SUCCESS,
                reference = "Maseru Groceries"
            ),
            Transaction(
                id = "TX-260618-101",
                type = TransactionType.SEND_MONEY,
                amount = 1200.00,
                fee = 15.00,
                partyName = "Kekeletso Molapo",
                partyNumber = "+266 5940 1211",
                timestamp = Date(System.currentTimeMillis() - 3600000 * 18), // 18 hrs ago
                status = TransactionStatus.SUCCESS,
                reference = "Rent Share"
            ),
            Transaction(
                id = "TX-260617-095",
                type = TransactionType.BUY_AIRTIME,
                amount = 50.00,
                fee = 0.00,
                partyName = "Ternet Mint Bundle 1GB",
                partyNumber = "*121#",
                timestamp = Date(System.currentTimeMillis() - 3600000 * 30), // 30 hrs ago
                status = TransactionStatus.SUCCESS,
                reference = "Airtime Self"
            ),
            Transaction(
                id = "TX-260617-084",
                type = TransactionType.DEPOSIT,
                amount = 3000.00,
                fee = 0.00,
                partyName = "Maseru Crossing Agent", // Lesotho retail agent hub
                partyNumber = "AG-7119",
                timestamp = Date(System.currentTimeMillis() - 3600000 * 48), // 2 days ago
                status = TransactionStatus.SUCCESS,
                reference = "Cash In Deposit"
            ),
            Transaction(
                id = "TX-260616-012",
                type = TransactionType.CASH_OUT,
                amount = 500.00,
                fee = 12.00,
                partyName = "Kingsway Shell Agent",
                partyNumber = "AG-9082",
                timestamp = Date(System.currentTimeMillis() - 3600000 * 72), // 3 days ago
                status = TransactionStatus.SUCCESS,
                reference = "Cashout urgent"
            )
        )
    }
}
