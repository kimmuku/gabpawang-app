package com.gabpawang.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

/**
 * Bar chart using BoxWithConstraints so bar heights are proportional to available space.
 * When showValues=true, each bar shows its numeric value just above it.
 * Pass Modifier.weight(1f) from a Column parent to make it fill available vertical space.
 */
@Composable
internal fun BarChart(
    data: List<Int>,
    labels: List<String>,
    color: Color,
    showValues: Boolean = false,
    modifier: Modifier = Modifier
) {
    val max = (data.maxOrNull() ?: 1).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val totalH = maxHeight
            val reservedH = if (showValues) 20.dp else 0.dp
            val maxBarH = (totalH - reservedH).coerceAtLeast(4.dp)

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                for (v in data) {
                    val fraction = v.toFloat() / max
                    val barH = (maxBarH * fraction).coerceAtLeast(if (v > 0) 2.dp else 0.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        if (showValues && v > 0) {
                            Text(
                                text = "$v",
                                color = TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(2.dp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(barH)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(color)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (l in labels) {
                Text(l, color = TextSub, fontSize = 9.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
internal fun StatBlock(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(value, color = Yellow, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextSub, fontSize = 12.sp)
    }
}

@Composable
internal fun Top3Item(title: String, sub: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = TextSub, fontSize = 11.sp)
        }
        Text(value, color = Yellow, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
    Spacer(Modifier.height(8.dp))
}
