package com.gabpawang.app.feature.challenge

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
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

private data class ChallengeItem(
    val emoji: String,
    val title: String,
    val desc: String,
    val progress: Float,
    val current: Int,
    val total: Int,
    val reward: String
)

private val challenges = listOf(
    ChallengeItem("💯", "100개 챌린지", "한 번에 100개에 도전!", 0f, 0, 100, "골드 뱃지"),
    ChallengeItem("📅", "30일 챌린지", "30일 연속 운동 도전", 0.4f, 12, 30, "다이아 뱃지"),
    ChallengeItem("⚡", "스피드 챌린지", "5분 안에 50개", 0.6f, 30, 50, "스피드 뱃지")
)

@Composable
fun ChallengeScreen(onNav: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text("챌린지", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("도전하고 보상을 받으세요 🎁", color = TextSub, fontSize = 13.sp)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (c in challenges) {
                    ChallengeCard(c)
                }
                Spacer(Modifier.height(40.dp))
            }
            BottomNav(active = "challenge", onNav = onNav)
        }
    }
}

@Composable
private fun ChallengeCard(c: ChallengeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(c.emoji, fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(c.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(c.desc, color = TextSub, fontSize = 12.sp)
            }
        }
        if (c.progress > 0f) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${c.current} / ${c.total}", color = TextPrimary, fontSize = 12.sp)
                Text("${(c.progress * 100).toInt()}%", color = Yellow, fontSize = 12.sp)
            }
            Spacer(Modifier.height(6.dp))
            GabpaProgressBar(value = c.progress, max = 1f, height = 6.dp)
        }
        Spacer(Modifier.height(10.dp))
        Text(text = "🎁 보상: ${c.reward}", color = Yellow, fontSize = 11.sp)
    }
}
