package com.example.pushupcounter.feature.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

/** Reusable bar chart drawn directly on Canvas — no chart libs available. */
@Composable
internal fun BarChart(data: List<Int>, labels: List<String>, color: Color) {
    val max = (data.maxOrNull() ?: 1).coerceAtLeast(1)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            val barW = size.width / (data.size * 1.6f)
            val gap = (size.width - barW * data.size) / (data.size + 1)
            for ((i, v) in data.withIndex()) {
                val h = (v.toFloat() / max) * size.height
                val x = gap + i * (barW + gap)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, size.height - h),
                    size = Size(barW, h),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (l in labels) {
                Text(l, color = TextSub, fontSize = 10.sp)
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
