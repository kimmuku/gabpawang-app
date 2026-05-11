package com.gabpawang.app.feature.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.STAGE_NAMES
import com.gabpawang.app.nationalRankText
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.thresholdFor
import com.gabpawang.app.feature.record.RecordViewModel
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun HomeScreen(
    charStage: Int,
    totalPushups: Int,
    oneRepMax: Int,
    streak: Int = 0,
    onNav: (String) -> Unit,
    onStartWorkout: () -> Unit,
    onCharacter: () -> Unit,
    recordVm: RecordViewModel = viewModel()
) {
    val colors = LocalAppColors.current
    val curThreshold = thresholdFor(charStage)
    val nextThreshold = nextThresholdFor(charStage)
    val progressIntoStage = (totalPushups - curThreshold).coerceAtLeast(0)
    val stageRange = (nextThreshold - curThreshold).coerceAtLeast(1)
    val remaining = (nextThreshold - totalPushups).coerceAtLeast(0)
    val recordState by recordVm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(12.dp))

            // Character + progress area — weight spacers replace Arrangement.Center
            // so content never clips when screen is tight
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                if (oneRepMax > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = "💪",
                            label = "1회 최고 기록",
                            value = "${oneRepMax}개"
                        )
                        val rankText = nationalRankText(oneRepMax)
                        if (rankText != null) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = "🏆",
                                label = "전국 성인남자",
                                value = rankText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val transition = rememberInfiniteTransition(label = "float")
                val offsetY by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = -22f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "floatY"
                )
                val scale by transition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.04f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )
                Box(
                    modifier = Modifier
                        .clickable { onCharacter() }
                        .graphicsLayer {
                            translationY = offsetY
                            scaleX = scale
                            scaleY = scale
                        }
                ) {
                    GabpaChar(stage = charStage, sizeDp = 180.dp)
                    if (streak >= 2) {
                        StreakBadge(
                            streak = streak,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 12.dp, y = (-4).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${charStage}단계 · ${STAGE_NAMES[charStage]}",
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${charStage}단계", color = colors.textSub, fontSize = 13.sp)
                        Text(
                            text = "레벨업까지 ${remaining}개",
                            color = colors.accent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${(charStage + 1).coerceAtMost(10)}단계 🔒",
                            color = colors.textSub,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val ratio = if (stageRange > 0) progressIntoStage.toFloat() / stageRange.toFloat() else 0f
                    val pct = (ratio * 100).toInt().coerceIn(0, 100)
                    Box(modifier = Modifier.fillMaxWidth().height(16.dp)) {
                        GabpaProgressBar(
                            value = progressIntoStage.toFloat(),
                            max = stageRange.toFloat(),
                            height = 16.dp
                        )
                        Box(
                            modifier = Modifier.fillMaxHeight().fillMaxWidth(ratio),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${pct}%",
                                color = Color(0xFF1A1000),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // Mini calendar below progress bar
            MiniCalendar(
                sessionsByDate = recordState.sessionsByDate,
                onDayClick = { onNav("record") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                BtnPrimary(text = "푸쉬업 시작 💪", onClick = onStartWorkout)
            }

            BottomNav(active = "home", onNav = onNav)
        }
    }
}

@Composable
private fun StreakBadge(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFF6B00))
            .border(1.dp, Color(0xFFFF9A4D), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text("🔥", fontSize = 12.sp, lineHeight = 14.sp)
        Text(
            text = "${streak}일 연속",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: String, label: String, value: String) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = colors.textSub, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, color = colors.accent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

