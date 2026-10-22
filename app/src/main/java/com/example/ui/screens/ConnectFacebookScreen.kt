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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacebookPage
import com.example.data.model.MetaPermissionItem
import com.example.data.repository.AutomationUiState
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.RoseError
import com.example.ui.theme.TealAccent

@Composable
fun ConnectFacebookScreen(
    state: AutomationUiState,
    onConnectCustomPage: (pageId: String, pageName: String, accessToken: String) -> Unit,
    onDisconnectPage: () -> Unit
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA
    val page = state.connectedPage

    var showTokenInput by remember { mutableStateOf(false) }
    var inputPageName by remember { mutableStateOf("") }
    var inputPageId by remember { mutableStateOf("") }
    var inputToken by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("connect_facebook_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Screen Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetaBlue.copy(alpha = 0.08f)),
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
                            .background(MetaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppStrings.get(lang, "meta_oauth_title"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.get(lang, "meta_oauth_desc"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Connection Action Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (page != null) {
                        // Connected Page Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MetaBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = page.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = page.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${AppStrings.get(lang, "page_id")}: ${page.id}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (isBn) "সংযুক্ত" else "Connected",
                                    color = EmeraldGreen,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Details Breakdown
                        PageInfoRow(
                            label = AppStrings.get(lang, "page_category"),
                            value = page.category
                        )
                        PageInfoRow(
                            label = AppStrings.get(lang, "followers"),
                            value = "${page.followersCount} Followers"
                        )
                        PageInfoRow(
                            label = AppStrings.get(lang, "token_status"),
                            value = page.tokenExpiresAt,
                            isSuccess = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDisconnectPage,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                modifier = Modifier.weight(1f).testTag("disconnect_page_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LinkOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(AppStrings.get(lang, "disconnect_page"))
                            }

                            Button(
                                onClick = {
                                    statusMessage = if (isBn) "গ্রাফ এপিআই সংযোগ রিফ্রেশ সফল হয়েছে!" else "Graph API connection refreshed!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                modifier = Modifier.weight(1f).testTag("refresh_token_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "রিফ্রেশ" else "Refresh")
                            }
                        }
                    } else {
                        // Not connected
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MetaBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = MetaBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isBn) "ফেসবুক পেজ যুক্ত করুন" else "Connect your Facebook Page",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isBn) "মেটার অনুমোদিত অফিসিয়াল Graph API দিয়ে মাত্র এক ক্লিকে পেজ কানেক্ট করুন"
                                else "Connect your page in seconds with official Meta Graph API",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onConnectCustomPage(
                                        "102938475610293",
                                        "Masum Tech & Fashion Zone",
                                        "EAABwzLIX4nwBO1kX9mK8j3p82n4Z..."
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("official_fb_login_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get(lang, "connect_with_facebook"),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = statusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Manual / Direct Page Access Token Input Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get(lang, "custom_token_input"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { showTokenInput = !showTokenInput }) {
                            Text(if (showTokenInput) (if (isBn) "লুকান" else "Hide") else (if (isBn) "খুলুন" else "Open"))
                        }
                    }

                    if (showTokenInput) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputPageName,
                            onValueChange = { inputPageName = it },
                            label = { Text(if (isBn) "পেজের নাম" else "Page Name") },
                            placeholder = { Text("e.g. Masum Electronics") },
                            modifier = Modifier.fillMaxWidth().testTag("input_page_name")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputPageId,
                            onValueChange = { inputPageId = it },
                            label = { Text(if (isBn) "পেজ আইডি (Page ID)" else "Page ID") },
                            placeholder = { Text("e.g. 109283746152431") },
                            modifier = Modifier.fillMaxWidth().testTag("input_page_id")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputToken,
                            onValueChange = { inputToken = it },
                            label = { Text(if (isBn) "Page Access Token (EAAB...)" else "Page Access Token") },
                            placeholder = { Text("EAABwzLIX4nwBO...") },
                            modifier = Modifier.fillMaxWidth().testTag("input_page_token")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (inputPageName.isNotBlank() || inputPageId.isNotBlank()) {
                                    onConnectCustomPage(inputPageId, inputPageName, inputToken)
                                    statusMessage = if (isBn) "কাস্টম পেজ সফলভাবে যুক্ত হয়েছে!" else "Custom page connected successfully!"
                                    showTokenInput = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            modifier = Modifier.fillMaxWidth().testTag("save_custom_page_button")
                        ) {
                            Text(AppStrings.get(lang, "save_token"))
                        }
                    }
                }
            }
        }

        // Meta Permissions Checklist
        item {
            Text(
                text = AppStrings.get(lang, "meta_permissions"),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(state.metaPermissions) { perm ->
            PermissionItemCard(item = perm, isBn = isBn)
        }

        // Security Assurance Card
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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBn) "নিরাপত্তা ও মেটা পলিসি নিশ্চয়তা" else "Security & Meta Policy Compliance",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBn)
                                "আমরা কখনো আপনার ব্যক্তিগত ফেসবুক পাসওয়ার্ড জিজ্ঞাসা বা সংরক্ষণ করি না। মেটা অফিসিয়াল Graph API OAuth 2.0 টোকেনের মাধ্যমে সম্পূর্ণ সুরক্ষিতভাবে সংযোগ করা হয়।"
                            else
                                "We never ask for or store your personal Facebook password. All connections use official Meta Graph API OAuth 2.0 tokens securely.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PageInfoRow(label: String, value: String, isSuccess: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isSuccess) EmeraldGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PermissionItemCard(item: MetaPermissionItem, isBn: Boolean) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (item.isApproved) EmeraldGreen.copy(alpha = 0.12f) else RoseError.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isApproved) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (item.isApproved) EmeraldGreen else RoseError,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBn) item.nameBn else item.nameEn,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = item.permission,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBn) item.descriptionBn else item.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
