package com.gabpawang.app

import android.app.Application
import androidx.room.Room
import com.gabpawang.app.data.WorkoutRepository
import com.gabpawang.app.data.db.GabpaDatabase
import com.gabpawang.app.data.db.MIGRATION_1_2
import com.gabpawang.app.data.db.MIGRATION_2_3
import com.gabpawang.app.ads.InterstitialAdManager
import com.gabpawang.app.billing.BillingManager
import com.gabpawang.app.data.remote.SupabaseSync
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** Application class that initializes Room, WorkoutRepository, AdMob, Billing, and Supabase. */
class GabpaApplication : Application() {
    val database: GabpaDatabase by lazy {
        Room.databaseBuilder(this, GabpaDatabase::class.java, "gabpa.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }
    val repository: WorkoutRepository by lazy { WorkoutRepository(database) }

    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob and preload the first interstitial.
        MobileAds.initialize(this) {
            InterstitialAdManager.preload(this)
        }
        // Initialize Play Billing to support the "remove ads" purchase.
        BillingManager.init(this)
        // Start anonymous Supabase session in background (tied to Application lifecycle).
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            SupabaseSync.ensureAnonymousSession()
        }
    }
}
