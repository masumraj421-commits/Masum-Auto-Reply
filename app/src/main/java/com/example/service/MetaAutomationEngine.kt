package com.example.service

import com.example.data.model.BusinessHoursConfig
import com.example.data.model.CommentRule
import com.example.data.model.MessengerRule
import java.util.Calendar

data class MessengerEngineResult(
    val matchedRuleName: String,
    val replyText: String,
    val isOffHours: Boolean,
    val graphApiEndpoint: String,
    val graphApiPayload: String,
    val httpStatus: Int = 200
)

data class CommentEngineResult(
    val matchedRuleName: String,
    val publicReplyText: String,
    val sendPrivateMessage: Boolean,
    val privateMessageText: String,
    val isDuplicate: Boolean,
    val graphApiEndpoint: String,
    val graphApiPayload: String,
    val httpStatus: Int = 200
)

class MetaAutomationEngine {

    private val processedCommentIds = mutableSetOf<String>()

    fun evaluateMessenger(
        senderId: String,
        senderName: String,
        messageText: String,
        isFirstMessage: Boolean,
        businessHours: BusinessHoursConfig,
        rules: List<MessengerRule>,
        welcomeRule: MessengerRule?,
        defaultFallback: MessengerRule?
    ): MessengerEngineResult {
        val trimmed = messageText.trim()
        val lower = trimmed.lowercase()

        // 1. First-time contact welcome message
        if (isFirstMessage && welcomeRule != null && welcomeRule.isEnabled) {
            val text = welcomeRule.replyText.replace("{user_name}", senderName)
            return MessengerEngineResult(
                matchedRuleName = "Welcome Message (স্বাগতম বার্তা)",
                replyText = text,
                isOffHours = false,
                graphApiEndpoint = "POST https://graph.facebook.com/v21.0/me/messages",
                graphApiPayload = """{"recipient": {"id": "$senderId"}, "message": {"text": "${escapeJson(text)}"}}"""
            )
        }

        // 2. Business Hours Check (if enabled)
        if (businessHours.enabled) {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            if (currentHour < businessHours.startHour || currentHour >= businessHours.endHour) {
                val offHoursReply = businessHours.offHoursReplyText.replace("{user_name}", senderName)
                return MessengerEngineResult(
                    matchedRuleName = "Business Hours (অফিস সময় নোটিশ)",
                    replyText = offHoursReply,
                    isOffHours = true,
                    graphApiEndpoint = "POST https://graph.facebook.com/v21.0/me/messages",
                    graphApiPayload = """{"recipient": {"id": "$senderId"}, "message": {"text": "${escapeJson(offHoursReply)}"}}"""
                )
            }
        }

        // 3. Keyword matching
        for (rule in rules) {
            if (!rule.isEnabled) continue
            val matches = rule.keywords.any { keyword ->
                val kw = keyword.trim().lowercase()
                kw.isNotEmpty() && lower.contains(kw)
            }
            if (matches) {
                val text = rule.replyText.replace("{user_name}", senderName)
                return MessengerEngineResult(
                    matchedRuleName = rule.name,
                    replyText = text,
                    isOffHours = false,
                    graphApiEndpoint = "POST https://graph.facebook.com/v21.0/me/messages",
                    graphApiPayload = """{"recipient": {"id": "$senderId"}, "message": {"text": "${escapeJson(text)}"}}"""
                )
            }
        }

        // 4. Fallback Default Reply
        val fallbackText = defaultFallback?.replyText?.replace("{user_name}", senderName)
            ?: "ধন্যবাদ $senderName আপনার বার্তার জন্য! আমাদের প্রতিনিধি খুব দ্রুত রিপ্লাই দেবেন।"
        return MessengerEngineResult(
            matchedRuleName = "Default Fallback (ডিফল্ট রিপ্লাই)",
            replyText = fallbackText,
            isOffHours = false,
            graphApiEndpoint = "POST https://graph.facebook.com/v21.0/me/messages",
            graphApiPayload = """{"recipient": {"id": "$senderId"}, "message": {"text": "${escapeJson(fallbackText)}"}}"""
        )
    }

    fun evaluateComment(
        commentId: String,
        postId: String,
        commenterName: String,
        commentText: String,
        rules: List<CommentRule>,
        defaultFallback: CommentRule?
    ): CommentEngineResult {
        // 1. Deduplication check
        if (processedCommentIds.contains(commentId)) {
            return CommentEngineResult(
                matchedRuleName = "Anti-Duplicate Filter (ইতিমধ্যে প্রসেসড)",
                publicReplyText = "[Deduplicated - No duplicate reply sent]",
                sendPrivateMessage = false,
                privateMessageText = "",
                isDuplicate = true,
                graphApiEndpoint = "N/A (Skipped)",
                graphApiPayload = "{}",
                httpStatus = 304
            )
        }
        processedCommentIds.add(commentId)

        val trimmed = commentText.trim()
        val lower = trimmed.lowercase()

        // 2. Keyword rules (and post-specific rules)
        for (rule in rules) {
            if (!rule.isEnabled) continue
            if (!rule.targetPostId.isNullOrEmpty() && rule.targetPostId != postId) {
                continue
            }
            val matches = rule.keywords.any { keyword ->
                val kw = keyword.trim().lowercase()
                kw.isNotEmpty() && lower.contains(kw)
            }
            if (matches) {
                val publicText = rule.replyText.replace("{user_name}", commenterName)
                val privateText = rule.privateMessageText.replace("{user_name}", commenterName)
                return CommentEngineResult(
                    matchedRuleName = rule.name,
                    publicReplyText = publicText,
                    sendPrivateMessage = rule.sendPrivateMessage && privateText.isNotEmpty(),
                    privateMessageText = privateText,
                    isDuplicate = false,
                    graphApiEndpoint = "POST https://graph.facebook.com/v21.0/$commentId/comments",
                    graphApiPayload = """{"message": "${escapeJson(publicText)}"}"""
                )
            }
        }

        // 3. Fallback default comment reply
        val fallbackText = defaultFallback?.replyText?.replace("{user_name}", commenterName)
            ?: "ধন্যবাদ $commenterName আপনার সুন্দর মন্তব্যের জন্য! বিস্তারিত জানতে আমাদের ইনবক্স করুন।"
        val privateFallback = defaultFallback?.privateMessageText?.replace("{user_name}", commenterName) ?: ""

        return CommentEngineResult(
            matchedRuleName = "Default Comment Reply (ডিফল্ট কমেন্ট রিপ্লাই)",
            publicReplyText = fallbackText,
            sendPrivateMessage = defaultFallback?.sendPrivateMessage == true && privateFallback.isNotEmpty(),
            privateMessageText = privateFallback,
            isDuplicate = false,
            graphApiEndpoint = "POST https://graph.facebook.com/v21.0/$commentId/comments",
            graphApiPayload = """{"message": "${escapeJson(fallbackText)}"}"""
        )
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}
