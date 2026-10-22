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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveTab
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
fun DashboardScreen(
    state: AutomationUiState,
    onNavigate: (ActiveTab) -> Unit,
    onToggleMaster: (Boolean) -> Unit
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Page Status Banner
            ConnectedPageBanner(
                state = state,
                isBn = isBn,
                onConnectClick = { onNavigate(ActiveTab.CONNECT) },
                onToggleMaster = onToggleMaster
            )
        }

        // Stats 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = AppStrings.get(lang, "total_messages"),
                        value = "${state.totalMessagesHandled}",
                        icon = Icons.Default.Chat,
                        accentColor = MetaBlue,
                        badge = if (isBn) "ইনবক্স অটো" else "Inbox Auto"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = AppStrings.get(lang, "total_comments"),
                        value = "${state.totalCommentsHandled}",
                        icon = Icons.Default.Forum,
                        accentColor = TealAccent,
                        badge = if (isBn) "পোস্ট রিপ্লাই" else "Post Reply"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = AppStrings.get(lang, "success_rate"),
                        value = "${state.successRatePercent}%",
                        icon = Icons.Default.Speed,
                        accentColor = EmeraldGreen,
                        badge = if (isBn) "গ্রাফ এপিআই" else "Graph API"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = AppStrings.get(lang, "active_rules"),
                        value = "${state.messengerRules.count { it.isEnabled } + state.commentRules.count { it.isEnabled }}",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Color(0xFF8B5CF6),
                        badge = if (isBn) "সক্রিয় রুলস" else "Active Rules"
                    )
                }
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = AppStrings.get(lang, "quick_actions"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionRow(
                    title = if (isBn) "মেসেঞ্জার অটোমেশন কনফিগার" else "Configure Messenger Rules",
                    subtitle = if (isBn) "স্বাগতম মেসেজ, কি-ওয়ার্ড রুলস ও অফিস সময়" else "Welcome msg, keyword rules & office hours",
                    icon = Icons.Default.Chat,
                    accentColor = MetaBlue,
                    onClick = { onNavigate(ActiveTab.MESSENGER) }
                )
                QuickActionRow(
                    title = if (isBn) "পোস্ট কমেন্ট অটো-রিপ্লাই ও ডুপ্লিকেট ফিল্টার" else "Configure Comment Rules & Anti-Dup",
                    subtitle = if (isBn) "পাবলিক কমেন্ট রিপ্লাই এবং ইনবক্স অফার সেন্ড" else "Public reply & inbox private message",
                    icon = Icons.Default.Forum,
                    accentColor = TealAccent,
                    onClick = { onNavigate(ActiveTab.COMMENTS) }
                )
                QuickActionRow(
                    title = if (isBn) "লাইভ মেটা ওয়েবহুক সিমুলেটর" else "Live Meta Webhook Simulator",
                    subtitle = if (isBn) "ইনবক্স মেসেজ ও কমেন্ট পাঠিয়ে ফলাফল দেখুন" else "Test instant auto-replies with live payload",
                    icon = Icons.Default.PlayArrow,
                    accentColor = EmeraldGreen,
                    onClick = { onNavigate(ActiveTab.SIMULATOR) }
                )
                QuickActionRow(
                    title = if (isBn) "মেটা প্রোডাক্টস: Webhooks ও Facebook Login" else "Meta Products: Webhooks & Facebook Login",
                    subtitle = if (isBn) "প্রোডাক্ট অ্যাক্টিভেশন, রিডাইরেক্ট URI ও হ্যান্ডশেক পিং" else "Product activation, OAuth redirect URIs & handshake ping",
                    icon = Icons.Default.Verified,
                    accentColor = MetaBlue,
                    onClick = { onNavigate(ActiveTab.EXPORT) }
                )
                QuickActionRow(
                    title = if (isBn) "ক্লাউডফ্লেয়ার ওয়ার্কার ও সুপাবেস কোড" else "Cloudflare Worker & Supabase Code",
                    subtitle = if (isBn) "সার্ভারলেস ডিপ্লয়মেন্ট স্ক্রিপ্ট ও SQL মাইগ্রেশন" else "Export webhook receiver & database tables",
                    icon = Icons.Default.Code,
                    accentColor = Color(0xFFF59E0B),
                    onClick = { onNavigate(ActiveTab.EXPORT) }
                )
            }
        }

        // Recent Activity Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get(lang, "recent_activity"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(
                    onClick = { onNavigate(ActiveTab.LOGS) },
                    modifier = Modifier.testTag("view_all_logs_button")
                ) {
                    Text(
                        text = AppStrings.get(lang, "view_all_logs"),
                        color = MetaBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MetaBlue
                    )
                }
            }
        }

        // Recent Activity Items (Latest 5)
        items(state.activityLogs.take(5)) { log ->
            ActivityLogCard(log = log, isBn = isBn)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ConnectedPageBanner(
    state: AutomationUiState,
    isBn: Boolean,
    onConnectClick: () -> Unit,
    onToggleMaster: (Boolean) -> Unit
) {
    val page = state.connectedPage
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("connected_page_banner")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (page != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MetaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = page.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = MetaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "${page.category} • ${page.followersCount} followers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Master Active Toggle
                    Switch(
                        checked = state.isAutomationActive,
                        onCheckedChange = onToggleMaster,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldGreen
                        ),
                        modifier = Modifier.testTag("dashboard_master_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isAutomationActive) EmeraldGreen else RoseError)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isAutomationActive)
                                (if (isBn) "মেটা গ্রাফ এপিআই লাইভ সক্রিয়" else "Graph API Live & Active")
                            else
                                (if (isBn) "অটোমেশন সাময়িক বন্ধ" else "Automations Paused"),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (state.isAutomationActive) EmeraldGreen else RoseError
                        )
                    }

                    TextButton(
                        onClick = onConnectClick,
                        modifier = Modifier.testTag("manage_connection_button")
                    ) {
                        Text(
                            text = if (isBn) "পেজ ম্যানেজ করুন" else "Manage Page",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MetaBlue
                        )
                    }
                }
            } else {
                // No Page Connected State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(RoseError.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = RoseError,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "কোনো ফেসবুক পেজ যুক্ত নেই" else "No Page Connected",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isBn) "স্বয়ংক্রিয় রিপ্লাই চালু করতে পেজ যুক্ত করুন" else "Connect a Facebook Page to activate auto-replies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onConnectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
                    ) {
                        Text(if (isBn) "কানেক্ট" else "Connect")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    badge: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun QuickActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ActivityLogCard(log: ActivityLog, isBn: Boolean) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(log.timestamp))

    val (icon, color) = when (log.type) {
        LogType.MESSENGER -> Pair(Icons.Default.Chat, MetaBlue)
        LogType.COMMENT -> Pair(Icons.Default.Forum, TealAccent)
        LogType.WEBHOOK_VERIFY -> Pair(Icons.Default.Verified, EmeraldGreen)
        LogType.TOKEN_CHECK -> Pair(Icons.Default.CheckCircle, Color(0xFF8B5CF6))
        LogType.ERROR -> Pair(Icons.Default.History, RoseError)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
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
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))
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
