package com.ternet.telecom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ternet.telecom.ui.screens.DashboardScreen
import com.ternet.telecom.ui.screens.LoginScreen
import com.ternet.telecom.ui.screens.PayMerchantScreen
import com.ternet.telecom.ui.screens.SendMoneyScreen
import com.ternet.telecom.ui.theme.TernetTelecomTheme
import com.ternet.telecom.viewmodel.DashboardViewModel
import com.ternet.telecom.viewmodel.LoginViewModel
import com.ternet.telecom.viewmodel.PayMerchantViewModel
import com.ternet.telecom.viewmodel.SendMoneyViewModel

/**
 * Screen destinations within Ternet Telecommunications Mobile Core App.
 */
enum class TernetScreen {
    LOGIN,
    DASHBOARD,
    SEND_MONEY,
    PAY_MERCHANT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TernetTelecomTheme {
                TernetAppOrchestrator()
            }
        }
    }
}

/**
 * High-fidelity Orchestrator/Navigation Engine managing state transitions
 * between Login, Dashboard, and payment screens, using smooth animative crossfades.
 */
@Composable
fun TernetAppOrchestrator() {
    var currentScreen by remember { mutableStateOf(TernetScreen.LOGIN) }

    // Instantiate ViewModels using standard compose state-lifecycle storage
    val loginViewModel: LoginViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val sendMoneyViewModel: SendMoneyViewModel = viewModel()
    val payMerchantViewModel: PayMerchantViewModel = viewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 400),
            label = "AppScreenTransition"
        ) { screen ->
            when (screen) {
                TernetScreen.LOGIN -> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            currentScreen = TernetScreen.DASHBOARD
                        }
                    )
                }
                TernetScreen.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToSend = {
                            currentScreen = TernetScreen.SEND_MONEY
                        },
                        onNavigateToMerchant = {
                            currentScreen = TernetScreen.PAY_MERCHANT
                        },
                        onLogout = {
                            loginViewModel.onClearPress() // secure PIN release
                            currentScreen = TernetScreen.LOGIN
                        }
                    )
                }
                TernetScreen.SEND_MONEY -> {
                    SendMoneyScreen(
                        viewModel = sendMoneyViewModel,
                        onBack = {
                            currentScreen = TernetScreen.DASHBOARD
                            dashboardViewModel.refreshData() // refresh ledger feed
                        }
                    )
                }
                TernetScreen.PAY_MERCHANT -> {
                    PayMerchantScreen(
                        viewModel = payMerchantViewModel,
                        onBack = {
                            currentScreen = TernetScreen.DASHBOARD
                            dashboardViewModel.refreshData() // refresh ledger feed
                        }
                    )
                }
            }
        }
    }
}
