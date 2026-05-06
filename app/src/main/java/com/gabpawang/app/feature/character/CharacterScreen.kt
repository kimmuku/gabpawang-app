package com.gabpawang.app.feature.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.STAGE_BOUNDARIES
import com.gabpawang.app.STAGE_NAMES
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.thresholdFor
import com.gabpawang.app.ui.components.BackHeader
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.GabpaCharSmall
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun CharacterScreen(charStage: Int, totalPushups: Int, onBack: () -> Unit) {
    val cur = thresholdFor(charStage)
    val next = nextThresholdFor(charStage)
    val pct = (totalPushups - cur).coerceAtLeast(0)
    val range = (next - cur).coerceAtLeast(1)
    val remaining = (next - totalPushups).coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            StatusBarSpacer()
            BackHeader(title = "나의 갑빠", onBack = onBack, subtitle = "누적 ${totalPushups}회")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GabpaChar(stage = charStage, sizeDp = 110.dp, glow = true)
                Spacer(Modifier.height(12.dp))
                Text("푸쉬업왕", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${charStage}단계 · ${STAGE_NAMES[charStage]}",
                    color = Yellow,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                GabpaProgressBar(value = pct.toFloat(), max = range.toFloat(), height = 8.dp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "다음 단계까지 ${remaining}개",
                    color = TextSub,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "진화 로드맵",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (s in 1..9) {
                    StageRow(
                        stage = s,
                        threshold = STAGE_BOUNDARIES[s - 1],
                        cur = charStage
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StageRow(stage: Int, threshold: Int, cur: Int) {
    val state = when {
        stage < cur -> "done"
        stage == cur -> "current"
        else -> "future"
    }
    val (icon, txtColor, alpha) = when (state) {
        "done" -> Triple("✅", TextPrimary, 1f)
        "current" -> Triple("⚡", Yellow, 1f)
        else -> Triple("🔒", TextSub, 0.55f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard.copy(alpha = 0.05f * (if (state == "current") 4f else 1f)))
            .border(
                1.dp,
                if (state == "current") Yellow.copy(alpha = 0.6f) else BorderCard,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(end = 10.dp)) {
            GabpaCharSmall(stage = stage)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${stage}단계 · ${STAGE_NAMES[stage]}",
                color = txtColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "누적 ${threshold}회 이상",
                color = TextSub.copy(alpha = alpha),
                fontSize = 11.sp
            )
        }
        Text(text = icon, fontSize = 18.sp)
    }
}
