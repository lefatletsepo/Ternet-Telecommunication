package com.ternet.telecom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _pinState = MutableStateFlow("")
    val pinState: StateFlow<String> = _pinState.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess: SharedFlow<Boolean> = _loginSuccess.asSharedFlow()

    fun onKeyPress(char: Char) {
        _errorMessage.value = null
        if (_pinState.value.length < 4) {
            _pinState.value += char
            if (_pinState.value.length == 4) {
                // Automatically attempt authentication when 4 digits are completed
                authenticatePin(_pinState.value)
            }
        }
    }

    fun onDeletePress() {
        _errorMessage.value = null
        if (_pinState.value.isNotEmpty()) {
            _pinState.value = _pinState.value.dropLast(1)
        }
    }

    fun onClearPress() {
        _pinState.value = ""
        _errorMessage.value = null
    }

    private fun authenticatePin(pin: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            // Emulate brief secure network delay
            kotlinx.coroutines.delay(1000)
            
            // Standard static verification for demo purposes (e.g., standard demo passcode "1234" or "0000")
            if (pin == "2660" || pin == "1234" || pin == "0000") {
                _loginSuccess.emit(true)
            } else {
                _pinState.value = "" // clear PIN
                _errorMessage.value = "Incorrect Security PIN. Please try again."
                _loginSuccess.emit(false)
            }
            _isAuthenticating.value = false
        }
    }
}
