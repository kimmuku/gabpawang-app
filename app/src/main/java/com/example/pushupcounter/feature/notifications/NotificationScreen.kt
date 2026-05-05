package com.example.pushupcounter.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.pushupcounter.ui.components.BackHeader
import com.example.pushupcounter.ui.components.StatusBarSpacer
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.Orange
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

private data class Notif(
    val emoji: String,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean
)

private val items = listOf(
    Notif("⚡", "단계 진화!", "3단계 보통 운동인을 달성했어요", "방금", true),
    Notif("🔥", "12일 연속 기록", "오늘도 운동을 빠뜨리지 마세요!", "1시간 전", true),
    Notif("🏆", "친구가 당신을 추월했어요", "민수가 1240회로 1위에 올랐어요", "오늘 오전 9:15", false),
    Notif("💯", "100개 챌린지", "새로운 챌린지가 시작되었어요", "어제", false),
    Notif("📅", "운동 알림", "오늘의 운동 시간이에요!", "어제", false)
)

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            BackHeader(title = "알림", onBack = onBack, subtitle = "새 알림 ${items.count { it.unread }}개")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                for (item in items) {
                    NotifCard(item)
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun NotifCard(item: Notif) {
    val bg = if (item.unread) Yellow.copy(alpha = 0.07f) else BgCard
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(item.emoji, fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (item.unread) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Orange)
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(item.body, color = TextSub, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(item.time, color = TextSub.copy(alpha = 0.6f), fontSize = 10.sp)
        }
    }
}
