package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.model.ActiveTab
import com.example.data.model.BusinessHoursConfig
import com.example.data.model.CommentRule
import com.example.data.model.MessengerRule
import com.example.data.model.MetaAppConfig
import com.example.data.repository.AutomationRepository
import com.example.data.repository.AutomationUiState
import com.example.service.CommentEngineResult
import com.example.service.MessengerEngineResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository: AutomationRepository = AutomationRepository()
) : ViewModel() {

    val uiState: StateFlow<AutomationUiState> = repository.uiState

    private val _currentTab = MutableStateFlow(ActiveTab.DASHBOARD)
    val currentTab: StateFlow<ActiveTab> = _currentTab.asStateFlow()

    fun selectTab(tab: ActiveTab) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        repository.toggleLanguage()
    }

    fun toggleAutomationMaster(active: Boolean) {
        repository.toggleAutomationMaster(active)
    }

    fun toggleMessengerAutomation(enabled: Boolean) {
        repository.toggleMessengerAutomation(enabled)
    }

    fun toggleCommentAutomation(enabled: Boolean) {
        repository.toggleCommentAutomation(enabled)
    }

    fun connectCustomPage(pageId: String, pageName: String, accessToken: String) {
        repository.connectCustomPage(pageId, pageName, accessToken)
    }

    fun disconnectPage() {
        repository.disconnectPage()
    }

    // Messenger Operations
    fun updateWelcomeRule(rule: MessengerRule) {
        repository.updateWelcomeRule(rule)
    }

    fun updateFallbackMessengerRule(rule: MessengerRule) {
        repository.updateFallbackMessengerRule(rule)
    }

    fun updateBusinessHours(config: BusinessHoursConfig) {
        repository.updateBusinessHours(config)
    }

    fun addMessengerRule(rule: MessengerRule) {
        repository.addMessengerRule(rule)
    }

    fun updateMessengerRule(rule: MessengerRule) {
        repository.updateMessengerRule(rule)
    }

    fun deleteMessengerRule(id: String) {
        repository.deleteMessengerRule(id)
    }

    fun toggleMessengerRule(id: String) {
        repository.toggleMessengerRule(id)
    }

    // Comment Operations
    fun updateFallbackCommentRule(rule: CommentRule) {
        repository.updateFallbackCommentRule(rule)
    }

    fun addCommentRule(rule: CommentRule) {
        repository.addCommentRule(rule)
    }

    fun updateCommentRule(rule: CommentRule) {
        repository.updateCommentRule(rule)
    }

    fun deleteCommentRule(id: String) {
        repository.deleteCommentRule(id)
    }

    fun toggleCommentRule(id: String) {
        repository.toggleCommentRule(id)
    }

    // Simulation
    fun simulateMessenger(name: String, text: String, isFirst: Boolean): MessengerEngineResult {
        return repository.simulateMessengerWebhook(name, text, isFirst)
    }

    fun simulateComment(name: String, text: String): CommentEngineResult {
        return repository.simulateCommentWebhook(name, text)
    }

    fun clearLogs() {
        repository.clearLogs()
    }

    fun saveMetaConfig(config: MetaAppConfig) {
        repository.updateMetaConfig(config)
    }

    fun toggleProductStatus(isFacebookLogin: Boolean) {
        repository.toggleProductStatus(isFacebookLogin)
    }

    fun toggleWebhookFieldSubscription(field: String) {
        repository.toggleWebhookFieldSubscription(field)
    }

    fun testWebhookHandshake(): Pair<Boolean, String> {
        return repository.testWebhookHandshake()
    }
}
