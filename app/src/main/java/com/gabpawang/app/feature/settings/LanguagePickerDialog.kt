package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.LocalAppColors

/** Pair of language tag (e.g. "ko", "en") and its native display name. */
internal data class LanguageOption(val tag: String, val nativeName: String)

/** Add new entries here to support additional languages. */
internal val languageOptions: List<LanguageOption> = listOf(
    LanguageOption("ko", "한국어"),
    LanguageOption("en", "English")
)

internal fun nativeLanguageName(tag: String): String =
    languageOptions.firstOrNull { it.tag == tag }?.nativeName ?: tag

@Composable
fun LanguagePickerDialog(
    currentTag: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgDark)
                .padding(20.dp)
        ) {
            Text(
                stringResource(R.string.dialog_language_title),
                color = colors.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            languageOptions.forEach { option ->
                val isCurrent = option.tag == currentTag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCurrent) colors.accent.copy(alpha = 0.15f) else colors.bgCard)
                        .clickable {
                            onSelect(option.tag)
                            onDismiss()
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        option.nativeName,
                        color = if (isCurrent) colors.accent else colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isCurrent) {
                        Text("✓", color = colors.accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            BtnPrimary(text = stringResource(R.string.common_close), onClick = onDismiss)
        }
    }
}
