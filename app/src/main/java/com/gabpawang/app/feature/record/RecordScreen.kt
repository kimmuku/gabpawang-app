package com.gabpawang.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.ads.AdBanner
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors

@Composable
fun RecordScreen(
    onNav: (String) -> Unit,
    vm: RecordViewModel = viewModel()
) {
    val colors = LocalAppColors.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "기록",
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CalendarTab(uiState)
            }
            AdBanner()
            BottomNav(active = "record", onNav = onNav)
        }
    }
}
