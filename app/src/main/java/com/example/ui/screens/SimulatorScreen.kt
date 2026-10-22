package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AutomationUiState
import com.example.service.CommentEngineResult
import com.example.service.MessengerEngineResult
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.TealAccent

@Composable
fun SimulatorScreen(
    state: AutomationUiState,
    onSimulateMessenger: (name: String, text: String, isFirst: Boolean) -> MessengerEngineResult,
    onSimulateComment: (name: String, text: String) -> CommentEngineResult
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA

    var selectedTab by remember { mutableStateOf(0) } // 0: Messenger, 1: Comment
    var senderName by remember { mutableStateOf("Tanvir Hasan") }
    var inputMessage by remember { mutableStateOf("ভাই এটার দাম কত?") }

    var messengerResult by remember { mutableStateOf<MessengerEngineResult?>(null) }
    var commentResult by remember { mutableStateOf<CommentEngineResult?>(null) }

    val messengerPresets = listOf(
        "দাম কত ভাই?",
        "ঢাকার বাইরে কি ডেলিভারি হবে?",
        "আপনাদের শোরুমের ঠিকানা কোথায়?",
        "অর্ডার কনফার্ম করতে চাই",
        "কাস্টমার কেয়ার নাম্বার দিন"
    )

    val commentPresets = listOf(
        "Price please",
        "এই পণ্যটি কি এখনও স্টকে আছে?",
        "অনেক সুন্দর ড্রেস!",
        "চট্টগ্রামে কিভাবে পাব?"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("simulator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppStrings.get(lang, "simulator_title"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = AppStrings.get(lang, "simulator_desc"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Test Mode Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MetaBlue,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        inputMessage = "ভাই এটার দাম কত?"
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get(lang, "simulate_messenger"), fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        inputMessage = "Price please"
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get(lang, "simulate_comment"), fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        }

        // Interactive Input Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedTab == 0)
                            (if (isBn) "মেসেঞ্জারে টেস্ট মেসেজ পাঠান" else "Send Test Messenger Message")
                        else
                            (if (isBn) "পেজ পোস্টে টেস্ট কমেন্ট পাঠান" else "Post Test Comment on Page"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        label = { Text(AppStrings.get(lang, "sender_name")) },
                        modifier = Modifier.fillMaxWidth().testTag("sim_sender_name")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        label = { Text(AppStrings.get(lang, "customer_message")) },
                        modifier = Modifier.fillMaxWidth().testTag("sim_message_text")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Chips
                    Text(
                        text = if (isBn) "কুইক স্যাম্পল প্রশ্ন:" else "Quick Sample Queries:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val presets = if (selectedTab == 0) messengerPresets else commentPresets
                        items(presets) { preset ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { inputMessage = preset }
                            ) {
                                Text(
                                    text = preset,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                messengerResult = onSimulateMessenger(senderName, inputMessage, false)
                            } else {
                                commentResult = onSimulateComment(senderName, inputMessage)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) MetaBlue else TealAccent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("trigger_simulation_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppStrings.get(lang, "send_test"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Result Output Section
        if (selectedTab == 0 && messengerResult != null) {
            val res = messengerResult!!
            item {
                SimulationResultCard(
                    title = if (isBn) "মেসেঞ্জার অটো-রিপ্লাই আউটপুট" else "Messenger Auto-Reply Output",
                    matchedRule = res.matchedRuleName,
                    replyText = res.replyText,
                    endpoint = res.graphApiEndpoint,
                    payload = res.graphApiPayload,
                    status = res.httpStatus,
                    isBn = isBn,
                    accentColor = MetaBlue
                )
            }
        } else if (selectedTab == 1 && commentResult != null) {
            val res = commentResult!!
            item {
                SimulationResultCard(
                    title = if (isBn) "কমেন্ট অটো-রিপ্লাই আউটপুট" else "Comment Auto-Reply Output",
                    matchedRule = res.matchedRuleName,
                    replyText = res.publicReplyText + if (res.sendPrivateMessage) "\n\n✉️ [ইনবক্স প্রাইভেট মেসেজ পাঠানো হয়েছে]: " + res.privateMessageText else "",
                    endpoint = res.graphApiEndpoint,
                    payload = res.graphApiPayload,
                    status = res.httpStatus,
                    isBn = isBn,
                    accentColor = TealAccent
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SimulationResultCard(
    title: String,
    matchedRule: String,
    replyText: String,
    endpoint: String,
    payload: String,
    status: Int,
    isBn: Boolean,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth().testTag("simulation_result_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldGreen.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Status: $status OK",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Matched Rule Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isBn) "ম্যাচ হওয়া রুল:" else "Matched Rule:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = matchedRule,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reply Bubble
            Text(
                text = if (isBn) "স্বয়ংক্রিয় প্রেরিত উত্তর (Auto-Replied Text):" else "Auto-Replied Text:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = replyText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Graph API payload view
            Text(
                text = if (isBn) "অফিশিয়াল Meta Graph API কল:" else "Official Meta Graph API Call:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = endpoint,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = payload,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
