package com.ternet.telecom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SendMoneyUiState {
    object Idle : SendMoneyUiState
    object VerifyingRecipient : SendMoneyUiState
    data class RecipientVerified(val name: String) : SendMoneyUiState
    object ProcessingTransfer : SendMoneyUiState
    data class Success(val transactionId: String, val recipientName: String, val amount: Double, val fee: Double) : SendMoneyUiState
    data class Error(val message: String) : SendMoneyUiState
}

class SendMoneyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SendMoneyUiState>(SendMoneyUiState.Idle)
    val uiState: StateFlow<SendMoneyUiState> = _uiState.asStateFlow()

    private val _recipientNumber = MutableStateFlow("")
    val recipientNumber: StateFlow<String> = _recipientNumber.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _calculatedFee = MutableStateFlow(0.0)
    val calculatedFee: StateFlow<Double> = _calculatedFee.asStateFlow()

    fun updateRecipientNumber(number: String) {
        if (number.length <= 15) {
            _recipientNumber.value = number
            if (_uiState.value is SendMoneyUiState.Error) {
                _uiState.value = SendMoneyUiState.Idle
            }
        }
    }

    fun updateAmount(amt: String) {
        // Permit only standard decimal numbers
        if (amt.isEmpty() || amt.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _amount.value = amt
            calculateTransferFee(amt.toDoubleOrNull() ?: 0.0)
            if (_uiState.value is SendMoneyUiState.Error) {
                _uiState.value = SendMoneyUiState.Idle
            }
        }
    }

    /**
     * Replicates the classic M-Pesa tiered transaction fees in Lesotho
     */
    private fun calculateTransferFee(amt: Double) {
        val fee = when {
            amt <= 0.0 -> 0.0
            amt <= 50.0 -> 1.50
            amt <= 100.0 -> 2.50
            amt <= 500.0 -> 7.00
            amt <= 1000.0 -> 12.00
            amt <= 2500.0 -> 22.00
            else -> 35.00
        }
        _calculatedFee.value = fee
    }

    fun verifyRecipient() {
        val phone = _recipientNumber.value.trim()
        val amt = _amount.value.toDoubleOrNull() ?: 0.0

        if (phone.length < 8) {
            _uiState.value = SendMoneyUiState.Error("Please enter a valid Lesotho mobile number.")
            return
        }
        if (amt <= 0.0) {
            _uiState.value = SendMoneyUiState.Error("Please enter a transfer amount greater than zero.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SendMoneyUiState.VerifyingRecipient
            kotlinx.coroutines.delay(1200) // Simulating RPC network handshake

            // Mock contact database lookup
            val name = when {
                phone.endsWith("456") || phone.contains("5812") -> "Thabo Lesenya"
                phone.endsWith("123") || phone.contains("5940") -> "Mpho Ramoholi"
                phone.endsWith("110") || phone.contains("5892") -> "Nthabiseng Lepolesa"
                else -> "Ternet Registered Customer"
            }
            _uiState.value = SendMoneyUiState.RecipientVerified(name)
        }
    }

    fun executeTransfer(confirmedRecipient: String) {
        val amt = _amount.value.toDoubleOrNull() ?: 0.0
        val fee = _calculatedFee.value

        viewModelScope.launch {
            _uiState.value = SendMoneyUiState.ProcessingTransfer
            kotlinx.coroutines.delay(1800) // Emulate banking ledger commits

            val txId = "TT-" + (100000..999999).random() + "-LS"
            _uiState.value = SendMoneyUiState.Success(
                transactionId = txId,
                recipientName = confirmedRecipient,
                amount = amt,
                fee = fee
            )
        }
    }

    fun resetState() {
        _recipientNumber.value = ""
        _amount.value = ""
        _calculatedFee.value = 0.0
        _uiState.value = SendMoneyUiState.Idle
    }
}
