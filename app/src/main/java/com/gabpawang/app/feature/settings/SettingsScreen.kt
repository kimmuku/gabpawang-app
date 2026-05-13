package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gabpawang.app.R
import com.gabpawang.app.ads.AdBanner
import com.gabpawang.app.ui.components.BottomNav
import com.gabpawang.app.ui.components.SectionTitle
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun SettingsScreen(
    onNav: (String) -> Unit,
    onFeedback: () -> Unit,
    voiceEnabled: Boolean,
    onVoiceChange: (Boolean) -> Unit,
    musicEnabled: Boolean,
    onMusicChange: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    isAdFree: Boolean = false,
    adRemovalPrice: String = stringResource(R.string.billing_fallback_price),
    onRemoveAds: () -> Unit = {},
    currentLanguage: String = "ko",
    onLanguageChange: (String) -> Unit = {}
) {
    val colors = LocalAppColors.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    stringResource(R.string.settings_title),
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SectionTitle(stringResource(R.string.settings_section_workout))
                SwitchRow(stringResource(R.string.settings_voice_count), voiceEnabled, onVoiceChange)
                SwitchRow(stringResource(R.string.settings_background_music), musicEnabled, onMusicChange)

                SectionTitle(stringResource(R.string.settings_section_app))
                SwitchRow(stringResource(R.string.settings_light_mode), !isDarkTheme) { onThemeChange(!it) }
                LanguageRow(
                    label = stringResource(R.string.settings_language),
                    currentName = nativeLanguageName(currentLanguage),
                    onClick = { showLanguageDialog = true }
                )

                SectionTitle(stringResource(R.string.settings_section_payment))
                if (isAdFree) {
                    InfoRow(
                        label = stringResource(R.string.settings_remove_ads_label),
                        value = stringResource(R.string.settings_remove_ads_active)
                    )
                } else {
                    ClickableRow(
                        label = stringResource(R.string.settings_remove_ads_format, adRemovalPrice),
                        icon = stringResource(R.string.settings_remove_ads_icon)
                    ) { onRemoveAds() }
                }

                SectionTitle(stringResource(R.string.settings_section_support))
                ClickableRow(
                    label = stringResource(R.string.settings_report_issue),
                    icon = stringResource(R.string.settings_report_issue_icon)
                ) { onFeedback() }

                SectionTitle(stringResource(R.string.settings_section_about))
                InfoRow(
                    label = stringResource(R.string.settings_version),
                    value = stringResource(R.string.settings_version_value)
                )
            }
            AdBanner()
            BottomNav(active = "settings", onNav = onNav)
        }

        if (showLanguageDialog) {
            LanguagePickerDialog(
                currentTag = currentLanguage,
                onSelect = { tag -> onLanguageChange(tag) },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
private fun LanguageRow(label: String, currentName: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🌐", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = colors.textPrimary, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(currentName, color = colors.textSub, fontSize = 13.sp)
            Text("›", color = colors.textSub, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(colors.bgCard)
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = colors.textPrimary, fontSize = 14.sp)
        Text(value, color = colors.textSub, fontSize = 13.sp)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(colors.bgCard)
    )
}

/** A tappable settings row with an icon prefix and a "›" chevron on the right. */
@Composable
private fun ClickableRow(label: String, icon: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = colors.textPrimary, fontSize = 14.sp)
        }
        Text("›", color = colors.textSub, fontSize = 20.sp, fontWeight = FontWeight.Light)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(0.5.dp)
            .background(colors.bgCard)
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = colors.textPrimary, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Yellow,
                uncheckedThumbColor = colors.textSub,
                uncheckedTrackColor = colors.bgCard
            )
        )
    }
}
