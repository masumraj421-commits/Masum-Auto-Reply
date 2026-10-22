package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessHoursConfig
import com.example.data.model.MessengerRule
import com.example.data.repository.AutomationUiState
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.RoseError
import java.util.UUID

@Composable
fun MessengerAutomationScreen(
    state: AutomationUiState,
    onToggleMessenger: (Boolean) -> Unit,
    onUpdateWelcomeRule: (MessengerRule) -> Unit,
    onUpdateFallbackRule: (MessengerRule) -> Unit,
    onUpdateBusinessHours: (BusinessHoursConfig) -> Unit,
    onAddRule: (MessengerRule) -> Unit,
    onUpdateRule: (MessengerRule) -> Unit,
    onDeleteRule: (String) -> Unit,
    onToggleRule: (String) -> Unit
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA

    var showAddDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<MessengerRule?>(null) }
    var showEditSpecialDialog by remember { mutableStateOf<MessengerRule?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MetaBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_messenger_rule_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Rule")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("messenger_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Master Messenger Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MetaBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.get(lang, "messenger_automation_title"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (state.isMessengerEnabled)
                                    (if (isBn) "ইনবক্সে স্বয়ংক্রিয় উত্তর পাঠানো সক্রিয়" else "Auto-replies active in Page Messenger")
                                else
                                    (if (isBn) "মেসেঞ্জার অটোমেশন বন্ধ আছে" else "Messenger auto-replies paused"),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.isMessengerEnabled) EmeraldGreen else RoseError
                            )
                        }
                        Switch(
                            checked = state.isMessengerEnabled,
                            onCheckedChange = onToggleMessenger,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MetaBlue
                            ),
                            modifier = Modifier.testTag("messenger_master_switch")
                        )
                    }
                }
            }

            // Welcome Message Card
            item {
                SpecialRuleCard(
                    title = AppStrings.get(lang, "welcome_message"),
                    subtitle = AppStrings.get(lang, "welcome_desc"),
                    rule = state.welcomeMessengerRule,
                    icon = Icons.Default.WavingHand,
                    accentColor = Color(0xFFF59E0B),
                    isBn = isBn,
                    onEditClick = { showEditSpecialDialog = state.welcomeMessengerRule },
                    onToggle = {
                        onUpdateWelcomeRule(state.welcomeMessengerRule.copy(isEnabled = !state.welcomeMessengerRule.isEnabled))
                    }
                )
            }

            // Default Fallback Reply Card
            item {
                SpecialRuleCard(
                    title = AppStrings.get(lang, "default_reply"),
                    subtitle = AppStrings.get(lang, "default_reply_desc"),
                    rule = state.fallbackMessengerRule,
                    icon = Icons.Default.Chat,
                    accentColor = MetaBlue,
                    isBn = isBn,
                    onEditClick = { showEditSpecialDialog = state.fallbackMessengerRule },
                    onToggle = {
                        onUpdateFallbackRule(state.fallbackMessengerRule.copy(isEnabled = !state.fallbackMessengerRule.isEnabled))
                    }
                )
            }

            // Business Hours Card
            item {
                BusinessHoursCard(
                    config = state.businessHours,
                    isBn = isBn,
                    onUpdateConfig = onUpdateBusinessHours
                )
            }

            // Keyword Rules List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.get(lang, "keyword_rules")} (${state.messengerRules.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_rule_text_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppStrings.get(lang, "add_new_rule"))
                    }
                }
            }

            items(state.messengerRules) { rule ->
                MessengerRuleItemCard(
                    rule = rule,
                    isBn = isBn,
                    onToggle = { onToggleRule(rule.id) },
                    onEdit = { editingRule = rule },
                    onDelete = { onDeleteRule(rule.id) }
                )
            }

            // Meta 24-hr policy note
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MetaBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBn)
                                "মেটা মেসেজিং স্ট্যান্ডার্ড: গ্রাহক মেসেজ পাঠানোর ২৪ ঘণ্টার মধ্যে স্ট্যান্ডার্ড মেসেজ পাঠানো যায়। ২৪ ঘণ্টার পরে নোটিফিকেশন পাঠাতে অফিশিয়াল Message Tag বা Utility Template অনুমোদন প্রয়োজন।"
                            else
                                "Meta 24-hour Standard Window: standard responses must occur within 24 hours of customer message. Message tags are required thereafter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Add / Edit Rule Dialog
    if (showAddDialog || editingRule != null) {
        val target = editingRule
        RuleEditDialog(
            isBn = isBn,
            existing = target,
            onDismiss = {
                showAddDialog = false
                editingRule = null
            },
            onSave = { name, keywords, reply ->
                if (target == null) {
                    val newRule = MessengerRule(
                        id = "m_" + UUID.randomUUID().toString().take(8),
                        name = name,
                        keywords = keywords,
                        replyText = reply,
                        isEnabled = true
                    )
                    onAddRule(newRule)
                } else {
                    val updated = target.copy(
                        name = name,
                        keywords = keywords,
                        replyText = reply
                    )
                    onUpdateRule(updated)
                }
                showAddDialog = false
                editingRule = null
            }
        )
    }

    // Edit Welcome / Fallback Dialog
    if (showEditSpecialDialog != null) {
        val target = showEditSpecialDialog!!
        var replyText by remember { mutableStateOf(target.replyText) }

        AlertDialog(
            onDismissRequest = { showEditSpecialDialog = null },
            title = {
                Text(target.name, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = if (isBn) "বার্তা লিখুন (কাস্টমারের নামের জন্য {user_name} ব্যবহার করুন):"
                        else "Message text (use {user_name} for customer name):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = target.copy(replyText = replyText)
                        if (target.isWelcomeMessage) onUpdateWelcomeRule(updated)
                        else onUpdateFallbackRule(updated)
                        showEditSpecialDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
                ) {
                    Text(if (isBn) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditSpecialDialog = null }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun SpecialRuleCard(
    title: String,
    subtitle: String,
    rule: MessengerRule,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isBn: Boolean,
    onEditClick: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.replyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isBn) "প্রেরিত হয়েছে:" else "Triggered:"} ${rule.matchCount} ${if (isBn) "বার" else "times"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "মেসেজ পরিবর্তন" else "Edit Text", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BusinessHoursCard(
    config: BusinessHoursConfig,
    isBn: Boolean,
    onUpdateConfig: (BusinessHoursConfig) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf(config.offHoursReplyText) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) "অফিস সময়সূচী (সকাল ৯টা - রাত ১০টা)" else "Office Hours (9:00 AM - 10:00 PM)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isBn) "অফিস সময়ের বাইরে এলে স্বয়ংক্রিয় নোটিশ পাঠাবে" else "Send off-hours notice outside schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = config.enabled,
                    onCheckedChange = { onUpdateConfig(config.copy(enabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF8B5CF6)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = config.offHoursReplyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun MessengerRuleItemCard(
    rule: MessengerRule,
    isBn: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Keywords pills
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        rule.keywords.take(4).forEach { kw ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MetaBlue.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = kw,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MetaBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MetaBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.replyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isBn) "ম্যাচ সংখ্যা:" else "Triggered:"} ${rule.matchCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MetaBlue, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RuleEditDialog(
    isBn: Boolean,
    existing: MessengerRule?,
    onDismiss: () -> Unit,
    onSave: (name: String, keywords: List<String>, reply: String) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var keywordsStr by remember { mutableStateOf(existing?.keywords?.joinToString(", ") ?: "") }
    var replyText by remember { mutableStateOf(existing?.replyText ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existing == null)
                    (if (isBn) "নতুন মেসেঞ্জার রুল তৈরি করুন" else "Create Messenger Rule")
                else
                    (if (isBn) "রুল সম্পাদনা করুন" else "Edit Messenger Rule"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBn) "রুলের নাম (e.g. প্রাইস অনুসন্ধান)" else "Rule Name") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_rule_name")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keywordsStr,
                    onValueChange = { keywordsStr = it },
                    label = { Text(if (isBn) "কি-ওয়ার্ডসমূহ (কমা দিয়ে আলাদা করুন)" else "Keywords (comma separated)") },
                    placeholder = { Text("দাম, price, কত, খরচ") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_rule_keywords")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text(if (isBn) "রিপ্লাই মেসেজ ({user_name} ব্যবহারযোগ্য)" else "Reply Message ({user_name})") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("dialog_rule_reply")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val kws = keywordsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (name.isNotBlank() && replyText.isNotBlank()) {
                        onSave(name, kws, replyText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                modifier = Modifier.testTag("dialog_save_button")
            ) {
                Text(if (isBn) "সংরক্ষণ করুন" else "Save Rule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBn) "বাতিল" else "Cancel")
            }
        }
    )
}
