package com.gabpawang.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.LocalAppColors

/** Threshold → rank-text resId pairs, ordered from highest to lowest threshold. */
private val rankTable: List<Pair<Int, Int>> = listOf(
    100 to R.string.rank_top_0_2,
    90 to R.string.rank_top_0_3,
    80 to R.string.rank_top_0_5,
    75 to R.string.rank_top_0_8,
    70 to R.string.rank_top_1,
    65 to R.string.rank_top_1_5,
    60 to R.string.rank_top_2_5,
    55 to R.string.rank_top_4_5,
    50 to R.string.rank_top_7,
    45 to R.string.rank_top_10,
    40 to R.string.rank_top_14,
    35 to R.string.rank_top_22,
    30 to R.string.rank_top_32,
    25 to R.string.rank_top_45,
    20 to R.string.rank_top_57,
    15 to R.string.rank_top_70,
    10 to R.string.rank_top_85,
    5 to R.string.rank_top_92
)

@Composable
fun RankBreakdownDialog(currentMax: Int, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    val matchedThreshold = rankTable.firstOrNull { currentMax >= it.first }?.first ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgDark)
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                Text(
                    stringResource(R.string.rank_dialog_title),
                    color = colors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.rank_dialog_subtitle),
                    color = colors.textSub,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        stringResource(R.string.rank_dialog_col_record),
                        modifier = Modifier.weight(1f),
                        color = colors.textSub,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.rank_dialog_col_rank),
                        modifier = Modifier.weight(1f),
                        color = colors.textSub,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderCard))
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                rankTable.forEach { (threshold, labelResId) ->
                    val isCurrent = threshold == matchedThreshold
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isCurrent) colors.accent.copy(alpha = 0.18f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.rank_dialog_row_format, threshold),
                            modifier = Modifier.weight(1f),
                            color = if (isCurrent) colors.accent else colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = stringResource(labelResId),
                            modifier = Modifier.weight(1f),
                            color = if (isCurrent) colors.accent else colors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)) {
                Text(
                    stringResource(R.string.rank_dialog_disclaimer),
                    color = colors.textSub,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                BtnPrimary(text = stringResource(R.string.common_close), onClick = onDismiss)
            }
        }
    }
}
