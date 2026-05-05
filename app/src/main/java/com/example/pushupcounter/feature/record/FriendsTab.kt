package com.example.pushupcounter.feature.record

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun FriendsTab() {
    val friends = listOf(
        Triple("민수", 1240, 1),
        Triple("영희", 980, 2),
        Triple("나", 847, 3),
        Triple("철수", 530, 4)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "이번 주 랭킹",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        for ((name, count, rank) in friends) {
            FriendRow(name, count, rank)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun FriendRow(name: String, count: Int, rank: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (rank <= 3) Yellow else BorderCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                color = if (rank <= 3) Color.Black else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(name, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("${count}회", color = Yellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
