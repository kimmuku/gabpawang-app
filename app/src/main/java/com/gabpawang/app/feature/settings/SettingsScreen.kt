package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.SectionTitle
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun SettingsScreen(
    onNav: (String) -> Unit,
    voiceEnabled: Boolean,
    onVoiceChange: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text("설정", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SectionTitle("운동 설정")
                SwitchRow("음성 카운트", voiceEnabled, onVoiceChange)

                SectionTitle("앱 정보")
                InfoRow(label = "버전", value = "1.0.0")
            }
            BottomNav(active = "settings", onNav = onNav)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextPrimary, fontSize = 14.sp)
        Text(value, color = TextSub, fontSize = 13.sp)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(BgCard)
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextPrimary, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Yellow,
                uncheckedThumbColor = TextSub,
                uncheckedTrackColor = BgCard
            )
        )
    }
}
