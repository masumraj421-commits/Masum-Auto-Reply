package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveTab
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.MetaBlue

@Composable
fun AppBottomNav(
    currentTab: ActiveTab,
    language: AppLanguage,
    onTabSelected: (ActiveTab) -> Unit
) {
    NavigationBar(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("app_bottom_nav")
    ) {
        val items = listOf(
            Triple(ActiveTab.DASHBOARD, Icons.Default.Dashboard, "tab_dashboard"),
            Triple(ActiveTab.CONNECT, Icons.Default.Link, "tab_connect"),
            Triple(ActiveTab.MESSENGER, Icons.Default.Chat, "tab_messenger"),
            Triple(ActiveTab.COMMENTS, Icons.Default.Forum, "tab_comments"),
            Triple(ActiveTab.SIMULATOR, Icons.Default.PlayCircleFilled, "tab_simulator")
        )

        items.forEach { (tab, icon, stringKey) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = AppStrings.get(language, stringKey)
                    )
                },
                label = {
                    Text(
                        text = AppStrings.get(language, stringKey),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MetaBlue,
                    selectedTextColor = MetaBlue,
                    indicatorColor = MetaBlue.copy(alpha = 0.12f)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
