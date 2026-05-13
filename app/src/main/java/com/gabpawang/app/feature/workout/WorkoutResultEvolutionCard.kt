package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.gabpawang.app.stageName
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Orange

/** Stage-evolution progress card shown on the workout result screen. */
@Composable
internal fun EvolutionCard(
    stage: Int,
    progressIntoStage: Int,
    stageRange: Int,
    gained: Int,
    remaining: Int
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.result_evolution_progress),
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.result_gained_format, gained),
                color = Orange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        GabpaProgressBar(
            value = progressIntoStage.toFloat(),
            max = stageRange.toFloat(),
            extra = gained.toFloat(),
            height = 10.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.result_stage_label_format, stage, stageName(stage)),
                color = colors.textSub,
                fontSize = 12.sp
            )
            Text(
                text = stringResource(R.string.result_remaining_to_next_format, remaining),
                color = colors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
