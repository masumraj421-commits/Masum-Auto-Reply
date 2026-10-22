package com.example

import com.example.data.model.BusinessHoursConfig
import com.example.data.model.CommentRule
import com.example.data.model.MessengerRule
import com.example.service.MetaAutomationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private val engine = MetaAutomationEngine()

    @Test
    fun testMessengerKeywordMatch() {
        val rules = listOf(
            MessengerRule(
                id = "rule_1",
                name = "Price Rule",
                keywords = listOf("দাম", "price"),
                replyText = "The price is 1500 BDT for {user_name}",
                isEnabled = true
            )
        )
        val defaultRule = MessengerRule(
            id = "fallback",
            name = "Fallback",
            keywords = emptyList(),
            replyText = "Hello {user_name}",
            isEnabled = true
        )

        val result = engine.evaluateMessenger(
            senderId = "user_123",
            senderName = "Masum",
            messageText = "ভাই এটার দাম কত?",
            isFirstMessage = false,
            businessHours = BusinessHoursConfig(enabled = false),
            rules = rules,
            welcomeRule = null,
            defaultFallback = defaultRule
        )

        assertEquals("Price Rule", result.matchedRuleName)
        assertTrue(result.replyText.contains("1500 BDT for Masum"))
        assertEquals("POST https://graph.facebook.com/v21.0/me/messages", result.graphApiEndpoint)
    }

    @Test
    fun testCommentDeduplication() {
        val rules = listOf(
            CommentRule(
                id = "c_1",
                name = "Price Comment",
                keywords = listOf("price"),
                replyText = "Check your inbox {user_name}!",
                sendPrivateMessage = true,
                privateMessageText = "Special offer for you",
                isEnabled = true
            )
        )

        val firstCall = engine.evaluateComment(
            commentId = "cmt_test_001",
            postId = "post_1",
            commenterName = "Rahim",
            commentText = "Price please",
            rules = rules,
            defaultFallback = null
        )
        assertFalse(firstCall.isDuplicate)
        assertEquals(200, firstCall.httpStatus)

        // Same commentId again -> Should be deduplicated
        val secondCall = engine.evaluateComment(
            commentId = "cmt_test_001",
            postId = "post_1",
            commenterName = "Rahim",
            commentText = "Price please",
            rules = rules,
            defaultFallback = null
        )
        assertTrue(secondCall.isDuplicate)
        assertEquals(304, secondCall.httpStatus)
    }
}
