package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.ActiveTab
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.ActivityLogsScreen
import com.example.ui.screens.CommentAutomationScreen
import com.example.ui.screens.ConnectFacebookScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MessengerAutomationScreen
import com.example.ui.screens.SettingsAndExportScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle back button on sub-screens to return to Dashboard
    BackHandler(enabled = currentTab != ActiveTab.DASHBOARD) {
        viewModel.selectTab(ActiveTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                language = uiState.language,
                isAutomationActive = uiState.isAutomationActive,
                userSession = uiState.userSession,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleAutomation = { viewModel.toggleAutomationMaster(it) }
            )
        },
        bottomBar = {
            AppBottomNav(
                currentTab = currentTab,
                language = uiState.language,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ActiveTab.DASHBOARD -> {
                    DashboardScreen(
                        state = uiState,
                        onNavigate = { viewModel.selectTab(it) },
                        onToggleMaster = { viewModel.toggleAutomationMaster(it) }
                    )
                }
                ActiveTab.CONNECT -> {
                    ConnectFacebookScreen(
                        state = uiState,
                        onConnectCustomPage = { id, name, token ->
                            viewModel.connectCustomPage(id, name, token)
                        },
                        onDisconnectPage = { viewModel.disconnectPage() }
                    )
                }
                ActiveTab.MESSENGER -> {
                    MessengerAutomationScreen(
                        state = uiState,
                        onToggleMessenger = { viewModel.toggleMessengerAutomation(it) },
                        onUpdateWelcomeRule = { viewModel.updateWelcomeRule(it) },
                        onUpdateFallbackRule = { viewModel.updateFallbackMessengerRule(it) },
                        onUpdateBusinessHours = { viewModel.updateBusinessHours(it) },
                        onAddRule = { viewModel.addMessengerRule(it) },
                        onUpdateRule = { viewModel.updateMessengerRule(it) },
                        onDeleteRule = { viewModel.deleteMessengerRule(it) },
                        onToggleRule = { viewModel.toggleMessengerRule(it) }
                    )
                }
                ActiveTab.COMMENTS -> {
                    CommentAutomationScreen(
                        state = uiState,
                        onToggleComment = { viewModel.toggleCommentAutomation(it) },
                        onUpdateFallbackRule = { viewModel.updateFallbackCommentRule(it) },
                        onAddRule = { viewModel.addCommentRule(it) },
                        onUpdateRule = { viewModel.updateCommentRule(it) },
                        onDeleteRule = { viewModel.deleteCommentRule(it) },
                        onToggleRule = { viewModel.toggleCommentRule(it) }
                    )
                }
                ActiveTab.SIMULATOR -> {
                    SimulatorScreen(
                        state = uiState,
                        onSimulateMessenger = { name, text, isFirst ->
                            viewModel.simulateMessenger(name, text, isFirst)
                        },
                        onSimulateComment = { name, text ->
                            viewModel.simulateComment(name, text)
                        }
                    )
                }
                ActiveTab.LOGS -> {
                    ActivityLogsScreen(
                        state = uiState,
                        onClearLogs = { viewModel.clearLogs() }
                    )
                }
                ActiveTab.EXPORT -> {
                    SettingsAndExportScreen(
                        state = uiState,
                        onSaveMetaConfig = { viewModel.saveMetaConfig(it) },
                        onToggleProduct = { viewModel.toggleProductStatus(it) },
                        onToggleWebhookSub = { viewModel.toggleWebhookFieldSubscription(it) },
                        onTestHandshake = { viewModel.testWebhookHandshake() }
                    )
                }
            }
        }
    }
}
