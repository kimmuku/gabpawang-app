package com.gabpawang.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.gabpawang.app.billing.BillingManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

// Production interstitial ad unit ID (shown every N set completions).
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-8613737163835175/9095796972"

// Show one interstitial every N completed sets.
private const val SET_INTERVAL = 3
private const val TAG = "InterstitialAdManager"

/**
 * Tracks set completions across the session and shows an interstitial ad
 * every [SET_INTERVAL] sets. Preloads the next ad after each show so it's
 * ready when the trigger fires.
 */
object InterstitialAdManager {
    private var ad: InterstitialAd? = null
    private var loading = false
    private var setCount = 0

    fun preload(context: Context) {
        if (ad != null || loading) return
        loading = true
        InterstitialAd.load(
            context.applicationContext,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(loaded: InterstitialAd) {
                    ad = loaded
                    loading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Interstitial load failed: ${error.message}")
                    ad = null
                    loading = false
                }
            }
        )
    }

    /**
     * Increments the set counter and shows the preloaded interstitial when the
     * counter hits [SET_INTERVAL]. After showing, requests the next ad.
     */
    fun onSetCompleted(activity: Activity) {
        if (BillingManager.isAdFree.value) return
        setCount++
        if (setCount % SET_INTERVAL != 0) return
        val current = ad
        if (current == null) {
            preload(activity)
            return
        }
        current.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ad = null
                preload(activity)
            }
        }
        current.show(activity)
    }
}
