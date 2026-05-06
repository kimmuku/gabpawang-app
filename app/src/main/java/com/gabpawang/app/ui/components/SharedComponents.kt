package com.gabpawang.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

/**
 * Spacer that mimics the system status bar height when not using full insets.
 * Compose lifts content above the status bar via WindowInsets in the parent box,
 * so we use a fixed gap as visual breathing room.
 */
@Composable
fun StatusBarSpacer() {
    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun BtnPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Yellow
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.Black,
            disabledContainerColor = color.copy(alpha = 0.4f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        )
    ) {
        Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BtnGhost(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        )
    ) {
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Yellow base bar with optional Orange "extra" segment overlaid for the most-recent gain.
 *
 * @param value current accumulated progress
 * @param max maximum value (denominator)
 * @param extra portion of [value] that should render in the Orange highlight color
 */
@Composable
fun GabpaProgressBar(
    value: Float,
    max: Float,
    extra: Float = 0f,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp
) {
    val capped = value.coerceIn(0f, max)
    val pct = if (max > 0f) capped / max else 0f
    val extraPct = if (max > 0f) extra.coerceAtLeast(0f) / max else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.15f))
    ) {
        // Base yellow fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(pct)
                .background(Yellow)
        )
        // Orange highlight for "extra"
        if (extraPct > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(extraPct.coerceAtMost(pct))
                    .background(Orange)
            )
        }
    }
}

@Composable
fun BackHeader(
    title: String,
    onBack: () -> Unit,
    subtitle: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BgCard)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", fontSize = 18.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSub,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    onClick: (() -> Unit)? = null
) {
    val baseModifier = modifier
        .clip(RoundedCornerShape(14.dp))
        .background(BgCard)
        .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
        .let { if (onClick != null) it.clickable { onClick() } else it }
        .padding(vertical = 16.dp, horizontal = 12.dp)

    Column(
        modifier = baseModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(text = label, color = TextSub, fontSize = 11.sp)
    }
}

private data class NavItem(val id: String, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("home", "홈", Icons.Outlined.Home),
    NavItem("record", "기록", Icons.Outlined.BarChart),
    NavItem("settings", "설정", Icons.Outlined.Settings)
)

@Composable
fun BottomNav(active: String, onNav: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark.copy(alpha = 0.98f))
            .border(0.5.dp, BorderCard)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (item in navItems) {
            val tint = if (item.id == active) Yellow else TextSub
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNav(item.id) }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = item.label, color = tint, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
