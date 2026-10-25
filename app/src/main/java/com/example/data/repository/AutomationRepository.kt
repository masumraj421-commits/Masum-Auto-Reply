package com.example.data.repository

import com.example.data.model.ActivityLog
import com.example.data.model.BusinessHoursConfig
import com.example.data.model.CommentRule
import com.example.data.model.FacebookPage
import com.example.data.model.LogStatus
import com.example.data.model.LogType
import com.example.data.model.MessengerRule
import com.example.data.model.MetaAppConfig
import com.example.data.model.MetaPermissionItem
import com.example.data.model.UserSession
import com.example.service.CommentEngineResult
import com.example.service.MessengerEngineResult
import com.example.service.MetaAutomationEngine
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class AutomationUiState(
    val language: AppLanguage = AppLanguage.BANGLA,
    val userSession: UserSession = UserSession(),
    val isAutomationActive: Boolean = true,
    val isMessengerEnabled: Boolean = true,
    val isCommentEnabled: Boolean = true,
    val connectedPage: FacebookPage? = FacebookPage(
        id = "vawalfreshfood",
        name = "Vawal Fresh Food (ভাওয়াল ফ্রেশ ফুড)",
        category = "Organic Agro & Fresh Food Store",
        accessToken = "EAABwzLIX4nwBO1kX9mK8j3p82n4Z...",
        followersCount = 18500,
        profilePicUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=150",
        isConnected = true,
        tokenExpiresAt = "Valid & Active (Page Token for @vawalfreshfood)",
        permissions = listOf(
            "pages_show_list",
            "pages_read_engagement",
            "pages_manage_metadata",
            "pages_manage_engagement",
            "pages_messaging"
        )
    ),
    val messengerRules: List<MessengerRule> = defaultMessengerRules(),
    val welcomeMessengerRule: MessengerRule = defaultWelcomeRule(),
    val fallbackMessengerRule: MessengerRule = defaultFallbackMessengerRule(),
    val commentRules: List<CommentRule> = defaultCommentRules(),
    val fallbackCommentRule: CommentRule = defaultFallbackCommentRule(),
    val businessHours: BusinessHoursConfig = BusinessHoursConfig(),
    val activityLogs: List<ActivityLog> = defaultActivityLogs(),
    val metaAppConfig: MetaAppConfig = MetaAppConfig(),
    val metaPermissions: List<MetaPermissionItem> = defaultMetaPermissions(),
    val totalMessagesHandled: Int = 1842,
    val totalCommentsHandled: Int = 3490,
    val successRatePercent: Int = 99
)

class AutomationRepository {

    private val engine = MetaAutomationEngine()
    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    fun toggleLanguage() {
        _uiState.update { current ->
            val nextLang = if (current.language == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
            current.copy(language = nextLang)
        }
    }

    fun setLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language) }
    }

    fun toggleAutomationMaster(active: Boolean) {
        _uiState.update { it.copy(isAutomationActive = active) }
        logActivity(
            type = LogType.TOKEN_CHECK,
            title = if (active) "অটোমেশন সক্রিয় করা হয়েছে" else "অটোমেশন সাময়িক বন্ধ করা হয়েছে",
            details = if (active) "Master automation switch turned ON" else "Master automation switch turned OFF",
            status = LogStatus.INFO
        )
    }

    fun toggleMessengerAutomation(enabled: Boolean) {
        _uiState.update { it.copy(isMessengerEnabled = enabled) }
    }

    fun toggleCommentAutomation(enabled: Boolean) {
        _uiState.update { it.copy(isCommentEnabled = enabled) }
    }

    fun connectCustomPage(pageId: String, pageName: String, accessToken: String) {
        val newPage = FacebookPage(
            id = pageId.ifBlank { "104928374829102" },
            name = pageName.ifBlank { "My Facebook Page" },
            category = "Business / Brand Page",
            accessToken = if (accessToken.isNotBlank()) accessToken else "EAABwz..." + UUID.randomUUID().toString().take(12),
            followersCount = 1250,
            profilePicUrl = "",
            isConnected = true,
            tokenExpiresAt = "Valid (Page Token)",
            permissions = listOf(
                "pages_show_list",
                "pages_read_engagement",
                "pages_manage_metadata",
                "pages_manage_engagement",
                "pages_messaging"
            )
        )
        _uiState.update { it.copy(connectedPage = newPage) }
        logActivity(
            type = LogType.TOKEN_CHECK,
            title = "ফেসবুক পেজ সংযুক্ত সফল",
            details = "Page: ${newPage.name} (ID: ${newPage.id}) connected via Graph API",
            status = LogStatus.SUCCESS
        )
    }

    fun disconnectPage() {
        _uiState.update { it.copy(connectedPage = null) }
        logActivity(
            type = LogType.TOKEN_CHECK,
            title = "পেজ সংযোগ বিচ্ছিন্ন করা হয়েছে",
            details = "Disconnected from Facebook Page",
            status = LogStatus.WARNING
        )
    }

    // Messenger Rule Operations
    fun updateWelcomeRule(rule: MessengerRule) {
        _uiState.update { it.copy(welcomeMessengerRule = rule) }
    }

    fun updateFallbackMessengerRule(rule: MessengerRule) {
        _uiState.update { it.copy(fallbackMessengerRule = rule) }
    }

    fun updateBusinessHours(config: BusinessHoursConfig) {
        _uiState.update { it.copy(businessHours = config) }
    }

    fun addMessengerRule(rule: MessengerRule) {
        _uiState.update { current ->
            current.copy(messengerRules = listOf(rule) + current.messengerRules)
        }
    }

    fun updateMessengerRule(updated: MessengerRule) {
        _uiState.update { current ->
            current.copy(
                messengerRules = current.messengerRules.map { if (it.id == updated.id) updated else it }
            )
        }
    }

    fun deleteMessengerRule(ruleId: String) {
        _uiState.update { current ->
            current.copy(messengerRules = current.messengerRules.filterNot { it.id == ruleId })
        }
    }

    fun toggleMessengerRule(ruleId: String) {
        _uiState.update { current ->
            current.copy(
                messengerRules = current.messengerRules.map {
                    if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it
                }
            )
        }
    }

    // Comment Rule Operations
    fun updateFallbackCommentRule(rule: CommentRule) {
        _uiState.update { it.copy(fallbackCommentRule = rule) }
    }

    fun addCommentRule(rule: CommentRule) {
        _uiState.update { current ->
            current.copy(commentRules = listOf(rule) + current.commentRules)
        }
    }

    fun updateCommentRule(updated: CommentRule) {
        _uiState.update { current ->
            current.copy(
                commentRules = current.commentRules.map { if (it.id == updated.id) updated else it }
            )
        }
    }

    fun deleteCommentRule(ruleId: String) {
        _uiState.update { current ->
            current.copy(commentRules = current.commentRules.filterNot { it.id == ruleId })
        }
    }

    fun toggleCommentRule(ruleId: String) {
        _uiState.update { current ->
            current.copy(
                commentRules = current.commentRules.map {
                    if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it
                }
            )
        }
    }

    // Simulated Webhook Test - Messenger
    fun simulateMessengerWebhook(
        senderName: String,
        messageText: String,
        isFirstMessage: Boolean = false
    ): MessengerEngineResult {
        val state = _uiState.value
        val senderId = "psid_${Math.abs(senderName.hashCode())}"

        val result = engine.evaluateMessenger(
            senderId = senderId,
            senderName = senderName,
            messageText = messageText,
            isFirstMessage = isFirstMessage,
            businessHours = state.businessHours,
            rules = state.messengerRules,
            welcomeRule = state.welcomeMessengerRule,
            defaultFallback = state.fallbackMessengerRule
        )

        // Increment stats & log
        _uiState.update { current ->
            current.copy(
                totalMessagesHandled = current.totalMessagesHandled + 1,
                messengerRules = current.messengerRules.map { rule ->
                    if (rule.name == result.matchedRuleName) rule.copy(matchCount = rule.matchCount + 1) else rule
                }
            )
        }

        logActivity(
            type = LogType.MESSENGER,
            title = "ইনবক্স মেসেজ অটো-রিপ্লাই (${result.matchedRuleName})",
            details = "প্রেরক: $senderName | মেসেজ: \"$messageText\" -> উত্তর: \"${result.replyText}\"",
            status = LogStatus.SUCCESS,
            senderName = senderName,
            metaEventId = "mid_${System.currentTimeMillis()}",
            responsePayload = result.graphApiPayload
        )

        return result
    }

    // Simulated Webhook Test - Comment
    fun simulateCommentWebhook(
        commenterName: String,
        commentText: String,
        postId: String = "post_9921",
        commentId: String = "cmt_${System.currentTimeMillis()}"
    ): CommentEngineResult {
        val state = _uiState.value

        val result = engine.evaluateComment(
            commentId = commentId,
            postId = postId,
            commenterName = commenterName,
            commentText = commentText,
            rules = state.commentRules,
            defaultFallback = state.fallbackCommentRule
        )

        if (!result.isDuplicate) {
            _uiState.update { current ->
                current.copy(
                    totalCommentsHandled = current.totalCommentsHandled + 1,
                    commentRules = current.commentRules.map { rule ->
                        if (rule.name == result.matchedRuleName) rule.copy(matchCount = rule.matchCount + 1) else rule
                    }
                )
            }
        }

        logActivity(
            type = LogType.COMMENT,
            title = if (result.isDuplicate) "ডুপ্লিকেট কমেন্ট স্কিপ করা হয়েছে" else "পোস্ট কমেন্টে রিপ্লাই সফল",
            details = "কমেন্টকারী: $commenterName | কমেন্ট: \"$commentText\" -> রিপ্লাই: \"${result.publicReplyText}\"" +
                    if (result.sendPrivateMessage) " (ইনবক্স প্রাইভেট মেসেজ পাঠানো হয়েছে)" else "",
            status = if (result.isDuplicate) LogStatus.WARNING else LogStatus.SUCCESS,
            senderName = commenterName,
            metaEventId = commentId,
            responsePayload = result.graphApiPayload
        )

        return result
    }

    fun logActivity(
        type: LogType,
        title: String,
        details: String,
        status: LogStatus = LogStatus.SUCCESS,
        senderName: String = "",
        metaEventId: String = "evt_${System.currentTimeMillis()}",
        responsePayload: String = ""
    ) {
        val newLog = ActivityLog(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            details = details,
            timestamp = System.currentTimeMillis(),
            status = status,
            senderName = senderName,
            metaEventId = metaEventId,
            responsePayload = responsePayload
        )
        _uiState.update { current ->
            current.copy(activityLogs = listOf(newLog) + current.activityLogs.take(49))
        }
    }

    fun clearLogs() {
        _uiState.update { it.copy(activityLogs = emptyList()) }
    }

    fun updateMetaConfig(config: MetaAppConfig) {
        _uiState.update { it.copy(metaAppConfig = config) }
    }

    fun toggleProductStatus(isFacebookLogin: Boolean) {
        _uiState.update { current ->
            val updatedConfig = if (isFacebookLogin) {
                current.metaAppConfig.copy(isFacebookLoginAdded = !current.metaAppConfig.isFacebookLoginAdded)
            } else {
                current.metaAppConfig.copy(isWebhooksAdded = !current.metaAppConfig.isWebhooksAdded)
            }
            current.copy(metaAppConfig = updatedConfig)
        }
        val name = if (isFacebookLogin) "Facebook Login for Business" else "Webhooks"
        val isAdded = if (isFacebookLogin) _uiState.value.metaAppConfig.isFacebookLoginAdded else _uiState.value.metaAppConfig.isWebhooksAdded
        logActivity(
            type = LogType.TOKEN_CHECK,
            title = if (isAdded) "$name প্রোডাক্ট সফলভাবে কনফিগার ও যুক্ত করা হয়েছে" else "$name প্রোডাক্ট নিষ্ক্রিয় করা হয়েছে",
            details = "Meta Developer App: Product status set to ${if (isAdded) "ACTIVE (Configured)" else "INACTIVE"}",
            status = LogStatus.INFO
        )
    }

    fun toggleWebhookFieldSubscription(field: String) {
        _uiState.update { current ->
            val currentSubs = current.metaAppConfig.webhookSubscriptions
            val updated = if (currentSubs.contains(field)) currentSubs - field else currentSubs + field
            current.copy(metaAppConfig = current.metaAppConfig.copy(webhookSubscriptions = updated))
        }
    }

    fun testWebhookHandshake(): Pair<Boolean, String> {
        val config = _uiState.value.metaAppConfig
        val challenge = "challenge_" + (100000..999999).random()
        val queryUrl = "${config.webhookCallbackUrl}?hub.mode=subscribe&hub.challenge=$challenge&hub.verify_token=${config.verifyToken}"

        logActivity(
            type = LogType.WEBHOOK_VERIFY,
            title = "Meta Webhook GET Handshake Test (200 OK)",
            details = "URL: $queryUrl | Result: Valid Token matched '${config.verifyToken}', Challenge returned '$challenge'",
            status = LogStatus.SUCCESS,
            metaEventId = challenge,
            responsePayload = """{"hub.mode": "subscribe", "hub.challenge": "$challenge", "status": 200, "verified": true}"""
        )
        return Pair(true, challenge)
    }
}

// Default Data Seeders
private fun defaultMessengerRules(): List<MessengerRule> = listOf(
    MessengerRule(
        id = "m_rule_1",
        name = "পণ্যের মূল্য তালিকা ও খাঁটি খাবার (Price & Catalog)",
        keywords = listOf("দাম", "price", "কত", "তেল", "ঘি", "মধু", "চাল", "ফুড"),
        replyText = "আসসালামু আলাইকুম {user_name}! ভাওয়াল ফ্রেশ ফুডে (Vawal Fresh Food) স্বাগতম। আমাদের ঘানি ভাঙা খাঁটি সরিষার তেল, গাওয়া ঘি, সুন্দরবনের প্রাকৃতিক মধু ও নিরাপদ অর্গানিক পণ্যের অফার মূল্য জানতে মেসেজ দিন অথবা ফেসবুক পেজ ভিজিট করুন: facebook.com/vawalfreshfood । আপনার কী পণ্য প্রয়োজন লিখে জানান!",
        isEnabled = true,
        matchCount = 1240
    ),
    MessengerRule(
        id = "m_rule_2",
        name = "ডেলিভারি চার্জ ও সময় (Delivery Info)",
        keywords = listOf("delivery", "ডেলিভারি", "চার্জ", "কবে পাব", "কুরিয়ার", "ক্যাশ অন"),
        replyText = "ধন্যবাদ {user_name}! ভাওয়াল ফ্রেশ ফুড সারাদেশে ক্যাশ অন ডেলিভারি (Cash on Delivery) সুবিধা দেয়। ঢাকার ভেতরে ডেলিভারি চার্জ মাত্র ৭০ টাকা (২৪-৪৮ ঘণ্টার মধ্যে), ঢাকার বাইরে ১৩০ টাকা (২-৩ কার্যদিবস)।",
        isEnabled = true,
        matchCount = 680
    ),
    MessengerRule(
        id = "m_rule_3",
        name = "অর্ডার করার নিয়ম (How to Order)",
        keywords = listOf("order", "অর্ডার", "কিনব", "চাই", "কিনতে চাই", "নেব"),
        replyText = "খাঁটি পণ্য অর্ডার করতে অনুগ্রহ করে আপনার: ১. পণ্যের নাম ও পরিমাণ, ২. পুরো নাম, ৩. সম্পূর্ণ ঠিকানা এবং ৪. মোবাইল নাম্বার লিখে পাঠান। আমাদের টিম কনফার্মেশন কল করে দ্রুত পার্সেল পাঠাবে!",
        isEnabled = true,
        matchCount = 490
    ),
    MessengerRule(
        id = "m_rule_4",
        name = "ফার্ম ও শোরুমের ঠিকানা (Farm & Location)",
        keywords = listOf("ঠিকানা", "location", "address", "দোকান", "কোথায়", "গাজীপুর", "ভাওয়াল"),
        replyText = "ভাওয়াল ফ্রেশ ফুডের প্রধান কেন্দ্র: ভাওয়াল পরগনা, গাজীপুর ও ঢাকা অফিস। আমরা সরাসরি কৃষকের মাঠ ও নিজস্ব তত্ত্বাবধানে সংগ্রহ করে বিশুদ্ধ খাবার ভোক্তার কাছে পৌঁছে দিই।",
        isEnabled = true,
        matchCount = 210
    ),
    MessengerRule(
        id = "m_rule_5",
        name = "হটলাইন ও কাস্টমার কেয়ার (Hotline Support)",
        keywords = listOf("support", "কথা বলব", "কল", "নাম্বার", "হেল্প"),
        replyText = "ভাওয়াল ফ্রেশ ফুডের প্রতিনিধির সাথে সরাসরি কথা বলতে ডায়াল করুন: +880 1700-000000 (সকাল ৯টা - রাত ১০টা)। আমরা সর্বদা আপনার স্বাস্থ্যসম্মত খাবারের সেবায় নিয়োজিত!",
        isEnabled = true,
        matchCount = 95
    )
)

private fun defaultWelcomeRule(): MessengerRule = MessengerRule(
    id = "m_welcome",
    name = "স্বাগতম মেসেজ (Welcome Message)",
    keywords = emptyList(),
    replyText = "আসসালামু আলাইকুম {user_name}! ভাওয়াল ফ্রেশ ফুডে (Vawal Fresh Food) আপনাকে স্বাগতম। আমরা খাঁটি ও নিরাপদ খাবার আপনার পরিবারের কাছে পৌঁছে দিতে প্রতিশ্রুতিবদ্ধ। কীভাবে আপনাকে সাহায্য করতে পারি?",
    isWelcomeMessage = true,
    isEnabled = true,
    matchCount = 890
)

private fun defaultFallbackMessengerRule(): MessengerRule = MessengerRule(
    id = "m_fallback",
    name = "ডিফল্ট রিপ্লাই (Default Fallback)",
    keywords = emptyList(),
    replyText = "ধন্যবাদ {user_name} ভাওয়াল ফ্রেশ ফুডে বার্তার জন্য! আমাদের প্রতিনিধি কিছুক্ষণের মধ্যেই আপনার বার্তার উত্তর দেবেন। জরুরি প্রয়োজনে পেজে চোখ রাখুন: facebook.com/vawalfreshfood।",
    isDefaultFallback = true,
    isEnabled = true,
    matchCount = 310
)

private fun defaultCommentRules(): List<CommentRule> = listOf(
    CommentRule(
        id = "c_rule_1",
        name = "দামের কমেন্ট রিপ্লাই (Price Comment)",
        keywords = listOf("price", "দাম", "কত", "cost"),
        replyText = "ধন্যবাদ {user_name}! বিস্তারিত মূল্য তালিকা ও ডিসকাউন্ট অফার আপনার ইনবক্সে সেন্ড করা হয়েছে। দয়া করে ইনবক্স চেক করুন ❤️",
        sendPrivateMessage = true,
        privateMessageText = "আসসালামু আলাইকুম {user_name}! আপনি পেজের পোস্টে দাম জানতে চেয়েছেন। এই প্রডাক্টটির বর্তমান অফার মূল্য মাত্র ১,৪৯০ টাকা (রেগুলার ১,৯৫০ টাকা)। অর্ডার করতে রিপ্লাই দিন!",
        isEnabled = true,
        matchCount = 1540
    ),
    CommentRule(
        id = "c_rule_2",
        name = "স্টক ও অ্যাভেইলেবিলিটি (Stock Available)",
        keywords = listOf("available", "আছে", "স্টক", "পাওয়া যাবে"),
        replyText = "জী {user_name}, আলহামদুলিল্লাহ পণ্যটি বর্তমানে স্টকে রয়েছে! দ্রুত অর্ডার করতে আমাদের ইনবক্সে মেসেজ পাঠান।",
        sendPrivateMessage = true,
        privateMessageText = "প্রিয় {user_name}, পণ্যটি বর্তমানে লিমিটেড স্টকে আছে। সাইজ ও কালার পছন্দ করতে ভিজিট করুন অথবা এখানে আপনার সাইজ জানান!",
        isEnabled = true,
        matchCount = 820
    ),
    CommentRule(
        id = "c_rule_3",
        name = "পণ্য প্রশংসা ও ফিডব্যাক (Compliment & Review)",
        keywords = listOf("nice", "সুন্দর", "good", "অসাধারণ", "ভালো", "love"),
        replyText = "অনেক অনেক ধন্যবাদ {user_name} আপনার চমৎকার মন্তব্যের জন্য! নিয়মিত নতুন অফার ও কালেকশন দেখতে আমাদের পেজে চোখ রাখুন 🥰",
        sendPrivateMessage = false,
        privateMessageText = "",
        isEnabled = true,
        matchCount = 640
    ),
    CommentRule(
        id = "c_rule_4",
        name = "ডেলিভারি সংক্রান্ত কমেন্ট (Delivery Query)",
        keywords = listOf("ডেলিভারি", "delivery", "চট্টগ্রাম", "সিলেট", "খুলনা", "কুরিয়ার"),
        replyText = "সারাদেশের যেকোনো প্রান্তে ক্যাশ অন ডেলিভারি দেওয়া হয় {user_name}! বিস্তারিত ডেলিভারি তথ্য আপনার ইনবক্সে পাঠানো হয়েছে।",
        sendPrivateMessage = true,
        privateMessageText = "ডেলিভারি চার্জ: ঢাকা সিটির ভেতরে ৭০ টাকা, ঢাকার বাইরে ১৩০ টাকা। অর্ডার করতে নাম ও মোবাইল নম্বর পাঠান!",
        isEnabled = true,
        matchCount = 490
    )
)

private fun defaultFallbackCommentRule(): CommentRule = CommentRule(
    id = "c_fallback",
    name = "ডিফল্ট কমেন্ট রিপ্লাই (Default Comment)",
    keywords = emptyList(),
    replyText = "ধন্যবাদ {user_name} আমাদের পেজে মন্তব্য করার জন্য! যেকোনো প্রয়োজনে আমাদের ইনবক্সে মেসেজ দিতে পারেন।",
    sendPrivateMessage = true,
    privateMessageText = "আসসালামু আলাইকুম {user_name}! আমাদের সাথে যুক্ত থাকার জন্য ধন্যবাদ। কোনো প্রশ্ন থাকলে জানান!",
    isDefaultFallback = true,
    isEnabled = true,
    matchCount = 310
)

private fun defaultActivityLogs(): List<ActivityLog> = listOf(
    ActivityLog(
        id = "log_1",
        type = LogType.MESSENGER,
        title = "ইনবক্স মেসেজ অটো-রিপ্লাই (পণ্যের দাম ও ক্যাটালগ)",
        details = "প্রেরক: Tanvir Ahmed | মেসেজ: \"ভাই এটার দাম কত?\" -> উত্তর পাঠানো হয়েছে",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 3,
        status = LogStatus.SUCCESS,
        senderName = "Tanvir Ahmed",
        metaEventId = "mid_17392847192",
        responsePayload = "{\"recipient\": {\"id\": \"psid_19482\"}, \"message\": {\"text\": \"আসসালামু আলাইকুম Tanvir...\"}}"
    ),
    ActivityLog(
        id = "log_2",
        type = LogType.COMMENT,
        title = "পোস্ট কমেন্টে রিপ্লাই ও প্রাইভেট মেসেজ পাঠানো সফল",
        details = "কমেন্টকারী: Sadia Afrin | পোস্ট #8129 | কমেন্ট: \"Price please\" -> পাবলিক কমেন্ট রিপ্লাই ও ইনবক্স অফার সেন্ড",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
        status = LogStatus.SUCCESS,
        senderName = "Sadia Afrin",
        metaEventId = "cmt_8291038471",
        responsePayload = "{\"message\": \"ধন্যবাদ Sadia Afrin! বিস্তারিত মূল্য তালিকা আপনার ইনবক্সে পাঠানো হয়েছে।\"}"
    ),
    ActivityLog(
        id = "log_3",
        type = LogType.WEBHOOK_VERIFY,
        title = "Meta Webhook GET Challenge ভেরিফাইড (200 OK)",
        details = "Webhook URL: https://masum-autoreply.workers.dev/api/webhook | hub.challenge token returned successfully",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
        status = LogStatus.SUCCESS,
        metaEventId = "challenge_928174"
    ),
    ActivityLog(
        id = "log_4",
        type = LogType.COMMENT,
        title = "ডুপ্লিকেট কমেন্ট আইডি ফিল্টারড (No double reply)",
        details = "Comment ID: cmt_7719283401 ইতিমধ্যে প্রসেস হয়েছে, স্প্যাম সুরক্ষা সক্রিয়",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 65,
        status = LogStatus.WARNING,
        metaEventId = "cmt_7719283401"
    ),
    ActivityLog(
        id = "log_5",
        type = LogType.TOKEN_CHECK,
        title = "Page Access Token ভ্যালিডিটি চেক সম্পন্ন",
        details = "Graph API /me?fields=id,name,permissions -> Token status: NEVER_EXPIRES, All 5 permissions granted",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
        status = LogStatus.SUCCESS,
        metaEventId = "token_audit_01"
    )
)

private fun defaultMetaPermissions(): List<MetaPermissionItem> = listOf(
    MetaPermissionItem(
        permission = "pages_show_list",
        nameBn = "পেজ তালিকা দেখা",
        nameEn = "Show Pages List",
        descriptionBn = "ইউজারের পরিচালিত ফেসবুক পেজগুলোর তালিকা লোড করা ও পেজ সিলেক্ট করার অনুমতি।",
        descriptionEn = "Allows viewing and selecting pages managed by the user.",
        isApproved = true,
        isRequired = true
    ),
    MetaPermissionItem(
        permission = "pages_read_engagement",
        nameBn = "পেজের পোস্ট ও কমেন্ট পড়া",
        nameEn = "Read Engagement",
        descriptionBn = "পেজের পোস্ট, ব্যবহারকারীর কমেন্ট এবং ইন্টার‍্যাকশন পড়ার অনুমতি।",
        descriptionEn = "Read page posts, comments and audience engagements.",
        isApproved = true,
        isRequired = true
    ),
    MetaPermissionItem(
        permission = "pages_manage_metadata",
        nameBn = "ওয়েবহুক সাবস্ক্রিপশন ও মেটাডাটা",
        nameEn = "Manage Metadata",
        descriptionBn = "মেটা পেজে ওয়েবহুক সাবস্ক্রাইব করা (messages ও feed events) এর জন্য আবশ্যক।",
        descriptionEn = "Required to subscribe to webhooks for messages and feed.",
        isApproved = true,
        isRequired = true
    ),
    MetaPermissionItem(
        permission = "pages_manage_engagement",
        nameBn = "কমেন্টে অটো-রিপ্লাই দেওয়া",
        nameEn = "Manage Engagement",
        descriptionBn = "পেজের পোস্টে আসা কমেন্টে অফিসিয়াল Graph API দিয়ে স্বয়ংক্রিয় রিপ্লাই পোস্ট করা।",
        descriptionEn = "Post official replies to comments using Graph API.",
        isApproved = true,
        isRequired = true
    ),
    MetaPermissionItem(
        permission = "pages_messaging",
        nameBn = "মেসেঞ্জারে অটো-রিপ্লাই পাঠানো",
        nameEn = "Page Messenger Send",
        descriptionBn = "গ্রাহকের ইনবক্স মেসেজে Send API এর মাধ্যমে স্বয়ংক্রিয় রিপ্লাই পাঠানো।",
        descriptionEn = "Send automatic replies in Messenger via Send API.",
        isApproved = true,
        isRequired = true
    )
)
