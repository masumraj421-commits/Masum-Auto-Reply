package com.example.ui.i18n

enum class AppLanguage {
    BANGLA,
    ENGLISH
}

object AppStrings {
    fun get(lang: AppLanguage, key: String): String {
        val bn = (lang == AppLanguage.BANGLA)
        return when (key) {
            "app_title" -> if (bn) "মাছুম অটো রিপ্লাই" else "Masum Auto Reply"
            "app_subtitle" -> if (bn) "ফেসবুক পেজ মেসেঞ্জার ও কমেন্ট অটোমেশন" else "Facebook Page Messenger & Comment Automation"
            "tab_dashboard" -> if (bn) "ড্যাশবোর্ড" else "Dashboard"
            "tab_connect" -> if (bn) "পেজ কানেক্ট" else "Connect Page"
            "tab_messenger" -> if (bn) "মেসেঞ্জার" else "Messenger"
            "tab_comments" -> if (bn) "কমেন্টস" else "Comments"
            "tab_simulator" -> if (bn) "সিমুলেটর" else "Simulator"
            "tab_logs" -> if (bn) "লগ হিস্ট্রি" else "Activity Logs"
            "tab_export" -> if (bn) "ডিপ্লয়মেন্ট ও কোড" else "Deploy & Code"

            // Dashboard
            "status_active" -> if (bn) "অটোমেশন চালু আছে" else "Automation Active"
            "status_paused" -> if (bn) "অটোমেশন সাময়িক বন্ধ" else "Automation Paused"
            "connected_page" -> if (bn) "সংযুক্ত পেজ" else "Connected Page"
            "no_page_connected" -> if (bn) "কোনো পেজ যুক্ত নেই" else "No Page Connected"
            "total_messages" -> if (bn) "মেসেজ প্রসেসড" else "Messages Handled"
            "total_comments" -> if (bn) "কমেন্ট রিপ্লাইড" else "Comments Handled"
            "success_rate" -> if (bn) "সফলতার হার" else "Success Rate"
            "active_rules" -> if (bn) "সক্রিয় রুলস" else "Active Rules"
            "quick_actions" -> if (bn) "কুইক অ্যাকশন" else "Quick Actions"
            "recent_activity" -> if (bn) "সাম্প্রতিক অ্যাক্টিভিটি" else "Recent Activity"
            "view_all_logs" -> if (bn) "সকল লগ দেখুন" else "View All Logs"
            "test_automation" -> if (bn) "লাইভ টেস্ট করুন" else "Test Live"
            "toggle_automation" -> if (bn) "অটোমেশন স্যুইচ" else "Toggle Automation"

            // Connect Screen
            "meta_oauth_title" -> if (bn) "অফিসিয়াল মেটা গ্রাফ এপিআই কানেকশন" else "Official Meta Graph API Connection"
            "meta_oauth_desc" -> if (bn) "আপনার ফেসবুক পেজ নিরাপদে কানেক্ট করুন। আপনার কোনো পাসওয়ার্ড আমরা সংরক্ষণ করি না।" else "Securely connect your Facebook Page. We never ask for or store passwords."
            "connect_with_facebook" -> if (bn) "ফেসবুকের সাথে কানেক্ট করুন" else "Connect with Facebook"
            "disconnect_page" -> if (bn) "পেজ ডিসকানেক্ট করুন" else "Disconnect Page"
            "page_details" -> if (bn) "পেজের তথ্য" else "Page Details"
            "page_id" -> if (bn) "পেজ আইডি" else "Page ID"
            "page_category" -> if (bn) "ক্যাটাগরি" else "Category"
            "followers" -> if (bn) "ফলোয়ার্স" else "Followers"
            "token_status" -> if (bn) "টোকেন স্ট্যাটাস" else "Token Status"
            "token_valid" -> if (bn) "ভ্যালিড (স্বীকৃত)" else "Valid & Active"
            "token_expired" -> if (bn) "মেয়াদোত্তীর্ণ" else "Expired"
            "meta_permissions" -> if (bn) "অনুমোদিত পারমিশনসমূহ" else "Approved Meta Permissions"
            "custom_token_input" -> if (bn) "সরাসরি পেজ এক্সেস টোকেন দিন (ঐচ্ছিক)" else "Direct Page Access Token (Optional)"
            "save_token" -> if (bn) "টোকেন যাচাই ও সংরক্ষণ" else "Verify & Save Token"

            // Messenger Screen
            "messenger_automation_title" -> if (bn) "ইনবক্স মেসেঞ্জার অটোমেশন" else "Messenger Inbox Automation"
            "welcome_message" -> if (bn) "স্বাগতম মেসেজ (Welcome Message)" else "Welcome Message (First Contact)"
            "welcome_desc" -> if (bn) "নতুন কোনো ইউজার পেজে মেসেজ দিলে স্বয়ংক্রিয়ভাবে পাঠানো হবে।" else "Automatically sent when a user sends their first message."
            "default_reply" -> if (bn) "ডিফল্ট রিপ্লাই (Fallback Reply)" else "Default Reply (Fallback)"
            "default_reply_desc" -> if (bn) "কোনো কি-ওয়ার্ডের সাথে না মিললে এই মেসেজটি রিপ্লাই হবে।" else "Sent when incoming message matches no specific keyword."
            "business_hours" -> if (bn) "অফিস সময়সূচী ও অটো রিপ্লাই" else "Business Hours & Schedule"
            "business_hours_desc" -> if (bn) "অফিস বন্ধের সময় কাস্টমারকে তাৎক্ষণিক নোটিশ পাঠানো।" else "Notify customers during off-hours automatically."
            "keyword_rules" -> if (bn) "কি-ওয়ার্ড ভিত্তিক রুলস" else "Keyword-Based Rules"
            "add_new_rule" -> if (bn) "নতুন রুল যোগ করুন" else "Add New Rule"
            "keywords_hint" -> if (bn) "কমা দিয়ে কি-ওয়ার্ড লিখুন (যেমন: দাম, price, কত)" else "Keywords separated by comma (e.g. price, cost, buy)"
            "reply_text_hint" -> if (bn) "রিপ্লাই মেসেজ লিখুন..." else "Type auto-reply response..."

            // Comment Screen
            "comment_automation_title" -> if (bn) "ফেসবুক পেজ পোস্ট কমেন্ট রিপ্লাই" else "Facebook Post Comment Auto-Reply"
            "comment_master_desc" -> if (bn) "আপনার পেজের প্রতিটি পোস্টে আসা কমেন্টে সাথে সাথে পাবলিক রিপ্লাই যাবে।" else "Instantly reply to customer comments on all Page posts."
            "dedup_protection" -> if (bn) "অ্যান্টি-ডুপ্লিকেট সুরক্ষা" else "Anti-Duplicate Protection"
            "dedup_desc" -> if (bn) "একই কমেন্টে একাধিকবার রিপ্লাই যাওয়া বন্ধ রাখতে স্বয়ংক্রিয় আইডি ট্র্যাকিং সক্রিয়।" else "Prevents duplicate replies by caching processed comment IDs."
            "private_reply" -> if (bn) "কমেন্টারকে ইনবক্সে মেসেজ পাঠান" else "Send Private Messenger Reply"
            "private_reply_desc" -> if (bn) "কমেন্টের পাশাপাশি ব্যবহারকারীকে পেজের ইনবক্সে পার্সোনাল মেসেজ পাঠানো হবে।" else "Sends private direct message along with public comment reply."
            "comment_rules" -> if (bn) "কমেন্ট রুলস তালিকা" else "Comment Rules List"

            // Simulator
            "simulator_title" -> if (bn) "মেটা ওয়েবহুক লাইভ সিমুলেটর" else "Meta Webhook Live Simulator"
            "simulator_desc" -> if (bn) "বাস্তবে পেজে মেসেজ বা কমেন্ট আসলে কীভাবে অটো-রিপ্লাই কাজ করবে তা এখনই পরীক্ষা করুন।" else "Simulate incoming webhook payloads and view immediate response outputs."
            "simulate_messenger" -> if (bn) "মেসেঞ্জার টেস্ট" else "Test Messenger"
            "simulate_comment" -> if (bn) "কমেন্ট টেস্ট" else "Test Comment"
            "sender_name" -> if (bn) "কাস্টমারের নাম" else "Customer Name"
            "customer_message" -> if (bn) "কাস্টমারের মেসেজ বা কমেন্ট" else "Customer Message or Comment"
            "send_test" -> if (bn) "টেস্ট মেসেজ পাঠান" else "Trigger Webhook Event"
            "sim_result" -> if (bn) "প্রসেসিং ফলাফল ও এপিআই পে-লোড" else "Processing Result & API Payload"

            // Common
            "save" -> if (bn) "সংরক্ষণ করুন" else "Save"
            "cancel" -> if (bn) "বাতিল" else "Cancel"
            "edit" -> if (bn) "এডিট" else "Edit"
            "delete" -> if (bn) "মুছুন" else "Delete"
            "enabled" -> if (bn) "সক্রিয়" else "Enabled"
            "disabled" -> if (bn) "নিষ্ক্রিয়" else "Disabled"
            "language" -> if (bn) "ভাষা: বাংলা" else "Lang: English"
            else -> key
        }
    }
}
