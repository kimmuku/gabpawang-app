package com.gabpawang.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gabpawang.app.R

@Composable
fun GabpaChar(
    stage: Int,
    sizeDp: Dp,
    glow: Boolean = false,
    modifier: Modifier = Modifier
) {
    val s = stage.coerceIn(1, 10)
    val resId = when (s) {
        1 -> R.drawable.char_stage_1
        2 -> R.drawable.char_stage_2
        3 -> R.drawable.char_stage_3
        4 -> R.drawable.char_stage_4
        5 -> R.drawable.char_stage_5
        6 -> R.drawable.char_stage_6
        7 -> R.drawable.char_stage_7
        8 -> R.drawable.char_stage_8
        9 -> R.drawable.char_stage_9
        else -> R.drawable.char_stage_10
    }

    Image(
        painter = painterResource(resId),
        contentDescription = "${s}단계 캐릭터",
        modifier = modifier.size(sizeDp).fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun GabpaCharSmall(stage: Int, modifier: Modifier = Modifier) {
    GabpaChar(stage = stage, sizeDp = 40.dp, glow = false, modifier = modifier)
}
