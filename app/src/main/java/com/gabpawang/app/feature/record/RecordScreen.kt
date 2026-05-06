package com.gabpawang.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

private val tabs = listOf(
    "1rm" to "1회 최고",
    "calendar" to "캘린더",
    "stats" to "통계",
    "friends" to "친구"
)

@Composable
fun RecordScreen(
    initialTab: String = "1rm",
    onNav: (String) -> Unit,
    vm: RecordViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(initialTab) }
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            // Title
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "기록",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // Tab row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
            ) {
                for ((id, label) in tabs) {
                    val active = id == activeTab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = id }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            color = if (active) Yellow else TextSub,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(2.dp)
                                .background(if (active) Yellow else BorderCard)
                        )
                    }
                }
            }
            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    "1rm" -> OneRepMaxTab(uiState)
                    "calendar" -> CalendarTab(uiState)
                    "stats" -> StatsTab(uiState)
                    "friends" -> FriendsTab()
                }
            }
            BottomNav(active = "record", onNav = onNav)
        }
    }
}
