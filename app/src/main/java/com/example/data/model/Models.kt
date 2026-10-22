package com.example.data.model

enum class LogType {
    MESSENGER,
    COMMENT,
    WEBHOOK_VERIFY,
    TOKEN_CHECK,
    ERROR
}

enum class LogStatus {
    SUCCESS,
    WARNING,
    FAILED,
    INFO
}

data class UserSession(
    val id: String = "usr_masum_01",
    val email: String = "masumraj421@gmail.com",
    val displayName: String = "Masum Raj",
    val role: String = "Page Admin / Owner",
    val isSupabaseConnected: Boolean = true
)

data class FacebookPage(
    val id: String,
    val name: String,
    val category: String,
    val accessToken: String,
    val followersCount: Int,
    val profilePicUrl: String,
    val isConnected: Boolean,
    val tokenExpiresAt: String,
    val permissions: List<String>
)

data class MessengerRule(
    val id: String,
    val name: String,
    val keywords: List<String>,
    val replyText: String,
    val isWelcomeMessage: Boolean = false,
    val isDefaultFallback: Boolean = false,
    val isEnabled: Boolean = true,
    val matchCount: Int = 0
)

data class CommentRule(
    val id: String,
    val name: String,
    val keywords: List<String>,
    val replyText: String,
    val sendPrivateMessage: Boolean = true,
    val privateMessageText: String = "",
    val targetPostId: String? = null,
    val isDefaultFallback: Boolean = false,
    val isEnabled: Boolean = true,
    val matchCount: Int = 0
)

data class ActivityLog(
    val id: String,
    val type: LogType,
    val title: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: LogStatus = LogStatus.SUCCESS,
    val metaEventId: String = "",
    val senderName: String = "",
    val responsePayload: String = ""
)

data class MetaPermissionItem(
    val permission: String,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val isApproved: Boolean,
    val isRequired: Boolean
)

data class BusinessHoursConfig(
    val enabled: Boolean = true,
    val startHour: Int = 9,    // 09:00 AM
    val endHour: Int = 22,    // 10:00 PM
    val offHoursReplyText: String = "ধন্যবাদ বার্তার জন্য! আমাদের অফিস সময় সকাল ৯টা থেকে রাত ১০টা পর্যন্ত। অফিস খোলার সাথে সাথে আমরা আপনার উত্তর দেব।"
)

data class MetaProductItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val isAdded: Boolean = true,
    val statusTextBn: String = "যুক্ত ও সক্রিয়",
    val statusTextEn: String = "Added & Active",
    val configKeyValues: Map<String, String> = emptyMap()
)

data class MetaAppConfig(
    val appId: String = "109823487192834",
    val appSecret: String = "a8f9c1b4e2d7e8a9f0b1c2d3e4f5a6b7",
    val verifyToken: String = "masum_autoreply_verify_token_2026",
    val webhookCallbackUrl: String = "https://masum-autoreply.workers.dev/api/webhook",
    val oauthRedirectUri: String = "https://masum-autoreply.workers.dev/auth/callback",
    val isFacebookLoginAdded: Boolean = true,
    val isWebhooksAdded: Boolean = true,
    val webhookSubscriptions: List<String> = listOf("messages", "messaging_postbacks", "feed"),
    val supabaseUrl: String = "https://masum-autoreply-db.supabase.co",
    val supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1hc3VtLWF1dG8ifQ..."
)

enum class ActiveTab {
    DASHBOARD,
    CONNECT,
    MESSENGER,
    COMMENTS,
    SIMULATOR,
    LOGS,
    EXPORT
}
