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

// Google's official test banner ad unit ID — safe for development.
// Replace with real ad unit ID after AdMob account/app setup.
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

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
                adUnitId = TEST_BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
