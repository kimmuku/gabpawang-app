package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var pushNotif by remember { mutableStateOf(true) }
    var workoutReminder by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(true) }
    val voiceOn = voiceEnabled

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
                    .verticalScroll(rememberScrollState())
            ) {
                SectionTitle("계정")
                ItemRow(label = "프로필 편집", value = "푸쉬업왕", onClick = {})
                ItemRow(label = "이메일", value = "user@gabpa.com", onClick = {})

                SectionTitle("알림")
                SwitchRow("푸시 알림", pushNotif) { pushNotif = it }
                SwitchRow("운동 리마인더", workoutReminder) { workoutReminder = it }

                SectionTitle("운동 설정")
                SwitchRow("효과음", soundOn) { soundOn = it }
                SwitchRow("음성 카운트", voiceOn) { onVoiceChange(it) }

                SectionTitle("개인정보")
                ItemRow(label = "데이터 백업", value = "", onClick = {})
                ItemRow(label = "이용 약관", value = "", onClick = {})
                ItemRow(label = "버전", value = "1.0.0", onClick = null)
                Spacer(Modifier.height(40.dp))
            }
            BottomNav(active = "settings", onNav = onNav)
        }
    }
}

@Composable
private fun ItemRow(label: String, value: String, onClick: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextPrimary, fontSize = 14.sp)
        if (value.isNotEmpty()) {
            Text(value, color = TextSub, fontSize = 13.sp)
        }
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
