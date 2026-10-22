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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.LogType
import com.example.data.repository.AutomationUiState
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.RoseError
import com.example.ui.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityLogsScreen(
    state: AutomationUiState,
    onClearLogs: () -> Unit
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA

    var selectedFilter by remember { mutableStateOf<LogType?>(null) }
    var selectedLogDetail by remember { mutableStateOf<ActivityLog?>(null) }

    val filteredLogs = remember(state.activityLogs, selectedFilter) {
        if (selectedFilter == null) state.activityLogs
        else state.activityLogs.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("activity_logs_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header with Clear Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStrings.get(lang, "tab_logs"),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${filteredLogs.size} ${if (isBn) "টি ইভেন্ট রেকর্ড" else "events recorded"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (state.activityLogs.isNotEmpty()) {
                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier.testTag("clear_logs_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Logs", tint = RoseError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text(if (isBn) "সকল লগ" else "All Logs") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MetaBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == LogType.MESSENGER,
                        onClick = { selectedFilter = LogType.MESSENGER },
                        label = { Text(if (isBn) "মেসেঞ্জার" else "Messenger") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == LogType.COMMENT,
                        onClick = { selectedFilter = LogType.COMMENT },
                        label = { Text(if (isBn) "কমেন্টস" else "Comments") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == LogType.WEBHOOK_VERIFY,
                        onClick = { selectedFilter = LogType.WEBHOOK_VERIFY },
                        label = { Text(if (isBn) "ওয়েবহুক" else "Webhook") }
                    )
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isBn) "কোনো লগ পাওয়া যায়নি" else "No logs found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredLogs) { log ->
                val timeFormat = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault())
                val formattedTime = timeFormat.format(Date(log.timestamp))

                val (icon, color) = when (log.type) {
                    LogType.MESSENGER -> Pair(Icons.Default.Chat, MetaBlue)
                    LogType.COMMENT -> Pair(Icons.Default.Forum, TealAccent)
                    LogType.WEBHOOK_VERIFY -> Pair(Icons.Default.Verified, EmeraldGreen)
                    LogType.TOKEN_CHECK -> Pair(Icons.Default.Key, Color(0xFF8B5CF6))
                    LogType.ERROR -> Pair(Icons.Default.History, RoseError)
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLogDetail = log }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = log.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Detail Dialog
    if (selectedLogDetail != null) {
        val log = selectedLogDetail!!
        AlertDialog(
            onDismissRequest = { selectedLogDetail = null },
            title = {
                Text(log.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = if (isBn) "বিস্তারিত বিবরণ:" else "Details:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.details, style = MaterialTheme.typography.bodyMedium)

                    if (log.metaEventId.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Meta Event ID: ${log.metaEventId}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (log.responsePayload.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBn) "Graph API Response / Payload:" else "Graph API Payload:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = log.responsePayload,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedLogDetail = null }) {
                    Text(if (isBn) "বন্ধ করুন" else "Close")
                }
            }
        )
    }
}
