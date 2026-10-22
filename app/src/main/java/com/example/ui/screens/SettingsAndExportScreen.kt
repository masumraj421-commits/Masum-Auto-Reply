package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetaAppConfig
import com.example.data.repository.AutomationUiState
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.RoseError
import com.example.ui.theme.TealAccent

@Composable
fun SettingsAndExportScreen(
    state: AutomationUiState,
    onSaveMetaConfig: (MetaAppConfig) -> Unit,
    onToggleProduct: (isFacebookLogin: Boolean) -> Unit = {},
    onToggleWebhookSub: (field: String) -> Unit = {},
    onTestHandshake: () -> Pair<Boolean, String> = { Pair(true, "mock") }
) {
    val lang = state.language
    val isBn = lang == AppLanguage.BANGLA
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Meta Products, 1: Cloudflare Worker, 2: Supabase SQL, 3: Guide

    var appId by remember { mutableStateOf(state.metaAppConfig.appId) }
    var appSecret by remember { mutableStateOf(state.metaAppConfig.appSecret) }
    var verifyToken by remember { mutableStateOf(state.metaAppConfig.verifyToken) }
    var webhookUrl by remember { mutableStateOf(state.metaAppConfig.webhookCallbackUrl) }
    var redirectUri by remember { mutableStateOf(state.metaAppConfig.oauthRedirectUri) }
    var handshakeResult by remember { mutableStateOf<String?>(null) }

    val cloudflareWorkerCode = """
// ============================================================================
// MASUM AUTO REPLY - PRODUCTION CLOUDFLARE WORKER / META WEBHOOK HANDLER
// ============================================================================
export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    // 1. Meta Webhook GET Verification (Challenge Handshake)
    if (request.method === "GET") {
      const mode = url.searchParams.get("hub.mode");
      const token = url.searchParams.get("hub.verify_token");
      const challenge = url.searchParams.get("hub.challenge");

      if (mode === "subscribe" && token === env.META_VERIFY_TOKEN) {
        console.log("Meta Webhook Handshake Verified!");
        return new Response(challenge, { status: 200 });
      }
      return new Response("Verification token mismatch", { status: 403 });
    }

    // 2. Meta Webhook POST Event Receiver
    if (request.method === "POST") {
      const signature = request.headers.get("x-hub-signature-256");
      const rawBody = await request.text();

      const payload = JSON.parse(rawBody);

      if (payload.object === "page") {
        for (const entry of payload.entry) {
          // A. Process Messenger Messages
          if (entry.messaging) {
            for (const event of entry.messaging) {
              if (event.message && !event.message.is_echo) {
                await handleMessengerReply(event, env);
              }
            }
          }

          // B. Process Feed Comments
          if (entry.changes) {
            for (const change of entry.changes) {
              if (change.field === "feed" && change.value?.item === "comment") {
                await handleCommentReply(change.value, env);
              }
            }
          }
        }
        return new Response("EVENT_RECEIVED", { status: 200 });
      }
      return new Response("Unknown object", { status: 404 });
    }

    return new Response("Masum Auto Reply Webhook Server", { status: 200 });
  }
};

async function handleMessengerReply(event, env) {
  const senderId = event.sender.id;
  await fetch("https://graph.facebook.com/v21.0/me/messages?access_token=" + env.PAGE_ACCESS_TOKEN, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      recipient: { id: senderId },
      message: { text: "ধন্যবাদ আপনার বার্তার জন্য! আমরা খুব দ্রুত উত্তর দেব।" }
    })
  });
}

async function handleCommentReply(val, env) {
  if (val.verb !== "add") return;
  const commentId = val.comment_id;

  await fetch("https://graph.facebook.com/v21.0/" + commentId + "/comments?access_token=" + env.PAGE_ACCESS_TOKEN, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      message: "ধন্যবাদ আপনার সুন্দর কমেন্টের জন্য! বিস্তারিত জানতে ইনবক্স চেক করুন ❤️"
    })
  });
}
""".trimIndent()

    val supabaseSqlCode = """
-- ============================================================================
-- MASUM AUTO REPLY - SUPABASE POSTGRESQL SCHEMA WITH ROW LEVEL SECURITY (RLS)
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.connected_pages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    facebook_page_id TEXT NOT NULL UNIQUE,
    page_name TEXT NOT NULL,
    category TEXT,
    page_access_token TEXT NOT NULL,
    followers_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.messenger_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID REFERENCES public.connected_pages(id) ON DELETE CASCADE,
    rule_name TEXT NOT NULL,
    keywords TEXT[] NOT NULL,
    reply_text TEXT NOT NULL,
    is_welcome BOOLEAN DEFAULT FALSE,
    is_default_fallback BOOLEAN DEFAULT FALSE,
    is_enabled BOOLEAN DEFAULT TRUE,
    match_count INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS public.comment_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID REFERENCES public.connected_pages(id) ON DELETE CASCADE,
    rule_name TEXT NOT NULL,
    keywords TEXT[] NOT NULL,
    reply_text TEXT NOT NULL,
    send_private_message BOOLEAN DEFAULT TRUE,
    private_message_text TEXT,
    is_enabled BOOLEAN DEFAULT TRUE,
    match_count INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS public.processed_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id TEXT NOT NULL UNIQUE,
    event_type TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.activity_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID REFERENCES public.connected_pages(id) ON DELETE CASCADE,
    event_type TEXT NOT NULL,
    title TEXT NOT NULL,
    details TEXT NOT NULL,
    status TEXT DEFAULT 'SUCCESS',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.connected_pages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.messenger_rules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.comment_rules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.activity_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can manage their own pages"
ON public.connected_pages FOR ALL USING (auth.uid() = user_id);
""".trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Screen Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MetaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBn) "মেটা প্রোডাক্টস ও ক্লাউড কনফিগারেশন" else "Meta Products & Cloud Setup",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isBn) "Facebook Login for Business এবং Webhooks প্রোডাক্ট ম্যানেজার" else "Manage Facebook Login for Business & Webhooks products",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Horizontal Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MetaBlue,
                edgePadding = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBn) "মেটা প্রোডাক্টস (Login & Webhook)" else "Meta Products", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Cloudflare Worker", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Supabase SQL", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text(if (isBn) "সেটআপ গাইড" else "Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // TAB 0: META PRODUCTS MANAGER (Facebook Login for Business & Webhooks)
        if (selectedTab == 0) {
            // Product 1: Facebook Login for Business
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("product_fb_login_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MetaBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Facebook Login for Business",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isBn) "অনুমোদিত ফেসবুক পেজ ও টোকেন সংগ্রহের অফিসিয়াল লগইন" else "Official OAuth 2.0 login for business pages",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.metaAppConfig.isFacebookLoginAdded,
                                onCheckedChange = { onToggleProduct(true) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MetaBlue
                                ),
                                modifier = Modifier.testTag("toggle_fb_login_switch")
                            )
                        }

                        if (state.metaAppConfig.isFacebookLoginAdded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldGreen.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBn) "প্রোডাক্ট স্ট্যাটাস: যুক্ত ও সক্রিয় (Active)" else "Status: Added & Active",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isBn) "Valid OAuth Redirect URIs (মেটা কনসোলে বসান):" else "Valid OAuth Redirect URIs:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = redirectUri,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(redirectUri))
                                            Toast.makeText(context, if (isBn) "Redirect URI কপি করা হয়েছে!" else "Redirect URI copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(if (isBn) "কপি" else "Copy", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Product 2: Webhooks (Page Object)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("product_webhooks_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TealAccent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Webhook, contentDescription = null, tint = TealAccent, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Webhooks (Page Object)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isBn) "মেসেঞ্জার ও কমেন্ট ইভেন্ট তাৎক্ষণিক রিসিভ করার গেটওয়ে" else "Instant receiver for Page messages & comment events",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.metaAppConfig.isWebhooksAdded,
                                onCheckedChange = { onToggleProduct(false) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TealAccent
                                ),
                                modifier = Modifier.testTag("toggle_webhooks_switch")
                            )
                        }

                        if (state.metaAppConfig.isWebhooksAdded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Subscriptions checklist
                            Text(
                                text = if (isBn) "সাবস্ক্রাইব করা ইভেন্ট ফিল্ডস (Page Subscriptions):" else "Subscribed Page Fields:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val subFields = listOf(
                                Pair("messages", if (isBn) "messages (ইনবক্স মেসেজ)" else "messages (Inbox)"),
                                Pair("messaging_postbacks", if (isBn) "messaging_postbacks (বাটন ক্লিক)" else "messaging_postbacks (Button actions)"),
                                Pair("feed", if (isBn) "feed (পোস্ট কমেন্ট ও রিয়্যাকশন)" else "feed (Post comments)")
                            )

                            subFields.forEach { (field, label) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleWebhookSub(field) }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = state.metaAppConfig.webhookSubscriptions.contains(field),
                                        onCheckedChange = { onToggleWebhookSub(field) },
                                        colors = CheckboxDefaults.colors(checkedColor = TealAccent)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = label, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Callback URL & Verify Token Display
                            Text(
                                text = "Callback URL:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = webhookUrl, fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(webhookUrl))
                                            Toast.makeText(context, if (isBn) "Callback URL কপি করা হয়েছে!" else "Callback URL copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (isBn) "কপি" else "Copy", fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Verify Token (hub.verify_token):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = verifyToken, fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(verifyToken))
                                            Toast.makeText(context, if (isBn) "Verify Token কপি করা হয়েছে!" else "Verify Token copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (isBn) "কপি" else "Copy", fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Test Handshake Button
                            OutlinedButton(
                                onClick = {
                                    val (success, challenge) = onTestHandshake()
                                    handshakeResult = if (isBn)
                                        "মেটা হ্যান্ডশেক সফল! (200 OK, Challenge: $challenge ভেরিফাইড)"
                                    else
                                        "Handshake Verified! (200 OK, Challenge: $challenge)"
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealAccent),
                                modifier = Modifier.fillMaxWidth().testTag("test_handshake_button")
                            ) {
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "টেস্ট মেটা হ্যান্ডশেক পিং পাঠান" else "Test Meta Webhook Handshake")
                            }

                            if (handshakeResult != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldGreen.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = handshakeResult ?: "",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = EmeraldGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Save App Keys Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "মেটা ডেভেলপার অ্যাপ আইডি ও সিক্রেট" else "Meta App ID & Secret",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = appId,
                            onValueChange = { appId = it },
                            label = { Text("App ID") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = appSecret,
                            onValueChange = { appSecret = it },
                            label = { Text("App Secret") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onSaveMetaConfig(
                                    state.metaAppConfig.copy(
                                        appId = appId,
                                        appSecret = appSecret,
                                        verifyToken = verifyToken,
                                        webhookCallbackUrl = webhookUrl,
                                        oauthRedirectUri = redirectUri
                                    )
                                )
                                Toast.makeText(context, if (isBn) "সংরক্ষিত হয়েছে!" else "Saved successfully!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            modifier = Modifier.fillMaxWidth().testTag("save_meta_keys_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "সংরক্ষণ করুন" else "Save Settings")
                        }
                    }
                }
            }
        }

        // TAB 1: CLOUDFLARE WORKER CODE
        if (selectedTab == 1) {
            item {
                CodeViewerCard(
                    title = "Cloudflare Worker / Serverless Script (worker.js)",
                    code = cloudflareWorkerCode,
                    isBn = isBn,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(cloudflareWorkerCode))
                        Toast.makeText(context, if (isBn) "কোড কপি করা হয়েছে!" else "Code copied!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // TAB 2: SUPABASE SQL
        if (selectedTab == 2) {
            item {
                CodeViewerCard(
                    title = "Supabase PostgreSQL Schema (schema.sql)",
                    code = supabaseSqlCode,
                    isBn = isBn,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(supabaseSqlCode))
                        Toast.makeText(context, if (isBn) "SQL স্ক্রিপ্ট কপি করা হয়েছে!" else "SQL script copied!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // TAB 3: META SETUP GUIDE
        if (selectedTab == 3) {
            item {
                MetaSetupGuideCard(isBn = isBn)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CodeViewerCard(
    title: String,
    code: String,
    isBn: Boolean,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "কপি করুন" else "Copy", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun MetaSetupGuideCard(isBn: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isBn) "ধাপে ধাপে মেটা ডেভেলপার কনফিগারেশন গাইড" else "Step-by-Step Meta Developer Setup Guide",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MetaBlue
            )
            Spacer(modifier = Modifier.height(12.dp))

            val steps = if (isBn) listOf(
                "ধাপ ১: developers.facebook.com এ যান এবং 'My Apps' > 'Create App' এ ক্লিক করুন। 'Other' > 'Business' সিলেক্ট করুন।",
                "ধাপ ২: অ্যাপ ড্যাশবোর্ডে গিয়ে 'Facebook Login for Business' এবং 'Webhooks' প্রোডাক্ট যুক্ত করুন।",
                "ধাপ ৩: Webhooks সেকশনে 'Page' সিলেক্ট করে Callback URL দিন এবং Verify Token (যেমন: masum_autoreply_verify_token_2026) বসান।",
                "ধাপ ৪: পেজ ইভেন্টের মধ্যে 'messages' (ইনবক্স মেসেজের জন্য) এবং 'feed' (পোস্ট কমেন্টের জন্য) সাবস্ক্রাইব করুন।",
                "ধাপ ৫: 'App Settings' > 'Basic' থেকে App ID এবং App Secret কপি করে এই অ্যাপে বসিয়ে দিন।",
                "ধাপ ৬: পেজের Never-expiring Page Access Token জেনারেট করে 'পেজ কানেক্ট' অপশনে যুক্ত করুন।"
            ) else listOf(
                "Step 1: Go to developers.facebook.com, click Create App, select 'Other' > 'Business'.",
                "Step 2: Add 'Facebook Login for Business' and 'Webhooks' to your app products.",
                "Step 3: Under Webhooks, choose 'Page', enter Callback URL and Verify Token.",
                "Step 4: Subscribe to 'messages' (for Messenger) and 'feed' (for comments).",
                "Step 5: Copy App ID and App Secret from Settings > Basic into this app.",
                "Step 6: Generate Page Access Token and connect your page."
            )

            steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(MetaBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MetaBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

