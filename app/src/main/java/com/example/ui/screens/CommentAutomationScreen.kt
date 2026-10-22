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
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import com.example.data.model.CommentRule
import com.example.data.repository.AutomationUiState
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.RoseError
import com.example.ui.theme.TealAccent
import java.util.UUID

@Composable
fun CommentAutomationScreen(
    state: AutomationUiState,
    onToggleComment: (Boolean) -> Unit,
    onUpdateFallbackRule: (CommentRule) -> Unit,
    onAddRule: (CommentRule) -> Unit,
    onUpdateRule: (CommentRule) -> Unit,
    onDeleteRule: (String) -> Unit,
    onToggleRule: (String) -> Unit
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA

    var showAddDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<CommentRule?>(null) }
    var showEditFallbackDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TealAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_comment_rule_fab")
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
                .testTag("comment_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Master Comment Card
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
                                .background(TealAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.get(lang, "comment_automation_title"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (state.isCommentEnabled)
                                    (if (isBn) "পোস্ট কমেন্টে অটো রিপ্লাই সক্রিয়" else "Auto-replies active for Page comments")
                                else
                                    (if (isBn) "কমেন্ট অটো রিপ্লাই সাময়িক বন্ধ" else "Comment auto-replies paused"),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.isCommentEnabled) EmeraldGreen else RoseError
                            )
                        }
                        Switch(
                            checked = state.isCommentEnabled,
                            onCheckedChange = onToggleComment,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TealAccent
                            ),
                            modifier = Modifier.testTag("comment_master_switch")
                        )
                    }
                }
            }

            // Anti-duplicate protection card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.get(lang, "dedup_protection"),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = AppStrings.get(lang, "dedup_desc"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (isBn) "সুরক্ষিত" else "Active",
                                color = EmeraldGreen,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Default Comment Fallback Card
            item {
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
                                    .background(TealAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) "ডিফল্ট কমেন্ট রিপ্লাই (Fallback)" else "Default Comment Reply",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isBn) "কোনো কি-ওয়ার্ডের সাথে না মিললে এই উত্তর যাবে" else "Sent when comment matches no specific keyword",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.fallbackCommentRule.isEnabled,
                                onCheckedChange = {
                                    onUpdateFallbackRule(state.fallbackCommentRule.copy(isEnabled = !state.fallbackCommentRule.isEnabled))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TealAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "💬 পাবলিক রিপ্লাই: ${state.fallbackCommentRule.replyText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (state.fallbackCommentRule.sendPrivateMessage && state.fallbackCommentRule.privateMessageText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "✉️ ইনবক্স মেসেজ: ${state.fallbackCommentRule.privateMessageText}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MetaBlue
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBn) "মোট প্রেরিত:" else "Triggered:"} ${state.fallbackCommentRule.matchCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = { showEditFallbackDialog = true }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBn) "এডিট করুন" else "Edit Text", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Comment Rules Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.get(lang, "comment_rules")} (${state.commentRules.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_comment_rule_text_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppStrings.get(lang, "add_new_rule"))
                    }
                }
            }

            items(state.commentRules) { rule ->
                CommentRuleItemCard(
                    rule = rule,
                    isBn = isBn,
                    onToggle = { onToggleRule(rule.id) },
                    onEdit = { editingRule = rule },
                    onDelete = { onDeleteRule(rule.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Add / Edit Rule Dialog
    if (showAddDialog || editingRule != null) {
        val target = editingRule
        CommentRuleEditDialog(
            isBn = isBn,
            existing = target,
            onDismiss = {
                showAddDialog = false
                editingRule = null
            },
            onSave = { name, keywords, publicReply, sendPrivate, privateReply ->
                if (target == null) {
                    val newRule = CommentRule(
                        id = "c_" + UUID.randomUUID().toString().take(8),
                        name = name,
                        keywords = keywords,
                        replyText = publicReply,
                        sendPrivateMessage = sendPrivate,
                        privateMessageText = privateReply,
                        isEnabled = true
                    )
                    onAddRule(newRule)
                } else {
                    val updated = target.copy(
                        name = name,
                        keywords = keywords,
                        replyText = publicReply,
                        sendPrivateMessage = sendPrivate,
                        privateMessageText = privateReply
                    )
                    onUpdateRule(updated)
                }
                showAddDialog = false
                editingRule = null
            }
        )
    }

    // Edit Fallback Dialog
    if (showEditFallbackDialog) {
        var publicReply by remember { mutableStateOf(state.fallbackCommentRule.replyText) }
        var sendPrivate by remember { mutableStateOf(state.fallbackCommentRule.sendPrivateMessage) }
        var privateReply by remember { mutableStateOf(state.fallbackCommentRule.privateMessageText) }

        AlertDialog(
            onDismissRequest = { showEditFallbackDialog = false },
            title = {
                Text(if (isBn) "ডিফল্ট কমেন্ট রিপ্লাই এডিট" else "Edit Default Comment", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = publicReply,
                        onValueChange = { publicReply = it },
                        label = { Text(if (isBn) "পাবলিক কমেন্ট রিপ্লাই" else "Public Comment Reply") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = sendPrivate, onCheckedChange = { sendPrivate = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "ইনবক্সেও প্রাইভেট মেসেজ পাঠান" else "Also send private message to inbox",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (sendPrivate) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = privateReply,
                            onValueChange = { privateReply = it },
                            label = { Text(if (isBn) "ইনবক্স মেসেজ" else "Private Inbox Message") },
                            modifier = Modifier.fillMaxWidth().height(100.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = state.fallbackCommentRule.copy(
                            replyText = publicReply,
                            sendPrivateMessage = sendPrivate,
                            privateMessageText = privateReply
                        )
                        onUpdateFallbackRule(updated)
                        showEditFallbackDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                ) {
                    Text(if (isBn) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditFallbackDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun CommentRuleItemCard(
    rule: CommentRule,
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
                                color = TealAccent.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = kw,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TealAccent,
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
                        checkedTrackColor = TealAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "💬 " + rule.replyText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (rule.sendPrivateMessage && rule.privateMessageText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "✉️ " + rule.privateMessageText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MetaBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isBn) "রিপ্লাই সংখ্যা:" else "Triggered:"} ${rule.matchCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TealAccent, modifier = Modifier.size(16.dp))
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
fun CommentRuleEditDialog(
    isBn: Boolean,
    existing: CommentRule?,
    onDismiss: () -> Unit,
    onSave: (name: String, keywords: List<String>, publicReply: String, sendPrivate: Boolean, privateReply: String) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var keywordsStr by remember { mutableStateOf(existing?.keywords?.joinToString(", ") ?: "") }
    var publicReply by remember { mutableStateOf(existing?.replyText ?: "") }
    var sendPrivate by remember { mutableStateOf(existing?.sendPrivateMessage ?: true) }
    var privateReply by remember { mutableStateOf(existing?.privateMessageText ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existing == null)
                    (if (isBn) "নতুন কমেন্ট রুল তৈরি করুন" else "Create Comment Rule")
                else
                    (if (isBn) "কমেন্ট রুল সম্পাদনা করুন" else "Edit Comment Rule"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBn) "রুলের নাম" else "Rule Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keywordsStr,
                    onValueChange = { keywordsStr = it },
                    label = { Text(if (isBn) "কি-ওয়ার্ডসমূহ (কমা দিয়ে)" else "Keywords (comma separated)") },
                    placeholder = { Text("দাম, price, কত") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = publicReply,
                    onValueChange = { publicReply = it },
                    label = { Text(if (isBn) "পাবলিক কমেন্ট রিপ্লাই" else "Public Comment Reply") },
                    modifier = Modifier.fillMaxWidth().height(90.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = sendPrivate, onCheckedChange = { sendPrivate = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "ইনবক্সে প্রাইভেট মেসেজ পাঠান" else "Send private inbox message",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (sendPrivate) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = privateReply,
                        onValueChange = { privateReply = it },
                        label = { Text(if (isBn) "ইনবক্স মেসেজ টেক্সট" else "Private Message Text") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val kws = keywordsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (name.isNotBlank() && publicReply.isNotBlank()) {
                        onSave(name, kws, publicReply, sendPrivate, privateReply)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
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
