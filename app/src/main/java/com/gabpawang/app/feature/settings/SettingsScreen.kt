package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.SectionTitle
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun SettingsScreen(
    onNav: (String) -> Unit,
    onFeedback: () -> Unit,
    voiceEnabled: Boolean,
    onVoiceChange: (Boolean) -> Unit,
    musicEnabled: Boolean,
    onMusicChange: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text("설정", color = colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SectionTitle("운동 설정")
                SwitchRow("음성 카운트", voiceEnabled, onVoiceChange)
                SwitchRow("배경음악", musicEnabled, onMusicChange)

                SectionTitle("앱 설정")
                SwitchRow("라이트 모드", !isDarkTheme) { onThemeChange(!it) }

                SectionTitle("지원")
                ClickableRow("불편사항 신고", "📢") { onFeedback() }

                SectionTitle("앱 정보")
                InfoRow(label = "버전", value = "1.0.0")
            }
            BottomNav(active = "settings", onNav = onNav)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = colors.textPrimary, fontSize = 14.sp)
        Text(value, color = colors.textSub, fontSize = 13.sp)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(colors.bgCard)
    )
}

/** A tappable settings row with an icon prefix and a "›" chevron on the right. */
@Composable
private fun ClickableRow(label: String, icon: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = colors.textPrimary, fontSize = 14.sp)
        }
        Text("›", color = colors.textSub, fontSize = 20.sp, fontWeight = FontWeight.Light)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(colors.bgCard)
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = colors.textPrimary, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Yellow,
                uncheckedThumbColor = colors.textSub,
                uncheckedTrackColor = colors.bgCard
            )
        )
    }
}
