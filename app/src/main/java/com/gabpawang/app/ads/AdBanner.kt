package com.gabpawang.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gabpawang.app.billing.BillingManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

// Production banner ad unit ID (home/record/settings bottom banner).
private const val BANNER_AD_UNIT_ID = "ca-app-pub-8613737163835175/4035041981"

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    val isAdFree by BillingManager.isAdFree.collectAsState()
    if (isAdFree) return
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
