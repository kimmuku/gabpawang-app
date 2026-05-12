package com.gabpawang.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.LocalAppColors

private val rankTable = listOf(
    100 to "상위 약 0.2%",
    90 to "상위 약 0.3%",
    80 to "상위 약 0.5%",
    75 to "상위 약 0.8%",
    70 to "상위 약 1%",
    65 to "상위 약 1.5%",
    60 to "상위 약 2.5%",
    55 to "상위 약 4.5%",
    50 to "상위 약 7%",
    45 to "상위 약 10%",
    40 to "상위 약 14%",
    35 to "상위 약 22%",
    30 to "상위 약 32%",
    25 to "상위 약 45%",
    20 to "상위 약 57%",
    15 to "상위 약 70%",
    10 to "상위 약 85%",
    5 to "상위 약 92%"
)

@Composable
fun RankBreakdownDialog(currentMax: Int, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    val matchedThreshold = rankTable.firstOrNull { currentMax >= it.first }?.first ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgDark)
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                Text("🏆 전국 성인남자 푸시업 기록", color = colors.textPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("한국 성인남자 1회 최대 푸시업 횟수 분포 추정치", color = colors.textSub, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("1회 최고 기록", modifier = Modifier.weight(1f), color = colors.textSub, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("전국 순위", modifier = Modifier.weight(1f), color = colors.textSub, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderCard))
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                rankTable.forEach { (threshold, label) ->
                    val isCurrent = threshold == matchedThreshold
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCurrent) colors.accent.copy(alpha = 0.18f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${threshold}개 이상",
                            modifier = Modifier.weight(1f),
                            color = if (isCurrent) colors.accent else colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f),
                            color = if (isCurrent) colors.accent else colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)) {
                Text("* 공식 통계가 아닌 참고용 추정치입니다.", color = colors.textSub, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(14.dp))
                BtnPrimary(text = "닫기", onClick = onDismiss)
            }
        }
    }
}
