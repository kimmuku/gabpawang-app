package com.gabpawang.app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "BillingManager"
private const val PRODUCT_ID = "remove_ads"
private const val PREFS = "gabpa_prefs"
private const val KEY_ADS_REMOVED = "ads_removed"
private const val FALLBACK_PRICE = "4,900원"

/**
 * Wraps Google Play Billing to support the "remove_ads" one-time purchase.
 * Loads the cached ad-free flag synchronously and verifies with the Play Store
 * asynchronously on init. Exposes [isAdFree] for reactive ad gating.
 */
object BillingManager {
    private val _isAdFree = MutableStateFlow(false)
    val isAdFree: StateFlow<Boolean> = _isAdFree.asStateFlow()

    private val _price = MutableStateFlow(FALLBACK_PRICE)
    val price: StateFlow<String> = _price.asStateFlow()

    private var client: BillingClient? = null
    private var productDetails: ProductDetails? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _isAdFree.value = prefs.getBoolean(KEY_ADS_REMOVED, false)

        val listener = PurchasesUpdatedListener { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { handlePurchase(it) }
            }
        }
        client = BillingClient.newBuilder(context.applicationContext)
            .setListener(listener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()

        client?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProduct()
                    queryExistingPurchases()
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    private fun queryProduct() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        client?.queryProductDetailsAsync(params) { result, list ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetails = list.firstOrNull()
                productDetails?.oneTimePurchaseOfferDetails?.formattedPrice?.let {
                    _price.value = it
                }
            } else {
                Log.w(TAG, "Product query failed: ${result.debugMessage}")
            }
        }
    }

    private fun queryExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        client?.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Query purchases failed: ${result.debugMessage}")
                return@queryPurchasesAsync
            }
            // Detect refund/revocation: if remove_ads is no longer in active
            // purchases but cached as ad-free, demote the flag so ads reappear.
            val hasActiveAdFree = purchases.any {
                it.products.contains(PRODUCT_ID) &&
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            if (!hasActiveAdFree && _isAdFree.value) {
                Log.d(TAG, "remove_ads no longer active — restoring ads")
                setAdFree(false)
            }
            purchases.forEach { handlePurchase(it) }
        }
    }

    fun launchPurchase(activity: Activity) {
        val pd = productDetails
        val billing = client
        if (pd == null || billing == null) {
            Log.w(TAG, "Cannot launch purchase: product or client not ready")
            return
        }
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(pd)
                        .build()
                )
            )
            .build()
        billing.launchBillingFlow(activity, params)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.products.contains(PRODUCT_ID)) return
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                setAdFree(true)
                if (!purchase.isAcknowledged) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    client?.acknowledgePurchase(params) { result ->
                        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                            Log.w(TAG, "Acknowledge failed: ${result.debugMessage}")
                        }
                    }
                }
            }
            Purchase.PurchaseState.PENDING -> {
                // Awaiting payment confirmation (e.g., 무통장입금) — do not grant yet.
                Log.d(TAG, "Purchase pending: ${purchase.purchaseToken}")
            }
            else -> {
                // UNSPECIFIED_STATE or unknown — treat as not entitled.
                if (_isAdFree.value) setAdFree(false)
            }
        }
    }

    private fun setAdFree(value: Boolean) {
        _isAdFree.value = value
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            ?.edit()?.putBoolean(KEY_ADS_REMOVED, value)?.apply()
    }
}
