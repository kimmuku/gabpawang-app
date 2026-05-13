package com.gabpawang.app.feature.notifications

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BackHeader
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.Yellow

private data class Notif(
    val emoji: String,
    val titleResId: Int,
    val bodyResId: Int,
    val timeResId: Int,
    val unread: Boolean
)

private val items = listOf(
    Notif("⚡", R.string.notif_levelup_title, R.string.notif_levelup_body, R.string.notif_levelup_time, true),
    Notif("🔥", R.string.notif_streak_title, R.string.notif_streak_body, R.string.notif_streak_time, true),
    Notif("🏆", R.string.notif_overtake_title, R.string.notif_overtake_body, R.string.notif_overtake_time, false),
    Notif("💯", R.string.notif_challenge_title, R.string.notif_challenge_body, R.string.notif_challenge_time, false),
    Notif("📅", R.string.notif_reminder_title, R.string.notif_reminder_body, R.string.notif_reminder_time, false)
)

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    val colors = LocalAppColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            BackHeader(
                title = stringResource(R.string.notif_title),
                onBack = onBack,
                subtitle = stringResource(R.string.notif_unread_format, items.count { it.unread })
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
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
    val colors = LocalAppColors.current
    val bg = if (item.unread) Yellow.copy(alpha = 0.07f) else colors.bgCard
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, colors.borderCard, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(item.emoji, fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(item.titleResId),
                    color = colors.textPrimary,
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
            Text(stringResource(item.bodyResId), color = colors.textSub, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(item.timeResId),
                color = colors.textSub.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}
