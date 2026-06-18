package com.ternet.telecom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PayMerchantUiState {
    object Idle : PayMerchantUiState
    object VerifyingMerchant : PayMerchantUiState
    data class MerchantVerified(val merchantName: String) : PayMerchantUiState
    object ProcessingPayment : PayMerchantUiState
    data class Success(val receiptId: String, val merchantName: String, val amount: Double) : PayMerchantUiState
    data class Error(val message: String) : PayMerchantUiState
}

class PayMerchantViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PayMerchantUiState>(PayMerchantUiState.Idle)
    val uiState: StateFlow<PayMerchantUiState> = _uiState.asStateFlow()

    private val _merchantCode = MutableStateFlow("")
    val merchantCode: StateFlow<String> = _merchantCode.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    fun updateMerchantCode(code: String) {
        // Merchant lipha codes are usually 4 to 6 digit codes
        if (code.length <= 8 && code.all { it.isDigit() }) {
            _merchantCode.value = code
            if (_uiState.value is PayMerchantUiState.Error) {
                _uiState.value = PayMerchantUiState.Idle
            }
        }
    }

    fun updateAmount(amt: String) {
        if (amt.isEmpty() || amt.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _amount.value = amt
            if (_uiState.value is PayMerchantUiState.Error) {
                _uiState.value = PayMerchantUiState.Idle
            }
        }
    }

    fun verifyMerchant() {
        val code = _merchantCode.value.trim()
        val amt = _amount.value.toDoubleOrNull() ?: 0.0

        if (code.length < 4) {
            _uiState.value = PayMerchantUiState.Error("Please enter a valid 4-6 digit Lipha Merchant code.")
            return
        }
        if (amt <= 0.0) {
            _uiState.value = PayMerchantUiState.Error("Please enter a valid payment amount.")
            return
        }

        viewModelScope.launch {
            _uiState.value = PayMerchantUiState.VerifyingMerchant
            kotlinx.coroutines.delay(1000)

            // Emulated database of major merchants in Lesotho
            val name = when (code) {
                "4109" -> "Shoprite Pioneer Mall"
                "1024" -> "Sefalana Maseru"
                "2048" -> "Celtis Pharmacy Maseru"
                "7712" -> "Vodacom World Lesotho"
                else -> "Ternet Registered Merchant (Code $code)"
            }
            _uiState.value = PayMerchantUiState.MerchantVerified(name)
        }
    }

    fun executePayment(confirmedMerchant: String) {
        val amt = _amount.value.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = PayMerchantUiState.ProcessingPayment
            kotlinx.coroutines.delay(1500)

            val receiptId = "LP-" + (1000000..9999999).random() + "-LS"
            _uiState.value = PayMerchantUiState.Success(
                receiptId = receiptId,
                merchantName = confirmedMerchant,
                amount = amt
            )
        }
    }

    fun resetState() {
        _merchantCode.value = ""
        _amount.value = ""
        _uiState.value = PayMerchantUiState.Idle
    }
}
