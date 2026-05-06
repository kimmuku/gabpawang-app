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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.STAGE_NAMES
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.thresholdFor
import com.gabpawang.app.feature.record.RecordViewModel
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    charStage: Int,
    totalPushups: Int,
    onNav: (String) -> Unit,
    onStartWorkout: () -> Unit,
    onCharacter: () -> Unit,
    recordVm: RecordViewModel = viewModel()
) {
    val curThreshold = thresholdFor(charStage)
    val nextThreshold = nextThresholdFor(charStage)
    val progressIntoStage = (totalPushups - curThreshold).coerceAtLeast(0)
    val stageRange = (nextThreshold - curThreshold).coerceAtLeast(1)
    val remaining = (nextThreshold - totalPushups).coerceAtLeast(0)
    val recordState by recordVm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()

            // Character + progress area
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val transition = rememberInfiniteTransition(label = "float")
                val offsetY by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = -10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "floatY"
                )
                Box(
                    modifier = Modifier
                        .clickable { onCharacter() }
                        .graphicsLayer { translationY = offsetY }
                ) {
                    GabpaChar(stage = charStage, sizeDp = 180.dp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${charStage}단계 · ${STAGE_NAMES[charStage]}",
                    color = TextPrimary,
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
                        Text("${charStage}단계", color = TextSub, fontSize = 13.sp)
                        Text(
                            text = "레벨업까지 ${remaining}개",
                            color = Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${(charStage + 1).coerceAtMost(10)}단계 🔒",
                            color = TextSub,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GabpaProgressBar(
                        value = progressIntoStage.toFloat(),
                        max = stageRange.toFloat(),
                        height = 16.dp
                    )
                }
            }

            // Mini calendar below progress bar
            MiniCalendar(
                sessionsByDate = recordState.sessionsByDate,
                onDayClick = { onNav("record") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                BtnPrimary(text = "운동 시작 💪", onClick = onStartWorkout)
            }

            BottomNav(active = "home", onNav = onNav)
        }
    }
}

@Composable
private fun MiniCalendar(
    sessionsByDate: Map<String, Int>,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = remember { Calendar.getInstance() }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val today = cal.get(Calendar.DAY_OF_MONTH)
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthLabel = remember {
        SimpleDateFormat("yyyy년 M월", Locale.getDefault()).format(cal.time)
    }
    val firstDayOfWeek = remember {
        Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
            .get(Calendar.DAY_OF_WEEK) - 1
    }
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun dateStr(day: Int): String {
        val c = Calendar.getInstance(); c.set(year, month, day); return fmt.format(c.time)
    }

    val padded = List(firstDayOfWeek) { 0 } + (1..maxDay).toList()
    val full = padded + List((7 - padded.size % 7) % 7) { 0 }
    val rows = full.chunked(7)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = monthLabel,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 1.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { label ->
                Text(
                    text = label, color = TextSub, fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                row.forEach { d ->
                    if (d == 0) {
                        Spacer(modifier = Modifier.weight(1f).height(30.dp))
                    } else {
                        val count = sessionsByDate[dateStr(d)] ?: 0
                        val intensity = (count / 50f).coerceIn(0f, 1f)
                        val bg = if (count > 0) Yellow.copy(alpha = 0.15f + intensity * 0.55f) else BgCard
                        val textColor = if (count > 0) Color.Black else TextSub
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(bg)
                                .border(
                                    width = if (d == today) 1.5.dp else 0.dp,
                                    color = if (d == today) Yellow else Color.Transparent,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable { onDayClick() }
                                .padding(vertical = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$d",
                                color = textColor,
                                fontSize = 8.sp,
                                fontWeight = if (d == today) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = if (count > 0) "${count}회" else "-",
                                color = textColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
